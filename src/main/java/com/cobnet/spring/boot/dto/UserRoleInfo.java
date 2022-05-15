package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.interfaces.security.Permission;
import com.cobnet.security.RoleRule;
import com.cobnet.spring.boot.entity.UserRole;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
public class UserRoleInfo implements ApplicationJson {

    private String role;

    private RoleRule rule;

    private Set<Permission> permissions;

    public UserRoleInfo(String role, RoleRule rule, Set<Permission> permissions) {
        this.role = role;
        this.rule = rule;
        this.permissions = permissions;
    }

    public UserRoleInfo(String role, RoleRule rule, Permission... permissions) {

        this(role, rule, Arrays.stream(permissions).collect(Collectors.toSet()));
    }

    public UserRoleInfo(UserRole role) {
        
        this(role.getRole(), role.getRule(), role.getPermissions());
    }

    public String getRole() {
        return role;
    }

    public UserRoleInfo setRole(String role) {
        this.role = role;
        return this;
    }

    public RoleRule getRule() {
        return rule;
    }

    public UserRoleInfo setRule(RoleRule rule) {
        this.rule = rule;
        return this;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public UserRoleInfo setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
        return this;
    }

    public static final class Builder {

        private String role;

        private RoleRule rule;

        private Set<Permission> permissions;

        public Builder setRole(String role) {
            this.role = role;
            return this;
        }

        public Builder setRule(RoleRule rule) {
            this.rule = rule;
            return this;
        }

        public Builder setPermissions(Permission... permissions) {
            this.permissions = Arrays.stream(permissions).collect(Collectors.toSet());
            return this;
        }

        public UserRoleInfo build() {

            return new UserRoleInfo(this.role, this.rule, this.permissions);
        }
    }
}
