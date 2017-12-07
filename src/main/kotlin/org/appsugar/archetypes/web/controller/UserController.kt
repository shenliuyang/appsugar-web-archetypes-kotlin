package org.appsugar.archetypes.web.controller

import org.appsugar.archetypes.condition.UserCondition
import org.appsugar.archetypes.entity.User
import org.appsugar.archetypes.extension.attr
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.RoleRepository
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.repository.UserSpecification
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
class UserController(val repository:UserRepository,val roleRepository: RoleRepository) {
	companion object {
	    val logger = getLogger<UserController>()
	}
	@ModelAttribute("user")
	fun modelAttribute(id:Long?) =when(id){
		 null,Long.MIN_VALUE -> User()
		 else -> repository.findById(id).get()
	}

	@RequestMapping(value = ["","list"])
	fun list(condition:UserCondition, @PageableDefault(sort = ["id"],direction = Sort.Direction.DESC) pageable:Pageable, model:Model)=model.attr("page",repository.findAll(UserSpecification(condition),pageable)).let { "system/user/list" }

	@RequestMapping("form")
	fun form(model:Model){
		model.attr("roles",roleRepository.findAll(Sort.by(Sort.Direction.ASC,"id")))
		model.attr("permissionGroups", Permission.GROUP_BY_PREFIX).let {"system/user/form"}
	}

	@PostMapping("save")
	fun save(user:User,roleIds:Array<Long>?,permissions:Array<String>?,ra: RedirectAttributes):String{
		logger.debug("prepare to save User {}  new permissions {} new roles {}",user,permissions,roleIds)
		user.roles = roleIds?.let{roleRepository.findByIdIn(roleIds.toList()).toMutableSet()} ?:  mutableSetOf()
		user.permissions = permissions?.let { it.toMutableList() }?: mutableListOf()
		repository.save(user)
		ra.addFlashAttribute("msg","保存[${user.name}]成功")
		return "redirect:/system/user/list"
	}

}