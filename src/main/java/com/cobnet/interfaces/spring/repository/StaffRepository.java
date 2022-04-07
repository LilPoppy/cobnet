package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.Staff;
import com.cobnet.spring.boot.entity.support.StaffKey;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JPABaseRepository<Staff, StaffKey> {

    @Override
    public Staff save(Staff entity);
}
