package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.dto.support.HumanValidationRequestStatus;

import java.util.List;
import java.util.stream.Stream;

public record HumanValidationRequest(HumanValidationRequestStatus status, List<Base64Image> images) implements ApplicationJson {

    public HumanValidationRequest(HumanValidationRequestStatus status, Base64Image... images) {

        this(status, Stream.of(images).toList());
    }

    public HumanValidationRequestStatus status() {
        return status;
    }

    @Override
    public List<Base64Image> images() {
        return images;
    }
}
