package com.cobnet.spring.boot.dto;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.exception.support.UserRegisterResultStatus;
import com.cobnet.spring.boot.entity.User;
import com.cobnet.spring.boot.entity.support.Gender;

public class UserRegisterForm extends FormBase<UserRegisterForm, ResponseResult<UserRegisterResultStatus>> {

    private String username;

    private String password;

    private Gender gender;

    private String email;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private AddressInfo addressInfo;

    public UserRegisterForm() {}

    public UserRegisterForm(String username, String password, Gender gender, String email, String phoneNumber, String firstName, String lastName, AddressInfo addressInfo) {
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.addressInfo = addressInfo;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Gender getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public AddressInfo getAddress() {
        return addressInfo;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAddress(AddressInfo addressInfo) {
        this.addressInfo = addressInfo;
    }


    @Override
    public ResponseResult<UserRegisterResultStatus> getResult(Object... args) {

        User user = ProjectBeanHolder.getAccountService().register(new User.Builder().setUsername(this.username).setPassword(this.password).setFirstName(this.firstName).setLastName(this.lastName).setGender(this.gender).setPhoneNumber(this.phoneNumber).setEmail(this.email).setRoles(ProjectBeanHolder.getUserRoleRepository().getDefaultRole().get()).build(), this.addressInfo.getEntity());

        if(user != null) {

            return new ResponseResult<>(UserRegisterResultStatus.SUCCESS);
        }

        return null;
    }
}
