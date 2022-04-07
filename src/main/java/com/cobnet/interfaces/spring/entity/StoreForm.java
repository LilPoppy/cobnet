package com.cobnet.interfaces.spring.entity;

import com.cobnet.spring.boot.entity.Store;
import com.cobnet.spring.boot.dto.support.FormType;
import com.cobnet.spring.boot.entity.support.JsonMapConverter;

import javax.persistence.*;
import java.util.HashMap;

@MappedSuperclass
public interface StoreForm {

    @Id
    public long getId();

    public void setId(long id);

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "json")
    public HashMap<String, Object> getData();

    public void setData(HashMap<String,Object> data);

    @Transient
    public FormType getFormType();

    @Transient
    public Store getStore();

    public void setStore(Store store);

}
