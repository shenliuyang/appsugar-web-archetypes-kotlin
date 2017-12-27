package org.appsugar.archetypes.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ui.Model
import java.util.*
import javax.persistence.criteria.*
import kotlin.reflect.KCallable

/****/
inline fun <reified T : Any> getLogger() = LoggerFactory.getLogger(T::class.java) as Logger

inline fun <R> Boolean.then(block: () -> R?) = if (this) block() else null

inline fun String.isNotBlankThen(block: String.() -> Unit): String {
    if (isNotBlank()) block()
    return this
}


/**
 * for Jpa CriteriaBuilder
 */
fun CriteriaBuilder.startWith(exp: Expression<String>, value: String): Predicate = this.like(exp, value + "%")

fun CriteriaBuilder.endWith(exp: Expression<String>, value: String): Predicate = this.like(exp, "%" + value)

fun <X> Root<X>.getString(proerty: KCallable<Any>): Path<String> = get<String>(proerty.name)

fun <X> Root<X>.getNumber(proerty: KCallable<Any>): Path<Number> = get<Number>(proerty.name)

fun <X> Root<X>.getDate(proerty: KCallable<Any>): Path<Date> = get<Date>(proerty.name)

fun <X, Y> Root<X>.get(proerty: KCallable<Any>): Path<Y> = get<Y>(proerty.name)
/**
 * for spring mvc
 */
fun Model.attr(name: String, value: Any) = this.addAttribute(name, value)

