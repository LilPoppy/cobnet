package com.cobnet.spring.boot.entity;

import com.cobnet.interfaces.spring.entity.StoreForm;
import com.cobnet.spring.boot.entity.support.FormType;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class CheckInForm implements StoreForm {

    @Id
    private long id;

    @ManyToOne
    @JoinColumn(name = "store")
    private Store store;

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
