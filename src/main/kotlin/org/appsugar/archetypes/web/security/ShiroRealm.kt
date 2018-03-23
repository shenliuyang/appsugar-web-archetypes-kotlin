package org.appsugar.archetypes.web.security

import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.SimpleAuthenticationInfo
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import java.io.Serializable

/**
 * 用户认证鉴权领域
 */
class ShiroRealm : AuthorizingRealm() {
    companion object {
        val logger = getLogger<ShiroRealm>()
    }

    @Autowired
    private lateinit var userRepository: UserRepository

    override fun doGetAuthenticationInfo(token: AuthenticationToken?) = when (token) {
        is UsernamePasswordToken -> {
            val loginName = token.username
            val password = String(token.password)
            val user = userRepository.findByLoginName(loginName)
            user?.let {
                when (user.password) {
                    password -> SimpleAuthenticationInfo(Principal(user.id, user.name), password, name)
                    else -> null
                }
            }
        }
        else -> null
    }

    override fun doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo {
        val principal = principals.oneByType(Principal::class.java)
        val user = userRepository.findById(principal.id).get()
        logger.debug("doGetAuthorizationInfo by id ${principal.id} user is $user roles is ${user.roles}")
        val info = SimpleAuthorizationInfo()
        info.addStringPermissionWithDependency(user.permissions)
        for (role in user.roles) {
            val name = role.name
            val permissions = role.permissions
            info.addRole(name)
            info.addStringPermissionWithDependency(permissions)
        }
        return info
    }


    private fun SimpleAuthorizationInfo.addStringPermissionWithDependency(permissions: Collection<String>) {
        permissions.forEach {
            this.addStringPermission(it)
            Permission.GROUP_BY_VALUE[it]?.let {
                this.addStringPermissions(it.dependencies.map { it.value })
            }
        }
    }
}


data class Principal(
        val id: Long = -1, val name: String = "anonymous"
) : Serializable
