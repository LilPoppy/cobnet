package com.cobnet.spring.boot.entity.support;

import com.cobnet.common.wrapper.AbstractSetWrapper;
import com.cobnet.interfaces.cache.CacheKeyProvider;
import com.cobnet.interfaces.cache.annotation.SimpleCacheEvict;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.cobnet.spring.boot.entity.Position;
import com.cobnet.spring.boot.entity.Staff;
import com.cobnet.spring.boot.entity.Store;
import com.cobnet.spring.boot.entity.User;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class OwnedStorePositionCollection extends AbstractSetWrapper<Position> {

    private final Set<Position> positions;

    public OwnedStorePositionCollection(Set<Position> positions) {

        this.positions = positions;
    }

    @Override
    protected Set<Position> getSet() {
        return this.positions;
    }

    @Override
    public boolean add(Position position) {

        if(ProjectBeanHolder.getPositionRepository() != null) {

            Optional<Position> existed = ProjectBeanHolder.getPositionRepository().findById(position.getId());

            if(existed.isEmpty()) {

                ProjectBeanHolder.getPositionRepository().save(position);
            }
        }

        return super.add(position);
    }
}
