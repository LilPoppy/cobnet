package com.cobnet.spring.boot.controller.support;

import com.cobnet.interfaces.spring.controller.ExpressionGenerator;
import com.cobnet.interfaces.spring.controller.annotations.EntityAttributeCondition;
import com.cobnet.spring.boot.controller.support.enums.ConjunctiveOperator;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import java.util.Arrays;

@Getter
@Setter
public class EntityAttributeConditionInfo implements ExpressionGenerator<Root<?>> {

    private EntityVariableInfo parent;

    private EntityAttributeInfo[] attributes;

    private ConjunctiveOperator operator;

    public EntityAttributeConditionInfo(EntityVariableInfo parent, EntityAttributeInfo[] attributes, ConjunctiveOperator operator) {

        this.parent = parent;

        this.attributes = Arrays.stream(attributes).map(attribute -> {

            if(attribute.getParent() != this) {

                attribute.setParent(this);
            }

            return attribute;

        }).toArray(EntityAttributeInfo[]::new);

        this.operator = operator;
    }

    public EntityAttributeConditionInfo(EntityVariableInfo parent, EntityAttributeCondition group) {

        this(parent, Arrays.stream(group.attributes()).map(attribute -> new EntityAttributeInfo(null, attribute)).toArray(EntityAttributeInfo[]::new), group.operator());
    }

    public void setAttributes(EntityAttributeInfo... attributes) {
        this.attributes = attributes;
    }

    @Override
    public String toString() {
        return "EntityAttributeConditionInfo{" +
                "attributes=" + Arrays.toString(attributes) +
                ", operator=" + operator +
                '}';
    }

    @Override
    public Expression<?> generate(EntityManager manager, CriteriaBuilder builder, Root<?> root) {

        return this.getPredicate(manager, builder, root, this.getAttributes());
    }

    public static final class Builder {

        private EntityVariableInfo parent;

        private EntityAttributeInfo[] attributes;

        private ConjunctiveOperator operator;

        public Builder setParent(EntityVariableInfo parent) {
            this.parent = parent;
            return this;
        }

        public Builder setAttributes(EntityAttributeInfo... attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder setOperator(ConjunctiveOperator operator) {
            this.operator = operator;
            return this;
        }

        public EntityAttributeConditionInfo build() {

            return new EntityAttributeConditionInfo(parent, attributes, operator);
        }
    }
}
