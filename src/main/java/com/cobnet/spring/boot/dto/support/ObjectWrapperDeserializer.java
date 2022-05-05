package com.cobnet.spring.boot.dto.support;

import com.cobnet.spring.boot.dto.ObjectWrapper;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;
import java.util.Iterator;

public final class ObjectWrapperDeserializer extends JsonDeserializer<ObjectWrapper> implements ContextualDeserializer {

    private JavaType type;

    public ObjectWrapperDeserializer() {

    }

    @Override
    public ObjectWrapper deserialize(JsonParser parser, DeserializationContext context) throws IOException, JacksonException {

        ObjectCodec codec = parser.getCodec();

        TreeNode node = parser.readValueAsTree();

        Iterator<String> it = node.fieldNames();

        String clazz = null;

        while(it.hasNext()) {

            String name = it.next();

            parser = node.get(name).traverse(codec);

            if(!name.equalsIgnoreCase("type")) {

                if(this.type != null) {

                    JavaType type = this.type.containedType(0);

                    if(type != null) {

                        return new ObjectWrapper<>(name, parser.readValueAs(type.getRawClass()));
                    }
                }

                try {

                    return new ObjectWrapper<>(name, parser.readValueAs(context.getTypeFactory().findClass(clazz)));

                } catch (ClassNotFoundException e) {

                    throw new RuntimeException(e);
                }

            } else {

                clazz = node.get("type").traverse(codec).readValueAs(String.class);
            }
        }

        return null;
    }


    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext context, BeanProperty property) throws JsonMappingException {

        this.type = property != null ? property.getType() : context.getContextualType();

        return this;
    }
}
