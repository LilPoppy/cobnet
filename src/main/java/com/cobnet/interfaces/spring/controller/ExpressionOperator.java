package com.cobnet.interfaces.spring.controller;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public interface ExpressionOperator {

    Predicate resolve(CriteriaBuilder builder, Expression<?>... expressions);
}
