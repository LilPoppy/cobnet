package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.HumanValidationValidateStatus;

public record HumanValidationValidate(HumanValidationValidateStatus status) implements ApplicationJson {

    public HumanValidationValidateStatus status() {
        return status;
    }
}
