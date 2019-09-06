package org.appsugar.archetypes.data.redis

import io.lettuce.core.RedisClient
import io.lettuce.core.resource.DefaultClientResources
import io.netty.channel.EventLoopGroup
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 配置lettuce线程池
 */

@Configuration
@ConditionalOnClass(RedisClient::class)
class RedisConfiguration {

    @Bean
    fun lettuceClientResources(provider: GlobalEventLoopGroupProvider): DefaultClientResources {
        return DefaultClientResources.builder()
                .eventLoopGroupProvider(provider)
                .eventExecutorGroup(provider.allocate(EventLoopGroup::class.java))
                .build()
    }
}