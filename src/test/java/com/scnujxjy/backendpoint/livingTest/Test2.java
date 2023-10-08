package com.scnujxjy.backendpoint.livingTest;

import com.scnujxjy.backendpoint.dao.entity.video_stream.ChannelResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.LiveRequestBody;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelInfoResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelInfoData;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelPlayBackInfoResponse;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@Slf4j
public class Test2 {
    @Resource
    private VideoStreamUtils videoStreamUtils;

    /**
     * 获取指定频道 id 下的所有角色信息
     */
    @Test
    public void test1(){
        String channelID = "4247418";
        try {
            ChannelResponse accountsByChannelId = videoStreamUtils.getAccountsByChannelId(channelID);
            log.info(channelID + " 下的角色信息为 \n" + accountsByChannelId.toString());
        }catch (Exception e){
            log.error("获取频道(" + channelID + ") 下的角色信息失败 " + e.toString());
        }
    }

    /**
     * 创建直播间
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void testBasicCreateV4() throws IOException, NoSuchAlgorithmException {

        LiveRequestBody liveRequestBody = new LiveRequestBody();
        liveRequestBody.setName("有延迟学历教育第一次直播测试");
        liveRequestBody.setNewScene("topclass");
        liveRequestBody.setTemplate("ppt");

        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // 将时间戳转换为Date对象
        Date currentDate = Date.from(Instant.ofEpochMilli(beijingTimestamp + 60 * 60 * 1000L));

        // 计算2小时后的时间
        Date twoHoursLaterDate = Date.from(Instant.ofEpochMilli(beijingTimestamp + 2 * 60 * 60 * 1000L));
        // 密码都不设置 让 保利威自行设置
        liveRequestBody.setStartTime(currentDate.getTime());
        liveRequestBody.setEndTime(twoHoursLaterDate.getTime());
        liveRequestBody.setType("normal");


        ApiResponse response = videoStreamUtils.createChannel(liveRequestBody);

        log.info("测试创建单个频道，返回值：{}", response);

        //do other test related things if needed
    }

    /**
     * 默认创建的直播间是没有开启回放的
     */
    @Test
    public void test3(){
        Map<String, Object> stringObjectMap = videoStreamUtils.deleteView("4276203");
        log.info("删除直播间 " + stringObjectMap);
    }

    /**
     * 查询指定频道的详细信息
     */
    @Test
    public void test4(){
        String channelID = "4276449";
        try {
            ChannelInfoResponse channelInfoByChannelId = videoStreamUtils.getChannelInfoByChannelId(channelID);
            log.info("频道信息包括 " + channelInfoByChannelId);
        }catch (Exception e){
            log.error("获取 (" + channelID + ") 的频道信息失败 " + e.toString());
        }
    }

    /**
     * 查询指定频道的回放信息
     */
    @Test
    public void test5(){
        String channelID = "4276449";
        try {
            ChannelPlayBackInfoResponse channelPlayBackInfoResponse = videoStreamUtils.getPlaybackSettingByChannelId(channelID);
            log.info("频道回放信息包括 " + channelPlayBackInfoResponse);
        }catch (Exception e){
            log.error("获取 (" + channelID + ") 的频道信息失败 " + e.toString());
        }
    }

    /**
     * 修改指定频道的回放信息
     *
     * Y list playback 可以设置回放为 回放列表
     * Y list vod 可以设置回放为 点播列表，不要设置 vid
     * Y single 则可以选择 record、playback 和 vod
     */
    @Test
    public void test6(){
        String channelID = "4276449";
        ChannelInfoData channelInfoData = new ChannelInfoData();
        channelInfoData.setChannelId(channelID);
        channelInfoData.setGlobalSettingEnabled("N");
        channelInfoData.setPlaybackEnabled("Y");
        channelInfoData.setType("list");
//        channelInfoData.setVideoId("27b07c2dc999caefedb9d3e4fb685471_2");
        channelInfoData.setOrigin("vod");

        try {
            ChannelResponse channelPlayBackInfoResponse =
                    videoStreamUtils.setRecordSetting(channelInfoData);
            log.info("频道回放信息包括 " + channelPlayBackInfoResponse);
        }catch (Exception e){
            log.error("设置 (" + channelID + ") 的频道回放信息失败 " + e.toString());
        }
    }

}
