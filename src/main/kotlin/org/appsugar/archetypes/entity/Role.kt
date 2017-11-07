package org.appsugar.archetypes.entity

import org.appsugar.bean.convert.StringListConverter
import org.appsugar.bean.entity.LongIdEntity
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Entity

@Entity
data class Role(
        var name:String="",
        @get:Column(length = 2500)
        @get:Convert(converter = StringListConverter::class)
        var permissions:List<String>?=null
):LongIdEntity()