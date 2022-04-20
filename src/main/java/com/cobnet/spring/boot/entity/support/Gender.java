package com.cobnet.spring.boot.entity.support;

public enum Gender {

    MALE(0),
    FEMALE(1);

    private int code;

    private Gender(int code) {

        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
