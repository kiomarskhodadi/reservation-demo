package com.reserve.doa.repository;

import com.reserve.doa.entity.AvailableSlot;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface IAvailableSlotRepo extends JpaRepository<AvailableSlot,Long> {


    @Query("  SELECT ent FROM AvailableSlot ent         "+
           "    WHERE ent.reserved = false AND          "+
           "          ent.startTime >= :startDate       ")
    List<AvailableSlot> findNNearestAvailableSlot(Timestamp startDate, Pageable page);
}
