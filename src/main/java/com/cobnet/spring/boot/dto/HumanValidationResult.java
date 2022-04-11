package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.HumanValidationResultStatus;

public record HumanValidationResult(HumanValidationResultStatus status) implements ApplicationJson {

    public HumanValidationResultStatus status() {
        return status;
    }
}
