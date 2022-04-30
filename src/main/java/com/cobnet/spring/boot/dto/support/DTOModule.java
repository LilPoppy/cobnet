package com.cobnet.spring.boot.dto.support;

import com.cobnet.spring.boot.dto.ObjectWrapper;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.Iterator;

public final class DTOModule extends SimpleModule {

    public DTOModule() {

        super(PackageVersion.VERSION);

        this.addDeserializer(ObjectWrapper.class, new ObjectWrapperDeserializer());
        this.addSerializer(ObjectWrapper.class, new ObjectWrapperSerializer());
    }

    private static final class ObjectWrapperSerializer extends JsonSerializer<ObjectWrapper> {

        @Override
        public void serialize(ObjectWrapper value, JsonGenerator gen, SerializerProvider serializers) throws IOException {

            gen.writeStartObject();
            gen.writeStringField("type", value.getValue().getClass().getName());
            gen.writePOJOField(value.getName(), value.getValue());
            gen.writeEndObject();
        }
    }

    public static final class ObjectWrapperDeserializer extends JsonDeserializer<ObjectWrapper> implements ContextualDeserializer {

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
}

