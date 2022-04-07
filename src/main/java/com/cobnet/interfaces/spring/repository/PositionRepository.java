package com.cobnet.interfaces.spring.repository;

import com.cobnet.spring.boot.entity.Position;
import com.cobnet.spring.boot.entity.support.PositionKey;

public interface PositionRepository extends JPABaseRepository<Position, PositionKey> {
}
