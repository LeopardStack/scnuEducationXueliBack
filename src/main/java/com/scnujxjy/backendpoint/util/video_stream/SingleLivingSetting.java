package com.scnujxjy.backendpoint.util.video_stream;

import com.alibaba.fastjson.JSON;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ChannelResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.LiveRequestBody;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ChannelResponseData;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelInfoData;
import com.scnujxjy.backendpoint.util.polyv.LiveSignUtil;
import com.scnujxjy.backendpoint.util.polyv.PolyvHttpUtil;
import lombok.extern.slf4j.Slf4j;
import net.polyv.common.v1.exception.PloyvSdkException;
import net.polyv.live.v1.config.LiveGlobalConfig;
import net.polyv.live.v1.entity.channel.operate.LiveSonChannelInfoListResponse;
import net.polyv.live.v1.entity.channel.playback.LiveChannelPlaybackEnabledInfoRequest;
import net.polyv.live.v1.service.channel.impl.LiveChannelPlaybackServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SingleLivingSetting {

    @Resource
    private VideoStreamUtils videoStreamUtils;

    /**
     * 创建频道
     *
     * @param livingRoomTitle 直播间标题
     * @param startDate       直播开始时间
     * @param endDate         直播截止时间
     * @param playRollback    是否开启回放 true 开启
     * @param pureRtcEnabled  使用开启无延迟 Y 开启 N 不开启
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ApiResponse createChannel(String livingRoomTitle, Date startDate, Date endDate,
                                     boolean playRollback, String pureRtcEnabled) throws IOException, NoSuchAlgorithmException {

        LiveRequestBody liveRequestBody = new LiveRequestBody();
        liveRequestBody.setName(livingRoomTitle);
        liveRequestBody.setNewScene("topclass");
        liveRequestBody.setTemplate("ppt");
        // 设置是否开启无延迟
        liveRequestBody.setPureRtcEnabled(pureRtcEnabled);

        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);


        // 密码都不设置 让 保利威自行设置
        liveRequestBody.setStartTime(startDate.getTime());
        liveRequestBody.setEndTime(endDate.getTime());
        liveRequestBody.setType("normal");

        //公共参数,填写自己的实际
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();

        //业务参数
        String url = "http://api.polyv.net/live/v4/channel/create";

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        requestMap.put("timestamp", timestamp);

        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("name", liveRequestBody.getName());
        bodyMap.put("newScene", liveRequestBody.getNewScene());
        bodyMap.put("template", liveRequestBody.getTemplate());
        bodyMap.put("channelPasswd", liveRequestBody.getChannelPasswd());
        bodyMap.put("seminarHostPassword", liveRequestBody.getSeminarHostPassword());
        bodyMap.put("seminarAttendeePassword", liveRequestBody.getSeminarAttendeePassword());
        bodyMap.put("pureRtcEnabled", liveRequestBody.getPureRtcEnabled());
        bodyMap.put("type", liveRequestBody.getType());
        bodyMap.put("doubleTeacherType", liveRequestBody.getDoubleTeacherType());
        bodyMap.put("cnAndEnLiveEnabled", liveRequestBody.getCnAndEnLiveEnabled());
        bodyMap.put("splashImg", liveRequestBody.getSplashImg());
        bodyMap.put("linkMicLimit", "" + liveRequestBody.getLinkMicLimit());
        bodyMap.put("categoryId", "" + liveRequestBody.getCategoryId());
        bodyMap.put("startTime", "" + liveRequestBody.getStartTime());
        bodyMap.put("endTime", "" + liveRequestBody.getEndTime());
//        bodyMap.put("subAccount", "" + liveRequestBody.getSubAccount());
        bodyMap.put("customTeacherId", "" + liveRequestBody.getCustomTeacherId());

        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));

        String body = JSON.toJSONString(bodyMap);
        url = PolyvHttpUtil.appendUrl(url, requestMap);
        log.info("保利威创建直播间请求URL " + url);
        String response = PolyvHttpUtil.postJsonBody(url, body, null);

        ApiResponse channel = JSON.parseObject(response, ApiResponse.class);

        if (channel.getCode().equals(200)) {
            ChannelResponseData channelResponseData = channel.getData();

            String channelID = String.valueOf(channelResponseData.getChannelId());
            try {
                // 升级默认助教权限
                LiveSonChannelInfoListResponse roleInfo = videoStreamUtils.getRoleInfo(channelID);
                // 获取这个唯一助教的账号
                String account = roleInfo.getSonChannelInfos().get(0).getAccount();


                // 将这个默认助教设置为最高权限
                boolean b = videoStreamUtils.generateTutor(channelID, "李四", "123456");
                if (b) {
                    log.info("生成助教成功!");
                    String s = videoStreamUtils.generateTutorSSOLink(channelID, account);
                    log.info("助教的单点登录链接为 " + s);
                }
            } catch (Exception e) {
                log.error("生成助教失败" + e.toString());
            }


            if (playRollback) {

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
                    if (channelPlayBackInfoResponse.getCode().equals(200)) {

                        log.info(channelID + "回放关闭设置成功");
                        log.info("创建的直播间频道 " + channelResponseData.getChannelId() + " 频道密码 " + channelResponseData.getChannelPasswd());

                        return channel;
                    } else {
                        log.info(channelID + "回放关闭设置失败");
                    }
                } catch (Exception e) {
                    log.error("设置 (" + channelID + ") 的频道回放信息失败 " + e.toString());
                }
            }

        } else {
            log.error("创建直播间频道失败 " + channel);
        }
        return channel;
    }


    /**
     * 设置指定直播间是否开启回放
     *
     * @param channelId
     * @param playBack
     * @param ident     该参数用来指定回放采用点播 还是 直播缓存
     * @return
     */
    public boolean setPlayBack(String channelId, boolean playBack, boolean ident) {
        ChannelInfoData channelInfoData = new ChannelInfoData();
        if (playBack) {
            // 开启回放

            if (ident) {
                // 直播缓存

                channelInfoData.setChannelId(channelId);
                channelInfoData.setGlobalSettingEnabled("N");
                channelInfoData.setPlaybackEnabled("Y");
                channelInfoData.setType("list");
//        channelInfoData.setVideoId("27b07c2dc999caefedb9d3e4fb685471_2");
                channelInfoData.setOrigin("record");
            } else {
                channelInfoData.setChannelId(channelId);
                channelInfoData.setGlobalSettingEnabled("N");
                channelInfoData.setPlaybackEnabled("Y");
                channelInfoData.setType("list");
//        channelInfoData.setVideoId("27b07c2dc999caefedb9d3e4fb685471_2");
                channelInfoData.setOrigin("vod");
            }

        } else {
            // 关闭回放
            channelInfoData.setChannelId(channelId);
            channelInfoData.setGlobalSettingEnabled("N");
            channelInfoData.setPlaybackEnabled("N");
            channelInfoData.setType("list");
//        channelInfoData.setVideoId("27b07c2dc999caefedb9d3e4fb685471_2");
            channelInfoData.setOrigin("playback");
        }
        try {
            ChannelResponse channelPlayBackInfoResponse =
                    videoStreamUtils.setRecordSetting(channelInfoData);
            log.info("频道回放信息包括 " + channelPlayBackInfoResponse);
            if (channelPlayBackInfoResponse.getCode().equals(200)) {
                log.info("设置成功");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            log.error("设置 (" + channelId + ") 的频道回放信息失败 " + e.toString());
        }
        return false;
    }

    /**
     * 获取直播间的状态
     *
     * @param channelId
     * @return
     */
    public boolean getPlayBackState(String channelId) {
        LiveChannelPlaybackEnabledInfoRequest liveChannelPlaybackEnabledInfoRequest =
                new LiveChannelPlaybackEnabledInfoRequest();
        String liveChannelPlaybackEnabledInfoResponse;
        try {
            liveChannelPlaybackEnabledInfoRequest.setChannelId(channelId);
            liveChannelPlaybackEnabledInfoResponse = new LiveChannelPlaybackServiceImpl().getChannelPlayBackEnabledInfo(
                    liveChannelPlaybackEnabledInfoRequest);
            if (liveChannelPlaybackEnabledInfoResponse != null) {
                if ("Y".equals(liveChannelPlaybackEnabledInfoResponse)) {
                    //to do something ......
                    log.debug("测试查询频道的回放开关状态成功{}", liveChannelPlaybackEnabledInfoResponse);
                    return true;
                }
            }
            return false;
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error("SDK调用异常", e);
        }
        return false;
    }




}
