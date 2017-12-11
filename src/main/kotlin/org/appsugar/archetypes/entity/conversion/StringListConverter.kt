package org.appsugar.archetypes.entity.conversion

import javax.persistence.AttributeConverter

class StringListConverter : AttributeConverter<List<String>, String> {
    override fun convertToEntityAttribute(dbData: String?) = when (dbData) {
        null -> mutableListOf()
        else -> dbData.split(",").toMutableList()
    }

    override fun convertToDatabaseColumn(attribute: List<String>?) = when (attribute) {
        null -> null
        else -> attribute.joinToString { "," }
    }

}