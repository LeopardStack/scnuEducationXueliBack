package com.scnujxjy.backendpoint.config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author leopard
 */
@Configuration
@MapperScan("com.scnujxjy.backendpoint.mapper")
public class MybatisPlusConfig {

}
