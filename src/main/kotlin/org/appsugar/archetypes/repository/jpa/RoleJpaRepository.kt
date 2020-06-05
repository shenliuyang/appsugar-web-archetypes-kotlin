package org.appsugar.archetypes.repository.jpa

import org.appsugar.archetypes.entity.Role

interface RoleJpaRepository : BaseJpaRepository<Role, Long> {
    fun findByIdIn(ids: List<Long>): List<Role>
}