package org.appsugar.archetypes.web.controller.system

import org.apache.shiro.authz.annotation.RequiresPermissions
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.condition.UserCondition
import org.appsugar.archetypes.entity.User
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.RoleRepository
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.repository.toPredicate
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/system/user")
class UserController(val repository: UserRepository, val roleRepository: RoleRepository) {
    companion object {
        val logger = getLogger<UserController>()
    }

    @RequiresPermissions("user:view")
    @RequestMapping(value = ["list", ""])
    fun list(condition: UserCondition, @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable): Response {
        val page = repository.findAll(repository.toPredicate(condition), pageable)
        return Response(page)
    }

    @RequiresPermissions("user:view")
    @RequestMapping("detail")
    fun form(id: Long): Response {
        return Response(repository.findById(id).get())
    }

    @RequiresPermissions("user:edit")
    @PostMapping("save")
    fun save(user: User, roleIds: Array<Long>?, permissions: Array<String>?): Response {
        logger.info("prepare to save User {}  new permissions {} new roles {}  ", user, permissions, roleIds)
        user.roles = roleIds?.let { roleRepository.findByIdIn(it.toList()).toMutableSet() } ?: mutableSetOf()
        user.permissions = permissions?.toMutableList() ?: mutableListOf()
        repository.save(user)
        return Response.SUCCESS
    }

}