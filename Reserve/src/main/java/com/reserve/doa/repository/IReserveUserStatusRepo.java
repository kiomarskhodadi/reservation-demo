package com.reserve.doa.repository;

import com.reserve.doa.entity.ReserveUserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IReserveUserStatusRepo extends JpaRepository<ReserveUserStatus,String> {
}
