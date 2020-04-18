package org.appsugar.archetypes.entity

import org.hibernate.annotations.GenericGenerator
import java.io.Serializable
import java.util.*
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

/**
 * 实体类总接口
 */
@MappedSuperclass
abstract class IdEntityable : Serializable {
    @get:Id
    @get:GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @get:GenericGenerator(name = "native", strategy = "native")
    abstract var id: Long
    abstract var createdAt: Date
    abstract var updatedAt: Date
}