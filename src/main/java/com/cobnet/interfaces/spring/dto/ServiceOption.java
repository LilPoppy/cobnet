package com.cobnet.interfaces.spring.dto;

import com.cobnet.interfaces.connection.EventListener;
import com.cobnet.interfaces.connection.web.TextPlain;
import com.cobnet.spring.boot.core.ProjectBeanHolder;

public interface ServiceOption<T> extends TextPlain {

    String name();

    Class<T> type();

    public static ServiceOption<?> generate(String name) {

        String[] beans = ProjectBeanHolder.getSpringContext().getBeanNamesForType(ServiceOptionGenerator.class);

        for(String bean : beans) {

            return ProjectBeanHolder.getSpringContext().getBean(bean, ServiceOptionGenerator.class).generate(name);
        }

        return null;
    }

    public ServiceOptionGenerator<? extends ServiceOption<T>> getGenerator();
}
