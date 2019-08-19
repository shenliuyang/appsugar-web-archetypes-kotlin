package org.appsugar.archetypes.web.controller.system

import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.entity.Role
import org.appsugar.archetypes.repository.RoleRepository
import org.appsugar.archetypes.web.controller.BaseController
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/system/role")
class RoleController(val repository: RoleRepository) : BaseController<Role>() {


    @PreAuthorize("hasAuthority('role:view')")
    @RequestMapping(value = ["list", ""])
    fun list(pageable: PageRequest) = mono {
        val page = repository.findAll(pageable)
        Response(page)
    }


    @PreAuthorize("hasAuthority('role:view')")
    @RequestMapping("detail")
    fun form(@ModelAttribute("entity") role: Mono<Role>) = role.map { Response(it) }


    @PreAuthorize("hasAuthority('role:edit')")
    @RequestMapping("save")
    fun save(@ModelAttribute("entity") role: Mono<Role>, roleData: RoleData) = mono {
        val r = role.awaitFirst()!!
        val permissions = roleData.permissions
        logger.info("prepare to save role {}, new permissions is {} ", r, permissions)
        r.permissions = permissions
        repository.saveAsync(r).await()
        Response.SUCCESS
    }

}

data class RoleData(var permissions: MutableList<String> = mutableListOf())