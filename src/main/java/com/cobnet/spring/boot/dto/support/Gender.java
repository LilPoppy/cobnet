package com.cobnet.spring.boot.dto.support;

import com.cobnet.exception.ServiceDownException;
import com.cobnet.spring.boot.core.ProjectBeanHolder;

import java.io.IOException;
import java.util.Locale;

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

    public String getMessage(Locale locale) throws IOException, ServiceDownException {

        return switch (this) {

            case MALE -> ProjectBeanHolder.getTranslatorMessageSource().getMessage("gender.male", "male", locale);
            case FEMALE -> ProjectBeanHolder.getTranslatorMessageSource().getMessage("gender.female", "female", locale);
        };
    }
}
