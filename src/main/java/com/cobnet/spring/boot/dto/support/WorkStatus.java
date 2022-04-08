package com.cobnet.spring.boot.dto.support;

public enum WorkStatus {

    WAITING(0),
    WORKING(1),
    INTERRUPTED(2),
    PAYMENT(3),
    DONE(4);

    private int code;

    private WorkStatus(int code) {

        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
