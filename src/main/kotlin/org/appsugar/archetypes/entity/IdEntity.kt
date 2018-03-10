package org.appsugar.archetypes.entity

import java.io.Serializable
import java.time.LocalDateTime
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class IdEntity(@get:Id
                        @get:GeneratedValue(strategy = GenerationType.IDENTITY)
                        var id: Long = 0L, var createdAt: LocalDateTime = LocalDateTime.now(),
                        var updatedAt: LocalDateTime = LocalDateTime.now()) : Serializable