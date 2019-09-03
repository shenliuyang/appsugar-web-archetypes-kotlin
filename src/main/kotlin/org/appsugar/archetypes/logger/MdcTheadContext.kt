package org.appsugar.archetypes.logger

import kotlinx.coroutines.ThreadContextElement
import org.slf4j.MDC
import kotlin.coroutines.CoroutineContext


fun Map<String, String>.asMdcContext() = ThreadLocalElement(this) as CoroutineContext

// top-level data class for a nicer out-of-the-box toString representation and class name
@PublishedApi
internal class ThreadLocalKey : CoroutineContext.Key<ThreadLocalElement>

internal data class ThreadLocalElement(
        private val mdc: Map<String, String>
) : ThreadContextElement<Map<String, String>> {
    override val key: CoroutineContext.Key<*> = ThreadLocalKey()

    override fun updateThreadContext(context: CoroutineContext): Map<String, String> {
        MDC.setContextMap(mdc)
        return this.mdc
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: Map<String, String>) {
        MDC.clear()
    }

}
