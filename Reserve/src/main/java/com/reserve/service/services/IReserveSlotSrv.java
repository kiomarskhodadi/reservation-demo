package com.reserve.service.services;

import com.infrastructure.form.OutputAPIForm;

public interface IReserveSlotSrv {
    OutputAPIForm reserveSlot(Long userId);
    OutputAPIForm cancelReserveSlot(Long reserveId);
}
