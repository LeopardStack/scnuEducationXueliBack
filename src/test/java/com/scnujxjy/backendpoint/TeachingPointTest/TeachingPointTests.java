package com.scnujxjy.backendpoint.TeachingPointTest;

import cn.hutool.core.collection.ListUtil;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class TeachingPointTests {
    @Autowired
    private PlatformUserService platformUserService;

    @Test
    void testCreateUser() {
        List<PlatformUserVO> platformUserVOS = platformUserService.batchCreateUser(ListUtil.of(PlatformUserRO
                .builder()
                .roleId(7L)
                .username("XW001")
                .password("XW0012023@")
                .name("宝安教学点管理员")
                .build()));
        // 2023-11-08 20:31:41.998 [main]  INFO  c.s.b.TeachingPointTest.TeachingPointTests - 新增用户结果：[PlatformUserVO(userId=76870, roleId=7, avatarImagePath=null, password=54e453bf2265ccd99d5ff09c95755d4ec5f80877333c38e259c600279679568f, username=XW001, name=宝安教学点管理员, wechatOpenId=null)]
        log.info("新增用户结果：{}", platformUserVOS);
    }

}
