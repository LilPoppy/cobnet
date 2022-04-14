package com.cobnet.spring.boot.entity.support;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AddressKey implements Serializable {

    private String street = "";

    @Column(nullable = true)
    private String unit = "";

    private int zipCode;

    public AddressKey(String street, String unit, int zipCode) {
        this.street = street;
        this.unit = unit;
        this.zipCode = zipCode;
    }

    public AddressKey() {}

    public String getStreet() {
        return street;
    }

    public String getUnit() {
        return unit;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AddressKey that = (AddressKey) o;
        return street != null && Objects.equals(street, that.street)
                && unit != null && Objects.equals(unit, that.unit)
                && Objects.equals(zipCode, that.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, unit, zipCode);
    }
}
