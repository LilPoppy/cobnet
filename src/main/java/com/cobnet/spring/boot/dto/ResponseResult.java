package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.interfaces.connection.web.Content;
import com.cobnet.interfaces.connection.web.NamedContent;
import com.cobnet.interfaces.connection.web.ReasonableStatus;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public record ResponseResult<T extends Enum<T> & ReasonableStatus>(T status, Content<?>... contents) implements ApplicationJson {

    public ResponseResult(T status, Collection<Content<?>> contents) {

        this(status, contents.stream().toArray(Content[]::new));
    }

    public <E extends Content<?>> @Nullable E get(Class<E> type, String name, int index) {

        try {
            return (E) Arrays.stream(contents).filter(content -> {

                if (type.isAssignableFrom(content.getClass())) {

                    if (NamedContent.class.isAssignableFrom(type) && name != null) {

                        return ((NamedContent) content).getName().equalsIgnoreCase(name);
                    }

                    return true;
                }

                return false;

            }).toArray()[index];

        } catch (ArrayIndexOutOfBoundsException ex) {

            return null;
        }
    }

    public <E extends Content<?>> @Nullable E get(Class<E> type) {

        return get(type, null, 0);
    }

    public <E extends Content<?>> @Nullable E get(Class<E> type, int index) {

        return get(type, null, index);
    }

    public <E extends Content<?>> @Nullable E get(Class<E> type, String name) {

        return get(type, name, 0);
    }

}
