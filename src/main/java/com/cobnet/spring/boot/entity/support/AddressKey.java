package com.cobnet.spring.boot.entity.support;

import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AddressKey implements Serializable {

    private String street = "";

    @Column(nullable = true, length = 16)
    private String unit = "";

    @Column(nullable = false, length = 32)
    private String postalCode = "";

    public AddressKey(String street, String unit, String postalCode) {
        this.street = street;
        this.unit = unit;
        this.postalCode = postalCode;
    }

    public AddressKey() {}

    public String getStreet() {
        return street;
    }

    public String getUnit() {
        return unit;
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

    public void setPostalCode(String code) {
        this.postalCode = code;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AddressKey that = (AddressKey) o;
        return street != null && Objects.equals(street, that.street)
                && unit != null && Objects.equals(unit, that.unit)
                && postalCode != null && Objects.equals(postalCode, that.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, unit, postalCode);
    }
}
