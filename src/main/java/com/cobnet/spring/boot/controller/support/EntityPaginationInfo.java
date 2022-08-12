package com.cobnet.spring.boot.controller.support;

import com.cobnet.interfaces.spring.controller.annotations.EntityPageHint;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
public class EntityPaginationInfo {

    private int number;

    private int size;

    private String expression;

    private String[] references;

    private Pageable value;

    public EntityPaginationInfo(int number, int size, String expression) {
        this.number = number;
        this.size = size;
        this.expression = expression;
    }

    public EntityPaginationInfo(EntityPageHint hint) {

        this(hint.number(), hint.size(), hint.value());
    }

    @Override
    public String toString() {
        return "EntityPaginationInfo{" +
                "number=" + number +
                ", size=" + size +
                '}';
    }
}
