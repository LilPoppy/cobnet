package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.spring.entity.StoreForm;
import com.cobnet.interfaces.spring.entity.StoreMemberRelated;
import com.cobnet.spring.boot.entity.support.OwnedStorePositionCollection;
import com.cobnet.spring.boot.entity.support.OwnedStoreStaffCollection;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.expression.ConstructorExecutor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Store implements Serializable, StoreMemberRelated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String location;

    private String name;

    private String phone;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "store", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Staff> crew = new HashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "store", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CheckInForm> checkInForms = new ArrayList<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "store", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Service> services = new HashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "store", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Position> positions = new HashSet<>();

    private transient OwnedStoreStaffCollection storeStaffCollection;

    private transient OwnedStorePositionCollection storePositionCollection;

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

    public Collection<Service> getServices() {

        return this.services.stream().collect(Collectors.toUnmodifiableList());
    }

    public void addStaff(Staff staff) {

        this.getOwnedStoreStaffCollection().add(staff);
    }

    public void addStaff(User user) {

        this.getOwnedStoreStaffCollection().add(new Staff(user, this, this.getDefaultPosition()));
    }

    public void addPosition(Position position) {

        this.getOwnedStorePositionCollection().add(position);
    }

    public void addPosition(String name, boolean isDefault) {

        this.getOwnedStorePositionCollection().add(new Position(this, name, isDefault));
    }

    public void addService(Service service) {

        this.services.add(service);
    }

    public Collection<Staff> getCrew() {
        return crew.stream().collect(Collectors.toUnmodifiableList());
    }

    public Collection<Position> getPositions() {

        return this.positions.stream().collect(Collectors.toUnmodifiableList());
    }

    public Position getDefaultPosition() {

        return this.positions.stream().filter(positon -> positon.isDefault()).findFirst().get();
    }

    public OwnedStoreStaffCollection getOwnedStoreStaffCollection() {

        if(this.storeStaffCollection == null) {

            this.storeStaffCollection = new OwnedStoreStaffCollection(this, this.crew);
        }

        return this.storeStaffCollection;
    }

    public OwnedStorePositionCollection getOwnedStorePositionCollection() {

        if(this.storePositionCollection == null) {

            this.storePositionCollection = new OwnedStorePositionCollection(this.positions);
        }

        return this.storePositionCollection;
    }

    @Override
    public String getIdentity() {
        return String.valueOf(this.getId());
    }
}
