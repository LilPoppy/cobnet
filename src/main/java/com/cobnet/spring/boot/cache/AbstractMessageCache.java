package com.cobnet.spring.boot.cache;

import com.cobnet.common.DateUtils;
import com.cobnet.common.Delegate;
import com.cobnet.spring.boot.dto.MethodInfo;
import com.cobnet.spring.boot.dto.MethodRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public abstract class AbstractMessageCache extends SessionIndexedCache {

    private Date creationTime;

    private Map<HttpMethod, Map<String, MethodRecord>> messages;

    protected AbstractMessageCache(String id, HttpMethod method, String path) {

        this(id, MethodInfo.of(method, path));
    }

    protected AbstractMessageCache(String id, Date creationTime, Map<HttpMethod, Map<String, MethodRecord>> messages) {

        super(id);
        this.creationTime = creationTime;
        this.messages = messages;
    }

    protected AbstractMessageCache(String id, MethodInfo... methods) {

        super(id);
        this.creationTime = DateUtils.now();
        this.messages = new HashMap<>();

        for(MethodInfo method : methods) {

            this.messages.put(method.getMethod(), new Delegate<>(new HashMap<String, MethodRecord>()).invoke(delegate -> {

                if(delegate.containsKey(method.getMethod())) {

                    delegate.put(method.getPath(), MethodRecord.of(delegate.get(method.getMethod()).getCount() + 1, this.creationTime));
                } else {
                    delegate.put(method.getPath(),  MethodRecord.of(1, this.creationTime));
                }

                return delegate;

            }));
        }
    }

    public AbstractMessageCache add(HttpMethod method, String path) {

        if(!this.messages.containsKey(method)) {

            this.messages.put(method, new HashMap<>());
        }

        Map<String, MethodRecord> paths = this.messages.get(method);

        if(paths.containsKey(path)) {

            paths.put(path, MethodRecord.of(paths.get(path).getCount() + 1, DateUtils.now()));

        } else {

            paths.put(path, MethodRecord.of(1, DateUtils.now()));
        }

        return this;
    }

    public Date getCreationTime() {
        return this.creationTime;
    }


    @JsonIgnore
    public int getCount() {
        return this.messages.values().stream().map(paths -> paths.values().stream().map(MethodRecord::getCount).collect(Collectors.summingInt(Integer::intValue))).collect(Collectors.summingInt(Integer::intValue));
    }

    public MethodRecord getMethodRecord(HttpMethod method, String path) {

        Map<String, MethodRecord> paths = this.messages.get(this.messages.get(method));

        if(paths == null) {

            return null;
        }

        return paths.get(path);
    }

    public Map<HttpMethod, Map<String, MethodRecord>> getMessages() {

        return this.messages;
    }
}
