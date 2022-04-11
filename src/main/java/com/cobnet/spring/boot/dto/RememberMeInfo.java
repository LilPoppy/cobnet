package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.entity.PersistentLogins;

import java.util.Date;

public record RememberMeInfo(String series, String token, Date lastUsed) implements ApplicationJson {

    public RememberMeInfo(PersistentLogins info) {

        this(info.getSeries(), info.getToken(), info.getLastUsed());
    }

    @Override
    public String series() {
        return series;
    }

    @Override
    public String token() {
        return token;
    }

    @Override
    public Date lastUsed() {
        return lastUsed;
    }
}
