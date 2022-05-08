package com.cobnet.spring.boot.service.support;

import com.cobnet.common.PuzzledImage;
import com.cobnet.interfaces.CacheValue;
import com.cobnet.spring.boot.service.HumanValidatorService;

import java.io.Serializable;
import java.util.Date;

public class HumanValidationCache implements CacheValue, Serializable {

    public static final String HumanValidatorKey = HumanValidatorService.class.getSimpleName();

    private final PuzzledImage image;

    private Date creationTime;

    private int count;

    private boolean validated;

    public HumanValidationCache(PuzzledImage image, Date creationTime, int count, boolean validated) {

        this.image = image;
        this.creationTime = creationTime;
        this.count = count;
        this.validated = validated;
    }

    public PuzzledImage getImage() {
        return image;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public Date creationTime() {
        return creationTime;
    }

    public boolean isValidated() {
        return validated;
    }

    public HumanValidationCache setCreationTime(Date creationTime) {

        this.creationTime = creationTime;

        return this;
    }

    public HumanValidationCache setCount(int count) {

        this.count = count;

        return this;
    }

    public HumanValidationCache setValidated(boolean validated) {

        this.validated = validated;

        return this;
    }


}
