package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.Work;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface WorkRepository extends JPABaseRepository<Work, Long>{
}
