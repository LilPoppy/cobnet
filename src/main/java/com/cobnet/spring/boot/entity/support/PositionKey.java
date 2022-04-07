package com.cobnet.spring.boot.entity.support;

import com.cobnet.spring.boot.entity.Store;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PositionKey implements Serializable {

    private static final long serialVersionUID = 1838438778954397256L;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    private String name;

    public PositionKey() {}

    public PositionKey(Store store, String name) {

        this.store = store;
        this.name = name;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {

        this.store = store;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PositionKey that = (PositionKey) o;
        return store != null && Objects.equals(store, that.store)
                && name != null && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(store, name);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "store = " + store + ", " +
                "name = " + name + ")";
    }
}
