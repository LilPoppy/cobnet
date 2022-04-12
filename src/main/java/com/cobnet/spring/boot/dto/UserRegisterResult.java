package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.dto.support.UserRegisterFormField;
import com.cobnet.spring.boot.dto.support.UserRegisterResultStatus;

import java.util.List;

public record UserRegisterResult(UserRegisterResultStatus status, List<UserRegisterFormField> content) {

    public UserRegisterResult(UserRegisterResultStatus status) {

        this(status, null);
    }

    @Override
    public UserRegisterResultStatus status() {
        return status;
    }

    @Override
    public List<UserRegisterFormField> content() {
        return content;
    }
}
