package com.cobnet.spring.boot.entity;

import com.cobnet.spring.boot.entity.support.OwnedPermissionCollection;
import com.cobnet.spring.boot.entity.support.PositionKey;
import com.cobnet.spring.boot.entity.support.StaffKey;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Staff extends EntityBase implements Serializable {

    @EmbeddedId
    private StaffKey id;

    @ManyToOne
    @MapsId("user")
    @JoinColumn(name = "user")
    private User user;

    @ManyToOne
    @MapsId("store")
    @JoinColumn(name = "store_id")
    private Store store;

    @MapsId("store")
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "store_id", referencedColumnName = "store_id"),
            @JoinColumn(name = "position_name", referencedColumnName = "name")
    })
    private Position position;

    private boolean inService;

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Staff() {}

    public Staff(User user, Store store, Position position) {
        this.id = new StaffKey(user, store);
        this.user = user;
        this.store = store;
        this.position = position;
    }

    public StaffKey getId() {
        return id;
    }

    public String getIdentity() {

        return this.getId().toString();
    }

    public User getUser() {
        return user;
    }

    public Store getStore() {
        return store;
    }

    public void setId(StaffKey id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public boolean isInService() {
        return inService;
    }

    public void setInService(boolean inService) {
        this.inService = inService;
    }

    public void leave() {
        this.inService = false;
    }

    public String getFirstName() {

        return this.user.getFirstName();
    }

    public String getLastName() {

        return this.user.getLastName();
    }

    public String getFullName() {

        return this.getFirstName() + " " + this.getLastName();
    }

    public String getPhoneNumber() {

        return this.getUser().getPhoneNumber();
    }

    public String getEmail() {

        return this.getUser().getEmail();
    }

    @Override
    public int hashCode() {

        return this.user.hashCode() + this.store.hashCode();
    }

    @Override
    public String toString() {

        return String.format("%s(%s,%s)",this.getClass().getSimpleName(), this.user.getUsername(), this.getStore().getLocation());
    }
}
