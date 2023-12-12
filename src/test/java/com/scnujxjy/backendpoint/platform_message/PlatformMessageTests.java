package com.scnujxjy.backendpoint.platform_message;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.UserUploadsPO;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.UserUploadsMapper;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

@SpringBootTest
@Slf4j
public class PlatformMessageTests {

    @Autowired
    private PlatformMessageMapper platformMessageMapper;

    @Autowired
    private UserUploadsMapper userUploadsMapper;

    @Autowired
    private PlatformUserService platformUserService;

    /**
     * 清洗数据
     */
    @Test
    void changePlatformMessageUsernameToUserId() {
        List<PlatformMessagePO> platformMessagePOS = platformMessageMapper.selectList(null);
        if (CollUtil.isNotEmpty(platformMessagePOS)) {
            platformMessagePOS.forEach(platformMessagePO -> {
                if (Objects.nonNull(platformMessagePO.getUserId())) {
                    Long userId = platformUserService.getUserIdByUsername(platformMessagePO.getUserId());
                    if (Objects.nonNull(userId)) {
                        int updated = platformMessageMapper.update(null, Wrappers.<PlatformMessagePO>lambdaUpdate()
                                .eq(PlatformMessagePO::getId, platformMessagePO.getId())
                                .set(PlatformMessagePO::getUserId, String.valueOf(userId)));
                        log.info("更新数量：{}", updated);
                    }
                }
            });
        }
    }

    @Test
    void changeUserUploadUsernameToUserId() {
        List<UserUploadsPO> userUploadsPOS = userUploadsMapper.selectList(null);
        if (CollUtil.isNotEmpty(userUploadsPOS)) {
            userUploadsPOS.forEach(userUploadsPO -> {
                if (Objects.nonNull(userUploadsPO.getUserId())) {
                    Long userId = platformUserService.getUserIdByUsername(userUploadsPO.getUserId());
                    if (Objects.nonNull(userId)) {
                        userUploadsMapper.update(null, Wrappers.<UserUploadsPO>lambdaUpdate()
                                .eq(UserUploadsPO::getId, userUploadsPO.getId())
                                .set(UserUploadsPO::getUserId, String.valueOf(userId)));
                    }
                }
            });
        }
    }
}
