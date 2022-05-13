package com.cobnet.spring.boot.cache;

import com.cobnet.common.Delegate;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Locale;
import java.util.Map;
import java.util.Properties;

@RedisHash
@NoArgsConstructor
public class MessageSourceCache {

    @Id
    private Locale locale;

    private Map<String, String> messages;

    public MessageSourceCache(Locale locale, Properties messages) {
        this(locale, (Map)messages);
    }

    public MessageSourceCache(Locale locale, Map<String, String> messages) {
        this.locale = locale;
        this.messages = messages;
    }

    public Locale getLocale() {
        return locale;
    }

    public Properties getMessages() {

        return new Delegate<>(new Properties()).call(properties -> properties.putAll(this.messages));
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void setMessages(Properties messages) {
        this.messages = (Map)messages;
    }
}
