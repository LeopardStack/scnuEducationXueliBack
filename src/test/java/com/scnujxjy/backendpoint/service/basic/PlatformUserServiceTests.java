package com.scnujxjy.backendpoint.service.basic;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.SM3;
import cn.hutool.crypto.symmetric.SM4;
import com.google.common.collect.Sets;
import com.scnujxjy.backendpoint.constant.enums.PermissionSourceEnum;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.util.List;
import java.util.Set;

@SpringBootTest
@Slf4j
public class PlatformUserServiceTests {
    @Resource
    private PlatformUserService platformUserService;

    @Test
    void testSM() {
        String content = "123456";
        SM4 sm4 = SmUtil.sm4();
        SecretKey secretKey = sm4.getSecretKey();
        log.info("sm4 密钥：{}，长度：{}", secretKey.toString(), secretKey.toString().length());
        String encryptHex = sm4.encryptHex(content);
        log.info("sm4 对称加密后的密文：{}", encryptHex);
        String decryptStr = sm4.decryptStr(encryptHex);
        log.info("sm4 解密后的原文：{}", decryptStr);
        SM3 sm3 = SmUtil.sm3();
        String digestHex = sm3.digestHex(content);
        log.info("sm3 摘要算法密文：{}", digestHex);
    }

    @Test
    void testBatchCreateUser() {
        List<PlatformUserRO> platformUserROS = ListUtil.of(PlatformUserRO.builder()
                        .roleId(1L)
                        .username("admin")
                        .password("admin")
                        .build(),
                PlatformUserRO.builder()
                        .roleId(2L)
                        .username("liweitang")
                        .password("liweitang")
                        .build());
        log.info("插入前的信息：{}", platformUserROS);
        List<PlatformUserVO> platformUserVOS = platformUserService.batchCreateUser(platformUserROS);
        log.info("插入后的信息：{}", platformUserVOS);
    }

    @Test
    void testSelectUsernameByPermissionSource() {
        Set<String> usernameList = platformUserService.selectUsernameByPermissionResource(Sets.newHashSet(PermissionSourceEnum.APPROVAL_APPROVAL.getPermissionSource(), PermissionSourceEnum.APPROVAL_WATCH.getPermissionSource()));
        log.info("username list {}", usernameList);
    }
}
