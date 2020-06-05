package org.appsugar.archetypes.web.controller.system

import kotlinx.coroutines.reactive.awaitFirst
import org.appsugar.archetypes.entity.Response
import org.appsugar.archetypes.entity.User
import org.appsugar.archetypes.repository.jpa.RoleJpaRepository
import org.appsugar.archetypes.repository.jpa.UserCondition
import org.appsugar.archetypes.repository.jpa.UserJpaRepository
import org.appsugar.archetypes.repository.jpa.toPredicate
import org.appsugar.archetypes.util.blockedMono
import org.appsugar.archetypes.web.controller.BaseController
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/system/user")
class UserController(val repository: UserJpaRepository, val roleRepository: RoleJpaRepository) : BaseController<User>() {


    @PreAuthorize("hasAuthority('user:view')")
    @RequestMapping(value = ["list", ""])
    fun list(condition: UserCondition, pageable: PageRequest) = blockedMono {
        val page = repository.findAll(condition.toPredicate(), pageable)
        Response(page.transfer { it.copy() })
    }

    @PreAuthorize("hasAuthority('user:view')")
    @RequestMapping("form")
    fun form(@ModelAttribute("entity") user: Mono<User>) = user


    @PreAuthorize("hasAuthority('user:edit')")
    @PostMapping("save")
    fun save(@ModelAttribute("entity") userMono: Mono<User>, userData: UserData) = blockedMono {
        val roleIds = userData.roleIds
        val permissions = userData.permissions
        val user = userMono.awaitFirst()!!
        logger.info("prepare to save User {}  new permissions {} new roles {}  ", user, permissions, roleIds)
        user.roles = if (roleIds.isEmpty()) mutableSetOf() else roleRepository.findByIdIn(roleIds).toMutableSet()
        user.permissions = permissions
        repository.save(user)
        Response.SUCCESS
    }


    @GetMapping("permissions")
    suspend fun permissions() = let {
        val ctx = ReactiveSecurityContextHolder.getContext().awaitFirst()
        val authentication = ctx.authentication
        Response(authentication.authorities.map { it.authority })
    }
}

data class UserData(var roleIds: MutableList<Long> = mutableListOf(), var permissions: MutableList<String> = mutableListOf())