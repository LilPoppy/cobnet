package com.cobnet.interfaces.spring.entity;

import com.cobnet.spring.boot.entity.Store;
import com.cobnet.spring.boot.entity.support.FormType;

import javax.persistence.*;

@MappedSuperclass
public interface StoreForm {

    @Id
    public long getId();

    public void setId(long id);

    @Transient
    public FormType getFormType();

    @Transient
    public Store getStore();

    public void setStore(Store store);

}
