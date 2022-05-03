package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.PageField;
import com.cobnet.interfaces.spring.dto.ServiceOption;
import com.cobnet.spring.boot.entity.Work;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;

public abstract class ObservationServiceOption<T> implements ServiceOption<T> {

    private String name;

    private String description;

    private T value;

    private PageField[] fields;

    protected ObservationServiceOption(String name, @Nullable String description, T value, PageField... fields) {

        this.name = name;
        this.description = description;
        this.value = value;
        this.fields = fields;
    }

    @Override
    public String name() {

        return this.name;
    }

    @Override
    public String description() {

        return this.description;
    }

    @Override
    public T value() {

        return this.value;
    }

    @Override
    public void setValue(T value) {

        this.value = value;
    }

    @Override
    public PageField[] fields() {

        return this.fields;
    }

    @Override
    public boolean isModifiable() {

        return fields != null && fields.length > 0;
    }

    @Override
    public boolean received(Work work, T value) {

        //TODO notify to store staffs

        return onReceived(work, value);
    }

    protected abstract boolean onReceived(Work work, T value);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObservationServiceOption<?> that = (ObservationServiceOption<?>) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
