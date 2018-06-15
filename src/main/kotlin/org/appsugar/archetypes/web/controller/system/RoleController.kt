package org.appsugar.archetypes.web.controller.system

import org.apache.shiro.authz.annotation.RequiresPermissions
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.entity.Role
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.RoleRepository
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


    @ModelAttribute("role")
    fun modelAttribute(id: Long? = 0) = when (id) {
        null, 0L -> Role()
        else -> repository.getOne(id)
    }

    @RequiresPermissions("role:view")
    @RequestMapping
    fun list(@PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable) = Response(repository.findAll(pageable))

    @RequiresPermissions("role:view")
    @RequestMapping("form")
    fun form(role: Role) = Response(role)

    @RequiresPermissions("role:edit")
    @RequestMapping("save")
    fun save(role: Role, permissions: Array<String>?, ra: RedirectAttributes): Response {
        logger.info("prepare to save role {}, new permissions is {} ", role, permissions)
        role.permissions = permissions?.toMutableList() ?: mutableListOf()
        repository.save(role)
        ra.addFlashAttribute("msg", "保存[${role.name}]成功")
        return Response.SUCCESS
    }
}