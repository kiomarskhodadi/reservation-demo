package com.reserve.doa.entity;

import com.reserve.basedata.ReservationStatus;
import com.reserve.service.dto.ReserveUserStatusDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Entity
@Table(name = "RESERVE_USER_STATUS",schema = "RESERVE_MNG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReserveUserStatus {

    @Id
    @Column(name = "id")
    @GeneratedValue( strategy = GenerationType.UUID)
    private String id;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "AVAILABLE_SLOT_ID")
    private Long availableSlotId;
    @Column(name = "CREATION_AT")
    private Timestamp creationAt;
    @Column(name = "RESERVATION_STATUS")
    private ReservationStatus reservationStatus;

    public ReserveUserStatus(ReserveUserStatusDto  dto){
        this(null,dto.getUserId(),dto.getAvailableSlotId(),dto.getCreationAt(),dto.getReservationStatus());
    }

}
