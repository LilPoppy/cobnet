package com.cobnet.spring.boot.entity;

import com.cobnet.spring.boot.dto.AddressForm;
import com.cobnet.spring.boot.entity.support.AddressKey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
public class Address extends EntityBase implements Serializable {

    @EmbeddedId
    private AddressKey id;

    private String city;

    private String state;

    private String country;

    public Address(String street, String unit, String city, String state, String country, String postalCode) {
        this.id = new AddressKey(street, unit, postalCode);
        this.city = city;
        this.state = state;
        this.country = country;
    }

    public Address(String street, String unit, String city, String state, String country) {

        this(street, unit, city, state, country, null);
    }

    public Address(String street, String unit, String city, String state) {

        this(street, unit, city, state, null);
    }

    public Address(String street, String unit, String city) {

        this(street, unit, city, null);
    }

    public Address(String street, String unit) {

        this(street, unit, null);
    }

    public Address(String street) {
        this(street, null);
    }

    public Address() {
        this(null);
    }

    @JsonIgnore
    public AddressKey getId() {
        return id;
    }

    public String getStreet() {
        return id.getStreet();
    }

    public String getUnit() {
        return id.getUnit();
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
        return id.getPostalCode();
    }

    @JsonIgnore
    public void setId(AddressKey id) {
        this.id = id;
    }

    public void setStreet(String street) {
        this.id.setStreet(street);
    }

    public void setUnit(String unit) {
        this.id.setUnit(unit);
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

    public void setPostalCode(String code) {
        this.id.setPostalCode(code);
    }

    public String address() {

        return new AddressForm.Builder().setStreet(this.getStreet()).setUnit(this.getUnit()).setCity(this.getCity()).setState(this.getState()).setCountry(this.getCountry()).setPostalCode(this.getPostalCode()).build().address();
    }

    @Override
    public String toString() {
        return this.address();
    }


    public static class Builder {

        private String street;

        private String unit = "";

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

        public Builder setPostalCode(String code) {

            this.postalCode = code;

            return this;
        }

        public Address build() {

            return new Address(this.street, this.unit, this.city, this.state, this.country, this.postalCode);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Address address = (Address) o;
        return id != null && Objects.equals(id, address.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
