package org.appsugar.archetypes.web.controller.system

import org.apache.shiro.authz.annotation.RequiresPermissions
import org.appsugar.archetypes.entity.Role
import org.appsugar.archetypes.extension.attr
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.RoleRepository
import org.appsugar.archetypes.web.security.Permission
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/system/role")
class RoleController(val repository: RoleRepository) {
    companion object {
        private val logger = getLogger<RoleController>()
    }


    @ModelAttribute("role")
    fun modelAttribute(id: Long?) = when (id) {
        null, 0L -> Role()
        else -> repository.findById(id).get()
    }

    @RequiresPermissions("role:view")
    @RequestMapping
    fun list(@PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable, model: Model): String {
        model.attr("page", repository.findAll(pageable))
        return "system/role/list"
    }

    @RequiresPermissions("role:view")
    @RequestMapping("form")
    fun form(model: Model) = model.attr("permissionGroups", Permission.GROUP_BY_PREFIX).let { "system/role/form" }

    @RequiresPermissions("role:edit")
    @RequestMapping("save")
    fun save(role: Role, permissions: Array<String>?, model: Model, ra: RedirectAttributes): String {
        logger.info("prepare to save role {}, new permissions is {} ", role, permissions)
        role.permissions = permissions?.joinToString(",") ?: ""
        repository.save(role)
        ra.addFlashAttribute("msg", "保存[${role.name}]成功")
        return "redirect:/system/role"
    }
}