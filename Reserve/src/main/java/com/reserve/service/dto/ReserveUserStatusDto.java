package com.reserve.service.dto;

import com.reserve.basedata.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Timestamp;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class ReserveUserStatusDto {
    private Long userId;
    private Long availableSlotId;
    private Timestamp creationAt;
    private ReservationStatus reservationStatus;
}
