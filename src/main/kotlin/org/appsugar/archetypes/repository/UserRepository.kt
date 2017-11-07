package org.appsugar.archetypes.repository

import org.springframework.data.repository.CrudRepository
import org.appsugar.archetypes.entity.User
import org.springframework.data.jpa.repository.EntityGraph

interface UserRepository :CrudRepository<User,Long>{

	/*find User by LoginName*/
	@EntityGraph(attributePaths = arrayOf("roles"))
	fun findByLoginName(loginName:String):User?

}