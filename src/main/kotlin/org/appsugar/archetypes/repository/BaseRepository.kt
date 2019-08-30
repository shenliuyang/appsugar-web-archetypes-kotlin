package org.appsugar.archetypes.repository

import com.querydsl.core.types.Predicate
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Async
import java.io.Serializable
import java.util.concurrent.CompletableFuture


@NoRepositoryBean
interface BaseRepository<T, ID : Serializable> : JpaRepository<T, ID>, QuerydslPredicateExecutor<T> {

    @Async
    @JvmDefault
    fun findByIdOrNullAsync(id: ID) = CompletableFuture.completedFuture(findByIdOrNull(id))

    @Async
    @JvmDefault
    fun findAllAsync(pageable: Pageable) = CompletableFuture.completedFuture(findAll(pageable))

    @Async
    @JvmDefault
    fun <S : T> saveAsync(entity: S) = CompletableFuture.completedFuture(save(entity))

    @Async
    @JvmDefault
    fun findAllAsync(predicate: Predicate, pageable: Pageable) = CompletableFuture.completedFuture(findAll(predicate, pageable))
}