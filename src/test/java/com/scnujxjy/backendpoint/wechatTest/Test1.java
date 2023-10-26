package com.scnujxjy.backendpoint.wechatTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

@SpringBootTest
@Slf4j
public class Test1 {
    @Value("${wechat.app-secret}")
    private String wechatAppSecret;

    @Value("${wechat.app-id}")
    private String wechatAppId;

    @Value("${wechat.request-url}")
    private String requestUrl;

    @Test
    public void test1(){
        String code = "0a3Bk01w3CAyB13F781w3gc3jr0Bk01q";
        // openId oJ02w4lDTzcTzEak79fyb-RrH_OU
        // openId oJ02w4lDTzcTzEak79fyb-RrH_OU
        String url = requestUrl
                + "?appid=" + wechatAppId
                + "&secret=" + wechatAppSecret
                + "&js_code=" + code
                + "&grant_type=authorization_code";

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        log.info("请求微信的响应结果 \n" + response);
        // {"errcode":40163,"errmsg":"code been used, rid: 6538ac7d-18c7e09b-572e68c3"}
        // 使用Jackson库解析返回的JSON
        ObjectMapper mapper = new ObjectMapper();
        try {
            // 这里仅作为示例，假设返回的JSON有一个openId字段
            // 实际上，您可能需要创建一个POJO类来映射返回的JSON结构
            String openId = mapper.readTree(response).get("openid").asText();
            // 查找或存储该openId在您的数据库中
            // 返回相应的用户信息和token给前端
            log.info("该用户的 openId 为 " + openId);
        } catch (Exception e) {
            throw new RuntimeException("解析微信响应失败", e);
        }

    }
}
