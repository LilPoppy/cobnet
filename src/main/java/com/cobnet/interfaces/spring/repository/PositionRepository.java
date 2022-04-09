package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.Position;
import com.cobnet.spring.boot.entity.support.PositionKey;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PositionRepository extends JPABaseRepository<Position, Long> {
}
