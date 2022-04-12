package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.FormGenerator;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.Position;
import com.cobnet.spring.boot.entity.Service;
import com.cobnet.spring.boot.entity.Store;
import com.cobnet.spring.boot.entity.User;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class StoreRegisterForm extends FormBase<StoreRegisterForm, Store> {

    private String location;

    private String name;

    private String phone;

    private Set<ServiceRegisterForm> services;

    private Set<PositionRegisterForm> positions;

    public StoreRegisterForm(String location, String name, String phone, Set<ServiceRegisterForm> services, Set<PositionRegisterForm> positions) {

        this.location = location;
        this.name = name;
        this.phone = phone;
        this.services = services;
        this.positions = positions;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public Set<ServiceRegisterForm> getServices() {
        return services;
    }

    public Set<PositionRegisterForm> getPositions() {
        return positions;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setServices(Set<ServiceRegisterForm> services) {
        this.services = services;
    }

    public void setPositions(Set<PositionRegisterForm> positions) {
        this.positions = positions;
    }

    @Override
    public FormGenerator<StoreRegisterForm> getGenerator() {

        return new StoreFormGenerator();
    }

    @Override
    public Store getEntity() {

        return new Store(this.location, this.name, this.phone, this.services.stream().map(ServiceRegisterForm::getEntity).collect(Collectors.toSet()), this.positions.stream().map(PositionRegisterForm::getEntity).collect(Collectors.toSet()));
    }


    public static class StoreFormGenerator implements FormGenerator<StoreRegisterForm> {

        @Override
        public StoreRegisterForm generate(Map<String, ?> fields) {

            return ProjectBeanHolder.getObjectMapper().convertValue(fields, StoreRegisterForm.class);
        }
    }
}
