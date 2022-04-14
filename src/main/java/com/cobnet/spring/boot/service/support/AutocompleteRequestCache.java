package com.cobnet.spring.boot.service.support;

import com.cobnet.spring.boot.service.GoogleMapService;

import java.io.Serializable;
import java.util.Date;

public record AutocompleteRequestCache(Date createdTime, int times) implements Serializable {

    public static final String GoogleMapServiceKey = GoogleMapService.class.getSimpleName();

    @Override
    public Date createdTime() {
        return createdTime;
    }

    @Override
    public int times() {
        return times;
    }
}
