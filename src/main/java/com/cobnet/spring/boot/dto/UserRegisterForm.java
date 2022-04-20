package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.interfaces.connection.web.Form;
import com.cobnet.interfaces.connection.web.FormGenerator;
import com.cobnet.interfaces.connection.web.annotation.FormParam;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.User;
import com.cobnet.spring.boot.entity.support.Gender;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

public class UserRegisterForm extends FormBase<UserRegisterForm, User> {

    private String username;

    private String password;

    private Gender gender;

    private String email;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    public UserRegisterForm(String username, String password, Gender gender, String email, String phoneNumber, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
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

    @Override
    public FormGenerator<UserRegisterForm> getGenerator() {

        return new RegisterFormGenerator();
    }

    @Override
    public User getEntity() {

        return new User.Builder().setUsername(this.username).setPassword(this.password).setFirstName(this.firstName).setLastName(this.lastName).setGender(this.gender).setPhoneNumber(this.phoneNumber).setEmail(this.email).setRoles(ProjectBeanHolder.getUserRoleRepository().getDefaultRole().get()).build();
    }

    public static class RegisterFormGenerator implements FormGenerator<UserRegisterForm> {

        @Override
        public UserRegisterForm generate(Map<String, ?> fields) {

            return ProjectBeanHolder.getObjectMapper().convertValue(fields, UserRegisterForm.class);
        }
    }
}
