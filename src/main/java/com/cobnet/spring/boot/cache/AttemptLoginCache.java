package com.cobnet.spring.boot.cache;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@RedisHash
@NoArgsConstructor
public class AttemptLoginCache extends SessionIndexedCache {

    private Date creationTime;

    private int count;

    public AttemptLoginCache(String id, Date creationTime, int count) {
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

    public AttemptLoginCache setCreationTime(Date creationTime) {

        this.creationTime = creationTime;

        return this;
    }

    public AttemptLoginCache setCount(int count) {

        this.count = count;

        return this;
    }
}
