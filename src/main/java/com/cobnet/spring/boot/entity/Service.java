package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.spring.dto.ServiceOption;
import com.cobnet.spring.boot.entity.support.JsonServiceAttributeConverter;
import com.cobnet.spring.boot.entity.support.JsonServiceOptionSetConverter;
import com.cobnet.spring.boot.entity.support.JsonSetConverter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Entity
public class Service {

    @Id
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "store")
    private Store store;

    private long price;

    @Convert(converter = JsonServiceOptionSetConverter.class)
    @Column(columnDefinition = "json")
    private Set<? extends ServiceOption> options = new HashSet<>();

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Convert(converter = JsonServiceAttributeConverter.class)
    @Column(columnDefinition = "json")
    private Map<? extends ServiceOption, Object> attribute = new HashMap<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
