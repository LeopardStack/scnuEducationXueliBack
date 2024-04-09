package com.scnujxjy.backendpoint.livingBatchOperationTest;

import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingService;
import com.scnujxjy.backendpoint.service.video_stream.VideoStreamRecordService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private VideoStreamRecordService videoStreamRecordService;

    @Resource
    private SingleLivingService singleLivingService;

    @Test
    public void test1(){
        List<VideoStreamRecordPO> videoStreamRecordPOS = videoStreamRecordService.getBaseMapper().selectList(null);

        for(VideoStreamRecordPO videoStreamRecordPO : videoStreamRecordPOS){
            try {
                String channelId = videoStreamRecordPO.getChannelId();
                SaResult saResult = singleLivingService.setRecordSetting(new ChannelInfoRequest()
                        .setChannelId(channelId)
                        .setPlaybackEnabled("N")
                );
            }catch (Exception e){
                log.error(videoStreamRecordPO.getChannelId() + "  设置回放失败 ");
            }
        }


    }
}
