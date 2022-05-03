package com.cobnet.interfaces.spring.dto;

import com.cobnet.exception.ServiceDownException;
import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.interfaces.connection.web.PageField;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.Work;

import java.io.IOException;
import java.util.Locale;

public interface ServiceOption<T> extends ApplicationJson {

    public String name();

    public String description();

    public default String getDisplayName(Locale locale) throws IOException, ServiceDownException {

        return ProjectBeanHolder.getTranslatorMessageSource().getMessage(name(), locale, value());
    }

    public default String getDisplayDescription(Locale locale) throws IOException, ServiceDownException {

        return ProjectBeanHolder.getTranslatorMessageSource().getMessage(description(), locale, value());
    }

    public T value();

    public void setValue(T value);

    public PageField[] fields();

    public PageField[] getFields(Locale locale);

    public boolean isModifiable();

    public boolean received(Work work, T value);

}
