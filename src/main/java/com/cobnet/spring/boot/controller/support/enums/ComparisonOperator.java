package com.cobnet.spring.boot.controller.support.enums;

import com.cobnet.interfaces.spring.controller.ExpressionOperator;
import org.hibernate.TypeMismatchException;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import java.util.Arrays;

public enum ComparisonOperator implements ExpressionOperator {

    NONE(""),
    IS_NOT_NULL("{1} is not null"),
    IS_NULL("{1} is null"),
    IS_NOT_BETWEEN("{0} not between {1} and {2}"),
    IS_BETWEEN("{0} between {1} and {2}"),
    IS_LESS_THAN("{0} < {1}"),
    IS_LESS_THAN_EQUAL("{0} <= {1}"),
    IS_GREATER_THAN("{0} > {1}"),
    IS_GREATER_THAN_EQUAL("{0} >= {1}"),
    IS_BEFORE("{0} < {1}"),
    IS_AFTER("{0} > {1}"),
    IS_NOT_LIKE("{0} not like {1}"),
    IS_NOT_LIKE_IGNORE_CASE("upper({0}) not like upper({1})"),
    IS_LIKE("upper({0}) like upper({1})"),
    IS_LIKE_IGNORE_CASE("{0} like {1}"),
    IS_NOT_STARTING_WITH("{0} not like concat({1}, '%')"),
    IS_STARTING_WITH("{0} like concat({1}, '%')"),
    IS_NOT_STARTING_WITH_IGNORE_CASE("upper({0}) not like concat(upper({1}), '%')"),
    IS_STARTING_WITH_IGNORE_CASE("upper({0}) like concat(upper({1}), '%')"),
    IS_NOT_ENDING_WITH("{0} not like concat('%', {1})"),
    IS_ENDING_WITH("{0} like concat('%', {1})"),
    IS_NOT_ENDING_WITH_IGNORE_CASE("upper({0}) not like concat('%', upper({1}))"),
    IS_ENDING_WITH_IGNORE_CASE("upper({0}) like concat('%', upper({1}))"),
    IS_NOT_CONTAINING("{0} not like concat('%', {1}, '%')"),
    IS_NOT_CONTAINING_IGNORE_CASE("upper({0}) not like concat('%', upper({1}), '%')"),
    IS_CONTAINING("{0} like concat('%', {1}, '%')"),
    IS_CONTAINING_IGNORE_CASE("upper({0}) like concat('%', upper({1}), '%')"),
    IS_NOT_IN("{0} not in {1}"),
    IS_IN("{0} in {1}"),
    IS_TRUE("{1} = true"),
    IS_FALSE("{1} = false"),
    IS_NOT("{0} <> {1}"),
    IS("{0} = {1}");

    private String expression;

    ComparisonOperator(String expression) {

        this.expression = expression;
    }

