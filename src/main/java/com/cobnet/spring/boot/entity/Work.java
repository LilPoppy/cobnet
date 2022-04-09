package com.cobnet.spring.boot.entity;

import com.cobnet.spring.boot.dto.support.WorkStatus;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Work extends EntityBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "store_id", referencedColumnName = "id"),
            @JoinColumn(name = "store_name", referencedColumnName = "name")
    })
    private Store store;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "service_id", referencedColumnName = "id"),
            @JoinColumn(name = "service_name", referencedColumnName = "name")
    })
    private Service service;

    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "staff_works",
            joinColumns = {
                @JoinColumn(name = "work_id", referencedColumnName = "id")
    },
            inverseJoinColumns = {
                @JoinColumn(name = "staff_id", referencedColumnName = "id")
    })
    private Set<Staff> workers = new HashSet<>();

    private Date checkInTime;

    @Enumerated
    @Column(name = "status")
    private WorkStatus status;

    public Work() {}

    public Work(Store store, Service service, Staff... workers) {
        this.store = store;
        this.service = service;
        this.workers = Arrays.stream(workers).collect(Collectors.toSet());
    }

    public void setService(Service service) {
        this.service = service;
    }

    public void setCheckInTime(Date checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Date getCheckInTime() {
        return checkInTime;
    }

    public Set<Staff> getWorkers() {
        return workers;
    }

    public WorkStatus getStatus() {
        return status;
    }

    public void setStatus(WorkStatus status) {
        this.status = status;
    }

    public Service getService() {
        return service;
    }

    public Long getId() {

        return id;
    }

    public Store getStore() {

        return store;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "store = " + store.getName() + ", " +
                "service = " + service.getName() + ", " +
                "workers = " + workers + ", " +
                "checkInTime = " + checkInTime + ", " +
                "status = " + status + ")";
    }




}
