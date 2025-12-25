package com.reserve.basedata;

public enum ReservationStatus {
    reserve(0),Cancel(1);

    private int reservationStatusCode;


    ReservationStatus(int reservationStatusCode) {
        this.reservationStatusCode = reservationStatusCode;
    }
}
