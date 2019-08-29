package org.appsugar.archetypes.web.filter


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.mono
import org.appsugar.archetypes.logger.MDC_IN_CONTEXT_KEY
import org.appsugar.archetypes.util.getLogger
import org.appsugar.archetypes.web.UserPrincipal
import org.slf4j.MDC
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
    companion object {
        val logger = getLogger<ReactorRequestAccessLoggerFilter>()
    }

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
        val time = System.currentTimeMillis()
        try {
            chain.filter(exchange).subscriberContext(Context.of(MDC_IN_CONTEXT_KEY, mdc)).awaitFirstOrNull()
        } finally {
            if (logger.isInfoEnabled) {
                //记录下每次请求消耗时间,请求路径远程地址
                val spendTime = System.currentTimeMillis() - time
                val requestUrl = req.uri.toString()
                val requestMethod = req.methodValue
                val remote = req.remoteAddress?.hostString
                mdc["url"] = requestUrl
                mdc["method"] = requestMethod
                remote?.let { mdc["remote"] = remote }
                mdc["elapsed"] = spendTime.toString()
                mdc["type"] = "access"
                MDC.setContextMap(mdc)
                logger.info("request access log")
                MDC.clear()
            }
            mdc.clear()
        }
    }

}