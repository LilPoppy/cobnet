package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.FormGenerator;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.CustomerInfo;
import com.cobnet.spring.boot.entity.support.Gender;

import java.util.Map;

public class CustomerInfoForm extends FormBase<CustomerInfoForm, CustomerInfo> {

    private String firstName;

    private String lastName;

    private Gender gender;

    private String phoneNumber;

    private String referral;

    public CustomerInfoForm(String firstName, String lastName, Gender gender, String phoneNumber, String referral) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.referral = referral;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getReferral() {
        return referral;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    @Override
    public FormGenerator<CustomerInfoForm> getGenerator() {

        return new CheckInInformationFormGenerator();
    }

    @Override
    public CustomerInfo getEntity() {

        return new CustomerInfo(this.firstName, this.lastName, this.phoneNumber, this.gender, this.referral);
    }

    public static class CheckInInformationFormGenerator implements FormGenerator<CustomerInfoForm> {

        @Override
        public CustomerInfoForm generate(Map<String, ?> fields) {

            return ProjectBeanHolder.getObjectMapper().convertValue(fields, CustomerInfoForm.class);
        }
    }

}
