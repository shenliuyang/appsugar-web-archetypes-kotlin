package org.appsugar.archetypes.web.controller.system

import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.entity.Role
import org.appsugar.archetypes.util.getLogger
import org.appsugar.archetypes.repository.RoleRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/system/role")
class RoleController(val repository: RoleRepository) {
    companion object {
        private val logger = getLogger<RoleController>()
    }


    @PreAuthorize("hasAuthority('role:view')")
    @RequestMapping(value = ["list", ""])
    fun list(@PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable): Response<Page<Role>> {
        val page = repository.findAll(pageable)
        return Response(page)
    }


    @PreAuthorize("hasAuthority('role:view')")
    @RequestMapping("detail")
    fun form(id: Long): Response<Role> {
        return Response(repository.findById(id).get())
    }


    @PreAuthorize("hasAuthority('role:edit')")
    @RequestMapping("save")
    fun save(role: Role, permissions: Array<String>?): Response<Void> {
        logger.info("prepare to save role {}, new permissions is {} ", role, permissions)
        role.permissions = permissions?.toMutableList() ?: mutableListOf()
        repository.save(role)
        return Response.SUCCESS
    }

}