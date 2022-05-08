package com.cobnet.spring.boot.service.support;

import com.cobnet.common.DateUtils;
import com.cobnet.common.Delegate;
import com.cobnet.interfaces.CacheValue;
import com.cobnet.spring.boot.service.SecurityService;
import org.springframework.http.HttpMethod;

import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbstractMessageCache implements CacheValue {

    public static final String SecurityServiceKey = SecurityService.class.getSimpleName();

    private final Date creationTime;

    private Map<HttpMethod, Map<String, Map.Entry<Integer, Date>>> called;

    protected AbstractMessageCache(HttpMethod method, String path) {

        this(new AbstractMap.SimpleEntry<>(method, path));
    }

    protected AbstractMessageCache(Map.Entry<HttpMethod, String>... methods) {

        this.creationTime = DateUtils.now();
        this.called = new HashMap<>();

        for(Map.Entry<HttpMethod, String> method : methods) {

            this.called.put(method.getKey(), new Delegate<>(new HashMap<String, Map.Entry<Integer, Date>>()).invoke(delegate -> {

                if(delegate.containsKey(method.getValue())) {

                    delegate.put(method.getValue(), new AbstractMap.SimpleEntry<>(delegate.get(method.getKey()).getKey() + 1, this.creationTime));
                } else {
                    delegate.put(method.getValue(), new AbstractMap.SimpleEntry<>(1, this.creationTime));
                }

                return delegate;

            }));
        }
    }

    public AbstractMessageCache add(HttpMethod method, String path) {

        if(!this.called.containsKey(method)) {

            this.called.put(method, new HashMap<>());
        }

        Map<String, Map.Entry<Integer, Date>> paths = this.called.get(method);

        if(paths.containsKey(path)) {

            paths.put(path, new AbstractMap.SimpleEntry<>(paths.get(path).getKey() + 1, DateUtils.now()));

        } else {

            paths.put(path, new AbstractMap.SimpleEntry<>(1, DateUtils.now()));
        }

        return this;
    }

    @Override
    public Date creationTime() {
        return this.creationTime;
    }

    @Override
    public int count() {
        return this.called.values().stream().map(paths -> paths.values().stream().map(Map.Entry::getKey).collect(Collectors.summingInt(Integer::intValue))).collect(Collectors.summingInt(Integer::intValue));
    }

    public Map.Entry<Integer, Date> getMethodInfo(HttpMethod method, String path) {

        Map<String, Map.Entry<Integer, Date>> paths = this.called.get(this.called.get(method));

        if(paths == null) {

            return null;
        }

        return paths.get(path);
    }

    public Map<HttpMethod, Map<String, Map.Entry<Integer, Date>>> getCalled() {

        return this.called;
    }
}
