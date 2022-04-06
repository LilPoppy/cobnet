package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.dto.support.HttpMapTransmission;
import com.cobnet.spring.boot.entity.PersistentLogins;

import java.util.Date;

public class RememberMeInfo extends HttpMapTransmission {

    private final String series;

    private final String token;

    private final Date lastUsed;

    public RememberMeInfo(String series, String token, Date lastUsed) {
        this.series = series;
        this.token = token;
        this.lastUsed = lastUsed;
    }

    public RememberMeInfo(PersistentLogins info) {

        this(info.getSeries(), info.getToken(), info.getLastUsed());
    }

    public String getSeries() {
        return series;
    }

    public String getToken() {
        return token;
    }

    public Date getLastUsed() {
        return lastUsed;
    }
}
