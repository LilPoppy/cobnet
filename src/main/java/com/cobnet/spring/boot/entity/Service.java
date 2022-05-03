package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.spring.dto.ServiceOption;
import com.cobnet.spring.boot.entity.support.JsonServiceAttributesConverter;
import com.cobnet.spring.boot.entity.support.JsonWorkAttributesConverter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Cacheable
public class Service implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumns({
            @JoinColumn(name = "store_id", referencedColumnName = "id"),
            @JoinColumn(name = "store_name", referencedColumnName = "name")
    })
    private Store store;

    private String name;

    private long price;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Convert(converter = JsonServiceAttributesConverter.class)
    @Column(columnDefinition = "json")
    private Set<? extends ServiceOption<?>> attributes = new HashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(mappedBy = "service")
    private Set<Work> works = new HashSet<>();

    public Service() {}

    public Service(Store store, String name) {

        this.store = store;
        this.name = name;
    }

    public Service(String name, long price, ServiceOption<?>... attributes) {

        this.name = name;
        this.price = price;
        this.attributes = Arrays.stream(attributes).collect(Collectors.toSet());
    }

    public String getName() {
        return this.name;
    }

    public long getId() {
        return id;
    }

    public Store getStore() {
        return store;
    }

    public long getPrice() {
        return price;
    }

    public Set<? extends ServiceOption<?>> getAttributes() {
        return attributes;
    }

    public void setName(String name) {
        this.name = name;
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
        return Objects.equals(id, service.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "store = " + store.getName() + ", " +
                "name = " + name + ", " +
                "price = " + price + ", " +
                "attribute = " + attributes + ")";
    }
}
