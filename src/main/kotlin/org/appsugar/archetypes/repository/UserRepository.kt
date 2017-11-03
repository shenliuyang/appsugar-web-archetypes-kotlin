package org.appsugar.archetypes.repository

import org.springframework.data.repository.CrudRepository
import org.appsugar.archetypes.entity.User

interface UserRepository :CrudRepository<User,Long>{
	/*find User by LoginName*/
	fun findByLoginName(loginName:String):User?

}