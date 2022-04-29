package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;

import java.io.Serializable;

public record ObjectWrapper<T>(T wrapped) implements ApplicationJson {
}
