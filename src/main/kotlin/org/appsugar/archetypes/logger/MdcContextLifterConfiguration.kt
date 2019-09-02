package org.appsugar.archetypes.logger

import ch.qos.logback.classic.LoggerContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Configuration


@Configuration
class MdcContextLifterConfiguration {


    /**
     * set application name and version in logback context
     */
    @Autowired(required = false)
    fun setBuildProperties(buildProperties: BuildProperties?) {
        if (buildProperties == null) return
        val version = buildProperties.version
        val lc = LoggerFactory.getILoggerFactory() as LoggerContext
        lc.putProperty("version", version)
    }
}


const val MDC_IN_CONTEXT_KEY = "MDC_IN_CONTEXT_KEY"
