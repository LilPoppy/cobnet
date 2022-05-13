package com.cobnet.spring.boot.cache;

import com.cobnet.spring.boot.dto.MethodInfo;
import com.cobnet.spring.boot.dto.MethodRecord;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.http.HttpMethod;

import java.util.Date;
import java.util.Map;

@RedisHash
@NoArgsConstructor
public class BadMessageCache extends AbstractMessageCache {

    public BadMessageCache(String id, HttpMethod method, String path) {
        super(id, method, path);
    }

    public BadMessageCache(String id, Date creationTime, Map<HttpMethod, Map<String, MethodRecord>> messages) {
        super(id, creationTime, messages);
    }

    public BadMessageCache(String id, MethodInfo... methods) {
        super(id, methods);
    }
}
