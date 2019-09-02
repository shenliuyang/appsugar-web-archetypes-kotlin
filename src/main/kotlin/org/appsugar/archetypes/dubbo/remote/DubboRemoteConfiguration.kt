package org.appsugar.archetypes.dubbo.remote

import io.netty.bootstrap.Bootstrap
import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.PooledByteBufAllocator
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.proxy.Socks5ProxyHandler
import io.netty.handler.timeout.IdleStateHandler
import org.apache.dubbo.common.URL
import org.apache.dubbo.common.Version
import org.apache.dubbo.common.constants.CommonConstants.DEFAULT_TIMEOUT
import org.apache.dubbo.common.constants.CommonConstants.TIMEOUT_KEY
import org.apache.dubbo.common.utils.ConfigUtils
import org.apache.dubbo.common.utils.ExecutorUtil
import org.apache.dubbo.common.utils.NetUtils
import org.apache.dubbo.remoting.Channel
import org.apache.dubbo.remoting.ChannelHandler
import org.apache.dubbo.remoting.RemotingException
import org.apache.dubbo.remoting.Transporter
import org.apache.dubbo.remoting.transport.AbstractChannel
import org.apache.dubbo.remoting.transport.AbstractClient
import org.apache.dubbo.remoting.transport.AbstractServer
import org.apache.dubbo.remoting.transport.dispatcher.ChannelHandlers
import org.apache.dubbo.remoting.transport.netty4.NettyClient
import org.apache.dubbo.remoting.transport.netty4.NettyClientHandler
import org.apache.dubbo.remoting.transport.netty4.NettyCodecAdapter
import org.apache.dubbo.remoting.transport.netty4.NettyServerHandler
import org.apache.dubbo.remoting.utils.UrlUtils
import org.appsugar.archetypes.netty.NettyConfiguration
import org.appsugar.archetypes.util.getLogger
import java.net.InetSocketAddress
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Epoll 优先的通讯
 */
class NettyNativeFirstTransporter : Transporter {
    override fun bind(url: URL, handler: ChannelHandler) = NettyNativeFirstServer(url, handler)

    override fun connect(url: URL, handler: ChannelHandler) = NettyNativeFirstClient(url, handler)
}

class NettyNativeFirstServer(url: URL, handler: ChannelHandler) : AbstractServer(url, ChannelHandlers.wrap(handler, ExecutorUtil.setThreadName(url, SERVER_THREAD_POOL_NAME))) {
    companion object {
        var logger = getLogger<NettyNativeFirstServer>()
    }

    lateinit var channels: MutableMap<String, Channel>
    /**
     * netty server bootstrap.
     */
    lateinit var bootstrap: ServerBootstrap
    /**
     * the boss channel that receive connections and dispatch these to worker channel.
     */
    var channel: io.netty.channel.Channel? = null

    override fun doOpen() {
        bootstrap = ServerBootstrap()
        val nettyServerHandler = NettyServerHandler(url, this)
        channels = nettyServerHandler.channels

        bootstrap.group(NettyConfiguration.eventLoopGroup)
                .channel(NettyConfiguration.serverSocketChannel)
                .childOption(ChannelOption.TCP_NODELAY, java.lang.Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, java.lang.Boolean.TRUE)
                .childOption<ByteBufAllocator>(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(object : ChannelInitializer<NioSocketChannel>() {
                    @Throws(Exception::class)
                    override fun initChannel(ch: NioSocketChannel) {
                        val idleTimeout = UrlUtils.getIdleTimeout(url)
                        val adapter = NettyCodecAdapter(codec, url, this@NettyNativeFirstServer)
                        ch.pipeline()
                                .addLast("decoder", adapter.decoder)
                                .addLast("encoder", adapter.encoder)
                                .addLast("server-idle-handler", IdleStateHandler(0, 0, idleTimeout.toLong(), TimeUnit.MILLISECONDS))
                                .addLast("handler", nettyServerHandler)
                    }
                })
        // bind
        val channelFuture = bootstrap.bind(bindAddress)
        channelFuture.syncUninterruptibly()
        channel = channelFuture.channel()
    }

    override fun getChannel(remoteAddress: InetSocketAddress) = channels[NetUtils.toAddressString(remoteAddress)]

    override fun isBound() = channel!!.isActive

    override fun getChannels(): MutableCollection<Channel> {
        val chs = HashSet<Channel>()
        for (channel in this.channels.values) {
            if (channel.isConnected) {
                chs.add(channel)
            } else {
                channels.remove(NetUtils.toAddressString(channel.remoteAddress))
            }
        }
        return chs
    }

    override fun doClose() {
        try {
            channel?.close()
        } catch (e: Throwable) {
            logger.warn(e.message, e)
        }

        try {
            val channels = getChannels()
            if (channels.isNotEmpty()) {
                for (channel in channels) {
                    try {
                        channel.close()
                    } catch (e: Throwable) {
                        logger.warn(e.message, e)
                    }

                }
            }
        } catch (e: Throwable) {
            logger.warn(e.message, e)
        }
        try {
            channels.clear()
        } catch (e: Throwable) {
            logger.warn(e.message, e)
        }

    }

}


class NettyNativeFirstClient(url: URL, handler: ChannelHandler) : AbstractClient(url, handler) {
    companion object {
        val logger = getLogger<NettyNativeFirstClient>()
    }

