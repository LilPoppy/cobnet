package com.storechain.spring.boot.entity;

import java.util.Date;

import javax.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
