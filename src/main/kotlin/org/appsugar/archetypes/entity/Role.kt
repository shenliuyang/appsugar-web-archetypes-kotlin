package org.appsugar.archetypes.entity

import org.appsugar.bean.convert.StringListConverter
import org.appsugar.bean.entity.LongIdEntity
import java.util.*
import javax.persistence.*

@Entity
data class Role(
        @get:Id
        @get:GeneratedValue(strategy = GenerationType.IDENTITY)
        @get:Column(insertable = false)
        var id:Long = Long.MIN_VALUE,
        var name:String="",
        @get:Column(columnDefinition="TEXT")
        @get:Convert(converter = StringListConverter::class)
        var permissions:List<String> = mutableListOf(),
        var createdAt: Date = Date(),
        var updatedAt: Date = Date()
)