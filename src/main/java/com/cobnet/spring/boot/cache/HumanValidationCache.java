package com.cobnet.spring.boot.cache;

import com.cobnet.common.PuzzledImage;
import com.cobnet.interfaces.spring.cache.IndexedCacheEntity;
import com.fasterxml.jackson.annotation.JsonGetter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Date;

@RedisHash
@NoArgsConstructor
public class HumanValidationCache extends SessionIndexedCache implements Serializable {

    private PuzzledImage image;

    private Date creationTime;

    private int count;

    private boolean validated;

    public HumanValidationCache(String id, PuzzledImage image, Date creationTime, int count, boolean validated) {
        super(id);
        this.image = image;
        this.creationTime = creationTime;
        this.count = count;
        this.validated = validated;
    }

    public PuzzledImage getImage() {
        return image;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public int getCount() {
        return count;
    }

    public boolean isValidated() {
        return validated;
    }

    public HumanValidationCache setImage(PuzzledImage image) {

        this.image = image;

        return this;
    }

    public HumanValidationCache setCreationTime(Date creationTime) {

        this.creationTime = creationTime;

        return this;
    }

    public HumanValidationCache setCount(int count) {

        this.count = count;

        return this;
    }

    public HumanValidationCache setValidated(boolean validated) {

        this.validated = validated;

        return this;
    }
}
