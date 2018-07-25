package org.appsugar.archetypes.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*


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

/**
 * for Collections
 */
inline fun <reified E> Collection<String>.toNumberList(): List<E> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        //filter null or empty string
        val list = this.filter { it.isNotBlank() }.toList()
        val cls = E::class.java
        val method = cls.getMethod("parse" + cls.simpleName.substring(0, 1).toUpperCase() + cls.simpleName.substring(1), String::class.java)
        list.map { method.invoke(null, it) }.toList() as List<E>
    }
}