package com.cobnet.spring.boot.dto;

import com.cobnet.common.StringUtils;
import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.spring.boot.entity.User;
import com.cobnet.spring.boot.dto.support.Gender;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class UserInfo implements ApplicationJson {

    private String username;

    private String firstName;

    private String lastName;

    private Gender gender;

    private String phoneNumber;

    private String email;

    private Set<AddressInfo> addressInfos;

    private Set<UserRoleInfo> userRoleInfos;

    public UserInfo(String username, String firstName, String lastName, Gender gender, String phoneNumber, String email, Set<AddressInfo> addressInfos, Set<UserRoleInfo> userRoleInfos) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.addressInfos = addressInfos;
        this.userRoleInfos = userRoleInfos;
    }

    public UserInfo(User user) {

        this(user.getUsername(), user.getFirstName(), user.getLastName(), user.getGender(), user.getPhoneNumber(), user.getEmail(), user.getAddresses().stream().map(address -> new AddressInfo(address)).collect(Collectors.toSet()), user.getRoles().stream().map(role -> new UserRoleInfo(role)).collect(Collectors.toSet()));
    }

    public UserInfo(User user, boolean secured) {

        this(user);

        if(secured) {

            if(this.phoneNumber != null && this.phoneNumber.length() > 4) {

                this.phoneNumber = StringUtils.secure(this.phoneNumber, 0, this.phoneNumber.length() - 4, '*');
            }

            if(this.email != null) {

                String[] nodes = this.email.split("@");

                if(nodes.length > 1 && nodes[0].length() > 4) {

                    this.email = StringUtils.secure(this.email, nodes[0].length() - 4, nodes[0].length(), '*');
                }
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<AddressInfo> getAddressInfos() {
        return addressInfos;
    }

    public void setAddressInfos(Set<AddressInfo> addressInfos) {
        this.addressInfos = addressInfos;
    }

    public Set<UserRoleInfo> getUserRoleInfos() {
        return userRoleInfos;
    }

    public void setUserRoleInfos(Set<UserRoleInfo> userRoleInfos) {
        this.userRoleInfos = userRoleInfos;
    }

    public static final class Builder {

        private String username;

        private String firstName;

        private String lastName;

        private Gender gender;

        private String phoneNumber;

        private String email;

        private Set<AddressInfo> addressInfos;

        private Set<UserRoleInfo> userRoleInfos;

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setGender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setAddressInfos(AddressInfo... addressInfos) {
            this.addressInfos = Arrays.stream(addressInfos).collect(Collectors.toSet());
            return this;
        }

        public Builder setUserRoleInfos(UserRoleInfo... userRoleInfos) {
            this.userRoleInfos = Arrays.stream(userRoleInfos).collect(Collectors.toSet());;
            return this;
        }

        public UserInfo build() {

            return new UserInfo(this.username, this.firstName, this.lastName, this.gender, this.phoneNumber, this.email, this.addressInfos, this.userRoleInfos);
        }
    }
}
