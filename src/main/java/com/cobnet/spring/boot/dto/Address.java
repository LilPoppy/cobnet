package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;

public class Address implements ApplicationJson {

    private String street;
    private String unit;
    private String city;
    private String state;
    private String country;

    private String postalCode;

    public Address(String street, String unit, String city, String state, String country, String postalCode) {
        this.street = street;
        this.unit = unit;
        this.city = city;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
    }

    public Address() {}

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

    public String getPostalCode() {
        return postalCode;
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

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String address() {

        StringBuilder sb = new StringBuilder();

        if(this.getStreet() != null && this.getStreet().length() > 0) {

            sb.append(this.getStreet());
        }

        if(this.getUnit() != null && this.getUnit().length() > 0) {

            sb.append(" ").append(this.getUnit());
        }

        if(this.getCity() != null && this.getCity().length() > 0) {

            sb.append(" ").append(this.getCity());
        }

        if(this.getState() != null && this.getState().length() > 0) {

            sb.append(", ").append(this.getState());
        }

        if(this.getPostalCode() != null && this.getPostalCode().length() > 0) {

            sb.append(" ").append(this.getPostalCode());
        }

        if(this.getCountry() != null && this.getCountry().length() > 0) {

            sb.append(" ").append(this.getCountry());
        }

        return sb.toString();
    }

    @Override
    public String toString() {

        return address();
    }

    public com.cobnet.spring.boot.entity.Address getEntity() {

        return new com.cobnet.spring.boot.entity.Address.Builder().setStreet(this.street).setUnit(this.unit).setCity(this.city).setState(this.state).setPostalCode(this.postalCode).setCountry(this.country).build();
    }

    public static final class Builder {

        private String street;
        private String unit;
        private String city;
        private String state;
        private String country;
        private String postalCode;

        public Builder setStreet(String street) {

            this.street = street;
            return this;
        }

        public Builder setUnit(String unit) {

            this.unit = unit;
            return this;
        }

        public Builder setCity(String city) {

            this.city = city;
            return this;
        }

        public Builder setState(String state) {

            this.state = state;
            return this;
        }

        public Builder setCountry(String country) {

            this.country = country;
            return this;
        }

        public Builder setPostalCode(String postalCode) {

            this.postalCode = postalCode;
            return this;
        }

        public Address build() {

            return new Address(this.street, this.unit, this.city, this.state, this.country, this.postalCode);
        }
    }
}
