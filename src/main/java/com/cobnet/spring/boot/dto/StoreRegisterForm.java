package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.FormGenerator;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.Position;
import com.cobnet.spring.boot.entity.Service;
import com.cobnet.spring.boot.entity.Store;
import com.cobnet.spring.boot.entity.User;
import com.google.maps.errors.ApiException;
import com.google.maps.model.PlaceDetails;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class StoreRegisterForm extends FormBase<StoreRegisterForm, Store> {

    private String placeId;

    private String phoneNumber;

    public StoreRegisterForm(String placeId, String phoneNumber) {

        this.placeId = placeId;
        this.phoneNumber = phoneNumber;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public FormGenerator<StoreRegisterForm> getGenerator() {

        return new StoreFormGenerator();
    }

    @Override
    public Store getEntity() {

        return new Store.Builder().setPlaceId(this.placeId).setPhone(this.phoneNumber).build();
    }


    public static class StoreFormGenerator implements FormGenerator<StoreRegisterForm> {

        @Override
        public StoreRegisterForm generate(Map<String, ?> fields) {

            return ProjectBeanHolder.getObjectMapper().convertValue(fields, StoreRegisterForm.class);
        }
    }
}
