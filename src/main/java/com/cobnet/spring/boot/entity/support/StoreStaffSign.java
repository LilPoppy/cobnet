package com.cobnet.spring.boot.entity.support;

import com.cobnet.spring.boot.entity.Store;
import com.cobnet.spring.boot.entity.User;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
public class StoreStaffSign implements Serializable {

    private static final long serialVersionUID = -2660047644853689253L;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store id;

    public StoreStaffSign() { }

    public StoreStaffSign(User user, Store id) {
        this.user = user;
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public Store getId() {
        return id;
    }
}
