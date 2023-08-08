package com.scnujxjy.backendpoint.service.basic;

import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.digest.SM3;
import cn.hutool.crypto.symmetric.SM4;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;

@SpringBootTest
@Slf4j
public class PlatformUserServiceTests {
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
}
