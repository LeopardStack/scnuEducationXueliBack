package com.scnujxjy.backendpoint.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import com.scnujxjy.backendpoint.util.filter.UserFilterArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {


    // 自定义参数解析器 用于将前端传递过来的用户群体实体 进行不同实现类的映射
//    private final UserFilterArgumentResolver userFilterArgumentResolver;
//
//    public CorsConfig(UserFilterArgumentResolver userFilterArgumentResolver) {
//        this.userFilterArgumentResolver = userFilterArgumentResolver;
//    }
//
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//        resolvers.add(userFilterArgumentResolver);
//    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new IPWhiteListInterceptor()).addPathPatterns("/**");

        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**")
        .excludePathPatterns("/platform-user/login")
        ;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*");
    }
}

