package com.scnujxjy.backendpoint.util.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
public class MD5Util {

    public static String getMD5(String input) {
        try {
            // 创建MD5实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 将输入字符串转换为字节数组并计算摘要
            byte[] messageDigest = md.digest(input.getBytes());

            // 将摘要结果转换为16进制字符串
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder hashText = new StringBuilder(no.toString(16));
            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }
            return hashText.toString();
        } catch (NoSuchAlgorithmException e) {
            // 处理异常
            log.info("MD5加密失败");
            return null;
        }
    }

}
