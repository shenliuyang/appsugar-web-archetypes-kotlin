package org.appsugar.archetypes.repository;

import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.repository
 * @className BaseJpaRepositoryCustom
 * @date 2021-12-09  14:38
 */
public abstract class BaseJpaRepositoryCustom <T extends EntityPathBase<?>>{

    protected T root;

    @Autowired
    protected EntityManager em;

    public BaseJpaRepositoryCustom(T root) {
        this.root = root;
    }

    protected <Result> JPAQuery<Result> createQuery(){
        return new JPAQuery<>(em);
    }
}
