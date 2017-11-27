package org.appsugar.archetypes.web.controller


import org.apache.shiro.authz.annotation.RequiresPermissions
import org.appsugar.archetypes.condition.UserCondition
import org.appsugar.archetypes.entity.User
import org.springframework.web.bind.annotation.RestController
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.repository.UserSpecification
import org.appsugar.archetypes.web.security.Permission
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/user")
class UserController(val userRepository:UserRepository) {

	@RequiresPermissions("user:view")
	@GetMapping("")
	fun users()=userRepository.findAll().map { it.copyWithOutRole() }

	@GetMapping("/simple")
	fun simple(id:Long)=userRepository.findById(id).orElse(User.EMPTY_USER).let {object{
		val id = it.id
		val name = it.name
		val loginName = it.loginName
	}}
	
	@GetMapping("/query")
	fun query(loginName:String)=userRepository.findByLoginName(loginName)

	@GetMapping("/condition")
	fun query(condition:UserCondition)=userRepository.findAll(UserSpecification(condition)).map { it.copyWithOutRole() }

	fun User.copyWithOutRole()=this.copy().let { it.roles= emptySet();it }

}