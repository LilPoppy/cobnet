package com.cobnet.spring.boot.entity.support;

import com.cobnet.spring.boot.entity.Store;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

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
}
