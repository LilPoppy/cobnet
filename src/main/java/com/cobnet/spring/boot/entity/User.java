package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.security.Account;
import com.cobnet.interfaces.security.Permissible;
import com.cobnet.interfaces.security.Permission;
import com.cobnet.security.RoleRule;
import com.cobnet.security.permission.PermissionValidator;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.dto.UserInfo;
import com.cobnet.spring.boot.entity.support.Gender;
import com.cobnet.spring.boot.entity.support.JsonPermissionSetConverter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@NoArgsConstructor
public class User extends EntityBase implements Permissible, Account, UserDetails, Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(User.class);

    @Id
    private String username;

    @Column(nullable = false)
    private String password;

    private boolean passwordEncoded;

    private String firstName;

    private String lastName;

    @Enumerated
    private Gender gender;

    private String phoneNumber;

    private boolean phoneNumberVerified;

    private String email;

    private boolean emailVerified;

    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "user_address", joinColumns = { @JoinColumn(name = "user", referencedColumnName = "username") },
            inverseJoinColumns = {
                    @JoinColumn(name = "STREET", referencedColumnName = "STREET"),
                    @JoinColumn(name = "UNIT", referencedColumnName = "UNIT"),
                    @JoinColumn(name = "POSTAL_CODE", referencedColumnName = "POSTALCODE")})

    private Set<Address> addresses = new HashSet<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "user_roles", joinColumns = { @JoinColumn(name = "user", referencedColumnName = "username") },
            inverseJoinColumns = { @JoinColumn(name = "role", referencedColumnName = "role") })
    private Set<UserRole> roles = new HashSet<>();

    @Convert(converter = JsonPermissionSetConverter.class)
    @Column(columnDefinition = "json")
    private Set<Permission> permissions = new HashSet<>();

    private boolean expired;

    private boolean locked;

    private Date lockTime;

    private boolean validPassword;

    private boolean enabled;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL)
    private Set<ExternalUser> externalUsers = new HashSet<>();

    @Transient
    private transient PermissionValidator permissionValidator;

    public User(@NonNull String username, @NonNull String password, @NonNull String firstName, @NonNull String lastName, @NonNull Gender gender, String phoneNumber, boolean phoneNumberVerified, String email, boolean emailVerified, @NonNull List<Address> addresses,  @NonNull List<UserRole> roles, boolean expired, boolean locked, boolean validPassword, boolean enabled) {

        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.phoneNumberVerified = phoneNumberVerified;
        this.email = email;
        this.emailVerified = emailVerified;

        if(addresses.size() > 0) {
            this.addresses.addAll(addresses);
        }
        this.roles.addAll(roles.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EntityBase::getCreatedTime))), ArrayList::new)));
        this.expired = expired;
        this.locked = locked;
        this.validPassword = validPassword;
        this.enabled = enabled;

    }

    public User(UserInfo info, String password, boolean phoneNumberVerified, boolean emailVerified, boolean expired, boolean locked, boolean validPassword, boolean enabled) {

        this(info.getUsername(), password, info.getFirstName(), info.getLastName(), info.getGender(), info.getPhoneNumber(), phoneNumberVerified, info.getEmail(), emailVerified, info.getAddressInfos().stream().map(address -> new Address(address)).toList(), info.getUserRoleInfos().stream().map(role -> new UserRole(role)).toList(), expired, locked, validPassword, enabled);
    }



    @Override
    public String getUsername() {
        return this.username;
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

    public boolean isPhoneNumberVerified() {
        return phoneNumberVerified;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEmailVerified() {
        return emailVerified;
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

    public void setPhoneNumberVerified(boolean phoneNumberVerified) {
        this.phoneNumberVerified = phoneNumberVerified;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.expired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.validPassword;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public Date getLockTime() {
        return lockTime;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.getPermissions();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {

        this.password = password;
        this.passwordEncoded = false;
    }

    @Override
    public String getIdentity() {
        return this.getUsername();
    }

    public void setPasswordEncoded(boolean passwordEncoded) {
        this.passwordEncoded = passwordEncoded;
    }

    public boolean isPasswordEncoded() {
        return passwordEncoded;
    }

    @Override
    public RoleRule getRule() {

        Optional<UserRole> role = this.roles.stream().max(Comparator.comparingInt(UserRole::getPower));

        return role.isEmpty() ? RoleRule.VISITOR : role.get().getRule();
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public Set<ExternalUser> getExternalUsers() {

        return this.externalUsers;
    }

    public Set<Address> getAddresses() {

        return this.addresses;
    }

    @Override
    public Collection<? extends Permission> getPermissions() {

        return Stream.of(roles.stream().map(UserRole::getPermissions).flatMap(Collection::stream).collect(Collectors.toList()), permissions).flatMap(Collection::stream).collect(Collectors.toUnmodifiableList());
    }

    public boolean hasRole(String... roles) {

        return Arrays.stream(roles).allMatch(this::isRole);
    }

    public boolean isRole(String name) {

        return this.roles.stream().anyMatch(role -> role.getName().equalsIgnoreCase(name) || role.getAlias().stream().anyMatch(alia -> alia.equalsIgnoreCase(name)));
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    private PermissionValidator getPermissionValidator() {

        if(this.permissionValidator == null) {

            this.permissionValidator = new PermissionValidator(this, this.permissions);
        }

        return this.permissionValidator;
    }

    @Override
    public boolean isPermitted(String authority) {

        return this.getPermissionValidator().hasPermission(authority) || this.roles.stream().anyMatch(role -> role.isPermitted(authority));
    }


    @Override
    public boolean addPermission(Permission permission) {

        return this.permissions.add(permission);
    }

    @Override
    public boolean removePermission(Permission permission) {

        return this.permissions.remove(permission) && externalUsers.stream().allMatch(user -> user.removePermission(permission));
    }

    public PersistentLogins getRemeberMeInfo() {

        return ProjectBeanHolder.getPersistentLoginsRepository().findByUsernameEqualsIgnoreCase(this.getUsername());
    }




    public static class Builder {

        private String username;

        private String password;

        private String firstName;

        private String lastName;

        private Gender gender;

        private String phoneNumber;

        private boolean phoneNumberVerified;

        private String email;

        private boolean emailVerified;

        private List<Address> addresses = new ArrayList<>();

        private List<UserRole> roles = new ArrayList<>();

        private boolean expired;

        private boolean locked;

        private boolean vaildPassword;

        private boolean enabled;

        public Builder setUsername(String username) {

            this.username = username;

            return this;
        }

        public Builder setPassword(String password) {

            this.password = password;

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

        public Builder setPhoneNumberVerified(boolean phoneNumberVerified) {

            this.phoneNumberVerified = phoneNumberVerified;

            return this;
        }

        public Builder setEmail(String email) {

            this.email = email;

            return this;
        }

        public Builder setEmailVerified(boolean emailVerified) {

            this.emailVerified = emailVerified;

            return this;
        }

        public Builder setAddresses(Address... addresses) {

            this.addresses = Arrays.stream(addresses).toList();

            return this;
        }

        public Builder setRoles(UserRole... roles) {

            this.roles = Arrays.stream(roles).toList();

            return this;
        }

        public Builder setExpired(boolean expired) {

            this.expired = expired;

            return this;
        }

        public Builder setLocked(boolean locked) {

            this.locked = locked;

            return this;
        }

        public Builder setVaildPassword(boolean vaildPassword) {

            this.vaildPassword = vaildPassword;

            return this;
        }

        public Builder setEnabled(boolean enabled) {

            this.enabled = enabled;

            return this;
        }

        public User build() {

            return new User(this.username, this.password, this.firstName, this.lastName, this.gender, this.phoneNumber, this.phoneNumberVerified, this.email, this.emailVerified, this.addresses, this.roles, this.expired, this.locked, this.vaildPassword, this.enabled);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return username != null && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "username = " + username + ", " +
                "firstName = " + firstName + ", " +
                "lastName = " + lastName + ", " +
                "phoneNumber = " + phoneNumber + ", " +
                "phoneNumberVerified = " + phoneNumberVerified + ", " +
                "email = " + email + ", " +
                "emailVerified = " + emailVerified + ", " +
                "permissions = " + permissions + ", " +
                "expired = " + expired + ", " +
                "locked = " + locked + ", " +
                "vaildPassword = " + validPassword + ", " +
                "enabled = " + enabled + ")";
    }
}