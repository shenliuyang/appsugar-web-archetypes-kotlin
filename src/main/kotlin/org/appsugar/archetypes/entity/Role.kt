package org.appsugar.archetypes.entity

import org.appsugar.archetypes.data.redis.ClassMapping
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.DynamicUpdate
import java.util.*
import javax.persistence.*


@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@DynamicUpdate
@ClassMapping(101)
data class Role(
        @get:Id
        @get:GeneratedValue(strategy = GenerationType.IDENTITY)
        override var id: Long = 0L,
        override var createdAt: Date = Date(),
        override var updatedAt: Date = Date(),
        var name: String = "",
        @get:Column(columnDefinition = "TEXT")
        var permissions: MutableList<String> = mutableListOf()
) : IdEntityable<Long>