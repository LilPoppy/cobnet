package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.security.Permissible;
import com.cobnet.interfaces.security.Permission;
import com.cobnet.security.RoleRule;
import com.cobnet.security.permission.PermissionValidator;
import com.cobnet.spring.boot.entity.support.JsonPermissionSetConverter;
import com.cobnet.spring.boot.entity.support.JsonStringSetConverter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.lang.NonNull;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
public class UserRole extends EntityBase implements Permissible, Serializable {

    @Id
    @Column(name = "role", nullable = false)
    private String role;

    private RoleRule rule;

    @Convert(converter = JsonStringSetConverter.class)
    @Column(columnDefinition = "json")
    private Set<String> alias = new HashSet<>();

    private boolean isDefault;

    @Convert(converter = JsonPermissionSetConverter.class)
    @Column(columnDefinition = "json")
    private Set<Permission> permissions = new HashSet<>();

    @Transient
    private transient PermissionValidator permissionCollection;

    public UserRole() {}

    public UserRole(@NonNull String role, RoleRule rule, boolean isDefault) {

        this.role = role;
        this.rule = rule;
        this.isDefault = isDefault;
    }

    public UserRole(@NonNull String role, RoleRule rule, boolean isDefault, Permission... permissions) {

        this(role, rule, isDefault);

        Arrays.stream(permissions).forEach(this::addPermission);
    }

    public UserRole(@NonNull String role) {

        this(role, RoleRule.USER, false);
    }

    public UserRole(@NonNull String role, Permission... permissions) {

        this(role, RoleRule.USER, false, permissions);
    }

    public String getName() {

        return this.role;
    }

    public int getPower() {

        return this.getPermissions().stream().map(Permission::getPower).mapToInt(Integer::valueOf).sum();
    }

    public Set<Permission> getPermissions() {

        return this.permissions;
    }

    public Set<String> getAlias() {
        return alias;
    }

    public String getRole() {

        return role;
    }

    public UserRole addAlias(String... alias) {

        this.alias.addAll(Arrays.stream(alias).toList());

        return this;
    }

    public boolean isDefault() {

        return isDefault;
    }

    public void setRule(RoleRule rule) {
        this.rule = rule;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    private PermissionValidator getPermissionValidator() {

        if(this.permissionCollection == null) {

            this.permissionCollection = new PermissionValidator(this, this.permissions);
        }

        return this.permissionCollection;
    }

    @Override
    public String getIdentity() {

        return this.role;
    }

    @Override
    public int hashCode() {

        return this.role.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {

            return true;
        }

        if (obj instanceof UserRole) {

            return this.role.equals(((UserRole) obj).getIdentity());
        }

        return false;
    }

    @Override
    public String toString() {

        return "[" + this.role + "]";
    }

    @Override
    public boolean isPermitted(String authority) {

        return this.getPermissionValidator().hasPermission(authority);
    }

    @Override
    public boolean addPermission(Permission permission) {

        return this.permissions.add(permission);
    }

    @Override
    public boolean removePermission(Permission permission) {

        return this.permissions.remove(permission);
    }

    @Override
    public RoleRule getRule() {

        return this.rule;
    }
}