    private val SOCKS_PROXY_HOST = "socksProxyHost"

    private val SOCKS_PROXY_PORT = "socksProxyPort"

    private val DEFAULT_SOCKS_PROXY_PORT = "1080"

    lateinit var bootstrap: Bootstrap

    /**
     * current channel. Each successful invocation of [NettyClient.doConnect] will
     * replace this with new channel and close old channel.
     * **volatile, please copy reference to use.**
     */
    @Volatile
    var channel: io.netty.channel.Channel? = null


    /**
     * Init bootstrap
     *
     * @throws Throwable
     */
    @Throws(Throwable::class)
    override fun doOpen() {
        val nettyClientHandler = NettyClientHandler(url, this)
        bootstrap = Bootstrap()
        bootstrap.group(NettyConfiguration.eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getTimeout())
                .channel(NettyConfiguration.socketChannel)

        if (connectTimeout < 3000) {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
        } else {
            bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeout)
        }

        bootstrap.handler(object : ChannelInitializer<io.netty.channel.Channel>() {
            override fun initChannel(ch: io.netty.channel.Channel) {
                val heartbeatInterval = UrlUtils.getHeartbeat(url)
                val adapter = NettyCodecAdapter(codec, url, this@NettyNativeFirstClient)
                ch.pipeline()//.addLast("logging",new LoggingHandler(LogLevel.INFO))//for debug
                        .addLast("decoder", adapter.decoder)
                        .addLast("encoder", adapter.encoder)
                        .addLast("client-idle-handler", IdleStateHandler(heartbeatInterval.toLong(), 0, 0, TimeUnit.MILLISECONDS))
                        .addLast("handler", nettyClientHandler)
                val socksProxyHost = ConfigUtils.getProperty(SOCKS_PROXY_HOST)
                if (socksProxyHost != null) {
                    val socksProxyPort = Integer.parseInt(ConfigUtils.getProperty(SOCKS_PROXY_PORT, DEFAULT_SOCKS_PROXY_PORT))
                    val socks5ProxyHandler = Socks5ProxyHandler(InetSocketAddress(socksProxyHost, socksProxyPort))
                    ch.pipeline().addFirst(socks5ProxyHandler)
                }
            }
        })
    }

    override fun doConnect() {
        val start = System.currentTimeMillis()
        val future = bootstrap.connect(connectAddress)
        try {
            val ret = future.awaitUninterruptibly(connectTimeout.toLong(), TimeUnit.MILLISECONDS)

            if (ret && future.isSuccess) {
                val newChannel = future.channel()
                try {
                    // Close old channel
                    // copy reference
                    val oldChannel = this.channel
                    if (oldChannel != null) {
                        try {
                            if (logger.isInfoEnabled) {
                                logger.info("Close old netty channel $oldChannel on create new netty channel $newChannel")
                            }
                            oldChannel.close()
                        } finally {
                            NettyChannel.removeChannelIfDisconnected(oldChannel)
                        }
                    }
                } finally {
                    if (this.isClosed) {
                        try {
                            if (logger.isInfoEnabled) {
                                logger.info("Close new netty channel $newChannel, because the client closed.")
                            }
                            newChannel.close()
                        } finally {
                            this.channel = null
                            NettyChannel.removeChannelIfDisconnected(newChannel)
                        }
                    } else {
                        this.channel = newChannel
                    }
                }
            } else if (future.cause() != null) {
                throw RemotingException(this, "client(url: " + url + ") failed to connect to server "
                        + remoteAddress + ", error message is:" + future.cause().message, future.cause())
            } else {
                throw RemotingException(this, "client(url: " + url + ") failed to connect to server "
                        + remoteAddress + " client-side timeout "
                        + connectTimeout + "ms (elapsed: " + (System.currentTimeMillis() - start) + "ms) from netty client "
                        + NetUtils.getLocalHost() + " using dubbo version " + Version.getVersion())
            }
        } finally {
            // just add new valid channel to NettyChannel's cache
        }
    }

