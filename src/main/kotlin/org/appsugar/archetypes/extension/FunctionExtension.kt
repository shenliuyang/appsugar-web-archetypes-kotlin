package org.appsugar.archetypes.extension

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.Expression
import javax.persistence.criteria.Predicate

/****/
inline fun <reified T:Any> getLogger()= LoggerFactory.getLogger(T::class.java) as Logger

inline fun <R> Boolean.then(block: () -> R?)=if(this)block()else null

/**
 * for Jpa CriteriaBuilder
 */
fun CriteriaBuilder.startWith(exp:Expression<String>,value:String):Predicate=this.like(exp,value+"%")
fun CriteriaBuilder.endWith(exp:Expression<String>,value:String):Predicate=this.like(exp,"%"+value)