package org.appsugar.archetypes.repository

import org.appsugar.archetypes.condition.UserCondition
import org.springframework.data.repository.CrudRepository
import org.appsugar.archetypes.entity.User
import org.appsugar.archetypes.extension.startWith
import org.appsugar.archetypes.extension.then
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

interface UserRepository : JpaRepository<User,Long>,JpaSpecificationExecutor<User>{

	/*find User by LoginName fetch with Roles*/
	@EntityGraph(attributePaths = arrayOf("roles"))
	fun findByLoginName(loginName:String):User?

}

class UserSpecification(private val c:UserCondition):Specification<User>{
    override fun toPredicate(root: Root<User>, query: CriteriaQuery<*>, cb: CriteriaBuilder): Predicate {
        val p = mutableListOf<Predicate>()
        c.name.isNotBlank().then {p.add(cb.startWith(root.get<String>(UserCondition::name.name),c.name))}
        c.loginName.isNotBlank().then {p.add(cb.equal(root.get<String>(UserCondition::loginName.name),c.loginName))}
        return cb.and(*p.toTypedArray())
    }
}