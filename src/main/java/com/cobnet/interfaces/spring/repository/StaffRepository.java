package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.Staff;
import com.cobnet.spring.boot.entity.support.StaffKey;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface StaffRepository extends JPABaseRepository<Staff, StaffKey> {

    @Override
    public Staff save(Staff entity);
}
