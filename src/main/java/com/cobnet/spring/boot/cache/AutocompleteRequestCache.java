package com.cobnet.spring.boot.cache;

import com.cobnet.interfaces.spring.cache.IndexedCacheEntity;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Date;

@RedisHash
@NoArgsConstructor
public class AutocompleteRequestCache extends SessionIndexedCache implements Serializable {

    private Date creationTime;

    private int count;

    public AutocompleteRequestCache(String id, Date creationTime, int count) {

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

    public AutocompleteRequestCache setCreationTime(Date creationTime) {

        this.creationTime = creationTime;

        return this;
    }

    public AutocompleteRequestCache setCount(int count) {

        this.count = count;

        return this;
    }



}
