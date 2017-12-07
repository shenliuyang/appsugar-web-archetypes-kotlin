package org.appsugar.archetypes.entity

import org.appsugar.bean.convert.StringListConverter
import org.appsugar.bean.entity.LongIdEntity
import org.hibernate.annotations.DynamicUpdate
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
@DynamicUpdate
data class Role(
        @get:Id
        @get:GeneratedValue(strategy = GenerationType.IDENTITY)
        var id:Long = Long.MIN_VALUE,
        var name:String="",
        @get:Column(columnDefinition="TEXT")
        @get:Convert(converter = StringListConverter::class)
        var permissions:MutableList<String> = mutableListOf(),
        var createdAt:Date = Date(),
        var updatedAt:Date = Date()
)