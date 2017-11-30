package org.appsugar.archetypes.web.controller


import org.appsugar.archetypes.condition.UserCondition
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.repository.UserSpecification
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/system/user")
class UserController(val repository:UserRepository) {
	@RequestMapping
	fun list(condition:UserCondition,pageable:Pageable,model:Model)=model.addAttribute("page",repository.findAll(UserSpecification(condition),pageable)).let { "system/user/list" }
}