package org.appsugar.archetypes.web.controller

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.appsugar.archetypes.common.domain.Response
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
class MainController {

    @Autowired
    lateinit var webSessionServerSecurityContextRepository: ServerSecurityContextRepository
    @Autowired
    lateinit var reactiveAuthenticationManager: ReactiveAuthenticationManager

    @PostMapping("/login")
    fun login(loginData: LoginData, serverWebExchange: ServerWebExchange) = mono {
        try {
            val authentication = reactiveAuthenticationManager.authenticate(UsernamePasswordAuthenticationToken(loginData.username, loginData.password)).awaitFirst()!!
            webSessionServerSecurityContextRepository.save(serverWebExchange, SecurityContextImpl(authentication)).awaitFirstOrNull()
            Response(authentication.authorities.map { it.authority })
        } catch (ex: AuthenticationException) {
            Response.error("username or password error!!")
        }
    }
}

data class LoginData(var username: String, var password: String)