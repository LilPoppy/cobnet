package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.security.Permission;
import com.cobnet.spring.boot.entity.support.JsonPermissionSetConverter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Cacheable
public class Position implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "store_id")
    private Store store;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "position")
    private Set<Staff> staffs = new HashSet<>();

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Convert(converter = JsonPermissionSetConverter.class)
    @Column(columnDefinition = "json")
    private Set<Permission> permissions = new HashSet<>();

    private boolean isDefault;

    public Position() {}

    public Position(Store store, String name) {

        this(store, name, false);
    }

    public Position(Store store, String name, boolean isDefault) {

        this.store = store;
        this.name = name;
        this.isDefault = isDefault;
    }

    public long getId() {
        return id;
    }

    public Store getStore() {

        return this.store;
    }

    public String getName() {

        return this.name;
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
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "name = " + name + ", " +
                "store = " + store + ", " +
                "permissions = " + permissions + ", " +
                "isDefault = " + isDefault + ")";
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
        return getClass().hashCode();
    }
}
