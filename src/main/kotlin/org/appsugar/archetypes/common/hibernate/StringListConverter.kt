package org.appsugar.archetypes.common.hibernate

import javax.persistence.AttributeConverter

/**
 * 类型转换器
 */
class StringListConverter : AttributeConverter<List<String>, String> {
    override fun convertToDatabaseColumn(attribute: List<String>): String {
        return attribute.joinToString(",")
    }

    override fun convertToEntityAttribute(dbData: String): List<String> {
        return dbData.split(",").toMutableList()
    }
}