package com.storechain.spring.boot.dto;

public class AuthenticateResult {

    private boolean result;

    private String token;

    public AuthenticateResult(boolean result, String token) {
        this.result = result;
        this.token = token;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return result;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
