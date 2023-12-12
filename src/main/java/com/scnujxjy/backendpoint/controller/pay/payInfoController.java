package com.scnujxjy.backendpoint.controller.pay;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import static com.scnujxjy.backendpoint.util.tool.MD5Util.getMD5;


@RestController
@RequestMapping("/pay")
@Slf4j
public class payInfoController {
//    @Resource
//    private RestTemplate restTemplate;


    @PostMapping("/queryPayInfo")
    public void queryPayInfo()  {
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
        RestTemplate restTemplate=new RestTemplate();
        String response = restTemplate.postForObject(url, requestEntity, String.class);
        String a="orderNo=1203162959&jylxh=&xmpch=004-2014050001" + "umz4aea6g97skeect0jtxigvjkrimd0o";
    }

    @PostMapping("/payInfo")
    public void payInfo()  {
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
        RestTemplate restTemplate=new RestTemplate();
        String response = restTemplate.postForObject(url, requestEntity, String.class);
        String a="orderNo=1203162959&jylxh=&xmpch=004-2014050001" + "umz4aea6g97skeect0jtxigvjkrimd0o";
    }

    public static void main(String[] args) {
        String a="orderDate=20231125224310&orderNo=2311250002&amount=0.01&xmpch=004-2014050001" +
                "&return_url=http://www.baidu.com&notify_url=http://zvupvu.natappfree.cc/pay/haha" +
                "umz4aea6g97skeect0jtxigvjkrimd0o";
        String abc = getMD5(a);
        System.out.println(abc);
    }

    @PostMapping("/haha")
    public void test(@RequestBody Object c)  {
      log.info("接收到的回调信息为："+c);
      //https://www.baidu.com/?orderDate=20231124184257&orderNo=2411220001&amount=0.01&jylsh=231124000110&tranStat=1&return_type=1&payMethod=WX&tradeNo=pt231124SZHTT000004&sign=e71ded46f0d531d7b89cd726378d6d71`
    }
}
