package com.cobnet.spring.boot.controller.support.enums;

import com.cobnet.interfaces.spring.controller.ExpressionOperator;
import org.hibernate.TypeMismatchException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;

public enum ConjunctiveOperator implements ExpressionOperator {

    NONE(""),
    IS_NOT_NULL("{0} is not null"),
    IS_NULL("{0} is null"),
    AND("{0} and {1}"),
    OR("{0} or {1}");

    private String expression;

    ConjunctiveOperator(String expression) {

        this.expression = expression;
    }


    @Override
    public Predicate resolve(CriteriaBuilder builder, Expression<?>... expressions) {

        return switch (this) {

            case NONE -> null;
            case IS_NOT_NULL -> builder.not(ConjunctiveOperator.IS_NULL.resolve(builder, expressions));
            case IS_NULL -> expressions[0].isNull();
            case AND -> builder.and(Arrays.stream(expressions).map(this::convert).toArray(Predicate[]::new));
            case OR -> builder.or(Arrays.stream(expressions).map(this::convert).toArray(Predicate[]::new));
        };
    }

    private <T> Predicate convert(Expression<?> expression) {

        Predicate result = (Predicate) expression;

        if(result == null) {

            throw new TypeMismatchException(String.format("Expression is not predicate!"));
        }

        return result;
    }
}
