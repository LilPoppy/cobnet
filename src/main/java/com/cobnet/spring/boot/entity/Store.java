package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.spring.entity.StoreForm;
import com.cobnet.security.OwnedExternalUserCollection;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.support.OwnedStoreStaffCollection;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String location;

    private String name;

    private String phone;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "store")
    private Set<StoreStaff> crew = new HashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy="store")
    private List<CheckInForm> checkInForms = new ArrayList<>();

    private transient OwnedStoreStaffCollection storeStaffCollection;

    public Store() {}

    public Store(String location, String name, String phone) {
        this.location = location;
        this.name = name;
        this.phone = phone;
    }

    public long getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<CheckInForm> getCheckInForms() {

        return checkInForms.stream().collect(Collectors.toUnmodifiableList());
    }

    public List<StoreForm> getForms() {

        return Stream.of(checkInForms).flatMap(Collection::stream).collect(Collectors.toUnmodifiableList());
    }

    public void addStoreStaff(StoreStaff staff) {

        this.getOwnedStoreStaffCollection().add(staff);
    }

    public void addStoreStaff(User user) {

        this.getOwnedStoreStaffCollection().add(new StoreStaff(user, this));
    }

    public Collection<StoreStaff> getCrew() {
        return crew.stream().collect(Collectors.toUnmodifiableList());
    }

    public OwnedStoreStaffCollection getOwnedStoreStaffCollection() {

        if(this.storeStaffCollection == null) {

            this.storeStaffCollection = new OwnedStoreStaffCollection(this.crew);
        }

        return this.storeStaffCollection;
    }
}
