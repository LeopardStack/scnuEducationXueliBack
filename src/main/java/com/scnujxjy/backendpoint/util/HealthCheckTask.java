package com.scnujxjy.backendpoint.util;

import com.scnujxjy.backendpoint.mapper.basic.PlatformUserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class HealthCheckTask {
    private static final Logger logger = LoggerFactory.getLogger(HealthCheckTask.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired(required = false)
    private PlatformUserMapper platformUserMapper;

    @Scheduled(fixedRate = 100000)  // 每100秒执行一次
    public void checkConnections() {
        try {
            // 尝试向Redis执行一个简单的命令
            stringRedisTemplate.opsForValue().get("health_check");
            // 从Redis中获取键为"your_key"的值
            stringRedisTemplate.opsForValue().set("test", "2023", 100L, TimeUnit.SECONDS);
            String test = stringRedisTemplate.opsForValue().get("test");
            logger.info("The value of 'test' is: " + test);

            // 如果没有异常则打印连接成功的日志
            logger.info("Redis连接成功");
        } catch (Exception e) {
            // 如果有异常则打印连接失败的日志
            logger.error("Redis连接失败 " + e.toString());
        }

        try {
            // 尝试向MySQL执行一个简单的SQL
            platformUserMapper.healthCheck();
            // 如果没有异常则打印连接成功的日志
            logger.info("MySQL连接成功");
        } catch (Exception e) {
            // 如果有异常则打印连接失败的日志
            logger.error("MySQL连接失败");
        }
    }
}

