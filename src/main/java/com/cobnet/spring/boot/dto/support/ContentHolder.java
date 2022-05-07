package com.cobnet.spring.boot.dto.support;

import com.cobnet.interfaces.connection.web.Content;
import com.cobnet.interfaces.connection.web.NamedContent;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.Collection;

public record ContentHolder(Content<?>... contents) {

    public ContentHolder(Collection<? extends Content<?>> contents) {

        this(contents.stream().toArray(Content[]::new));
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
