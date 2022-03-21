package com.cobnet.spring.boot.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class EntityBase {

    @Column(name = "created_time")
    @CreatedDate
    private Date createdTime;

    @LastModifiedDate
    @Column(name = "last_modified_time", nullable = false)
    private Date lastModifiedTime;

    protected EntityBase() {

        this.createdTime = new Date(System.currentTimeMillis());
        this.lastModifiedTime = createdTime;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public Date getLastModfiedTime() {
        return lastModifiedTime;
    }
}
