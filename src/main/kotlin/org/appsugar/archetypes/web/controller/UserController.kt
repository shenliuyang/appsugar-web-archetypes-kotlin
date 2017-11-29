package org.appsugar.archetypes.web.controller


import org.appsugar.archetypes.condition.UserCondition
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.repository.UserSpecification
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/system/user")
class UserController(val repository:UserRepository) {
	@RequestMapping()
	fun list(condition:UserCondition,model:Model)=model.addAttribute("users",repository.findAll(UserSpecification(condition))).let { "/system/user/list" }
}