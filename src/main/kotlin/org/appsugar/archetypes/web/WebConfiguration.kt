package org.appsugar.archetypes.web


import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.mono
import org.appsugar.archetypes.entity.Response
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.util.getLogger
import org.springframework.boot.actuate.autoconfigure.security.reactive.EndpointRequest
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.config.EnableWebFlux
import reactor.core.publisher.Mono
import java.io.Serializable

@EnableWebFlux
@EnableWebFluxSecurity
@Configuration
class WebConfiguration {


    @Bean
    fun customLoginFilter(shs: ServerHttpSecurity) = let {
        val om = ObjectMapper()
        val unAuthentication = om.writeValueAsBytes(Response.UN_AUTHENTICATED)
        val success = om.writeValueAsBytes(Response.SUCCESS)
        shs.authorizeExchange().matchers(EndpointRequest.toAnyEndpoint()).hasAuthority(UserDetailServiceImpl.endpointPermission)
                .pathMatchers("/login").permitAll().anyExchange().authenticated()
                .and().exceptionHandling().authenticationEntryPoint { ex, _ -> ex.response.writeJsonByteArray(unAuthentication) }
                .and().logout().logoutSuccessHandler { ex, _ -> ex.exchange.response.writeJsonByteArray(success) }
                .and().httpBasic()
                .and().csrf().disable()
        shs.build()
    }


    @Bean
    fun encoder() = BCryptPasswordEncoder(11)

    @Bean
    fun reactiveAuthenticationManager(reactiveUserDetailsService: ReactiveUserDetailsService) = UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService).apply { setPasswordEncoder(encoder()) }

    @Bean
    fun serverSecurityContextrepository() = WebSessionServerSecurityContextRepository()

    fun ServerHttpResponse.writeJsonByteArray(byteArray: ByteArray): Mono<Void> {
        this.headers.contentType = MediaType.APPLICATION_JSON
        val data = this.bufferFactory().wrap(byteArray)
        return writeWith(Mono.just(data))
    }

}

@Service
@Primary
@ConfigurationProperties("spring.security.user")
class UserDetailServiceImpl(val userRepository: UserRepository, val passwordEncoder: PasswordEncoder) : ReactiveUserDetailsService {

    var name = "user"
    var password = System.currentTimeMillis().toString()

    companion object {
        var endpointPermission = "endpoint"
        val logger = getLogger<UserDetailServiceImpl>()
    }


    override fun findByUsername(username: String): Mono<UserDetails> = mono {
        if (username == name) return@mono UserPrincipal(0, name, passwordEncoder.encode(password), mutableListOf(SimpleGrantedAuthority(endpointPermission)))
        val user = userRepository.findByLoginName(username).await()
                ?: throw UsernameNotFoundException("username: [$username] did not found")
        val permissions = HashSet(user.permissions)
        for (role in user.roles) {
            permissions.addAll(role.permissions)
        }
        if (permissions.contains("*")) permissions.addAll(Permission.values().map { it.value })
        UserPrincipal(user.id, user.loginName, passwordEncoder.encode(user.password), permissions.asSequence().map { SimpleGrantedAuthority(it) }.toMutableList())
    }
}


class UserPrincipal<T : GrantedAuthority>(val id: Long, username: String, password: String, authorities: MutableCollection<T>)
    : User(username, password, authorities), Serializable {
    val attributes = mutableMapOf<String, Any>()
}