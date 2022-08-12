package com.cobnet.spring.boot.controller.support;

import com.cobnet.interfaces.spring.controller.annotations.EntityCollectionHint;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class EntityCollectionInfo {

    private EntityOrderInfo[] orders;

    private EntityPaginationInfo pagination;

    private boolean distinct;

    private long total;

    public EntityCollectionInfo(EntityOrderInfo[] orders, EntityPaginationInfo pagination, boolean distinct, long total) {
        this.orders = orders;
        this.pagination = pagination;
        this.distinct = distinct;
        this.total = total;
    }

    public EntityCollectionInfo(EntityCollectionHint hint) {

        this(Arrays.stream(hint.orderBy()).map(EntityOrderInfo::new).toArray(EntityOrderInfo[]::new), new EntityPaginationInfo(hint.pagination()), hint.distinct(), hint.total());
    }

    @Override
    public String toString() {
        return "EntityCollectionInfo{" +
                "orders=" + Arrays.toString(orders) +
                ", pagination=" + pagination +
                ", distinct=" + distinct +
                ", total=" + total +
                '}';
    }
}
