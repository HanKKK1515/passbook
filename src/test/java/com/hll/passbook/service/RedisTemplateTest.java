package com.hll.passbook.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * <h1> Redis 客户端测试</h1>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTemplateTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedisTemplate() {
        // 清空 redis ，谨慎操作
        redisTemplate.execute((RedisCallback<Object>) redisConnection -> {
           redisConnection.flushAll();
           return null;
        });

        assert redisTemplate.opsForValue().get("name") == null;

        redisTemplate.opsForValue().set("name", "Hello Redis!");

        assert redisTemplate.opsForValue().get("name") != null;

        System.out.println(redisTemplate.opsForValue().get("name"));
    }
}
