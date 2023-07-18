package com.scnujxjy.backendpoint.redisTest;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
@Slf4j
public class RedisClusterTest1 {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test1(){
        String test = stringRedisTemplate.opsForValue().get("test");
        log.info("The value of 'test' is: " + test);
    }
}
