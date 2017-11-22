package org.appsugar.archetypes.web.security

import org.apache.shiro.authc.AuthenticationInfo
import org.apache.shiro.authc.AuthenticationToken
import org.apache.shiro.authc.SimpleAuthenticationInfo
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.authz.AuthorizationInfo
import org.apache.shiro.authz.SimpleAuthorizationInfo
import org.apache.shiro.realm.AuthorizingRealm
import org.apache.shiro.subject.PrincipalCollection
import org.appsugar.archetypes.entity.Role
import org.appsugar.archetypes.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired

/**
 * 用户认证鉴权领域
 */
class ShiroRealm : AuthorizingRealm() {

    @Autowired
    lateinit var userRepository: UserRepository;

    override fun doGetAuthenticationInfo(token: AuthenticationToken?)=when(token){
        is UsernamePasswordToken ->{
            val loginName=token.username
            val password=String(token.password)
            val user = userRepository.findByLoginName(loginName)
            user?.let{
                when(user.password){
                    password -> SimpleAuthenticationInfo(Principal(user.id, user.name, user),password,name)
                    else -> null
                }
            }
        }
        else -> null
    }

    override fun doGetAuthorizationInfo(principals: PrincipalCollection): AuthorizationInfo {
        val principal = principals.oneByType(Principal::class.java)
        val user = principal.user
        val info = SimpleAuthorizationInfo()
        user?.let {
            info.addStringPermissions(user.permissions)
            for((_,name, permissions) in user.roles){
                info.addRole(name)
                info.addStringPermissions(permissions)
            }
        }
        return info
    }
}


