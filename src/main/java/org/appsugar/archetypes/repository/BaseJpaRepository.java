package org.appsugar.archetypes.repository;

import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraph;
import com.cosium.spring.data.jpa.entity.graph.domain.EntityGraphUtils;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphQuerydslPredicateExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;


@NoRepositoryBean
public interface BaseJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, EntityGraphQuerydslPredicateExecutor {

    default EntityGraph fromPaths(String... attributePaths) {
        return EntityGraphUtils.fromAttributePaths(attributePaths);
    }
}
