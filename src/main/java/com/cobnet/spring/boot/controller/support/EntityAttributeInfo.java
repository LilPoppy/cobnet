package com.cobnet.spring.boot.controller.support;

import com.cobnet.common.JPAUtils;
import com.cobnet.interfaces.spring.controller.ExpressionGenerator;
import com.cobnet.interfaces.spring.controller.annotations.EntityAttribute;
import com.cobnet.spring.boot.controller.support.enums.ComparisonOperator;
import com.cobnet.spring.boot.controller.support.enums.ConjunctiveOperator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.metamodel.model.domain.internal.AbstractPluralAttribute;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.*;
import java.util.Arrays;
import java.util.Collection;

@Getter
@Setter
public class EntityAttributeInfo implements ExpressionGenerator<Root<?>> {

    private EntityAttributeConditionInfo parent;

    private Class<?> type;

    private String attribute;

    private ConjunctiveOperator operator;

    private EntityKeyPredicateInfo[] predicates;

    public EntityAttributeInfo(EntityAttributeConditionInfo parent, Class<?> type, String attribute, ConjunctiveOperator operator, EntityKeyPredicateInfo[] predicates) {
        this.parent = parent;
        this.type = type;
        this.attribute = attribute;
        this.operator = operator;
        this.predicates = Arrays.stream(predicates).map(condition -> {

            if(condition.getParent() != this) {

                condition.setParent(this);
            }

            return condition;

        }).toArray(EntityKeyPredicateInfo[]::new);
    }

    public EntityAttributeInfo(EntityAttributeConditionInfo parent, EntityAttribute attribute) {

        this(parent,null, attribute.attribute(), attribute.operator(), Arrays.stream(attribute.keys()).map(condition -> new EntityKeyPredicateInfo(null, condition)).toArray(EntityKeyPredicateInfo[]::new));
    }

    public void setPredicates(EntityKeyPredicateInfo... predicates) {
        this.predicates = predicates;
    }

    public boolean isKeyNullable(String key) {

        return Arrays.stream(this.predicates).anyMatch(condition -> condition.getComparison() == ComparisonOperator.IS_NULL && Arrays.stream(condition.getKeys()).anyMatch(info -> info.getKey().equals(key)));
    }

    public Object getKeyValue(String key) {

        return Arrays.stream(this.predicates).map(condition -> Arrays.stream(condition.getKeys()).filter(child -> child.getKey().equals(key) && child.getValue() != null).map(EntityKeyInfo::getValue).toList())
                .flatMap(Collection::stream).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "EntityAttributeInfo{" +
                "type=" + type +
                ", attribute='" + attribute + '\'' +
                ", predicates=" + Arrays.toString(predicates) +
                ", operator=" + operator +
                '}';
    }

    private Join<?, ?> resolveJoin(EntityManager manager, Path<?> path, String node) {

        if(path instanceof Root<?> root) {

            Attribute<?, ?> attribute = JPAUtils.getAttribute(manager, root.getJavaType(), node);

            if (attribute == null) {

                return null;
            }

            if (attribute instanceof AbstractPluralAttribute<?, ?, ?> plural) {

                if (JPAUtils.isJPAEntity(plural.getElementType().getJavaType())) {

                    if (plural instanceof CollectionAttribute collection) {

                        return (Join<?, ?>) root.join(collection).alias(collection.getName());
                    }

                    if (plural instanceof SetAttribute set) {

                        return (Join<?, ?>) root.join(set).alias(set.getName());
                    }

                    if (plural instanceof ListAttribute list) {

                        return (Join<?, ?>) root.join(list).alias(list.getName());
                    }

                    if (plural instanceof MapAttribute map) {

                        return (Join<?, ?>) root.join(map).alias(map.getName());
                    }
                }
            }

            if (attribute instanceof SingularAttribute singular) {

                if (JPAUtils.isJPAEntity(singular.getJavaType())) {

                    return (Join<?, ?>) root.join(singular).alias(singular.getName());
                }
            }
        }

        return null;
    }

    public Path<?> getPath(EntityManager manager, Root<?> root) {

        String[] nodes = this.getAttribute().split("\\.");

        Path<?> path = null;

        for(int i = 0 ; i < nodes.length; i++) {

            if(i == 0) {

                path = resolveJoin(manager, root, nodes[i]);

                if(path != null) {

                    continue;
                }

                path = root.get(nodes[0]);

                continue;
            }

            Join<?, ?> join = resolveJoin(manager, path, nodes[i]);

            if(join != null) {

                path = join;

                continue;
            }

            path = path.get(nodes[i]);
        }

        return path;
    }

    @Override
    public Expression<?> generate(EntityManager manager, CriteriaBuilder builder, Root<?> root) {

        return getPredicate(manager, builder, getPath(manager, root), this.getPredicates());
    }

    public static final class Builder {

        private EntityAttributeConditionInfo parent;

        private Class<?> type;

        private String attribute;

        private ConjunctiveOperator operator;

        private EntityKeyPredicateInfo[] predicates;

        public Builder setParent(EntityAttributeConditionInfo parent) {
            this.parent = parent;
            return this;
        }

        public Builder setType(Class<?> type) {
            this.type = type;
            return this;
        }

        public Builder setAttribute(String attribute) {
            this.attribute = attribute;
            return this;
        }

        public Builder setOperator(ConjunctiveOperator operator) {
            this.operator = operator;
            return this;
        }

        public Builder setPredicates(EntityKeyPredicateInfo... conditions) {
            this.predicates = conditions;
            return this;
        }

        public EntityAttributeInfo build() {

            return new EntityAttributeInfo(this.parent, this.type, this.attribute, this.operator, this.predicates);
        }
    }
}
