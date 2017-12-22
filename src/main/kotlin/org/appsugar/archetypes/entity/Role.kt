package org.appsugar.archetypes.entity

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.DynamicUpdate
import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.*


@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@DynamicUpdate
data class Role(
        @get:Id
        @get:GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Long = Long.MIN_VALUE,
        var name: String = "",
        @get:Column(columnDefinition = "TEXT")
        var permissions: String = "",
        var createdAt: LocalDateTime = LocalDateTime.now(),
        var updatedAt: LocalDateTime = LocalDateTime.now()
) : Serializable