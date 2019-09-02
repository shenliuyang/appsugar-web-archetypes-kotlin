package org.appsugar.archetypes.web.controller

import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.appsugar.archetypes.common.domain.Response
import org.appsugar.archetypes.logger.MDC_IN_CONTEXT_KEY
import org.appsugar.archetypes.util.monoWithMdc
import org.appsugar.archetypes.web.UserPrincipal
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@RestController
class MainController {

    @Autowired
    lateinit var webSessionServerSecurityContextRepository: ServerSecurityContextRepository
    @Autowired
    lateinit var reactiveAuthenticationManager: ReactiveAuthenticationManager

    @PostMapping("/login")
    fun login(loginData: LoginData, serverWebExchange: ServerWebExchange) = monoWithMdc {
        try {
            val authentication = reactiveAuthenticationManager.authenticate(UsernamePasswordAuthenticationToken(loginData.username, loginData.password)).awaitFirst()!!
            webSessionServerSecurityContextRepository.save(serverWebExchange, SecurityContextImpl(authentication)).awaitFirstOrNull()
            @Suppress("UNCHECKED_CAST")
            val principal = authentication.principal as UserPrincipal<GrantedAuthority>
            val id = principal.id
            val username = principal.username
            val ctx = Mono.subscriberContext().awaitFirst()!!
            val optional = ctx.getOrEmpty<MutableMap<String, String>>(MDC_IN_CONTEXT_KEY)
            optional.ifPresent {
                it["userId"] = id.toString()
                it["username"] = username
            }
            Response(authentication.authorities.map { it.authority })
        } catch (ex: AuthenticationException) {
            Response.error("username or password error!!")
        }
    }
}

data class LoginData(var username: String, var password: String)