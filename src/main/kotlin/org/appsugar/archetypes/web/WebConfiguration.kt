package org.appsugar.archetypes.web


import com.fasterxml.jackson.databind.ObjectMapper
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.util.getLogger
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.stereotype.Service
import java.io.Serializable


@Configuration
class WebConfiguration : WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {
        val om = ObjectMapper()
        val unAuthentication = om.writeValueAsBytes(Response.UN_AUTHENTICATED)!!
        val accessDenine = om.writeValueAsBytes(Response.UN_AUTHORIZED)!!
        val success = om.writeValueAsBytes(Response.SUCCESS)!!
        val contentType = "application/json; charset=utf-8"
        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().fullyAuthenticated()
                .and().logout().logoutRequestMatcher(AntPathRequestMatcher("/logout")).logoutSuccessHandler { _, response, _ ->
                    response.contentType = contentType
                    response.outputStream.write(success)
                }.and().exceptionHandling().authenticationEntryPoint { _, response, _ ->
                    response.contentType = contentType
                    response.outputStream.write(unAuthentication)
                }.accessDeniedHandler { _, response, _ ->
                    response.contentType = contentType
                    response.outputStream.write(accessDenine)
                }
                .and().csrf().disable()
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        super.configure(auth)
        //TODO custom  authentication provider  auth.authenticationProvider(authProvider);
    }
}

@Configuration
@Order(1)
class ActuatorSecurity : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.requestMatcher(EndpointRequest.toAnyEndpoint()).authorizeRequests()
                .anyRequest().hasAuthority(UserDetailServiceImpl.endpointPermission)
                .and().httpBasic()
    }

}

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
        return UserPrincipal(user.id, user.loginName, passwordEncoder.encode(user.password), permissions.asSequence().map { SimpleGrantedAuthority(it) }.toMutableList())
    }


}

@Configuration
class PasswordEncoderConfiguration {
    @Bean
    fun encoder() = BCryptPasswordEncoder(11)
}

class UserPrincipal<T : GrantedAuthority>(val id: Long, username: String, password: String, authorities: MutableCollection<T>)
    : User(username, password, authorities), Serializable {
    companion object {
        val currentUser: UserPrincipal<GrantedAuthority>?
            @Suppress("UNCHECKED_CAST")
            get() = SecurityContextHolder.getContext()?.let { ctx -> ctx.authentication?.let { auth -> auth.principal as? UserPrincipal<GrantedAuthority> } }
    }

    val attributes = mutableMapOf<String, Any>()
}