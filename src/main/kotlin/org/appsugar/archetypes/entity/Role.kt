package org.appsugar.archetypes.entity

import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.Column
import javax.persistence.Entity


@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@DynamicUpdate
open class Role(
        open var name: String = "",
        @get:Column(columnDefinition = "TEXT")
        open var permissions: String = ""
) : IdEntity()