package com.cobnet.spring.boot.cache;

import com.cobnet.spring.boot.dto.MethodInfo;
import com.cobnet.spring.boot.dto.MethodRecord;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.http.HttpMethod;

import java.util.Date;
import java.util.Map;

@RedisHash
@NoArgsConstructor
public class MessageCallsCache extends AbstractMessageCache {

    public MessageCallsCache(String id, HttpMethod method, String path) {
        super(id, method, path);
    }

    public MessageCallsCache(String id, Date creationTime, Map<HttpMethod, Map<String, MethodRecord>> messages) {
        super(id, creationTime, messages);
    }

    public MessageCallsCache(String id, MethodInfo... methods) {
        super(id, methods);
    }
}
