package com.cobnet.spring.boot.entity;

import com.cobnet.spring.boot.dto.support.WorkStatus;
import com.cobnet.spring.boot.entity.support.JsonStaffSetConverter;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Work extends EntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "store_id", referencedColumnName = "id"),
            @JoinColumn(name = "store_name", referencedColumnName = "name")
    })
    private Store store;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "service_id", referencedColumnName = "id"),
            @JoinColumn(name = "service_name", referencedColumnName = "name")
    })
    private Service service;

    @Convert(converter = JsonStaffSetConverter.class)
    @Column(columnDefinition = "json")
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
}
