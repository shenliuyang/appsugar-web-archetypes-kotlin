package org.appsugar.archetypes.web.controller.system

import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.awaitFirst
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.entity.User
import org.appsugar.archetypes.repository.RoleRepository
import org.appsugar.archetypes.repository.UserCondition
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.repository.toPredicate
import org.appsugar.archetypes.util.monoWithMdc
import org.appsugar.archetypes.web.controller.BaseController
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/system/user")
class UserController(val repository: UserRepository, val roleRepository: RoleRepository) : BaseController<User>() {

    @PreAuthorize("hasAuthority('user:view')")
    @RequestMapping(value = ["list", ""])
    fun list(condition: UserCondition, pageable: PageRequest) = monoWithMdc {
        Mono.subscriberContext().awaitFirst()
        logger.debug("query user by condition {}", condition)
        val page = repository.findAllAsync(condition.toPredicate(), pageable).await()
        logger.debug("query user by condition {}", condition)
        Response(page.transfer { it.copy() })
    }

    @PreAuthorize("hasAuthority('user:view')")
    @RequestMapping("detail")
    fun form(@ModelAttribute("entity") user: Mono<User>) = user.map { Response(it.copy()) }


    @PreAuthorize("hasAuthority('user:edit')")
    @PostMapping("save")
    fun save(@ModelAttribute("entity") userMono: Mono<User>, userData: UserData) = monoWithMdc {
        val roleIds = userData.roleIds
        val permissions = userData.permissions
        val user = userMono.awaitFirst()!!
        logger.info("prepare to save User {}  new permissions {} new roles {}  ", user, permissions, roleIds)
        user.roles = if (roleIds.isEmpty()) mutableSetOf() else roleRepository.findByIdIn(roleIds).await().toMutableSet()
        user.permissions = permissions
        repository.saveAsync(user).await()
        Response.SUCCESS
    }


    @GetMapping("permissions")
    fun permissions() = monoWithMdc {
        val ctx = ReactiveSecurityContextHolder.getContext().awaitFirst()
        val authentication = ctx.authentication
        Response(authentication.authorities.map { it.authority })
    }
}

data class UserData(var roleIds: MutableList<Long> = mutableListOf(), var permissions: MutableList<String> = mutableListOf())