package org.appsugar.archetypes.logger

import kotlinx.coroutines.ThreadContextElement
import org.slf4j.MDC
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


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
        return mdc;
    }

    override fun restoreThreadContext(context: CoroutineContext, oldState: Map<String, String>) {
        MDC.clear()
    }

    // this method is overridden to perform value comparison (==) on key
    override fun minusKey(key: CoroutineContext.Key<*>): CoroutineContext {
        return if (this.key == key) EmptyCoroutineContext else this
    }

    // this method is overridden to perform value comparison (==) on key
    public override operator fun <E : CoroutineContext.Element> get(key: CoroutineContext.Key<E>): E? =
            @Suppress("UNCHECKED_CAST")
            if (this.key == key) this as E else null

}
