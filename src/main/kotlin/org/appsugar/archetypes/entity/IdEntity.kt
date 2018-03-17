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
                        open var id: Long = 0L,
                        open var createdAt: LocalDateTime = LocalDateTime.now(),
                        open var updatedAt: LocalDateTime = LocalDateTime.now()) : Serializable