package com.cobnet.spring.boot.dto.support;

import com.cobnet.spring.boot.dto.MessageWrapper;
import com.cobnet.spring.boot.dto.ObjectWrapper;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;
import java.util.Iterator;

public final class MessageWrapperDeserializer extends JsonDeserializer<MessageWrapper> {

    public MessageWrapperDeserializer() {}

    @Override
    public MessageWrapper deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {

        ObjectCodec codec = parser.getCodec();

        TreeNode node = parser.readValueAsTree();

        Iterator<String> it = node.fieldNames();

        while(it.hasNext()) {

            String name = it.next();

            parser = node.get(name).traverse(codec);

            return new MessageWrapper(name, parser.readValueAs(String.class));
        }

        return null;
    }
}
