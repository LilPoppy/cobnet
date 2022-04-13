package com.cobnet.spring.boot.dto;

import com.cobnet.common.KeyValuePair;
import com.cobnet.interfaces.connection.web.FormGenerator;
import com.cobnet.interfaces.spring.dto.ServiceOption;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceRegisterForm extends FormBase<ServiceRegisterForm, Service> {

    private String name;

    private long price;

    private Map<ServiceOption<?>, ?> options;

    public ServiceRegisterForm(String name, long price, Map<String, Object> options) {

        this.name = name;
        this.price = price;
        this.options = options != null ? options.entrySet().stream().map(option -> new KeyValuePair<>(ServiceOption.generate(option.getKey()), option.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)) : new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public Map<ServiceOption<?>, ?> getOptions() {
        return options;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setOptions(Map<ServiceOption<?>, ?> options) {
        this.options = options;
    }

    @Override
    public FormGenerator<ServiceRegisterForm> getGenerator() {

        return new ServiceFormGenerator();
    }

    @Override
    public Service getEntity() {

        return new Service();
    }

    public static class ServiceFormGenerator implements FormGenerator<ServiceRegisterForm> {

        @Override
        public ServiceRegisterForm generate(Map<String, ?> fields) {

            return ProjectBeanHolder.getObjectMapper().convertValue(fields, ServiceRegisterForm.class);
        }
    }
}
