package com.reserve.service.event;

import com.reserve.doa.entity.ReserveUserStatus;
import com.reserve.doa.repository.IReserveUserStatusRepo;
import com.reserve.service.dto.ReserveUserStatusDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
@Slf4j
public class ReserveUserStatueListener {

    private final IReserveUserStatusRepo reserveUserStatusRepo;

    public ReserveUserStatueListener(IReserveUserStatusRepo reserveUserStatusRepo) {
        this.reserveUserStatusRepo = reserveUserStatusRepo;
    }

    @Async
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMethodExecutedEvent(ReserveUserStatusDto event) {
        try{
            ReserveUserStatus ent = new ReserveUserStatus(event);
            reserveUserStatusRepo.saveAndFlush(ent);
        }catch (Exception e){
            log.error("Error in Save Log Status:" ,e);
        }
    }
}
