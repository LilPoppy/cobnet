package com.cobnet.spring.boot.service.support;

import com.cobnet.common.KeyValuePair;
import com.google.cloud.Tuple;
import org.springframework.http.HttpMethod;

public class MessageCallsCache extends AbstractMessageCache {

    public MessageCallsCache(HttpMethod method, String path) {

        super(method, path);
    }

    public MessageCallsCache(KeyValuePair<HttpMethod, String>... methods) {

        super(methods);
    }
}
