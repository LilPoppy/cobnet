package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.FormGenerator;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.Address;

import java.util.Map;

public class AddressForm extends FormBase<AddressForm, Address> {

    private String street;
    private String unit;
    private String city;
    private String state;
    private String country;
    private Integer zipCode;

    public AddressForm(String street, String unit, String city, String state, String country, Integer zipCode) {
        this.street = street;
        this.unit = unit;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipCode = zipCode;
    }

    public AddressForm(String street, String unit, String city, String state, String country) {

        this(street, unit, city, state, country, null);
    }

    public AddressForm(String street, String unit, String city, String state) {

        this(street, unit, city, state, null);
    }

    public AddressForm(String street, String unit, String city) {

        this(street, unit, city, null);
    }

    public AddressForm(String street, String unit) {

        this(street, unit, null);
    }

    public AddressForm(String street) {

        this(street, null);
    }

    public AddressForm() {
        this(null);
    }

    public String getStreet() {
        return street;
    }

    public String getUnit() {
        return unit;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    public Integer getZipCode() {
        return zipCode;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setZipCode(Integer zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public FormGenerator<AddressForm> getGenerator() {

        return new AddressFormGenerator();
    }

    @Override
    public Address getEntity() {

        return new Address.Builder().setStreet(this.street).setUnit(this.unit).setCity(this.city).setState(this.state).setCountry(this.country).setZipCode(this.zipCode).build();
    }

    public static class AddressFormGenerator implements FormGenerator<AddressForm> {

        @Override
        public AddressForm generate(Map<String, ?> options) {

            return ProjectBeanHolder.getObjectMapper().convertValue(options, AddressForm.class);
        }
    }
}
