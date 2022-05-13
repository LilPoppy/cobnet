package com.cobnet.spring.boot.cache.support;

import com.cobnet.common.StringUtils;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import lombok.SneakyThrows;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.redis.core.PartialUpdate;
import org.springframework.data.redis.core.convert.*;
import org.springframework.data.redis.core.mapping.RedisMappingContext;
import org.springframework.data.redis.core.mapping.RedisPersistentEntity;
import org.springframework.data.util.ClassTypeInformation;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.*;

public class MappingJsonRedisConverter extends MappingRedisConverter {

    private static final Character PATH_OPEN = '[';

    private static final Character PATH_CLOSE = ']';

    private ObjectMapper mapper;

    private RedisTypeMapper typeMapper;

    private CustomConversions conversions;

    public MappingJsonRedisConverter(ObjectMapper mapper, RedisMappingContext mappingContext, IndexResolver indexResolver, ReferenceResolver referenceResolver, RedisTypeMapper typeMapper, CustomConversions conversions) {
        super(mappingContext, indexResolver, referenceResolver, typeMapper);
        this.setCustomConversions(conversions);
        this.mapper = mapper;
        this.typeMapper = typeMapper;
        this.conversions = conversions;

    }

    @SneakyThrows
    @Override
    public <R> R read(Class<R> type, RedisData source) {

        TypeInformation<?> readType = typeMapper.readType(source.getBucket().getPath(), ClassTypeInformation.from(type));

        ObjectNode tree = mapper.createObjectNode();

        source.getBucket().rawMap().entrySet().stream().map(entry -> new AbstractMap.SimpleEntry(new String(entry.getKey()), new String(entry.getValue()))).forEach(field -> {

            getChild(tree, field, mapper.getNodeFactory());
        });

        return tree.traverse(mapper.getFactory().getCodec()).readValueAs(type);
    }

    @SneakyThrows
    @Override
    public void write(Object source, RedisData sink) {

        if(source == null) {

            return;
        }

        if(source instanceof PartialUpdate<?>) {

            writePartialUpdate((PartialUpdate) source, sink);
            return;
        }

        RedisPersistentEntity<?> entity = this.getMappingContext().getPersistentEntity(source.getClass());

        if (entity == null) {

            typeMapper.writeType(ClassUtils.getUserClass(source), sink.getBucket().getPath());
            sink.getBucket().put("_raw", this.getConversionService().convert(source, byte[].class));
            return;
        }

        sink.setKeyspace(entity.getKeySpace());

        JsonNode tree = mapper.valueToTree(source);

        Set<Map.Entry<String, String>> fields = getFields(null , tree, mapper.getFactory().getCodec());

        for(Map.Entry<String, String> field : fields) {

            sink.getBucket().put(field.getKey(), field.getValue().getBytes());
        }

        Object identifier = entity.getIdentifierAccessor(source).getIdentifier();

        if (identifier != null) {

            sink.setId(getConversionService().convert(identifier, String.class));
        }

        Long ttl = entity.getTimeToLiveAccessor().getTimeToLive(source);

        if (ttl != null && ttl > 0) {

            sink.setTimeToLive(ttl);
        }

        for (IndexedData indexedData : this.getIndexResolver().resolveIndexesFor(entity.getTypeInformation(), source)) {

            sink.addIndexedData(indexedData);
        }
    }

    protected Set<Map.Entry<String, String>> getFields(String name, JsonNode node, ObjectCodec codec) throws IOException {

        Set<Map.Entry<String, String>> fields = new HashSet<>();

        if(node.size() > 0) {

            Iterator<Map.Entry<String, JsonNode>> it = node.fields();

            while(it.hasNext()) {

                Map.Entry<String, JsonNode> current = it.next();

                String childName;

                if(name == null) {

                    childName = new StringBuilder(current.getKey()).toString();

                } else {

                    childName = new StringBuilder(name).append(PATH_OPEN).append(current.getKey()).append(PATH_CLOSE).toString();
                }

                fields.addAll(getFields(childName, current.getValue(), codec));
            }

        } else {

            fields.add(new AbstractMap.SimpleEntry<>(name, node.traverse(codec).readValueAs(String.class)));
        }

        return fields;
    }

    protected JsonNode getChild(JsonNode parent, Map.Entry<String, String> field, JsonNodeFactory factory) {

        Map.Entry<String, String[]> nodes = StringUtils.substringsBetween(field.getKey(), PATH_OPEN, PATH_CLOSE);

        if(nodes.getValue().length > 0) {

            if(parent instanceof ObjectNode tree) {

                String fieldName = nodes.getKey();

                JsonNode fieldNode = tree.get(fieldName);

                if(fieldNode == null) {

                    fieldNode = new ObjectNode(factory);
                }

                StringBuilder sb = new StringBuilder(nodes.getValue()[0]);

                for(int i = 1; i < nodes.getValue().length; i++) {

                    sb.append(PATH_OPEN).append(nodes.getValue()[i]).append(PATH_CLOSE);
                }

                return tree.set(fieldName, getChild(fieldNode, new AbstractMap.SimpleEntry<>(sb.toString(), field.getValue()), factory));
            }
        }

        if(parent instanceof ObjectNode tree) {

            ValueNode fieldNode = (ValueNode) tree.get(field.getKey());

            if(fieldNode == null) {

                fieldNode = new TextNode(field.getValue());
            }

            return tree.set(field.getKey(), fieldNode);
        }

        return null;
    }
}
