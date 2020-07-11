package org.appsugar.archetypes.data.redis;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.appsugar.archetypes.BaseTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import redis.embedded.RedisServer;

/**
 * 启动redis做单元测试
 */
@DataRedisTest
@Slf4j
public abstract class BaseRedisTest extends BaseTest {
    private static RedisServer redisServer;

    @BeforeAll
    @SneakyThrows
    public static void init() {
        int port = 6379;
        log.info("trying to start embedded redis listen to " + port);
        redisServer = new RedisServer(port);
        redisServer.start();
    }

    @AfterAll
    @SneakyThrows
    public static void destroy() {
        log.info("trying to stop  embedded redis");
        RedisServer server = redisServer;
        redisServer = null;
        if (server != null) {
            server.stop();
        }
    }
}
