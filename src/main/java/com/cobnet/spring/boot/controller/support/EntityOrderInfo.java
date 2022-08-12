package com.cobnet.spring.boot.controller.support;

import com.cobnet.interfaces.spring.controller.annotations.EntityOrderHint;
import com.cobnet.spring.boot.controller.support.enums.OrderDirection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityOrderInfo {

    private String attribute;

    private OrderDirection orderDirection;

    public EntityOrderInfo(String attribute, OrderDirection orderDirection) {
        this.attribute = attribute;
        this.orderDirection = orderDirection;
    }

    public EntityOrderInfo(EntityOrderHint order) {

        this(order.attribute(), order.orderByDirection());
    }

    @Override
    public String toString() {
        return "EntityOrderInfo{" +
                "attribute='" + attribute + '\'' +
                ", orderDirection=" + orderDirection +
                '}';
    }
}
