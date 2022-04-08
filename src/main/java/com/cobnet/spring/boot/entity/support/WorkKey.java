package com.cobnet.spring.boot.entity.support;

import com.cobnet.spring.boot.entity.Store;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WorkKey implements Serializable {

    private static final long serialVersionUID = -6553453938679507425L;

    @ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "store_id")
    private Store store;

    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public WorkKey() {}

    public WorkKey(Store store) {
        this.store = store;
    }

    public Store getStore() {
        return store;
    }

    public long getId() {
        return id;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        WorkKey workKey = (WorkKey) o;
        return store != null && Objects.equals(store, workKey.store)
                 && Objects.equals(id, workKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(store, id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "store = " + store + ", " +
                "id = " + id + ")";
    }
}
