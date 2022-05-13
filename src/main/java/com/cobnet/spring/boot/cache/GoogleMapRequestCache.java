package com.cobnet.spring.boot.cache;

import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;

@RedisHash
@NoArgsConstructor
public class GoogleMapRequestCache extends SessionIndexedCache implements Serializable {

    private Date creationTime;

    private int count;

    public GoogleMapRequestCache(String id, Date creationTime, int count) {

        super(id);
        this.creationTime = creationTime;
        this.count = count;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public int getCount() {
        return count;
    }

    public GoogleMapRequestCache setCreationTime(Date creationTime) {

        this.creationTime = creationTime;

        return this;
    }

    public GoogleMapRequestCache setCount(int count) {

        this.count = count;

        return this;
    }



}
