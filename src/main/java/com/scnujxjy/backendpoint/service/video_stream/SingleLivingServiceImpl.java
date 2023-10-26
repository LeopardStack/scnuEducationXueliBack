package com.scnujxjy.backendpoint.service.video_stream;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ChannelResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.LiveRequestBody;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ChannelResponseData;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelInfoData;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;
import com.scnujxjy.backendpoint.service.SingleLivingService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.polyv.LiveSignUtil;
import com.scnujxjy.backendpoint.util.polyv.PolyvHttpUtil;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.polyv.common.v1.exception.PloyvSdkException;
import net.polyv.live.v1.config.LiveGlobalConfig;
import net.polyv.live.v1.constant.LiveConstant;
import net.polyv.live.v1.entity.channel.operate.LiveChannelSettingRequest;
import net.polyv.live.v1.entity.channel.operate.LiveDeleteChannelListRequest;
import net.polyv.live.v1.entity.web.auth.LiveCreateChannelWhiteListRequest;
import net.polyv.live.v1.entity.web.auth.LiveUpdateChannelAuthRequest;
import net.polyv.live.v1.service.channel.impl.LiveChannelOperateServiceImpl;
import net.polyv.live.v1.service.web.impl.LiveWebAuthServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Slf4j
public class SingleLivingServiceImpl implements SingleLivingService {


