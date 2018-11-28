package org.appsugar.archetypes.entity

import java.io.Serializable
import java.util.*

interface IdEntityable<T : Serializable> : Serializable {
    var id: T
    var createdAt: Date
    var updatedAt: Date
}
