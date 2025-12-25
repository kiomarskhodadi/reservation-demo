package com.infrastructure.basedata;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CodeException {
    SYSTEM_EXCEPTION(1),
    UNDEFINED(2),
    LENGTH_FIELD(5),
    MANDATORY_FIELD(6),
    DATA_BASE_EXCEPTION(7),
    INVALID_LANGUAGE(8),
    INVALIDATE_DATETIME(9),
    ACCESS_DENIED(10),
    BAD_USER_PASS(401),
    EXPIRED_TOKEN(401),
    INVALID_TOKEN(401),
    INVALID_MAIL(13),
    INVALID_CELLPHONE(14),
    INVALID_USERNAME(15),
    NOT_FIND_REFERENCE(16),
    INTERNAL_ERROR(17),
    INVALID_IMAGE(18),
    MAX_DATA(19),
    INVALID_DATA(20),
    DUPLICATE_PROVIDER(21),
    BAD_SELECTED_SERVICE(22),
    CONFLICT_CALENDAR(23),
    EXTENDED_CALENDAR(24),
    MAX_OTP(25),
    BAD_CAPTCHA(26),
    INVALID_MAIL_USER(27),
    NOT_INVALID_MAIL(28),
    INVALID_OTP(29),
    INVALID_FORMAT_PASS(30),
    NOT_FIND_REFERENCE_PLACE(30),
    UPLOAD_PROBLEM(31),
    SEAT_SALE_RESERVE_DELETE(32),
    CONFLICT_APPOINTMENT(33),
    INVALID_CALENDAR(34),
    DUPLICATE_RECORD(35),
    NO_IMAGE(36),
    USED_IN_APPOINTMENT(37),
    USED_MAIL(38),
    TICKET_NOT_FREE(39),
    TICKET_NOT_VALID(40),
    INVALID_PERFORMANCE_STATUS_FOR_UPDATE(41),
    INVALID_PERFORMANCE_STATUS_TO_UPDATE(42),
    INVALID_DATE_FOR_PREVIOUS_DAY(43);


    private int codeException;
    CodeException(int code) {
        this.codeException = code;
    }

    @JsonValue
    public int getCodeException() {
        return codeException;
    }

    public void setCodeException(int codeException) {
        if(codeException < 0 || codeException > 43){
            throw new IllegalArgumentException("The given exception code is invalid.");
        }
        this.codeException = codeException;
    }


}
