package com.cobnet.spring.boot.dto.support;

public enum StaffStatus {

    AVAILABLE(0),
    BUSY(1),
    BREAK(2),
    DAY_OFF(3);

    private int code;

    private StaffStatus(int code) {

        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
