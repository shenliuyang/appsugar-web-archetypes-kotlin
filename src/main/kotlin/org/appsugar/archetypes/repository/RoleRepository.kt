package org.appsugar.archetypes.repository

import org.appsugar.archetypes.entity.Role
import org.springframework.data.jpa.repository.JpaRepository

interface  RoleRepository:JpaRepository<Role,Long> {
    fun findByIdIn(ids: List<Long>):List<Role>
}