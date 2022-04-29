package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.interfaces.connection.web.Content;
import org.springframework.http.HttpMethod;

public record MethodHint(HttpMethod method, String path, Content<?>... args) implements ApplicationJson {

}
