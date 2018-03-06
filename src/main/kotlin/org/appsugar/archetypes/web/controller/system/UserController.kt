package org.appsugar.archetypes.web.controller.system

import org.apache.shiro.authz.annotation.RequiresPermissions
import org.appsugar.archetypes.condition.UserCondition
import org.appsugar.archetypes.entity.User
import org.appsugar.archetypes.extension.attr
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.RoleRepository
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.repository.toPredicate
import org.appsugar.archetypes.web.security.Permission
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/system/user")
class UserController(val repository: UserRepository, val roleRepository: RoleRepository) {
    companion object {
        val logger = getLogger<UserController>()
    }

    @ModelAttribute("user")
    fun modelAttribute(id: Long?) = when (id) {
        null, Long.MIN_VALUE -> User()
        else -> repository.findById(id).get()
    }


    @RequiresPermissions("user:view")
    @RequestMapping
    fun list(condition: UserCondition, @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable, model: Model): String {
        model.attr("page", repository.findAll(repository.toPredicate(condition), pageable))
        return "system/user/list"
    }

    @RequiresPermissions("user:view")
    @RequestMapping("form")
    fun form(model: Model): String {
        model.attr("roles", roleRepository.findAll().sortedBy { it.id })
        model.attr("permissionGroups", Permission.GROUP_BY_PREFIX)
        return "system/user/form"
    }

    @RequiresPermissions("user:edit")
    @PostMapping("save")
    fun save(user: User, roleIds: Array<Long>?, permissions: Array<String>?, ra: RedirectAttributes): String {
        logger.info("prepare to save User {}  new permissions {} new roles {}", user, permissions, roleIds)
        user.roles = roleIds?.let { roleRepository.findByIdIn(roleIds.toList()).toMutableSet() } ?: mutableSetOf()
        user.permissions = permissions?.joinToString(",") ?: ""
        repository.save(user)
        ra.addFlashAttribute("msg", "保存[${user.name}]成功")
        return "redirect:/system/user"
    }
}