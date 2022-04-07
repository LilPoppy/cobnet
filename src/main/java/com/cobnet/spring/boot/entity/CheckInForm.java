package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.spring.entity.StoreForm;
import com.cobnet.spring.boot.dto.support.FormType;
import com.cobnet.spring.boot.entity.support.JsonMapConverter;

import javax.persistence.*;
import java.util.HashMap;

@Entity
public class CheckInForm implements StoreForm {

    @Id
    private long id;

    @ManyToOne
    @JoinColumn(name = "store")
    private Store store;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Convert(converter = JsonMapConverter.class)
    @Column(columnDefinition = "json")
    private HashMap<String, Object> data = new HashMap<>();

    public CheckInForm() {}

    public CheckInForm(long id, Store store) {
        this.id = id;
        this.store = store;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public HashMap<String, Object> getData() {
        return this.data;
    }

    @Override
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    @Override
    public FormType getFormType() {
        return FormType.CHECK_IN;
    }

    @Override
    public Store getStore() {
        return this.store;
    }

    @Override
    public void setStore(Store store) {
        this.store = store;
    }
}
