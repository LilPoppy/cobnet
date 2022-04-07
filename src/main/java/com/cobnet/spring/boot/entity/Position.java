package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.security.Permission;
import com.cobnet.spring.boot.entity.support.JsonPermissionSetConverter;
import com.cobnet.spring.boot.entity.support.PositionKey;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Position {

    @EmbeddedId
    private PositionKey id;

    @ManyToOne
    @MapsId("store")
    @JoinColumn(name = "store_id")
    private Store store;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "position", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Staff> staffs = new HashSet<>();

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Convert(converter = JsonPermissionSetConverter.class)
    @Column(columnDefinition = "json")
    private Set<Permission> permissions = new HashSet<>();

    private boolean isDefault;

    public Position() {}

    public Position(PositionKey id) {
        this.id = id;
    }

    public Position(Store store, String name) {

        this(store, name, false);
    }

    public Position(Store store, String name, boolean isDefault) {

        this.id = new PositionKey(store, name);
        this.isDefault = isDefault;
    }

    public PositionKey getId() {
        return id;
    }

    public Store getStore() {

        return this.store;
    }

    public String getName() {

        return this.id.getName();
    }

    public Set<Staff> getStaffs() {
        return staffs;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Position position = (Position) o;
        return id != null && Objects.equals(id, position.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "name = " + id.getName() + ", " +
                "default = " + isDefault + ")";
    }
}
