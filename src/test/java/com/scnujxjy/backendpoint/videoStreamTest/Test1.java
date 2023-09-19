package com.scnujxjy.backendpoint.videoStreamTest;

import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private VideoStreamUtils videoStreamUtils;

    /**
     * 获取指定频道号的角色信息
     */
    @Test
    public void test1(){
        try {
            videoStreamUtils.getAccountsByChannelId("4252181");
        }catch (Exception e){
            log.error(e.toString());
        }

    }
}
