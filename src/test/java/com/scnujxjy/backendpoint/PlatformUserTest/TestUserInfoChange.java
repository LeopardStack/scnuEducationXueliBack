package com.scnujxjy.backendpoint.PlatformUserTest;

import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class TestUserInfoChange {

    @Resource
    private PlatformUserService platformUserService;

    @Test
    public void changePassword(){
        Boolean aBoolean = platformUserService.changePassword(6L, "123456");
        log.info("修改密码 " + aBoolean);
    }
}
