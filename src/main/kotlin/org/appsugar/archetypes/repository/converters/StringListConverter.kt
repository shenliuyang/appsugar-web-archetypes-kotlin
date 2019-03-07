package org.appsugar.archetypes.repository.converters

import javax.persistence.AttributeConverter
import javax.persistence.Converter

/**
 * 类型转换器
 */
@Converter(autoApply = true)
class StringListConverter : AttributeConverter<List<String>, String> {
    override fun convertToDatabaseColumn(attribute: List<String>) = attribute.joinToString(",")

    override fun convertToEntityAttribute(dbData: String?) = if (dbData.isNullOrBlank()) mutableListOf() else dbData.split(",").toMutableList()
}