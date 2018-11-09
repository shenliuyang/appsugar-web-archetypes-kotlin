package org.appsugar.archetypes.repository.hibernate

import javax.persistence.AttributeConverter

/**
 * 类型转换器
 */
class StringListConverter : AttributeConverter<List<String>, String> {
    override fun convertToDatabaseColumn(attribute: List<String>) = attribute.joinToString(",")

    override fun convertToEntityAttribute(dbData: String) = if (dbData.isBlank()) mutableListOf() else dbData.split(",").toMutableList()
}