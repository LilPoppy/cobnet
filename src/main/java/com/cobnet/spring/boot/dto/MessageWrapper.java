package com.cobnet.spring.boot.dto;

import com.cobnet.interfaces.connection.web.ApplicationJson;
import com.cobnet.interfaces.connection.web.NamedContent;

import java.util.Map;

public final class MessageWrapper implements ApplicationJson, NamedContent<Map<String, Object>> {

    private final String name;

    private String text;

    public MessageWrapper(String text) {
        this.name = "name";
        this.text = text;
    }

    public MessageWrapper(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public MessageWrapper(String name, Object text) {
        this.name = name;
        this.text = text.toString();
    }

    @Override
    public String getName() {
        return this.name;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "MessageWrapper{" +
                "name='" + name + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}