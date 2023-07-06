package com.scnujxjy.backendpoint.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.url}")
    private String url;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Bean
    public MinioClient generateMinioClient() {
        MinioClient ret = MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
        System.out.println("开始注入 Minio ");
        System.out.println(ret);
        return ret;
    }
}

