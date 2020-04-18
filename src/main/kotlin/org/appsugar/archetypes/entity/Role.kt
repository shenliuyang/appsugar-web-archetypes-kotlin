package org.appsugar.archetypes.entity

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.DynamicUpdate
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity


@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@DynamicUpdate
class Role(
        override var id: Long = 0,
        override var createdAt: Date = Date(),
        override var updatedAt: Date = Date(),
        var name: String = "",
        @get:Column(columnDefinition = "TEXT")
        var permissions: MutableList<String> = mutableListOf()
) : IdEntityable()