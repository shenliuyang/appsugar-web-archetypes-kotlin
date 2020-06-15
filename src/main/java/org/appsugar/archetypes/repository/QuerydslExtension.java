package org.appsugar.archetypes.repository;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface QuerydslExtension<T> extends QuerydslPredicateExecutor<T> {

    default Predicate ops(String ops, Expression<?> left, Object value) {
        return Expressions.predicate(Ops.valueOf(ops), left, Expressions.constant(value));
    }
}
