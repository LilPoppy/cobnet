package com.cobnet.spring.boot.dto;

import com.cobnet.common.KeyValuePair;
import com.cobnet.interfaces.connection.web.FormGenerator;
import com.cobnet.interfaces.spring.dto.ServiceOption;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.support.WorkStatus;
import com.cobnet.spring.boot.entity.*;
import com.cobnet.spring.boot.entity.support.Gender;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WorkInfoForm extends FormBase<WorkInfoForm, Work> {

    private long orderId;

    private List<Long> staffIds;

    private String serviceId;

    private Map<ServiceOption<?>, ?> options;

    public WorkInfoForm(long orderId, List<Long> staffIds, String serviceId, Map<String, Object> options) {

        this.orderId = orderId;
        this.staffIds = staffIds;
        this.serviceId = serviceId;
        //this.options = options != null ? options.entrySet().stream().map(option -> new KeyValuePair<>(ServiceOption.generate(option.getKey()), option.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)) : new HashMap<>();;
    }

    @Override
    public FormGenerator<WorkInfoForm> getGenerator() {

        return new WorkInfoFormGenerator();
    }

    public long getOrderId() {
        return orderId;
    }

    public List<Long> getStaffIds() {
        return staffIds;
    }

    public String getServiceId() {
        return serviceId;
    }

    public Map<ServiceOption<?>, ?> getOptions() {
        return options;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public void setStaffIds(List<Long> staffIds) {
        this.staffIds = staffIds;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setOptions(Map<ServiceOption<?>, ?> options) {
        this.options = options;
    }

    @Override
    public Work getEntity() {

        return new Work();
    }

    public static class WorkInfoFormGenerator implements FormGenerator<WorkInfoForm> {

        @Override
        public WorkInfoForm generate(Map<String, ?> fields) {

            return ProjectBeanHolder.getObjectMapper().convertValue(fields, WorkInfoForm.class);
        }
    }

}
