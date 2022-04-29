package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.interfaces.connection.web.Content;
import com.cobnet.interfaces.connection.web.ReasonableStatus;

import java.util.Collection;

public record ResponseResult<T extends Enum<T> & ReasonableStatus>(T status, Content<?>... contents) implements ApplicationJson {

    public ResponseResult(T status, Collection<Content<?>> contents) {

        this(status, contents.stream().toArray(Content[]::new));
    }

}
