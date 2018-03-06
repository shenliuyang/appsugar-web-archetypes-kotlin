package org.appsugar.archetypes.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ui.Model


/****/
inline fun <reified T : Any> getLogger() = LoggerFactory.getLogger(T::class.java) as Logger

inline fun <R> Boolean.then(block: () -> R?) = if (this) block() else null

inline fun String.isNotBlankThen(block: String.() -> Unit): String {
    if (isNotBlank()) block()
    return this
}

/**
 * for spring mvc
 */
fun Model.attr(name: String, value: Any) = this.addAttribute(name, value)

