package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.security.Permissible;
import com.cobnet.interfaces.security.Permission;
import com.cobnet.security.RoleRule;
import com.cobnet.security.permission.OwnedPermissionCollection;
import com.cobnet.spring.boot.entity.support.JsonPermissionSetConverter;
import org.springframework.lang.NonNull;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
public class UserRole extends EntityBase implements Permissible, Serializable {

    @Id
    @Column(name = "role", nullable = false)
    private String role;

    private RoleRule rule;

    @Convert(converter = JsonPermissionSetConverter.class)
    @Column(columnDefinition = "json")
    private Set<Permission> permissions = new HashSet<>();

    private transient OwnedPermissionCollection permissionCollection;

    public UserRole() {}

    public UserRole(@NonNull String role, RoleRule rule) {

        this.role = role;
        this.rule = rule;
    }

    public UserRole(@NonNull String role, RoleRule rule, Permission... permissions) {

        this(role, rule);

        Arrays.stream(permissions).forEach(this::addPermission);
    }

    public UserRole(@NonNull String role) {

        this(role, RoleRule.USER);
    }

    public UserRole(@NonNull String role, Permission... permissions) {

        this(role, RoleRule.USER, permissions);
    }

    public String getName() {

        return this.role;
    }

    public int getPower() {

        return this.getPermissions().stream().map(Permission::getPower).mapToInt(Integer::valueOf).sum();
    }

    public Set<? extends Permission> getPermissions() {

        return Collections.unmodifiableSet(permissions);
    }

    public OwnedPermissionCollection getOwnedPermissionCollection() {

        if(this.permissionCollection == null) {

            this.permissionCollection = new OwnedPermissionCollection(this, this.permissions);
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

        return this.getOwnedPermissionCollection().hasPermission(authority);
    }

    @Override
    public boolean addPermission(Permission permission) {

        return this.getOwnedPermissionCollection().add(permission);
    }

    @Override
    public boolean removePermission(Permission permission) {

        return this.getOwnedPermissionCollection().remove(permission);
    }

    @Override
    public RoleRule getRule() {

        return this.rule;
    }
}
