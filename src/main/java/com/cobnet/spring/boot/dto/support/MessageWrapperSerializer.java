package com.cobnet.spring.boot.dto.support;

import com.cobnet.spring.boot.dto.MessageWrapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public final class MessageWrapperSerializer extends JsonSerializer<MessageWrapper> {

    @Override
    public void serialize(MessageWrapper value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        gen.writeStartObject();
        gen.writePOJOField(value.getName(), value.getText());
        gen.writeEndObject();
    }
}