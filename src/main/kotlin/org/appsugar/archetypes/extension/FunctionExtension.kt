package org.appsugar.archetypes.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory


/****/
inline fun <reified T : Any> getLogger() = LoggerFactory.getLogger(T::class.java) as Logger

inline fun <R> Boolean.then(block: () -> R?) = if (this) block() else null

inline fun String.isNotBlankThen(block: String.() -> Unit): String {
    if (isNotBlank()) block()
    return this
}

inline fun Long.notZero(block: Long.() -> Unit): Long {
    if (this != 0L) {
        block()
    }
    return this
}

inline fun Long?.notZero(block: Long.() -> Unit): Long? {
    if (this != null && this != 0L) {
        block()
    }
    return this
}

