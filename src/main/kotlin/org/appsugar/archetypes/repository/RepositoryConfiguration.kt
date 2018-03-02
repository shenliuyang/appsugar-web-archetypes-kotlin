package org.appsugar.archetypes.repository

import org.appsugar.archetypes.entity.IdEntity
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import java.time.LocalDateTime
import javax.persistence.EntityManager


class CustomSimpleJpaRepository<T, ID>(entityInformation: JpaEntityInformation<T, *>, entityManager: EntityManager) : SimpleJpaRepository<T, ID>(entityInformation, entityManager) {
    
    override fun <S : T> save(entity: S): S {
        print("save $entity")
        if (entity is IdEntity) {
            entity.updatedAt = LocalDateTime.now()
        }
        return super.save(entity)
    }
}