package org.appsugar.archetypes.repository

import org.appsugar.archetypes.entity.IdEntity
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.EntityManager


class CustomSimpleJpaRepository<T, ID : Serializable>(entityInformation: JpaEntityInformation<T, ID>, entityManager: EntityManager) : QuerydslJpaRepository<T, ID>(entityInformation, entityManager) {

    override fun <S : T> save(entity: S): S {
        if (entity is IdEntity) {
            entity.updatedAt = LocalDateTime.now()
        }
        return super.save(entity)
    }
}