package org.appsugar.archetypes.web.controller

import org.springframework.web.bind.annotation.RestController
import org.appsugar.archetypes.repository.UserRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/user")
class UserController(val userRepository:UserRepository) {
	
	@GetMapping("")
	fun users()=userRepository.findAll()
	
	@GetMapping("/query")
	fun query(loginName:String)=userRepository.findByLoginName(loginName)
	
}