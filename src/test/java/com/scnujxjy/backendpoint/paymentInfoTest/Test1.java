package com.scnujxjy.backendpoint.paymentInfoTest;

import cn.hutool.http.HttpUtil;
import com.scnujxjy.backendpoint.service.core_data.PaymentInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private PaymentInfoService paymentInfoService;

    @Test
    public void test1(){
        String url = "http://gx1.szhtkj.com.cn/micro/payAccept.aspx";

        // 构建表单
        Map<String, String> signRequest = new HashMap<>();
        signRequest.put("orderDate", "20231122224310");
        signRequest.put("orderNo", "2311220004");
        signRequest.put("amount", "0.01");
        signRequest.put("xmpch", "004-2014050001");
        signRequest.put("return_url", "http://www.test.com/returnPage.htm");
        signRequest.put("notify_url", "http://www.test.com/notifyPage.htm");
        signRequest.put("sign", "f703bd452f772360024c2934c883cd33");
        String response = HttpUtil.post(url, new HashMap<>(signRequest));
        log.info(response);
    }

    @Test
    public void test2(){
        ResponseEntity<String> response = paymentInfoService.makePayment();
        log.info(response.toString());
    }
}
