package com.cobnet.spring.boot.entity.support;

import com.cobnet.spring.boot.entity.Store;
import com.cobnet.spring.boot.entity.User;
import org.hibernate.Hibernate;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StaffKey implements Serializable {

    private static final long serialVersionUID = -2660047644853689253L;

    @ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "store_id")
    private Store store;

    public StaffKey() { }

    public StaffKey(User user, Store id) {
        this.user = user;
        this.store = id;
    }

    public User getUser() {
        return user;
    }

    public Store getStore() {
        return store;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StaffKey staffKey = (StaffKey) o;
        return user != null && Objects.equals(user, staffKey.user)
                && store != null && Objects.equals(store, staffKey.store);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, store);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "user = " + user + ", " +
                "store = " + store + ")";
    }
}
