package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.interfaces.connection.web.Content;
import com.cobnet.interfaces.connection.web.NamedContent;
import com.cobnet.interfaces.connection.web.ReasonableStatus;
import com.cobnet.spring.boot.dto.support.ContentHolder;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public record ResponseResult<T extends Enum<T> & ReasonableStatus>(T status, Content<?>... contents) implements ApplicationJson {

    public ResponseResult(T status, Collection<? extends Content<?>> contents) {

        this(status, contents.stream().toArray(Content[]::new));
    }

    public ResponseResult(T status, ContentHolder holder) {

        this(status, holder.contents());
    }

    public ResponseResult setContents(Content<?>... contents) {

        return new ResponseResult(this.status, contents);
    }

    public ResponseResult setContents(Collection<? extends Content<?>> contents) {

        return new ResponseResult(this.status, contents);
    }

    public ResponseResult setContents(ContentHolder holder) {

        return new ResponseResult(this.status, holder);
    }
}
