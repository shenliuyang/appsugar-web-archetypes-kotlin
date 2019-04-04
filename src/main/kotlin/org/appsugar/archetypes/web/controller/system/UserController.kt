package org.appsugar.archetypes.web.controller.system

import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.entity.User
import org.appsugar.archetypes.repository.RoleRepository
import org.appsugar.archetypes.repository.UserCondition
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.repository.toPredicate
import org.appsugar.archetypes.web.UserPrincipal
import org.appsugar.archetypes.web.controller.BaseController
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/system/user")
class UserController(val repository: UserRepository, val roleRepository: RoleRepository) : BaseController<User>() {


    @PreAuthorize("hasAuthority('user:view')")
    @RequestMapping(value = ["list", ""])
    fun list(condition: UserCondition, pageable: PageRequest): Response<Page<User>> {
        println("form jpa repository is $pageable")
        val page = repository.findAll(repository.toPredicate(condition), pageable)
        return Response(page)
    }

    @PreAuthorize("hasAuthority('user:view')")
    @RequestMapping("detail")
    fun form(user: User): Response<User> {
        return Response(user)
    }


    @PreAuthorize("hasAuthority('user:edit')")
    @PostMapping("save")
    fun save(@ModelAttribute user: User, roleIds: Array<Long>?, permissions: Array<String>?): Response<Void> {
        logger.info("prepare to save User {}  new permissions {} new roles {}  ", user, permissions, roleIds)
        user.roles = roleIds?.let { roleRepository.findByIdIn(it.toList()).toMutableSet() } ?: mutableSetOf()
        user.permissions = permissions?.toMutableList() ?: mutableListOf()
        repository.save(user)
        return Response.SUCCESS
    }


    @GetMapping("permissions")
    fun permissions(): Response<List<String>> {
        return Response(UserPrincipal.currentUser!!.authorities.map { it.authority!! })
    }
}