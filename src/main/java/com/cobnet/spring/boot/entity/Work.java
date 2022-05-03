package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.spring.dto.ServiceOption;
import com.cobnet.spring.boot.dto.support.WorkStatus;
import com.cobnet.spring.boot.entity.support.JsonWorkAttributesConverter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Work extends EntityBase implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


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

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumns({
            @JoinColumn(name = "service_id", referencedColumnName = "id"),
            @JoinColumn(name = "service_name", referencedColumnName = "name")
    })
    private Service service;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Convert(converter = JsonWorkAttributesConverter.class)
    @Column(columnDefinition = "json")
    private Map<String, ? extends ServiceOption<?>> attributes = new HashMap<>();

    @Enumerated
    @Column(name = "status")
    private WorkStatus status;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private StoreOrder order;


    public Work(StoreOrder order, Set<Staff> workers, Service service, WorkStatus status) {
        this.order = order;
        this.workers = workers;
        this.service = service;
        this.attributes = this.service.getAttributes().stream().map(option -> Map.entry(option.name(), option)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        this.status = status;
    }

    public Work() {}

    public long getId() {
        return id;
    }

    public StoreOrder getOrder() {
        return order;
    }

    public Set<Staff> getWorkers() {
        return workers;
    }

    public Service getService() {
        return service;
    }

    public WorkStatus getStatus() {
        return status;
    }

    public void setOrder(StoreOrder order) {
        this.order = order;
    }

    public void setStatus(WorkStatus status) {
        this.status = status;
    }

    public Map<String, ? extends ServiceOption<?>> getAttributes() {
        return attributes;
    }

}
