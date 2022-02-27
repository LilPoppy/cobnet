package com.storechain.interfaces.spring.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ExtensionRepository<T, ID extends Serializable> extends JpaRepository<T,ID>, JpaSpecificationExecutor<T> {

}