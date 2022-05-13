package com.cobnet.spring.boot.dto;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import java.io.Serializable;
import java.util.Date;

@NoArgsConstructor
public class MethodInfo implements Serializable {

    private HttpMethod method;

    private String path;

    public MethodInfo(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public static MethodInfo of(HttpMethod method, String path) {

        return new MethodInfo(method, path);
    }
}