    @Override
    public Predicate resolve(CriteriaBuilder builder, Expression<?>... expressions) {
        return switch (this) {

            case NONE -> null;
            case IS_NOT_NULL -> ComparisonOperator.IS_NOT.resolve(builder, ComparisonOperator.IS_NULL.resolve(builder, expressions));
            case IS_NULL -> builder.isNull(expressions[1]);
            case IS_NOT_BETWEEN -> ComparisonOperator.IS_NOT.resolve(builder, ComparisonOperator.IS_BETWEEN.resolve(builder, expressions));
            case IS_BETWEEN -> builder.between(this.convert(expressions[0]), this.convert(expressions[1]), this.convert(expressions[2]));
            case IS_LESS_THAN, IS_BEFORE -> builder.lessThan(this.convert(expressions[0]), this.convert(expressions[1]));
            case IS_LESS_THAN_EQUAL -> builder.lessThanOrEqualTo(this.convert(expressions[0]), this.convert(expressions[1]));
            case IS_GREATER_THAN, IS_AFTER -> builder.greaterThan(this.convert(expressions[0]), this.convert(expressions[1]));
            case IS_GREATER_THAN_EQUAL -> builder.greaterThanOrEqualTo(this.convert(expressions[0]), this.convert(expressions[1]));
            case IS_NOT_LIKE -> ComparisonOperator.IS_NOT.resolve(builder, ComparisonOperator.IS_LIKE.resolve(builder, expressions));
            case IS_NOT_LIKE_IGNORE_CASE -> ComparisonOperator.IS_NOT.resolve(builder, ComparisonOperator.IS_LIKE_IGNORE_CASE.resolve(builder, expressions));
            case IS_LIKE -> builder.like(this.convert(String.class, expressions[0]), this.convert(String.class, expressions[1]));
            case IS_LIKE_IGNORE_CASE -> ComparisonOperator.IS_LIKE.resolve(builder, Arrays.stream(expressions).map(expression -> this.convert(String.class, expression)).toArray(Expression[]::new));
            case IS_NOT_STARTING_WITH -> ComparisonOperator.IS_NOT.resolve(builder, ComparisonOperator.IS_STARTING_WITH.resolve(builder, expressions));
            case IS_NOT_STARTING_WITH_IGNORE_CASE -> ComparisonOperator.IS_NOT.resolve(builder, ComparisonOperator.IS_STARTING_WITH_IGNORE_CASE.resolve(builder, expressions));
            case IS_STARTING_WITH -> ComparisonOperator.IS_LIKE.resolve(builder, expressions[0], builder.concat(this.convert(String.class, expressions[1]), "%"));
            case IS_STARTING_WITH_IGNORE_CASE -> ComparisonOperator.IS_STARTING_WITH.resolve(builder, Arrays.stream(expressions).map(expression -> this.convert(String.class, expression)).toArray(Expression[]::new));
            case IS_NOT_ENDING_WITH -> ComparisonOperator.IS_NOT.resolve(builder, ComparisonOperator.IS_ENDING_WITH.resolve(builder, expressions));
            case IS_NOT_ENDING_WITH_IGNORE_CASE -> ComparisonOperator.IS_NOT.resolve(builder, ComparisonOperator.IS_ENDING_WITH_IGNORE_CASE.resolve(builder, expressions));
            case IS_ENDING_WITH -> ComparisonOperator.IS_LIKE.resolve(builder, expressions[0], builder.concat("%", this.convert(String.class, expressions[1])));
            case IS_ENDING_WITH_IGNORE_CASE -> ComparisonOperator.IS_ENDING_WITH.resolve(builder, Arrays.stream(expressions).map(expression -> builder.upper(this.convert(String.class, expression))).toArray(Expression[]::new));
            case IS_NOT_CONTAINING -> ComparisonOperator.IS_NOT.resolve(builder, ComparisonOperator.IS_CONTAINING.resolve(builder, expressions));
            case IS_NOT_CONTAINING_IGNORE_CASE -> ComparisonOperator.IS_NOT.resolve(builder, ComparisonOperator.IS_CONTAINING_IGNORE_CASE.resolve(builder, expressions));
            case IS_CONTAINING -> ComparisonOperator.IS_LIKE.resolve(builder, expressions[0], builder.concat("%", builder.concat(this.convert(String.class, expressions[1]), "%")));
            case IS_CONTAINING_IGNORE_CASE -> ComparisonOperator.IS_CONTAINING.resolve(builder, Arrays.stream(expressions).map(expression -> builder.upper(this.convert(String.class, expression))).toArray(Expression[]::new));
            case IS_NOT_IN -> ComparisonOperator.IS_NOT.resolve(builder, ComparisonOperator.IS_IN.resolve(builder, expressions));
            case IS_IN -> expressions[0].in(Arrays.copyOfRange(expressions, 1, expressions.length));
            case IS_TRUE -> builder.isTrue(this.convert(Boolean.class, expressions[1]));
            case IS_FALSE -> builder.isFalse(this.convert(Boolean.class, expressions[1]));
            case IS_NOT -> builder.not(ComparisonOperator.IS.resolve(builder, expressions));
            case IS -> builder.equal(expressions[0], expressions[1]);
        };
    }

    private <T extends Comparable<? super T>> Expression<T> convert(Expression<?> expression) {

        Expression<T> result = (Expression<T>) expression;

        if(result == null) {

            throw new TypeMismatchException(String.format("Expression is not comparable!"));
        }

        return result;
    }

    private <T> Expression<T> convert(Class<T> type, Expression<?> expression) {

        Expression<T> result = (Expression<T>) expression;

        if(result == null) {

            throw new TypeMismatchException(String.format("Expression is not %s!", type));
        }

        return result;
    }
}
