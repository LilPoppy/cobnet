package com.cobnet.spring.boot.entity.support;

import com.cobnet.interfaces.spring.repository.JPABaseRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class JpaExtensionRepository <T, ID extends Serializable>  extends SimpleJpaRepository<T,ID> implements JPABaseRepository<T,ID>  {

    private final EntityManager entityManager;

    public JpaExtensionRepository(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
    }

    public JpaExtensionRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager em) {
        super(entityInformation, em);
        this.entityManager = em;
    }

    @Transactional
    @Override
    public <S extends T> S save(S entity) {

        return super.save(entity);
    }


}
