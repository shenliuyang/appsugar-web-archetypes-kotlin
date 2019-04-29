package org.appsugar.archetypes.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.reactor.mono
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/****/
inline fun <reified T : Any> getLogger() = LoggerFactory.getLogger(T::class.java) as Logger


inline fun String.isNotBlankThen(block: String.() -> Unit): String {
    if (isNotBlank()) block()
    return this
}

fun <T> GlobalScope.monoWithContext(block: suspend CoroutineScope.() -> T?) = mono(Dispatchers.Unconfined, block)