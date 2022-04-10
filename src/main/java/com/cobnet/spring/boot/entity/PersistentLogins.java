package com.cobnet.spring.boot.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Entity
public class PersistentLogins implements Serializable {

    @Id
    private String username;

    private boolean enable;

    private String series;

    private String token;

    private Date lastUsed;

    public PersistentLogins() {}

    public PersistentLogins(boolean enable, String username, String series, String token, Date lastUsed) {
        this.enable = enable;
        this.username = username;
        this.series = series;
        this.token = token;
        this.lastUsed = lastUsed;
    }

    public String getUsername() {
        return username;
    }

    public boolean isEnable() {
        return enable;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }
}
