package com.scnujxjy.backendpoint.livingTest;

import com.scnujxjy.backendpoint.service.SingleLivingService;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class TutorLinkTest {

    @Resource
    private SingleLivingService singleLivingService;

    /**
     * 获取指定助教老师的单点登录链接
     */
    public void test1(){
//        singleLivingService.getTutorChannelUrl()
    }
}
