package com.cobnet.common;

import com.cobnet.spring.boot.core.ProjectBeanHolder;
import org.hibernate.internal.SessionImpl;
import org.hibernate.metamodel.model.domain.internal.*;
import org.hibernate.metamodel.model.domain.spi.EntityTypeDescriptor;
import org.hibernate.query.Query;
import org.hibernate.tuple.entity.EntityTuplizer;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.MapAttribute;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

public class JPAUtils {

    public static boolean isJPAEntity(Class<?> type) {

        return type.isAnnotationPresent(Entity.class);
    }

    public static Class<?> getEntityType(EntityManager manager, Class<?> type, String property) {

        Attribute<?, ?> entity = JPAUtils.getAttribute(manager, type, property);

        if(entity == null) {

            return null;
        }

        return JPAUtils.getEntityType(entity);
    }

    public static Class<?> getEntityType(Attribute<?, ?> attribute) {

        if(attribute instanceof AbstractPluralAttribute<?, ?, ?> plural) {

            return plural.getElementType().getJavaType();
        }

        return attribute.getJavaType();
    }

    public static Class<?> getEntityType(Type type) {

        if(type instanceof Class<?> clazz) {

            if(JPAUtils.isJPAEntity(clazz)) {

                return clazz;
            }

            if(clazz.isArray()) {

                return JPAUtils.getEntityType(clazz.getComponentType());
            }
        }

        if(type instanceof ParameterizedType parameterized) {

            if(parameterized.getRawType() instanceof Class<?> clazz) {

                if(Iterable.class.isAssignableFrom(clazz) || Optional.class.isAssignableFrom(clazz)) {

                    for (Type subType : parameterized.getActualTypeArguments()) {

                        Class<?> result = JPAUtils.getEntityType(subType);

                        if (result != null) {

                            return result;
                        }
                    }
                }
            }
        }

        return null;
    }

    public static EntityType<?> getMetadata(EntityManager manager, Class<?> type) {

        return manager.getMetamodel().entity(type);
    }

    public static String getId(EntityManager manager, Class<?> type) {

        EntityType<?> metadata = JPAUtils.getMetadata(manager, type);

        return metadata.getId(metadata.getIdType().getJavaType()).getName();
    }

    public static javax.persistence.metamodel.Type<?> getIdType(EntityManager manager, Class<?> type) {

        EntityType<?> metadata = JPAUtils.getMetadata(manager, type);

        return metadata.getIdType();
    }

    public static Class<?> getAttributeType(EntityManager manager, Class<?> type, String attribute) {

        Attribute<?,?> attr = JPAUtils.getAttribute(manager, type, attribute);

        if(attr == null) {

            return null;
        }

        return attr.getJavaType();
    }

    private static <T> Attribute<?, ?> resolveAttribute(T obj, String[] nodes) {

        if (obj instanceof EntityTypeImpl entity) {

            if(nodes.length > 1) {

                return JPAUtils.resolveAttribute(entity.getAttribute(nodes[0]), Arrays.copyOfRange(nodes,1, nodes.length));
            }

            return entity.getAttribute(nodes[0]);
        }

        if(obj instanceof EmbeddableTypeImpl embeddable) {

            if(nodes.length > 1) {

                return JPAUtils.resolveAttribute(embeddable.getAttribute(nodes[0]), Arrays.copyOfRange(nodes,1, nodes.length));
            }

            return embeddable.getAttribute(nodes[0]);
        }

        if(obj instanceof AbstractPluralAttribute<?, ?, ?> attribute) {

            if(nodes.length > 0) {

                return JPAUtils.resolveAttribute(attribute.getElementType(), nodes);
            }

            return attribute;
        }

        if(obj instanceof SingularAttributeImpl singular) {

            if(nodes.length > 0) {

                return JPAUtils.resolveAttribute(singular.getType(), nodes);
            }

            return singular;
        }

        return null;
    }


    public static Attribute<?, ?> getAttribute(EntityManager manager, Class<?> type, String attribute) {

        String[] nodes = attribute.split("\\.");

        if(nodes.length > 1) {

            return JPAUtils.resolveAttribute(JPAUtils.getAttribute(manager, type, nodes[0]), Arrays.copyOfRange(nodes, 1, nodes.length));
        }

        return JPAUtils.getMetadata(manager, type).getAttribute(attribute);
    }

    public static Object setProperty(EntityManager manager, EntityTuplizer tuplizer, Object instance, String name, Object value) {

        if(instance == null) {

            return null;
        }

        SessionImpl session = (SessionImpl)manager.getDelegate();

        EntityType<?> metadata = session.getMetamodel().entity(instance.getClass());

        if(metadata.getId(metadata.getIdType().getJavaType()).getName().equals(name)) {

            if(value instanceof Serializable id) {

                tuplizer.setIdentifier(instance, id, session);
            }

        } else {

            tuplizer.setPropertyValue(instance, name, value);
        }

        return instance;
    }

    public static Object getProperty(EntityManager manager, Object instance, String name) {

        if(instance == null) {

            return null;
        }

        SessionImpl session = (SessionImpl)manager.getDelegate();

        EntityTypeDescriptor<?> descriptor = session.getMetamodel().entity(instance.getClass());

        if(descriptor.getId(descriptor.getIdType().getJavaType()).getName().equals(name)) {

            return session.getIdentifier(instance);

        }

        return session.getEntityPersister(descriptor.getName(), instance).getPropertyValue(instance, descriptor.getAttribute(name).getName());
    }

    public static <T extends javax.persistence.Query> String getQueryString(EntityManager manager, T query) {

        return query.unwrap(Query.class).getQueryString();
    }
}
