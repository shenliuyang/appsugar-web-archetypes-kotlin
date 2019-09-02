package org.appsugar.archetypes.data.redis

import io.lettuce.core.resource.EventLoopGroupProvider
import io.netty.channel.EventLoopGroup
import io.netty.util.concurrent.EventExecutorGroup
import io.netty.util.concurrent.Future
import org.appsugar.archetypes.netty.NettyConfiguration
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * 配置lettuce 统一使用netty线程池
 */
@Component
class GlobalEventLoopGroupProvider(val eventLoopGroup: EventExecutorGroup) : EventLoopGroupProvider {

    override fun shutdown(quietPeriod: Long, timeout: Long, timeUnit: TimeUnit?): Future<Boolean> {
        TODO("not implemented")
    }

    override fun release(eventLoopGroup: EventExecutorGroup?, quietPeriod: Long, timeout: Long, unit: TimeUnit?): Future<Boolean> {
        TODO("not implemented")
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : EventLoopGroup> allocate(type: Class<T>) = eventLoopGroup as T

    override fun threadPoolSize() = NettyConfiguration.threadSize
}