    @Override
    public SaResult createChannel(ChannelCreateRequestBO channelCreateRequestBO) throws IOException, NoSuchAlgorithmException {

        SaResult saResult = new SaResult();
        LiveRequestBody liveRequestBody = new LiveRequestBody();
        liveRequestBody.setName(channelCreateRequestBO.getLivingRoomTitle());
        liveRequestBody.setNewScene("topclass");
        liveRequestBody.setTemplate("ppt");
        // 设置是否开启无延迟
        liveRequestBody.setPureRtcEnabled(channelCreateRequestBO.getPureRtcEnabled());

        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // 密码都不设置 让 保利威自行设置
        liveRequestBody.setStartTime(channelCreateRequestBO.getStartDate().getTime());
        liveRequestBody.setEndTime(channelCreateRequestBO.getEndDate().getTime());
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
        bodyMap.put("customTeacherId", "" + liveRequestBody.getCustomTeacherId());

        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));

        String body = JSON.toJSONString(bodyMap);
        url = PolyvHttpUtil.appendUrl(url, requestMap);
        log.info("保利威创建直播间请求参数:{},url地址为:{} ", requestMap, url);
        String response = PolyvHttpUtil.postJsonBody(url, body, null);

        ApiResponse channel = JSON.parseObject(response, ApiResponse.class);

        //设置频道直播间白名单，只有设置了频道白名单，后续才能设置白名单观看。
        try {
            if (Objects.nonNull(channel.getData().getChannelId())) {
                CreateChannelWhiteList(channel.getData().getChannelId().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置指定频道的观看条件
        if (Objects.nonNull(channel.getData().getChannelId())) {
            setWatchCondition(channel.getData().getChannelId().toString());
        }

        saResult.setCode(ResultCode.SUCCESS.getCode());
        saResult.setMsg(ResultCode.SUCCESS.getMessage());
        saResult.setData(channel);
        return saResult;
    }


    @Override
    public SaResult testGetChannelInfo() {
        return null;
    }

    @Override
    public SaResult setWatchCondition() {
        return null;
    }

    /**
     * 修改频道回放设置
     *
     * @param channelId 请求参数
     * @return 返回的JSON字符串
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ChannelResponse setRecordSetting(String channelId,String playbackEnabled) throws IOException, NoSuchAlgorithmException {
        ChannelInfoData channelInfoData = new ChannelInfoData();
        channelInfoData.setChannelId(channelId);
        channelInfoData.setGlobalSettingEnabled("N");
        channelInfoData.setPlaybackEnabled(playbackEnabled);
        channelInfoData.setType("list");
//        channelInfoData.setVideoId("27b07c2dc999caefedb9d3e4fb685471_2");
        channelInfoData.setOrigin("vod");


        String url = "http://api.polyv.net/live/v3/channel/playback/set-setting";

        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // Constructing the request parameters
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", LiveGlobalConfig.getAppId());
        requestMap.put("timestamp", timestamp);
        requestMap.put("channelId", channelInfoData.getChannelId() == null ? "" : channelInfoData.getChannelId());
        requestMap.put("globalSettingEnabled", channelInfoData.getGlobalSettingEnabled() == null ? "" : channelInfoData.getGlobalSettingEnabled());
        requestMap.put("crontabType", channelInfoData.getCrontType() == null ? "" : channelInfoData.getCrontType());
        requestMap.put("startTime", channelInfoData.getStartTime() == null ? "" : "" + channelInfoData.getStartTime());
        requestMap.put("endTime", channelInfoData.getEndTime() == null ? "" : "" + channelInfoData.getEndTime());
        requestMap.put("playbackEnabled", channelInfoData.getPlaybackEnabled() == null ? "" : channelInfoData.getPlaybackEnabled());
        requestMap.put("type", channelInfoData.getType() == null ? "" : channelInfoData.getType());
        requestMap.put("origin", channelInfoData.getOrigin() == null ? "" : channelInfoData.getOrigin());
        requestMap.put("videoId", channelInfoData.getVideoId() == null ? "" : channelInfoData.getVideoId());
        requestMap.put("sectionEnabled", channelInfoData.getSectionEnabled() == null ? "" : channelInfoData.getSectionEnabled());
        requestMap.put("chatPlaybackEnabled", channelInfoData.getChatPlaybackEnabled() == null ? "" : channelInfoData.getChatPlaybackEnabled());
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, LiveGlobalConfig.getAppSecret()));

        log.info("请求参数  " + requestMap);
        ChannelResponse playbackResponse = null;
        try {
            String response = PolyvHttpUtil.postFormBody(url, requestMap);  // assuming PolyvHttpUtil can be used here
            log.info("回放设置返回值 \n" + response);
            // 解析响应为 PlaybackSettingResponse POJO
            playbackResponse = JSON.parseObject(response, new TypeReference<ChannelResponse>() {
            });
        } catch (Exception e) {
            log.error("设置频道 (" + channelInfoData.getChannelId() + ") 的回放参数失败 " + e.toString());
        }

        return playbackResponse;

    }


    /**
     * 设置指定频道的观看条件
     *
     * @param channelId 需要设置观看条件的频道ID
     * @return 响应字符串
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ResultCode setWatchCondition(String channelId) {
        LiveUpdateChannelAuthRequest liveUpdateChannelAuthRequest = new LiveUpdateChannelAuthRequest();
        Boolean liveUpdateChannelAuthResponse;
        try {
            LiveChannelSettingRequest.AuthSetting authSetting = new LiveChannelSettingRequest.AuthSetting().setAuthType(
                    LiveConstant.AuthType.PHONE.getDesc())
                    .setRank(1)
                    .setEnabled("Y");
            List<LiveChannelSettingRequest.AuthSetting> authSettings = new ArrayList<>();
            authSettings.add(authSetting);

            liveUpdateChannelAuthRequest.setChannelId(channelId)
                    .setAuthSettings(authSettings);
            liveUpdateChannelAuthResponse = new LiveWebAuthServiceImpl().updateChannelAuth(
                    liveUpdateChannelAuthRequest);
            //如果返回结果不为空并且为true，说明修改成功
            if (liveUpdateChannelAuthResponse != null && liveUpdateChannelAuthResponse) {
                log.info("设置白名单观看条件成功");
                return ResultCode.SUCCESS;
            }
            return ResultCode.FAIL;
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            e.printStackTrace();
        } catch (Exception e) {
            log.error("设置频道的白名单接口调用异常", e);
        }
        return ResultCode.FAIL;
    }


    //添加单个白名单
    public ResultCode CreateChannelWhiteList(String channelId) throws Exception {
        LiveCreateChannelWhiteListRequest liveCreateChannelWhiteListRequest = new LiveCreateChannelWhiteListRequest();
        Boolean liveCreateChannelWhiteListResponse;
        try {
            liveCreateChannelWhiteListRequest.setRank(1)
                    .setChannelId(channelId)
                    .setCode("441424")
                    .setName("学员1");
            liveCreateChannelWhiteListResponse = new LiveWebAuthServiceImpl().createChannelWhiteList(
                    liveCreateChannelWhiteListRequest);
//            Assert.assertNotNull(liveCreateChannelWhiteListResponse);
            if (liveCreateChannelWhiteListResponse) {
                //to do something ......
                log.info("测试添加单个白名单-全局白名单成功");
                return ResultCode.SUCCESS;
            }
            return ResultCode.FAIL;
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            log.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            log.error("SDK调用异常", e);
            throw e;
        }
    }

    //批量删除直播间频道
    public ResultCode testDeleteChannelList(String[] channelIds ) throws Exception, NoSuchAlgorithmException {
        LiveDeleteChannelListRequest liveDeleteChannelListRequest = new LiveDeleteChannelListRequest();
        Boolean liveDeleteChannelListResponse;
        try {
            //准备测试数据
            liveDeleteChannelListRequest.setChannelIds(channelIds);
            liveDeleteChannelListResponse = new LiveChannelOperateServiceImpl().deleteChannelList(
                    liveDeleteChannelListRequest);
            if (liveDeleteChannelListResponse!=null && liveDeleteChannelListResponse) {
                log.info("批量删除频道成功");
                return ResultCode.SUCCESS;
            }
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage(),B
            e.printStackTrace();
        } catch (Exception e) {
            log.error("调用批量删除直播间接口异常", e);
            throw e;
        }
        return ResultCode.FAIL;
    }

}
