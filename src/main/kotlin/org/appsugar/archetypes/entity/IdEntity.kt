package org.appsugar.archetypes.entity

import java.io.Serializable
import java.util.*

/**
 * 实体类总接口
 */
interface IdEntityable<T : Serializable> : Serializable {
    var id: T
    var createdAt: Date
    var updatedAt: Date
}
