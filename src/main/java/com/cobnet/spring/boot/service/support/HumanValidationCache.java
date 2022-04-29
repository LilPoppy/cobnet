package com.cobnet.spring.boot.service.support;

import com.cobnet.common.PuzzledImage;
import com.cobnet.spring.boot.service.HumanValidatorService;

import java.io.Serializable;
import java.util.Date;

public class HumanValidationCache implements Serializable {

    public static final String HumanValidatorKey = HumanValidatorService.class.getSimpleName();

    private final PuzzledImage image;

    private Date createdTime;

    private boolean validated;

    public HumanValidationCache(PuzzledImage image, Date createdTime, boolean validated) {

        this.image = image;
        this.createdTime = createdTime;
        this.validated = validated;
    }

    public PuzzledImage getImage() {
        return image;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public boolean isValidated() {
        return validated;
    }

    public HumanValidationCache setCreatedTime(Date createdTime) {

        this.createdTime = createdTime;

        return this;
    }

    public HumanValidationCache setValidated(boolean validated) {

        this.validated = validated;

        return this;
    }
}
