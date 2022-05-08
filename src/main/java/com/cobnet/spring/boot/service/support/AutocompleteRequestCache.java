package com.cobnet.spring.boot.service.support;

import com.cobnet.interfaces.CacheValue;
import com.cobnet.spring.boot.service.GoogleMapService;

import java.util.Date;

public record AutocompleteRequestCache(Date creationTime, int count) implements CacheValue {

    public static final String GoogleMapServiceKey = GoogleMapService.class.getSimpleName();

    @Override
    public Date creationTime() {
        return creationTime;
    }

    @Override
    public int count() {
        return count;
    }

}
