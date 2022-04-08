package com.cobnet.spring.boot.entity.support;

import com.cobnet.interfaces.spring.repository.JPABaseRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class JpaExtensionRepository <T, ID extends Serializable>  extends SimpleJpaRepository<T,ID> implements JPABaseRepository<T,ID>  {

    private final EntityManager manager;
    private final JpaEntityInformation<T, ?> information;


    public JpaExtensionRepository(Class<T> domainClass, EntityManager manager) {

        super(domainClass, manager);
        this.manager = manager;
        this.information = JpaEntityInformationSupport.getEntityInformation(domainClass, manager);
    }

    public JpaExtensionRepository(JpaEntityInformation<T, ?> information, EntityManager manager) {

        super(information, manager);
        this.information = information;
        this.manager = manager;
    }

    @Transactional
    @Override
    public <S extends T> S save(S entity) {
        return super.save(entity);
    }


}
