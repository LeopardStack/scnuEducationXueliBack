package com.scnujxjy.backendpoint.config;

import net.polyv.live.v1.config.LiveGlobalConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 默认启动配置类
 *
 * @author: thomas
 **/
@Component
public class StartupListener implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(StartupListener.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        String userId = "27b07c2dc9";
        String appId = "gj95rpxjhf";
        String appSecret = "a642eb8a7e8f425995d9aead5bdd83ea";
        try {
            LiveGlobalConfig.init(appId, userId, appSecret);
            logger.info("--保利威直播 SDK 初始化完成--");
        } catch (Exception e) {
            logger.error("--保利威直播 SDK 初始化失败--");
        }
    }


}
