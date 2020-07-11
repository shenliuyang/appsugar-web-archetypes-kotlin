package org.appsugar.archetypes.data.redis;

import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 使用Repository 与 RedisCache时 注意序列化
 */
public class UserRedisRepositoryTest extends BaseRedisTest {
    @Autowired
    private StringRedisTemplate template;

    @Test
    public void testSetAndGet() {
        val valueOps = template.opsForValue();
        val key = "name";
        val value = "NewYoung";
        valueOps.set(key, value);
        Assertions.assertEquals(value, valueOps.get(key));
    }

}
