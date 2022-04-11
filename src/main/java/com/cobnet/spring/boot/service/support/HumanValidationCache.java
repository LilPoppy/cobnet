package com.cobnet.spring.boot.service.support;

import com.cobnet.common.PuzzledImage;
import com.cobnet.spring.boot.service.HumanValidator;

import java.io.Serializable;
import java.util.Date;

public class HumanValidationCache implements Serializable {

    public static final String HumanValidatorKey = HumanValidator.class.getSimpleName();

    private final PuzzledImage image;

    private final Date createdTime;

    private boolean validated;

    public HumanValidationCache(PuzzledImage image, Date createdTime, boolean validated, boolean used) {

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

    public void setValidated(boolean validated) {
        this.validated = validated;
    }
}
