package org.appsugar.archetypes.repository

import org.appsugar.archetypes.entity.Role
import org.springframework.scheduling.annotation.Async
import java.util.concurrent.CompletableFuture

interface RoleRepository : BaseRepository<Role, Long> {

    @Async
    fun findByIdIn(ids: List<Long>): CompletableFuture<List<Role>>
}