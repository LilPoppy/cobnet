package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.dto.support.ApplicationJsonTransmission;
import com.cobnet.spring.boot.dto.support.HumanValidationRequireResult;

import java.util.List;
import java.util.stream.Stream;

public class HumanValidationRequire extends ApplicationJsonTransmission {

    private final HumanValidationRequireResult result;

    private final List<Base64Image> images;

    public HumanValidationRequire(HumanValidationRequireResult result, Base64Image... images) {

        this.result = result;
        this.images = Stream.of(images).toList();
    }

    public HumanValidationRequireResult getResult() {
        return result;
    }

    public List<Base64Image> getImages() {
        return images;
    }
}
