package org.appsugar.archetypes.service

import org.appsugar.archetypes.extension.getLogger
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.web.Permission
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.io.Serializable

@Service
@Primary
@ConfigurationProperties("spring.security.user")
class UserDetailServiceImpl(val userRepository: UserRepository, val passwordEncoder: PasswordEncoder) : UserDetailsService {

    var name = "user"
    var password = System.currentTimeMillis().toString()

    companion object {
        var endpointPermission = "endpoint"
    }

    val logger = getLogger<UserDetailServiceImpl>()

    override fun loadUserByUsername(username: String): UserDetails {
        if (username == name) return UserPrincipal(0, name, passwordEncoder.encode(password), mutableListOf(SimpleGrantedAuthority(endpointPermission)))
        val user = userRepository.findByLoginName(username)
                ?: throw UsernameNotFoundException("user $username not found")
        var permissions = mutableSetOf<String>().apply { user.permissions }
        for (role in user.roles) {
            permissions.addAll(role.permissions)
        }
        if (permissions.contains("*")) permissions.addAll(Permission.values().map { it.value })
        return UserPrincipal(user.id, user.loginName, passwordEncoder.encode(user.password), permissions.map { SimpleGrantedAuthority(it) }.toMutableList())
    }


}

@Configuration
class PasswordEncoderConfiguration {
    @Bean
    fun encoder() = BCryptPasswordEncoder(11)
}

class UserPrincipal<T : GrantedAuthority>(val id: Long, username: String, password: String, authorities: MutableCollection<T>)
    : User(username, password, authorities), Serializable {
    val attributes = mutableMapOf<String, Any>()
}