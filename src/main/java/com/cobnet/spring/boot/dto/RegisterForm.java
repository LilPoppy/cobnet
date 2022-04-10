package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.FormGenerator;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public class RegisterForm extends FormBase<RegisterForm, User> {

    private String username;

    private String password;

    private String email;

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private int movement;

    public RegisterForm(String username, String password, String email, String phoneNumber, String firstName, String lastName, int movement) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.movement = movement;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getMovement() {
        return movement;
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

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMovement(int movement) {
        this.movement = movement;
    }

    @Override
    public FormGenerator<RegisterForm> getGenerator() {

        return new RegisterFormGenerator();
    }

    @Override
    public User getEntity() {

        return new User(this.username, this.password, this.firstName, this.lastName, this.phoneNumber, this.email, ProjectBeanHolder.getUserRoleRepository().getDefaultRole().get());
    }


    public static class RegisterFormGenerator implements FormGenerator<RegisterForm> {

        @Override
        public RegisterForm generate(Map<String, ?> options) {

            return ProjectBeanHolder.getObjectMapper().convertValue(options, RegisterForm.class);
        }
    }
}
