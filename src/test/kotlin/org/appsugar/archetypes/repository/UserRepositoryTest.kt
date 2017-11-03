package org.appsugar.archetypes.repository

import org.appsugar.archetypes.BaseTestCase
import org.springframework.beans.factory.annotation.Autowired
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions

class UserRepositoryTest:BaseTestCase() {
	@Autowired
	lateinit var  repository:UserRepository
	
	@Test
	fun findByLoginName(){
		val loginName = "admin"
		val user = repository.findByLoginName(loginName)
		logger.debug("find user by loginName $loginName  result is $user")
		Assertions.assertNotNull(user)
	}
} 