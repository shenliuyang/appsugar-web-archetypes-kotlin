package org.appsugar.archetypes.web.filter


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.appsugar.archetypes.logger.MDC_IN_CONTEXT_KEY
import org.appsugar.archetypes.web.UserPrincipal
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.util.context.Context

/**
 * make sure each request have request id and session id to be logger
 */
@Component
class ReactorRequestAccessLoggerFilter : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain) = mono<Void>(Dispatchers.Unconfined) {
        val req = exchange.request
        val session = exchange.session.awaitFirst()
        val mdc = LinkedHashMap<String, String>()
        mdc["request"] = req.id
        mdc["session"] = session.id
        val ctx = ReactiveSecurityContextHolder.getContext().awaitFirstOrNull()
        if (ctx != null) {
            @Suppress("UNCHECKED_CAST")
            val principal = ctx.authentication.principal as UserPrincipal<GrantedAuthority>
            val id = principal.id
            val username = principal.username
            mdc["userId"] = id.toString()
            mdc["username"] = username
        }
        chain.filter(exchange).subscriberContext(Context.of(MDC_IN_CONTEXT_KEY, mdc)).awaitFirstOrNull().apply { mdc.clear() }
    }

}