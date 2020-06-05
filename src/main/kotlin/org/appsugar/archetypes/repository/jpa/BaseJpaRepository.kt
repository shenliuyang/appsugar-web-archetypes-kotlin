package org.appsugar.archetypes.repository.jpa

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.io.Serializable


@NoRepositoryBean
interface BaseJpaRepository<T, ID : Serializable> : JpaRepository<T, ID>, QuerydslPredicateExecutor<T> {
}