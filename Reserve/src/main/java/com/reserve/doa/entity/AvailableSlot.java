package com.reserve.doa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "AVAILABLE_SLOT",schema = "RESERVE_MNG")
@Cacheable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailableSlot {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "SEQ_AVAILABLE_SLOT", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_AVAILABLE_SLOT", allocationSize = 1, sequenceName = "SEQ_AVAILABLE_SLOT",schema = "RESERVE_MNG")
    private Long id;
    @Column(name = "START_TIME")
    private Timestamp startTime;
    @Column(name = "END_TIME")
    private Timestamp endTime;
    @Column(name = "IS_RESERVED")
    private boolean reserved;
    @Version
    private Integer version;
    @Column(name = "USER_ID")
    private Long userId;
    @Column(name = "CREATION_AT")
    private Timestamp creationAt;
    @Column(name = "UPDATED_AT")
    private Timestamp updatedAt;

    public void reserveAvailableSlot(Long UserId){
        this.setReserved(true);
        this.setUserId(UserId);
        this.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }
    public void cancelReservation(){
        this.setReserved(false);
        this.setUserId(null);
        this.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
    }
}
