package com.scnujxjy.backendpoint.service.video_stream;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.ro.video_stream.VideoStreamRecordRO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordVO;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class VideoStreamRecordTests {

    @Autowired
    private VideoStreamRecordService videoStreamRecordService;

    @Autowired
    private VideoStreamUtils videoStreamUtils;

    @Test
    void testGenerate() {
        log.info("现在时间是：{}，测试开始", LocalDateTimeUtil.now());
        List<VideoStreamRecordRO> videoStreamRecordROS = ListUtil.of(VideoStreamRecordRO.builder().courseScheduleId(1L).build());
        List<List<VideoStreamRecordVO>> generateVideoStream = videoStreamRecordService.generateVideoStream(videoStreamRecordROS);
        log.info("创建直播间信息：{}", generateVideoStream);
    }

    @Test
    void testDelete() {
        log.info("现在时间是：{}，测试开始", LocalDateTimeUtil.now());
        Map<String, Object> map = videoStreamUtils.deleteView("4239049");
        log.info("删除结果：{}", map.toString());
    }

    @Test
    void testBasicInfo() {
        log.info("现在时间是：{}，测试开始", LocalDateTimeUtil.now());
        ChannelResponseBO channelBasicInfo = videoStreamUtils.getChannelBasicInfo("4240569");
        log.info("查询结果：{}", channelBasicInfo);
    }
}
