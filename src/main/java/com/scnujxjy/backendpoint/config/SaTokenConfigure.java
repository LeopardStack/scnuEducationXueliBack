package com.scnujxjy.backendpoint.config;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.basic.SaBasicUtil;
import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    @Value("${scnu-cors.open}")
    private boolean corsOpen;

    // 注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。

        if(corsOpen){
            registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                    .addPathPatterns("/**")
                    .excludePathPatterns("/platform-user/login");
        }else{
            // 注册 Sa-Token 拦截器，打开注解式鉴权功能
            registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
        }
    }

//    @Bean
//    public SaServletFilter getSaServletFilter() {
//        return new SaServletFilter()
//                .addInclude("/**").addExclude("/favicon.ico")
//                .setAuth(obj -> {
//                    SaRouter.match("/platform-user/**", StpUtil::checkActiveTimeout);
//                });
//    }

}

