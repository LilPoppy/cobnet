package com.cobnet.spring.boot.controller.support;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.QueryHint;

@Getter
@Setter
public class EntityQueryHintInfo {

    private String name;

    private String value;

    public EntityQueryHintInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public EntityQueryHintInfo(QueryHint hint) {

        this(hint.name(), hint.value());
    }

    @Override
    public String toString() {
        return "EntityQueryHintInfo{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
