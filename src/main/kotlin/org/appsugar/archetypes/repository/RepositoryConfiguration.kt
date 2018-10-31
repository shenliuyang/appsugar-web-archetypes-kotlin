package org.appsugar.archetypes.repository

import org.appsugar.archetypes.entity.IdEntity
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import java.io.Serializable
import java.util.*
import javax.persistence.EntityManager


class CustomSimpleJpaRepository<T, ID : Serializable>(entityInformation: JpaEntityInformation<T, ID>, entityManager: EntityManager) : SimpleJpaRepository<T, ID>(entityInformation, entityManager) {

    override fun <S : T> save(entity: S): S {
        if (entity is IdEntity) {
            entity.updatedAt = Date()
        }
        return super.save(entity)
    }
}