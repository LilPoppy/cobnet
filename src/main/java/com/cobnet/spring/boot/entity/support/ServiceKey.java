package com.cobnet.spring.boot.entity.support;

import com.cobnet.spring.boot.entity.Store;
import com.cobnet.spring.boot.entity.User;
import org.hibernate.Hibernate;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ServiceKey implements Serializable {

    private static final long serialVersionUID = -8992103030994708502L;
    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    private String name;

    public ServiceKey() { }

    public ServiceKey(Store store, String name) {
        this.store = store;
        this.name = name;
    }


    public Store getStore() {
        return store;
    }

    public String getName() {
        return name;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ServiceKey that = (ServiceKey) o;
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
