package com.cobnet.security.permission;

import com.cobnet.interfaces.security.Permission;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Data
public class UserPermission implements Permission {

    private String authority;

    private String description;

    private int power;

    public UserPermission() {}

    public UserPermission(@NonNull String authority, String description, int power) {

        this.authority = authority;
        this.description = description;
        this.power = power;
    }

    public UserPermission(@NonNull String authority, String description) {

        this(authority, description, ProjectBeanHolder.getSecurityConfiguration().getPermissionDefaultPower());
    }

    public UserPermission(@NonNull String authority, int power) {

        this(authority, "", power);
    }

    public UserPermission(@NonNull String authority) {

        this(authority, "");
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int getPower() {
        return this.power;
    }

    @Override
    public String getAuthority() {
        return this.authority;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {

            return true;
        }

        if (obj instanceof SimpleGrantedAuthority) {
            return this.authority.equals(((SimpleGrantedAuthority) obj).getAuthority());
        }

        if (obj instanceof UserPermission) {

            return this.authority.equals(((UserPermission) obj).getAuthority());
        }

        return false;
    }

    @Override
    public int hashCode() {

        return this.authority.hashCode();
    }

    @Override
    public String toString() {

        return this.authority;
    }
}
