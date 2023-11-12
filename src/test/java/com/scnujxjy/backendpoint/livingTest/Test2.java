package com.scnujxjy.backendpoint.livingTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ChannelResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.LiveRequestBody;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelInfoResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ChannelResponseData;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelInfoData;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelPlayBackInfoResponse;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.util.video_stream.SingleLivingSetting;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.polyv.live.v1.constant.LiveConstant;
import net.polyv.live.v1.entity.channel.operate.LiveChannelSettingRequest;
import net.polyv.live.v1.entity.channel.operate.LiveSonChannelInfoListResponse;
import net.polyv.live.v1.entity.web.auth.LiveCreateChannelWhiteListRequest;
import net.polyv.live.v1.entity.web.auth.LiveUpdateChannelAuthRequest;
import net.polyv.live.v1.service.web.impl.LiveWebAuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class Test2 {
    @Resource
    private VideoStreamUtils videoStreamUtils;

    @Resource
    private SingleLivingSetting singleLivingSetting;

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;

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
        String channelID = "4417539";
        String channelID1 = "4400659";
        try {
            ChannelInfoResponse channelInfoByChannelId = videoStreamUtils.getChannelInfo(channelID);
            ChannelInfoResponse channelInfoByChannelId1 = videoStreamUtils.getChannelInfo(channelID1);
            log.info("频道信息包括 " + channelInfoByChannelId);
            log.info("频道信息包括1 " + channelInfoByChannelId1);
            if(channelInfoByChannelId1.getCode().equals(200) && channelInfoByChannelId1.getSuccess()){
                log.info("创建频道成功");
            }
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
     * 获取讲师的单点登录链接
     */
    @Test
    public void testTeacherLogin(){
        String channelID = "4400659";
        try {
            String s = videoStreamUtils.generateTeacherSSOLink(channelID);
            ChannelInfoResponse channelInfoByChannelId = videoStreamUtils.getChannelInfo(channelID);
            log.info("频道信息" + channelInfoByChannelId);
            log.info("单点登录链接" + s);
        }catch (Exception e){
            log.error(e.toString());
        }
    }

    /**
     * 获取指定频道下的角色信息
     */
    @Test
    public void testGetChannelRoleInfo(){
        String channelID = "4401416";
        try {
            LiveSonChannelInfoListResponse roleInfo = videoStreamUtils.getRoleInfo(channelID);
            // 获取这个唯一助教的账号
            String account = roleInfo.getSonChannelInfos().get(0).getAccount();


            // 将这个默认助教设置为最高权限
            boolean b = videoStreamUtils.generateTutor(channelID, "李四",  "123456");
            if(b){
                log.info("生成助教成功!");
                String s = videoStreamUtils.generateTutorSSOLink(channelID, account);
                log.info("助教的单点登录链接为 " + s);
            }
            log.info("角色信息" + roleInfo);
        }catch (Exception e){
            log.error(e.toString());
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
        String channelID = "4416383";
        ChannelInfoData channelInfoData = new ChannelInfoData();
        channelInfoData.setChannelId(channelID);
        channelInfoData.setGlobalSettingEnabled("N");
        channelInfoData.setPlaybackEnabled("Y");
        channelInfoData.setType("single");
//        channelInfoData.setVideoId("27b07c2dc999caefedb9d3e4fb685471_2");
        channelInfoData.setOrigin("record");

        try {
            ChannelResponse channelPlayBackInfoResponse =
                    videoStreamUtils.setRecordSetting(channelInfoData);
            log.info("频道回放信息包括 " + channelPlayBackInfoResponse);
            if(channelPlayBackInfoResponse.getCode().equals(200)){
                log.info("设置成功");
            }else{
                log.info("设置失败");
            }
        }catch (Exception e){
            log.error("设置 (" + channelID + ") 的频道回放信息失败 " + e.toString());
        }
    }

    @Test
    public void testLivingRootCreate() throws IOException, NoSuchAlgorithmException {
        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);
        // 将时间戳转换为Date对象
        Date currentDate = Date.from(Instant.ofEpochMilli(beijingTimestamp + 60 * 60 * 1000L));

        // 计算2小时后的时间
        Date twoHoursLaterDate = Date.from(Instant.ofEpochMilli(beijingTimestamp + 2 * 60 * 60 * 1000L));
        try {
            ApiResponse channel = singleLivingSetting.createChannel("测试简单直播间", currentDate, twoHoursLaterDate, true,
                    "N");
            log.info(channel.toString());
            if(channel.getCode().equals(200)){
                ChannelResponseData channelResponseData = channel.getData();
                log.info("创建的直播间频道 " + channelResponseData.getChannelId() + " 频道密码 " + channelResponseData.getChannelPasswd());
            }
        }catch (Exception e){
            log.info("创建简单直播间失败 " + e.toString());
        }
    }

    @Test
    public void testDeleteLivingRoom(){
        Map<String, Object> stringObjectMap = videoStreamUtils.deleteView("4327258");
        if(stringObjectMap.containsKey("code") && stringObjectMap.get("code").equals(200)){
            log.info("删除直播间成功");
        }else{
            log.info("删除直播间失败");
        }
        log.info(stringObjectMap.toString());
    }

    @Test
    public void TestWhiteList(){
        String channelId = "4327520";

        LiveUpdateChannelAuthRequest liveUpdateChannelAuthRequest = new LiveUpdateChannelAuthRequest();
        LiveCreateChannelWhiteListRequest liveCreateChannelWhiteListRequest = new LiveCreateChannelWhiteListRequest();
        Boolean liveUpdateChannelAuthResponse;
        try {
            liveCreateChannelWhiteListRequest.setCode("454467");
            liveCreateChannelWhiteListRequest.setName("阿龙");
            LiveChannelSettingRequest.AuthSetting authSetting = new LiveChannelSettingRequest.AuthSetting().setAuthType(
                            LiveConstant.AuthType.CODE.getDesc())
                    .setRank(1)
                    .setEnabled("Y")
                    .setAuthType("phone")
                    .setAuthTips("请输入你的身份证号码")
                    .setQcodeImg("https://live.polyv.net/static/images/live-header-logo.png");
            List<LiveChannelSettingRequest.AuthSetting> authSettings =
                    new ArrayList<LiveChannelSettingRequest.AuthSetting>();
            authSettings.add(authSetting);

            liveUpdateChannelAuthRequest.setChannelId(channelId)
                    .setAuthSettings(authSettings);
            liveUpdateChannelAuthResponse = new LiveWebAuthServiceImpl().updateChannelAuth(
                    liveUpdateChannelAuthRequest);
            if (liveUpdateChannelAuthResponse) {
                log.info("测试设置观看条件成功");
            }else{
                log.info("测试设置观看条件失败");
            }
            log.info(liveUpdateChannelAuthResponse.toString());
        }catch (Exception e){
            log.error(e.toString());
        }
    }


    /**
     * 分析 videoStreamRecord 表里 有哪些直播间被删除了
     */
    @Test
    public void test11(){

        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(null);
        List<VideoStreamRecordPO> videoStreamRecordPOS = new ArrayList<>();
        for(CourseSchedulePO courseSchedulePO : courseSchedulePOS){
            String onlinePlatform = courseSchedulePO.getOnlinePlatform();
            try{
                VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(new LambdaQueryWrapper<VideoStreamRecordPO>()
                        .eq(VideoStreamRecordPO::getChannelId, Long.parseLong(onlinePlatform)));
                videoStreamRecordPOS.add(videoStreamRecordPO);
            }catch (Exception e){
                log.error(e.toString());
            }

        }


        List<String> uniqueChannelIds = videoStreamRecordPOS.stream()
                .map(VideoStreamRecordPO::getChannelId) // 将 VideoStreamRecordPO 映射到它的 channelId
                .distinct() // 去除重复的 channelId
                .collect(Collectors.toList()); // 收集到一个新的 List 中

        for(String channelID : uniqueChannelIds){
            try {
                ChannelInfoResponse channelInfoByChannelId = videoStreamUtils.getChannelInfo(channelID);
                log.info("频道信息包括 " + channelInfoByChannelId);
                if(channelInfoByChannelId.getCode().equals(200) && channelInfoByChannelId.getSuccess()){

                }else{
                    log.info("该频道不存在 " + channelID);
                }
            }catch (Exception e){
                log.error("获取 (" + channelID + ") 的频道信息失败 " + e.toString());
            }
        }

    }

}
