package com.cobnet.spring.boot.service.support;

import com.cobnet.common.PuzzledImage;
import com.cobnet.spring.boot.service.HumanValidatorService;

import java.io.Serializable;
import java.util.Date;

public class HumanValidationCache implements Serializable {

    public static final String HumanValidatorKey = HumanValidatorService.class.getSimpleName();

    private final PuzzledImage image;

    private Date createdTime;

    private int times;

    private boolean validated;

    public HumanValidationCache(PuzzledImage image, Date createdTime, int times, boolean validated) {

        this.image = image;
        this.createdTime = createdTime;
        this.times = times;
        this.validated = validated;
    }

    public PuzzledImage getImage() {
        return image;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public int getTimes() {
        return times;
    }

    public boolean isValidated() {
        return validated;
    }

    public HumanValidationCache setCreatedTime(Date createdTime) {

        this.createdTime = createdTime;

        return this;
    }

    public HumanValidationCache setTimes(int times) {

        this.times = times;

        return this;
    }

    public HumanValidationCache setValidated(boolean validated) {

        this.validated = validated;

        return this;
    }
}
