package org.appsugar.archetypes.web.controller

import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.repository.UserRepository
import org.appsugar.archetypes.util.getLogger
import org.appsugar.archetypes.util.withMdcContext
import org.appsugar.archetypes.web.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange


@RestController
class MainController {

    val logger = getLogger<MainController>()
    @Autowired
    lateinit var webSessionServerSecurityContextRepository: ServerSecurityContextRepository
    @Autowired
    lateinit var reactiveAuthenticationManager: ReactiveAuthenticationManager

    @Autowired
    lateinit var userRepository: UserRepository

    @PostMapping("/login")
    suspend fun login(loginData: LoginData, serverWebExchange: ServerWebExchange) = withMdcContext {
        logger.debug("all user is {}", userRepository.findAllAsync(PageRequest.of(0, 20)).await())
        try {
            val authentication = reactiveAuthenticationManager.authenticate(UsernamePasswordAuthenticationToken(loginData.username, loginData.password)).awaitFirst()!!
            webSessionServerSecurityContextRepository.save(serverWebExchange, SecurityContextImpl(authentication)).awaitFirstOrNull()
            @Suppress("UNCHECKED_CAST")
            val principal = authentication.principal as UserPrincipal<GrantedAuthority>
            Response(authentication.authorities.map { it.authority })
        } catch (ex: AuthenticationException) {
            Response.error("username or password error!!")
        }
    }
}

data class LoginData(var username: String, var password: String)