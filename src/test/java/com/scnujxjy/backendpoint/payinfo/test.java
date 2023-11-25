package com.scnujxjy.backendpoint.payinfo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
@Slf4j
public class test {

    @Test
    public void test2(){
        RestTemplate restTemplate=new RestTemplate();
        String url="http://gx1.szhtkj.com.cn/micro/OrderTradeJson.aspx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("op", "GetSingleTrade");
        params.add("orderNo", "1203162959");
        params.add("jylxh", "");
        params.add("xmpch", "004-2014050001");
        params.add("sign", "d537e2d4a0f960a255c147be94d4c3a9");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        String response = restTemplate.postForObject(url, requestEntity, String.class);
        log.info("获取返回的数据:{}", response);
    }

    @Test
    public void test3(){
        RestTemplate restTemplate=new RestTemplate();
        String url="http://gx1.szhtkj.com.cn/micro/payAccept.aspx";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("orderDate", "20231123001510");
        params.add("orderNo", "2311220004");
        params.add("amount", "0.01");
        params.add("xmpch", "004-2014050001");
        params.add("return_url", "http://www.test.com/returnPage.htm");
        params.add("notify_url", "http://www.test.com/notifyPage.htm");
        params.add("sign", "8fa93f9eb38bfe6232d3872b0e34b39d");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        String response = restTemplate.postForObject(url, requestEntity, String.class);
        log.info("获取返回的数据:{}", response);
    }



    public static void main(String[] args) {
        String a="orderDate=20231124224310&orderNo=2411220001&amount=0.01&xmpch=004-2014050001" +
                "&return_url=http://www.baidu.com&notify_url=http://zvupvu.natappfree.cc/pay/haha" +
                "umz4aea6g97skeect0jtxigvjkrimd0o";
        String abc = getMD5(a);
        System.out.println(abc);
    }
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
            return null;
        }
    }
}
