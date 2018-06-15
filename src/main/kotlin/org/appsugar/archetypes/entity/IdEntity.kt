package org.appsugar.archetypes.entity

import java.io.Serializable
import java.util.*
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class IdEntity(@get:Id
                        @get:GeneratedValue(strategy = GenerationType.IDENTITY)
                        open var id: Long = 0L,
                        open var createdAt: Date = Date(),
                        open var updatedAt: Date = Date()) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is IdEntity) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    abstract override fun toString(): String
}