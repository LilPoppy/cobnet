package com.cobnet.spring.boot.entity;


import com.cobnet.spring.boot.dto.support.WorkStatus;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class StoreOrder extends EntityBase {

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
            @JoinColumn(name = "customer_id", referencedColumnName = "id"),
            @JoinColumn(name = "customer_first_name", referencedColumnName = "firstName"),
            @JoinColumn(name = "customer_last_name", referencedColumnName = "lastName")
    })
    private CustomerInfo info;

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "order", orphanRemoval = true, cascade = CascadeType.ALL)
    private Set<Work> works = new HashSet<>();

    private Date bookTime;

    private Date checkInTime;

    public StoreOrder() {}

    public StoreOrder(Store store, Work... works) {

        this.store = store;

        if(works.length > 0) {
            this.works.addAll(List.of(works));
        }
    }

    public void setCheckInTime(Date checkInTime) {
        this.checkInTime = checkInTime;
    }

    public Date getCheckInTime() {
        return checkInTime;
    }

    public void setBookTime(Date bookTime) {
        this.bookTime = bookTime;
    }

    public Date getBookTime() {
        return bookTime;
    }

    public Set<Work> getWorks() {
        return works;
    }

    public void addWork(Work work) {

        this.works.add(work);
    }

    public void addWork(Service service, Staff... workers) {

        this.works.add(new Work(this, Arrays.stream(workers).collect(Collectors.toSet()), service, WorkStatus.WAITING));
    }

    public Long getId() {

        return id;
    }

    public Store getStore() {

        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "store = " + store + ", " +
                "bookTime = " + bookTime + ", " +
                "checkInTime = " + checkInTime + ")";
    }
}
