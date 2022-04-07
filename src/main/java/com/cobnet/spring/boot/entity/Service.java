package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.spring.dto.ServiceOption;
import com.cobnet.spring.boot.entity.support.JsonServiceAttributeConverter;
import com.cobnet.spring.boot.entity.support.JsonServiceOptionSetConverter;
import com.cobnet.spring.boot.entity.support.JsonSetConverter;
import com.cobnet.spring.boot.entity.support.ServiceKey;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.*;

@Entity
public class Service {

    @EmbeddedId
    private ServiceKey id;

    @ManyToOne
    @MapsId("store")
    @JoinColumn(name = "store_id")
    private Store store;

    private long price;

    @Convert(converter = JsonServiceOptionSetConverter.class)
    @Column(columnDefinition = "json")
    private Set<? extends ServiceOption> options = new HashSet<>();

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Convert(converter = JsonServiceAttributeConverter.class)
    @Column(columnDefinition = "json")
    private Map<? extends ServiceOption, Object> attribute = new HashMap<>();

    public Service() {}

    public Service(ServiceKey id) {
        this.id = id;
    }

    public Service(Store store, String name) {
        this(new ServiceKey(store, name));
    }

    public String getName() {
        return this.id.getName();
    }

    public ServiceKey getId() {
        return id;
    }

    public Store getStore() {
        return store;
    }

    public long getPrice() {
        return price;
    }

    public Set<? extends ServiceOption> getOptions() {
        return options;
    }

    public Map<? extends ServiceOption, Object> getAttribute() {
        return attribute;
    }

    public void setName(String name) {
        this.id.setName(name);
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Service service = (Service) o;
        return id != null && Objects.equals(id, service.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "name = " + this.getName() + ", " +
                "price = " + price + ")";
    }
}
