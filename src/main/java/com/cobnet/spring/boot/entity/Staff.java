package com.cobnet.spring.boot.entity;

import com.cobnet.spring.boot.dto.support.StaffStatus;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Cacheable
public class Staff extends EntityBase implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.REFRESH} )
    @JoinColumns(value = {
            @JoinColumn(name = "username", referencedColumnName = "username"),
            @JoinColumn(name = "first_name", referencedColumnName = "firstName"),
            @JoinColumn(name = "last_name", referencedColumnName = "lastName")
    })
    private User user;

    @ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumns(value = {
            @JoinColumn(name = "store_id", referencedColumnName = "id"),
            @JoinColumn(name = "store_name", referencedColumnName = "name")
    })
    private Store store;

    @ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumns(value = {
            @JoinColumn(name = "position_id", referencedColumnName = "id"),
            @JoinColumn(name = "position_name", referencedColumnName = "name")
    })
    private Position position;

    private boolean inService;

    @Enumerated
    @Column(name = "status")
    private StaffStatus status;

    public Staff() {}

    public Staff(Store store, User user, Position position) {
        this.user = user;
        this.store = store;
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Store getStore() {
        return store;
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

    public StaffStatus getStatus() {
        return status;
    }

    public void setStatus(StaffStatus status) {
        this.status = status;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Staff staff = (Staff) o;
        return Objects.equals(id, staff.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "user = " + user.getUsername() + ", " +
                "store = " + store.getName() + ", " +
                "position = " + position.getName() + ", " +
                "inService = " + inService + ", " +
                "status = " + status + ")";
    }
}
