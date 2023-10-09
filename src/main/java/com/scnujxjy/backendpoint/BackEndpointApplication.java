package com.scnujxjy.backendpoint;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.SM3;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author leopard
 */
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class BackEndpointApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackEndpointApplication.class, args);
    }

    /**
     * 注入摘要加密算法
     *
     * @return sm3
     */
    @Bean
    public SM3 sm3() {
        return SmUtil.sm3();
    }
}
