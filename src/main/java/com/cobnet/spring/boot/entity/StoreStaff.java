package com.cobnet.spring.boot.entity;

import com.cobnet.spring.boot.entity.support.StoreStaffSign;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class StoreStaff extends EntityBase implements Serializable {

    @EmbeddedId
    private StoreStaffSign id;

    @ManyToOne
    @MapsId("user")
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "store_id")
    private Store store;

    public StoreStaff() {}

    public StoreStaff(User user, Store store) {
        this.id = new StoreStaffSign(user, store);
        this.user = user;
        this.store = store;
    }

    public StoreStaffSign getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Store getStore() {
        return store;
    }

    public void setId(StoreStaffSign id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @Override
    public String toString() {
        System.out.println("aaaa");
        return String.format("%s(%s,%s)",this.getClass().getSimpleName(), this.user.getUsername(), this.getStore().getLocation());
    }
}
