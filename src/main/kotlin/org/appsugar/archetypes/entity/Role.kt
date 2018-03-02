package org.appsugar.archetypes.entity

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.Column
import javax.persistence.Entity


@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@DynamicUpdate
data class Role(
        var name: String = "",
        @get:Column(columnDefinition = "TEXT")
        var permissions: String = ""
) : IdEntity()