package org.appsugar.archetypes;

import io.netty.channel.EventLoopGroup;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorResourceFactory;

@Configuration
@AutoConfigureBefore
public class WebConfiguration {

    /**
     * 共享EventLoopGroup
     * 共享给项目内其它使用到netty的框架, 减少系统线程数量从而提高性能.
     */
    @Bean
    public EventLoopGroup eventLoopGroup(ReactorResourceFactory factory) {
        return factory.getLoopResources().onServer(true);
    }

}
