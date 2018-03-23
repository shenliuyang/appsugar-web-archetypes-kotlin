package org.appsugar.archetypes.entity

import org.appsugar.archetypes.common.hibernate.StringListConverter
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity


@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Entity
@DynamicUpdate
open class Role(
        open var name: String = "",
        @get:Convert(converter = StringListConverter::class)
        @get:Column(columnDefinition = "TEXT")
        open var permissions: MutableList<String> = mutableListOf()
) : IdEntity() {
    override fun toString(): String {
        return "Role( id='$id',name='$name', permissions='$permissions')"
    }
}