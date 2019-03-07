package org.appsugar.archetypes.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory


/****/
inline fun <reified T : Any> getLogger() = LoggerFactory.getLogger(T::class.java) as Logger


inline fun String.isNotBlankThen(block: String.() -> Unit): String {
    if (isNotBlank()) block()
    return this
}
