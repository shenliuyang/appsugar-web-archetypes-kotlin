package org.appsugar.archetypes.repository

import com.querydsl.core.BooleanBuilder
import org.appsugar.archetypes.entity.QUser
import org.appsugar.archetypes.entity.User
import org.appsugar.archetypes.util.isNotBlankThen
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import java.io.Serializable

interface UserRepository : JpaRepository<User, Long>, QuerydslPredicateExecutor<User> {

    /*find User by LoginName fetch with Roles*/
    @EntityGraph(attributePaths = ["roles"])
    fun findByLoginName(loginName: String): User?
}

private val u = QUser.user
fun UserRepository.toPredicate(c: UserCondition) = BooleanBuilder().apply {
    c.name.isNotBlankThen { and(u.name.startsWith(this)) }
    c.loginName.isNotBlankThen { and(u.loginName.eq(this)) }
}

data class UserCondition(
        //startLike
        var name: String = "",
        //eq
        var loginName: String = ""
) : Serializable