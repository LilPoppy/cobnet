package com.cobnet.spring.boot.entity;

import com.cobnet.spring.boot.entity.support.Gender;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class CustomerInfo extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    @Enumerated
    private Gender gender;

    private String referral;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "info", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<StoreOrder> orders = new HashSet<>();

    public CustomerInfo(String firstName, String lastName, String phoneNumber, Gender gender, String referral) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
        this.referral = referral;
    }

    public CustomerInfo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Gender getGender() {
        return gender;
    }

    public String getReferral() {
        return referral;
    }

    public Set<StoreOrder> getOrders() {
        return orders;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }
}
