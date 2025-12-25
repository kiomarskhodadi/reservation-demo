package com.reserve.service.services;

import com.infrastructure.form.OutputAPIForm;
import com.infrastructure.utility.InfraSecurityUtils;
import com.reserve.basedata.ReservationStatus;
import com.reserve.doa.entity.AvailableSlot;
import com.reserve.doa.entity.ReserveUserStatus;
import com.reserve.doa.repository.IAvailableSlotRepo;
import com.reserve.service.dto.AvailableSlotDto;
import com.reserve.service.dto.ReserveUserStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReserveSlotSrv implements IReserveSlotSrv{
    private static final String ALL_AVAILABLE_KEY = "available:slot:processing:queue";
    private static final String LOCK_KEY = "critical:cache:available:slot";
    private static final Integer cacheSize = 100;
    private final RedissonClient redissonClient;
    private final IAvailableSlotRepo availableSlotRepo;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ApplicationEventPublisher eventPublisher;

    public ReserveSlotSrv(RedissonClient redissonClient, IAvailableSlotRepo availableSlotRepo, RedisTemplate<String, Object> redisTemplate, ApplicationEventPublisher eventPublisher) {
        this.redissonClient = redissonClient;
        this.availableSlotRepo = availableSlotRepo;
        this.redisTemplate = redisTemplate;
        this.eventPublisher = eventPublisher;
    }
    @Transactional
    public OutputAPIForm reserveSlot(Long userId){
        OutputAPIForm retVal = new OutputAPIForm();
        AvailableSlot availableSlot = getAvailableSlot();
        if(Objects.nonNull(availableSlot)){
            availableSlot.reserveAvailableSlot(userId);
            availableSlotRepo.saveAndFlush(availableSlot);
            retVal.setData(new AvailableSlotDto(availableSlot));
            eventPublisher.publishEvent(new ReserveUserStatusDto(userId, availableSlot.getId(),availableSlot.getUpdatedAt(), ReservationStatus.reserve));
        }else{
            retVal.setSuccess(false);
        }
        return retVal;
    }
    @Transactional
    public OutputAPIForm cancelReserveSlot(Long reserveId){
        OutputAPIForm retVal = new OutputAPIForm();
        Optional<AvailableSlot> availableSlot = availableSlotRepo.findById(reserveId);
        if(availableSlot.isPresent() && availableSlot.get().getUserId().equals(InfraSecurityUtils.getCurrentUser())){
            availableSlot.get().cancelReservation();
            availableSlotRepo.saveAndFlush(availableSlot.get());
            eventPublisher.publishEvent(new ReserveUserStatusDto(InfraSecurityUtils.getCurrentUser(),
                                                                 availableSlot.get().getId(),
                                                                 availableSlot.get().getUpdatedAt(),
                                                                 ReservationStatus.Cancel));
            addAvailableSlotCache(availableSlot.get());
        }else{
            retVal.setSuccess(false);
        }
        return retVal;
    }

    public void updateAvailableSlotCache() {
        Set<String> availableSlotKeys = redisTemplate.keys(ALL_AVAILABLE_KEY);
        if(CollectionUtils.isEmpty(availableSlotKeys)){
            redisTemplate.delete(ALL_AVAILABLE_KEY);
            PageRequest page = PageRequest.of(0, cacheSize + 1, Sort.by("creationAt"));
            List<AvailableSlot> availableSlots = availableSlotRepo.findNNearestAvailableSlot(new Timestamp(System.currentTimeMillis()),page);
            for(AvailableSlot availableSlot:availableSlots){
                redisTemplate.opsForList().rightPush(ALL_AVAILABLE_KEY,availableSlot);
            }
        }
    }

    public void addAvailableSlotCache(AvailableSlot data) {
        String lockKey = LOCK_KEY ;
        RLock lock = redissonClient.getLock(lockKey);
        try{
            boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if(isLocked){
                try{
                    List<AvailableSlot> availableSlots= new ArrayList<>();
                    AvailableSlot availableSlotCache =  (AvailableSlot)redisTemplate.opsForList().leftPop(ALL_AVAILABLE_KEY);
                    while(Objects.nonNull(availableSlotCache)){
                        availableSlots.add(availableSlotCache);
                        availableSlotCache =  (AvailableSlot)redisTemplate.opsForList().leftPop(ALL_AVAILABLE_KEY);
                    }
                    availableSlots.add(data);
                    availableSlots = availableSlots.stream().sorted(Comparator.comparing(AvailableSlot::getCreationAt)).collect(Collectors.toList());
                    for(AvailableSlot availableSlot:availableSlots){
                        redisTemplate.opsForList().rightPush(ALL_AVAILABLE_KEY,availableSlot);
                    }
                }catch(Exception e){
                    redisTemplate.delete(ALL_AVAILABLE_KEY);
                    updateAvailableSlotCache();
                }finally {
                    lock.unlock();
                }
            }
        }catch (Exception e){

        }


    }
    public AvailableSlot getAvailableSlot(){
        String lockKey = LOCK_KEY ;
        RLock lock = redissonClient.getLock(lockKey);
        AvailableSlot retVal ;
        try{
            boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);
            if(isLocked){
                try{
                    retVal = (AvailableSlot)redisTemplate.opsForList().leftPop(ALL_AVAILABLE_KEY);
                    if(Objects.isNull(retVal)){
                        updateAvailableSlotCache();
                        retVal = (AvailableSlot) redisTemplate.opsForList().leftPop(ALL_AVAILABLE_KEY);
                    }
                }catch (Exception e){
                    updateAvailableSlotCache();
                    retVal = (AvailableSlot) redisTemplate.opsForList().leftPop(ALL_AVAILABLE_KEY);
                }finally {
                    lock.unlock();
                }
            } else {
                throw new Exception("Could not acquire lock for operation: " + lockKey);
            }

        }catch (Exception e){
            retVal = null;
        }
        return retVal;
    }
}
