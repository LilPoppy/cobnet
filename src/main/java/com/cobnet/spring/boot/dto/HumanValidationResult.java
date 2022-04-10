package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.dto.support.HttpMapTransmission;
import com.cobnet.spring.boot.dto.support.HumanValidationFailureReason;

public class HumanValidationResult extends HttpMapTransmission {

    private boolean result;

    private HumanValidationFailureReason reason;

    public HumanValidationResult(boolean result, HumanValidationFailureReason reason) {
        this.result = result;
        this.reason = reason;
    }

    public boolean isSuccess() {
        return result;
    }

    public HumanValidationFailureReason getReason() {
        return reason;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public void setReason(HumanValidationFailureReason reason) {
        this.reason = reason;
    }
}
