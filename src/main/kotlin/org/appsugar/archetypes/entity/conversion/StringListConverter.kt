package org.appsugar.archetypes.entity.conversion

import javax.persistence.AttributeConverter

class StringListConverter : AttributeConverter<List<String>, String> {
    override fun convertToEntityAttribute(dbData: String) = println(dbData).let {
        dbData.split(",").toMutableList()
    }

    override fun convertToDatabaseColumn(attribute: List<String>) = println(attribute).let { attribute.joinToString { "," } }

}