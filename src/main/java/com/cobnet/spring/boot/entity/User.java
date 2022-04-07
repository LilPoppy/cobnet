package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.security.Account;
import com.cobnet.interfaces.security.Permissible;
import com.cobnet.interfaces.security.Permission;
import com.cobnet.interfaces.spring.entity.StoreMemberRelated;
import com.cobnet.security.RoleRule;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.support.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class User extends EntityBase implements Permissible, Account, UserDetails, StoreMemberRelated {

    private static final Logger LOG = LoggerFactory.getLogger(User.class);

    @Id
    private String username;

    @Column(nullable = false)
    private String password;

    private boolean passwordEncoded;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private boolean phoneNumberVerified;

    private String email;

    private boolean emailVerified;

    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "user_roles", joinColumns = { @JoinColumn(name = "user", referencedColumnName = "username") },
            inverseJoinColumns = { @JoinColumn(name = "role", referencedColumnName = "role") })
    private Set<UserRole> roles = new HashSet<>();

    @Convert(converter = JsonPermissionSetConverter.class)
    @Column(columnDefinition = "json")
    private Set<Permission> permissions = new HashSet<>();

    private boolean expired;

    private boolean locked;

    private boolean vaildPassword;

    private boolean enabled;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="user")
    private Set<ExternalUser> externalUsers = new HashSet<ExternalUser>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private Set<Staff> associated = new HashSet<>();

    private transient OwnedRoleCollection roleCollection;

    private transient OwnedPermissionCollection permissionsCollection;

    private transient OwnedExternalUserCollection externalUserCollection;

    private transient OwnedStoreStaffCollection storeStaffCollection;

    public User() {}

    public User(@NonNull String username, @NonNull String password, @NonNull String firstName, @NonNull String lastName, String phoneNumber, boolean phoneNumberVerified, String email, boolean emailVerified,  @NonNull List<UserRole> roles, boolean expired, boolean locked, boolean vaildPassword, boolean enabled) {

        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.phoneNumberVerified = phoneNumberVerified;
        this.email = email;
        this.emailVerified = emailVerified;
        this.getOwnedRoleCollection().addAll(roles.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EntityBase::getCreatedTime))), ArrayList::new)));
        this.expired = expired;
        this.locked = locked;
        this.vaildPassword = vaildPassword;
        this.enabled = enabled;

    }

    public User(@NonNull String username, @NonNull String password, @NonNull String firstName, @NonNull String lastName, @NonNull List<UserRole> roles) {

        this(username, password,firstName, lastName, null, false, null, false, roles, false, false, true, true);
    }

    public User(@NonNull String username, @NonNull String password, @NonNull String firstName, @NonNull String lastName, UserRole... roles) {

        this(username, password, firstName, lastName, Arrays.stream(roles).toList());
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
        return this.vaildPassword;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
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

    public OwnedRoleCollection getOwnedRoleCollection() {

        if(this.roleCollection == null) {

            this.roleCollection = new OwnedRoleCollection(this, this.roles);
        }

        return this.roleCollection;
    }

    public OwnedPermissionCollection getOwnedPermissionCollection() {

        if(this.permissionsCollection == null) {

            this.permissionsCollection = new OwnedPermissionCollection(this, this.permissions);
        }

        return this.permissionsCollection;
    }

    public OwnedExternalUserCollection getOwnedExternalUserCollection() {

        if(this.externalUserCollection == null) {

            this.externalUserCollection = new OwnedExternalUserCollection(this, this.externalUsers);
        }

        return this.externalUserCollection;
    }

    public OwnedStoreStaffCollection getOwnedStoreStaffCollection() {

        if(this.storeStaffCollection == null) {

            this.storeStaffCollection = new OwnedStoreStaffCollection(this, this.associated);
        }

        return this.storeStaffCollection;
    }

    public Set<? extends ExternalUser> getExternalUsers() {

        return Collections.unmodifiableSet(this.externalUsers);
    }


    @Override
    public Collection<? extends Permission> getPermissions() {

        return Stream.of(roles.stream().map(UserRole::getPermissions).flatMap(Collection::stream).collect(Collectors.toList()), permissions).flatMap(Collection::stream).collect(Collectors.toUnmodifiableList());
    }

    public Collection<Staff> getAssociated() {

        return associated.stream().collect(Collectors.toUnmodifiableList());
    }

    public void addStore(Store store) {

        this.getOwnedStoreStaffCollection().add(new Staff(this, store, store.getDefaultPosition()));
    }

    public Collection<Store> getStores() {

        return associated.stream().map(Staff::getStore).collect(Collectors.toUnmodifiableList());
    }

    public boolean hasRole(String... roles) {

        return Arrays.stream(roles).allMatch(this::isRole);
    }

    public boolean isRole(String name) {

        return this.roles.stream().anyMatch(role -> role.getName().equalsIgnoreCase(name));
    }

    @Override
    public boolean isPermitted(String authority) {

        return this.getOwnedPermissionCollection().hasPermission(authority) || this.getOwnedRoleCollection().stream().anyMatch(role -> role.isPermitted(authority));
    }

    @Override
    public boolean addPermission(Permission permission) {

        return this.getOwnedPermissionCollection().add(permission);
    }

    @Override
    public boolean removePermission(Permission permission) {

        if(this.getOwnedPermissionCollection().hasPermission(permission)) {

            return this.getOwnedPermissionCollection().remove(permission);
        }

        boolean result = false;

        for(ExternalUser user : this.getOwnedExternalUserCollection()) {

            result = user.removePermission(permission);
        }

        return result;
    }

    public boolean addExternalUser(ExternalUser user) {

        return this.getOwnedExternalUserCollection().add(user);
    }

    public boolean removeExternalUser(ExternalUser user) {

        return this.getOwnedExternalUserCollection().remove(user);
    }

    public PersistentLogins getRemeberMeInfo() {

        return ProjectBeanHolder.getPersistentLoginsRepository().findByUsernameEqualsIgnoreCase(this.getUsername());
    }

    public static class Builder {

        private String username;

        private String password;

        private String firstName;

        private String lastName;

        private String phoneNumber;

        private boolean phoneNumberVerified;

        private String email;

        private boolean emailVerified;

        private List<UserRole> roles;

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

            return new User(this.username, this.password, this.firstName, this.lastName, this.phoneNumber, this.phoneNumberVerified, this.email, this.emailVerified, this.roles, this.expired, this.locked, this.vaildPassword, this.enabled);
        }
    }
}
