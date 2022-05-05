package com.cobnet.spring.boot.dto.support;

import com.cobnet.spring.boot.dto.ObjectWrapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public final class ObjectWrapperSerializer extends JsonSerializer<ObjectWrapper> {

    @Override
    public void serialize(ObjectWrapper value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        gen.writeStartObject();
        gen.writeStringField("type", value.getValue().getClass().getName());
        gen.writePOJOField(value.getName(), value.getValue());
        gen.writeEndObject();
    }
}