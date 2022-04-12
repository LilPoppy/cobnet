package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.dto.support.PhoneNumberSmsVerifyResultStatus;

public record PhoneNumberSmsVerifyResult(PhoneNumberSmsVerifyResultStatus status) {

    @Override
    public PhoneNumberSmsVerifyResultStatus status() {
        return status;
    }
}
