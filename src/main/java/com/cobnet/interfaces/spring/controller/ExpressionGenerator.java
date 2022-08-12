package com.cobnet.interfaces.spring.controller;

import com.cobnet.spring.boot.controller.support.enums.ConjunctiveOperator;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;

public interface ExpressionGenerator<T> {

    Expression<?> generate(EntityManager manager, CriteriaBuilder builder, T arg);

    ExpressionOperator getOperator();

    default <E extends Expression<?>> Predicate getPredicate(EntityManager manager, CriteriaBuilder builder, E arg, ExpressionGenerator<E>... children) {

        if(this.getOperator() instanceof ConjunctiveOperator conjunctive && conjunctive != ConjunctiveOperator.OR && conjunctive != ConjunctiveOperator.AND && conjunctive != ConjunctiveOperator.NONE) {

            return this.getOperator().resolve(builder, arg);
        }

        final E root = arg;

        Expression<?>[] expressions = Arrays.stream(children).map(child -> child.generate(manager, builder, root)).toArray(Expression[]::new);

        Predicate predicate = null;

        for(int i = 0; i < expressions.length; i++) {

            if(predicate == null) {

                predicate = (Predicate) expressions[i];

                continue;
            }

            predicate = children[i - 1].getOperator().resolve(builder, predicate, expressions[i]);
        }

        return predicate;
    }
}
