package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.PageField;
import com.cobnet.spring.boot.entity.Work;

import java.util.Locale;

public class CustomServiceOption<E, T extends ObservationServiceOption<E>> extends ObservationServiceOption<E> {

    private final T parent;

    public CustomServiceOption(T parent, String name, String description, E value, PageField... fields) {

        super(name, description, value, fields);

        this.parent = parent;
    }

    @Override
    protected boolean onReceived(Work work, E value) {

        return this.parent.onReceived(work, value);
    }

    @Override
    public PageField[] getFields(Locale locale) {
        return fields();
    }
}
