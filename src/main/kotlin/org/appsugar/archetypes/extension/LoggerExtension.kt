package org.appsugar.archetypes.extension

import org.slf4j.LoggerFactory
/****/
inline fun <reified T:Any> getLogger()= LoggerFactory.getLogger(T::class.java)