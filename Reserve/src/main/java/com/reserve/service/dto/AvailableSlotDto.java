package com.reserve.service.dto;

import com.reserve.doa.entity.AvailableSlot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlotDto {

    private Long id;
    private Timestamp startTime;
    private Timestamp endTime;
    private boolean reserved;
    private Long userId;
    private Timestamp creationAt;
    private Timestamp updatedAt;

    public AvailableSlotDto(AvailableSlot ent){
        this(ent.getId(),
             ent.getStartTime(),
             ent.getEndTime(),
             ent.isReserved(),
             ent.getUserId(),
             ent.getCreationAt(),
             ent.getUpdatedAt());
    }
}
