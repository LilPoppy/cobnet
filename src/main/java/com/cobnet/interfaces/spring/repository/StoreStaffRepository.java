package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.StoreStaff;
import com.cobnet.spring.boot.entity.support.StoreStaffSign;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreStaffRepository extends JPABaseRepository<StoreStaff, StoreStaffSign> {

    @Override
    public StoreStaff save(StoreStaff entity);
}
