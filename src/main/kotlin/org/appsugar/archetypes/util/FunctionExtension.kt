package org.appsugar.archetypes.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.appsugar.archetypes.logger.MDC_IN_CONTEXT_KEY
import org.appsugar.archetypes.logger.asMdcContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import kotlin.coroutines.CoroutineContext


/****/
inline fun <reified T : Any> getLogger() = LoggerFactory.getLogger(T::class.java) as Logger


inline fun String.isNotBlankThen(block: String.() -> Unit): String {
    if (isNotBlank()) block()
    return this
}

public fun <T> monoWithMdc(
        context: CoroutineContext = Dispatchers.Unconfined,
        block: suspend CoroutineScope.() -> T?
) = mono(context) {
    val ctx = Mono.subscriberContext().awaitFirst()!!
    val optional = ctx.getOrEmpty<Map<String, String>>(MDC_IN_CONTEXT_KEY)
    if (optional.isPresent) {
        withContext(optional.get().asMdcContext()) {
            block()
        }
    } else {
        block()
    }

}