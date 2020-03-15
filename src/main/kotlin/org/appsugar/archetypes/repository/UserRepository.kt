package org.appsugar.archetypes.repository

import com.querydsl.core.BooleanBuilder
import org.appsugar.archetypes.entity.QUser
import org.appsugar.archetypes.entity.User
import org.appsugar.archetypes.util.isNotBlankThen
import org.springframework.data.jpa.repository.EntityGraph
import java.io.Serializable
import java.util.concurrent.CompletableFuture

interface UserRepository : BaseRepository<User, Long> {
    /*find User by LoginName fetch with Roles*/
    @EntityGraph(attributePaths = ["roles"])
    fun findByLoginName(loginName: String): CompletableFuture<User?>
}


fun UserCondition.toPredicate() = BooleanBuilder().apply {
    val u = QUser.user!!
    val c = this@toPredicate
    c.name.isNotBlankThen { and(u.name.startsWith(this)) }
    c.loginName.isNotBlankThen { and(u.loginName.eq(this)) }
}

data class UserCondition(
        //startLike
        var name: String = "",
        //eq
        var loginName: String = ""
) : Serializable