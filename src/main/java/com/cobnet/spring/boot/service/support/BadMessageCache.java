package com.cobnet.spring.boot.service.support;

import com.cobnet.common.KeyValuePair;
import com.google.cloud.Tuple;
import org.springframework.http.HttpMethod;

public class BadMessageCache extends AbstractMessageCache {

    public BadMessageCache(HttpMethod method, String path) {

        super(method, path);
    }

    public BadMessageCache(KeyValuePair<HttpMethod, String>... methods) {

        super(methods);
    }

}
