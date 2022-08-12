package com.cobnet.spring.boot.controller.support;

import com.cobnet.interfaces.spring.controller.ExpressionGenerator;
import com.cobnet.interfaces.spring.controller.annotations.EntityKey;
import com.cobnet.spring.boot.controller.support.enums.ComparisonOperator;
import com.cobnet.spring.boot.controller.support.enums.ConjunctiveOperator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import java.util.Arrays;

@Getter
@Setter
public class EntityKeyPredicateInfo implements ExpressionGenerator<Path<?>> {

    private EntityKeyInfo[] keys;

    private boolean reversed;

    private ComparisonOperator comparison;

    private ConjunctiveOperator operator;

    private EntityAttributeInfo parent;

    public EntityKeyPredicateInfo(EntityAttributeInfo parent, EntityKeyInfo[] keys, boolean reversed, ComparisonOperator comparison, ConjunctiveOperator operator) {

        this.parent = parent;

        this.keys = keys == null ? new EntityKeyInfo[0] : Arrays.stream(keys).map(key -> {

            if(key.getParent() != this) {

                key.setParent(this);
            }

            return key;

        }).toArray(EntityKeyInfo[]::new);

        this.reversed = reversed;
        this.comparison = comparison;
        this.operator = operator;
    }

    public EntityKeyPredicateInfo(EntityAttributeInfo parent, EntityKey key) {

        this(parent, Arrays.stream(key.keys()).map(text -> new EntityKeyInfo(null,null, text)).toArray(EntityKeyInfo[]::new), key.reversed(), key.operator(), key.conjunctive());
    }

    public void setKeys(EntityKeyInfo... keys) {
        this.keys = keys;
    }

    @Override
    public String toString() {
        return "EntityKeyPredicateInfo{" +
                "keys=" + Arrays.toString(keys) +
                ", reversed=" + reversed +
                ", operator=" + comparison +
                ", logical=" + operator +
                '}';
    }

    @Override
    public Expression<?> generate(EntityManager manager, CriteriaBuilder builder, Path<?> path) {

        Expression<?>[] expressions = Arrays.stream(this.getKeys()).map(key -> key.generate(manager, builder, path)).toArray(Expression[]::new);

        if(this.isReversed()) {

            expressions = ArrayUtils.add(expressions, path);

        } else {

            expressions = ArrayUtils.insert(0, expressions, path);
        }

        return this.getComparison().resolve(builder, expressions);
    }

    public static final class Builder {

        private EntityAttributeInfo parent;

        private EntityKeyInfo[] keys;

        private boolean reversed;

        private ComparisonOperator comparison;

        private ConjunctiveOperator operator;

        public Builder setParent(EntityAttributeInfo parent) {
            this.parent = parent;
            return this;
        }

        public Builder setKeys(EntityKeyInfo... keys) {
            this.keys = keys;
            return this;
        }

        public Builder setReversed(boolean reversed) {
            this.reversed = reversed;
            return this;
        }

        public Builder setComparison(ComparisonOperator comparison) {
            this.comparison = comparison;
            return this;
        }

        public Builder setOperator(ConjunctiveOperator operator) {
            this.operator = operator;
            return this;
        }

        public EntityKeyPredicateInfo build() {

            return new EntityKeyPredicateInfo(this.parent, this.keys, this.reversed, this.comparison, this.operator);
        }
    }
}
