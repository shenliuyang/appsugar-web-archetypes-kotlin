package org.appsugar.archetypes.netty

import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.epoll.EpollSocketChannel
import io.netty.channel.kqueue.KQueue
import io.netty.channel.kqueue.KQueueEventLoopGroup
import io.netty.channel.kqueue.KQueueServerSocketChannel
import io.netty.channel.kqueue.KQueueSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.ServerSocketChannel
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.util.concurrent.DefaultThreadFactory
import io.netty.util.concurrent.FastThreadLocalThread
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 配置全局唯一eventLoopGroup
 * 所有依赖netty第三方框架共享eventLoopGroup减少系统线程开辟
 */
@Configuration
class NettyConfiguration {
    companion object {
        val threadSize get() = 1.coerceAtLeast(Runtime.getRuntime().availableProcessors() + 1)
        val eventLoopGroup: EventLoopGroup by lazy {
            val processNumber = threadSize
            val threadFactory = object : DefaultThreadFactory("netty", true) {
                protected override fun newThread(r: Runnable, name: String): Thread {
                    return FastThreadLocalDispatcherThread(threadGroup, r, name)
                }
            }
            when {
                Epoll.isAvailable() -> EpollEventLoopGroup(processNumber, threadFactory)
                KQueue.isAvailable() -> KQueueEventLoopGroup(processNumber, threadFactory)
                else -> NioEventLoopGroup(processNumber, threadFactory)
            }
        }
        val serverSocketChannel: Class<ServerSocketChannel> by lazy {
            @Suppress("UNCHECKED_CAST")
            when {
                Epoll.isAvailable() -> EpollServerSocketChannel::class.java
                KQueue.isAvailable() -> KQueueServerSocketChannel::class.java
                else -> NioServerSocketChannel::class.java
            } as Class<ServerSocketChannel>
        }
        val socketChannel: Class<SocketChannel> by lazy {
            @Suppress("UNCHECKED_CAST")
            when {
                Epoll.isAvailable() -> EpollSocketChannel::class.java
                KQueue.isAvailable() -> KQueueSocketChannel::class.java
                else -> NioSocketChannel::class.java
            } as Class<SocketChannel>
        }
    }

    @Bean(destroyMethod = "shutdownGracefully")
    fun eventLoopGroup() = eventLoopGroup.apply {
        forEach {
            it.execute {
                val currentThread = Thread.currentThread() as FastThreadLocalDispatcherThread
                currentThread.dispatcher = it.asCoroutineDispatcher()
            }
        }
    }

    @Bean
    fun serverSocketChannel(): Class<ServerSocketChannel> = serverSocketChannel

    @Bean
    fun socketChannel(): Class<SocketChannel> = socketChannel
}

class FastThreadLocalDispatcherThread(group: ThreadGroup, target: Runnable, name: String) : FastThreadLocalThread(group, target, name) {
    lateinit var dispatcher: CoroutineDispatcher
}


val GlobalLoopGroup = NettyConfiguration.eventLoopGroup.asCoroutineDispatcher()
