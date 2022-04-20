package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.StoreOrder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface StoreOrderRepository extends JPABaseRepository<StoreOrder, Long> {
}
