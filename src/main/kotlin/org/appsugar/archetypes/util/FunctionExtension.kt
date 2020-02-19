package org.appsugar.archetypes.util

import kotlinx.coroutines.*
import kotlinx.coroutines.future.future
import kotlinx.coroutines.reactive.awaitFirstOrNull
import org.appsugar.archetypes.logger.MDC_IN_CONTEXT_KEY
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


suspend fun <T> withMdcContext(block: suspend () -> T?): T? {
    val ctx = Mono.subscriberContext().awaitFirstOrNull() ?: return block()
    val optional = ctx.getOrEmpty<CoroutineContext>(MDC_IN_CONTEXT_KEY)
    return if (optional.isPresent) {
        withContext(optional.get()) { block() }
    } else {
        block()
    }
}

fun <T> future(
        context: CoroutineContext = Dispatchers.Unconfined,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> T
) = GlobalScope.future(context, start, block)