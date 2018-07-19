package org.appsugar.archetypes.web.controller.system

import org.apache.shiro.authz.annotation.RequiresPermissions
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.entity.Role
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.RoleRepository
import org.appsugar.archetypes.web.security.Permission
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@RestController
@RequestMapping("/system/role")
class RoleController(val repository: RoleRepository) {
    companion object {
        private val logger = getLogger<RoleController>()
    }


    @RequiresPermissions("role:view")
    @RequestMapping(value = ["list", ""])
    fun list(@PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable): Response {
        val page = repository.findAll(pageable)
        return Response(page)
    }

    @RequiresPermissions("role:view")
    @RequestMapping("detail")
    fun form(id: Long): Response {
        return Response(repository.findById(id).get())
    }

    @RequiresPermissions("role:edit")
    @RequestMapping("save")
    fun save(role: Role, permissions: Array<String>?): Response {
        logger.info("prepare to save role {}, new permissions is {} ", role, permissions)
        role.permissions = permissions?.toMutableList()?: mutableListOf()
        repository.save(role)
        return Response.SUCCESS
    }

    @RequiresPermissions("role:view")
    @RequestMapping("permissions")
    fun permissions(): Response {
        return Response(Permission.getPermissionDtoGroupByPrefix())
    }
}