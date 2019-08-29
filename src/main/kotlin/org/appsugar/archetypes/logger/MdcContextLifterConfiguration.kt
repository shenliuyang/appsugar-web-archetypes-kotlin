package org.appsugar.archetypes.logger

import ch.qos.logback.classic.LoggerContext
import org.reactivestreams.Subscription
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Configuration
import reactor.core.CoreSubscriber
import reactor.core.publisher.Hooks
import reactor.core.publisher.Operators
import reactor.util.context.Context
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy


@Configuration
class MdcContextLifterConfiguration {

    companion object {
        val MDC_CONTEXT_REACTOR_KEY: String = MdcContextLifterConfiguration::class.java.name
    }

    @PostConstruct
    fun contextOperatorHook() {
        Hooks.onEachOperator(MDC_CONTEXT_REACTOR_KEY, Operators.lift { _, subscriber -> MdcContextLifter(subscriber) })
    }

    @PreDestroy
    fun cleanupHook() {
        Hooks.resetOnEachOperator(MDC_CONTEXT_REACTOR_KEY)
    }

    /**
     * set application name and version in logback context
     */
    @Autowired(required = false)
    fun setBuildProperties(buildProperties: BuildProperties?) {
        if (buildProperties == null) return
        val name = buildProperties.name
        val version = buildProperties.version
        val lc = LoggerFactory.getILoggerFactory() as LoggerContext
        lc.putProperty("app", name)
        lc.putProperty("version", version)
    }
}

/**
 * Helper that copies the state of Reactor [Context] to MDC on the #onNext function.
 */
class MdcContextLifter<T>(private val coreSubscriber: CoreSubscriber<T>) : CoreSubscriber<T> {

    override fun onNext(t: T) {
        coreSubscriber.currentContext().copyToMdc()
        coreSubscriber.onNext(t)
    }

    override fun onSubscribe(subscription: Subscription) {
        coreSubscriber.onSubscribe(subscription)
    }

    override fun onComplete() {
        coreSubscriber.onComplete()
    }

    override fun onError(throwable: Throwable?) {
        coreSubscriber.onError(throwable)
    }

    override fun currentContext(): Context {
        return coreSubscriber.currentContext()
    }
}


const val MDC_IN_CONTEXT_KEY = "MDC_IN_CONTEXT_KEY"
/**
 * Extension function for the Reactor [Context]. Copies the current context to the MDC, if context is empty clears the MDC.
 * State of the MDC after calling this method should be same as Reactor [Context] state.
 * One thread-local access only.
 */
private fun Context.copyToMdc() {
    val optional = this.getOrEmpty<Map<String, String>>(MDC_IN_CONTEXT_KEY)
    if (optional.isPresent) MDC.setContextMap(optional.get()) else MDC.clear()
}