    override fun doDisConnect() {
        try {
            //removeChannelIfDisconnected(channel)
        } catch (t: Throwable) {
            logger.warn(t.message)
        }

    }

    override fun doClose() {}

    override fun getChannel(): Channel? {
        val c = channel
        return if (c == null || !c.isActive) null else NettyChannel.getOrAddChannel(c, url, this)
    }

    override fun canHandleIdle() = true

}


class NettyChannel(val channel: io.netty.channel.Channel, url: URL, handler: ChannelHandler) : AbstractChannel(url, handler) {
    companion object {
        val logger = getLogger<NettyChannel>()
        val CHANNEL_MAP = ConcurrentHashMap<io.netty.channel.Channel, NettyChannel>()
        fun getOrAddChannel(ch: io.netty.channel.Channel?, url: URL, handler: ChannelHandler): NettyChannel? {
            if (ch == null) {
                return null
            }
            var ret: NettyChannel? = CHANNEL_MAP[ch]
            if (ret == null) {
                val nettyChannel = NettyChannel(ch, url, handler)
                if (ch.isActive) {
                    ret = CHANNEL_MAP.putIfAbsent(ch, nettyChannel)
                }
                if (ret == null) {
                    ret = nettyChannel
                }
            }
            return ret
        }

        fun removeChannelIfDisconnected(ch: io.netty.channel.Channel?) {
            if (ch != null && !ch.isActive) {
                CHANNEL_MAP.remove(ch)
            }
        }
    }


    val attributes = ConcurrentHashMap<String, Any>()


    override fun getLocalAddress(): InetSocketAddress {
        return channel.localAddress() as InetSocketAddress
    }

    override fun getRemoteAddress(): InetSocketAddress {
        return channel.remoteAddress() as InetSocketAddress
    }

    override fun isConnected(): Boolean {
        return !isClosed && channel.isActive
    }

    /**
     * Send message by netty and whether to wait the completion of the send.
     *
     * @param message message that need send.
     * @param sent whether to ack async-sent
     * @throws RemotingException throw RemotingException if wait until timeout or any exception thrown by method body that surrounded by try-catch.
     */
    override fun send(message: Any, sent: Boolean) {
        // whether the channel is closed
        super.send(message, sent)

        var success = true
        var timeout = 0
        try {
            val future = channel.writeAndFlush(message)
            if (sent) {
                // wait timeout ms
                timeout = url.getPositiveParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT)
                success = future.await(timeout.toLong())
            }
            val cause = future.cause()
            if (cause != null) {
                throw cause
            }
        } catch (e: Throwable) {
            throw RemotingException(this, "Failed to send message " + message + " to " + remoteAddress + ", cause: " + e.message, e)
        }

        if (!success) {
            throw RemotingException(this, "Failed to send message " + message + " to " + remoteAddress
                    + "in timeout(" + timeout + "ms) limit")
        }
    }

    override fun close() {
        try {
            super.close()
        } catch (e: Exception) {
            logger.warn(e.message, e)
        }

        try {
            removeChannelIfDisconnected(channel)
        } catch (e: Exception) {
            logger.warn(e.message, e)
        }

        try {
            attributes.clear()
        } catch (e: Exception) {
            logger.warn(e.message, e)
        }

        try {
            if (logger.isInfoEnabled) {
                logger.info("Close netty channel " + channel)
            }
            channel.close()
        } catch (e: Exception) {
            logger.warn(e.message, e)
        }

    }

    override fun hasAttribute(key: String): Boolean {
        return attributes.containsKey(key)
    }

    override fun getAttribute(key: String) = attributes[key]

    override fun setAttribute(key: String, value: Any?) {
        // The null value is unallowed in the ConcurrentHashMap.
        if (value == null) {
            attributes.remove(key)
        } else {
            attributes[key] = value
        }
    }

    override fun removeAttribute(key: String) {
        attributes.remove(key)
    }


    override fun toString(): String {
        return "NettyChannel [channel=$channel]"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NettyChannel

        if (channel != other.channel) return false

        return true
    }

    override fun hashCode(): Int {
        return channel.hashCode()
    }


}