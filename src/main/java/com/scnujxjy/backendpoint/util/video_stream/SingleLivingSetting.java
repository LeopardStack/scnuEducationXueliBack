package com.scnujxjy.backendpoint.util.video_stream;

import com.alibaba.fastjson.JSON;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ChannelResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.LiveRequestBody;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ChannelResponseData;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelInfoData;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.polyv.LiveSignUtil;
import com.scnujxjy.backendpoint.util.polyv.PolyvHttpUtil;
import lombok.extern.slf4j.Slf4j;
import net.polyv.common.v1.exception.PloyvSdkException;
import net.polyv.live.v1.config.LiveGlobalConfig;
import net.polyv.live.v1.constant.LiveConstant;
import net.polyv.live.v1.entity.channel.operate.*;
import net.polyv.live.v1.entity.channel.playback.LiveChannelPlaybackSettingRequest;
import net.polyv.live.v1.entity.channel.playback.LiveMergeMp4RecordRequest;
import net.polyv.live.v1.entity.channel.playback.LiveMergeMp4RecordResponse;
import net.polyv.live.v1.entity.web.auth.LiveCreateChannelWhiteListRequest;
import net.polyv.live.v1.entity.web.auth.LiveUpdateChannelAuthRequest;
import net.polyv.live.v1.entity.web.auth.LiveUploadWhiteListRequest;
import net.polyv.live.v1.service.channel.impl.LiveChannelOperateServiceImpl;
import net.polyv.live.v1.service.channel.impl.LiveChannelPlaybackServiceImpl;
import net.polyv.live.v1.service.web.impl.LiveWebAuthServiceImpl;
import net.polyv.live.v2.entity.channel.operate.LiveUpdateChannelRequest;
import net.polyv.live.v2.entity.channel.operate.account.LiveCreateAccountRequest;
import net.polyv.live.v2.entity.channel.operate.account.LiveCreateAccountResponse;
import net.polyv.live.v1.entity.channel.operate.LiveSonChannelInfoListResponse;
import net.polyv.live.v1.entity.channel.playback.LiveChannelPlaybackEnabledInfoRequest;
import net.polyv.live.v1.service.channel.impl.LiveChannelPlaybackServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Component
@Slf4j
public class SingleLivingSetting {

    @Resource
    private VideoStreamUtils videoStreamUtils;

    @Resource
    private MinioService minioService;

    /**
     * 创建频道
     * @param livingRoomTitle 直播间标题
     * @param startDate 直播开始时间
     * @param endDate 直播截止时间
     * @param playRollback 是否开启回放 true 开启
     * @param pureRtcEnabled 使用开启无延迟 Y 开启 N 不开启
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ApiResponse createChannel(String livingRoomTitle, Date startDate, Date endDate,
                                     boolean playRollback, String pureRtcEnabled) throws IOException, NoSuchAlgorithmException {

        LiveRequestBody liveRequestBody = new LiveRequestBody();
        liveRequestBody.setName("有延迟学历教育第一次直播测试");
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

        if(channel.getCode().equals(200)){
            ChannelResponseData channelResponseData = channel.getData();
            Integer channelId = channelResponseData.getChannelId();
            if(playRollback){

                ChannelInfoData channelInfoData = new ChannelInfoData();
                channelInfoData.setChannelId(String.valueOf(channelId));
                channelInfoData.setGlobalSettingEnabled("N");
                channelInfoData.setPlaybackEnabled("Y");
                channelInfoData.setType("list");
//        channelInfoData.setVideoId("27b07c2dc999caefedb9d3e4fb685471_2");
                channelInfoData.setOrigin("vod");

                try {
                    ChannelResponse channelPlayBackInfoResponse =
                            videoStreamUtils.setRecordSetting(channelInfoData);
                    log.info("频道回放信息包括 " + channelPlayBackInfoResponse);
                    if(channelPlayBackInfoResponse.getCode().equals(200)){

                        log.info(channelId + "回放关闭设置成功");
                        log.info("创建的直播间频道 " + channelResponseData.getChannelId() + " 频道密码 " + channelResponseData.getChannelPasswd());
                        return channel;
                    }else{
                        log.info(channelId + "回放关闭设置失败");
                    }
                }catch (Exception e){
                    log.error("设置 (" + channelId + ") 的频道回放信息失败 " + e.toString());
                }
            }

            videoStreamUtils.deleteView(String.valueOf(channelId));
        }else{
            log.error("创建直播间频道失败 " + channel);
        }
        return channel;
    }

   //查询频道信息，返回教师开播链接
    public void testGetChannelInfo(String channelId) throws Exception, NoSuchAlgorithmException {
        LiveChannelInfoRequest liveChannelInfoRequest = new LiveChannelInfoRequest();
        LiveChannelInfoResponse liveChannelInfoResponse;
        try {
            //准备测试数据
            liveChannelInfoRequest.setChannelId(channelId);
            liveChannelInfoResponse = new LiveChannelOperateServiceImpl().getChannelInfo(liveChannelInfoRequest);
//            Assert.assertNotNull(liveChannelInfoResponse);
            if (liveChannelInfoResponse != null) {
                //to do something ......
                log.debug("查询频道信息成功{}", JSON.toJSONString(liveChannelInfoResponse));
                liveChannelInfoResponse.getUrl();
            }
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage(),B
            log.error(e.getMessage(), e);
            // 异常返回做B端异常的业务逻辑，记录log 或者 上报到ETL 或者回滚事务
            throw e;
        } catch (Exception e) {
            log.error("SDK调用异常", e);
            throw e;
        }
    }


    /**
     * 设置指定频道的观看条件
     *
     * @param channelId 需要设置观看条件的频道ID
     * @param body      观看条件的JSON字符串
     * @return 响应字符串
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ResultCode setWatchCondition(String channelId, String body) throws IOException, NoSuchAlgorithmException {
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
//            Assert.assertNotNull(liveUpdateChannelAuthResponse);
            //如果返回结果部位空并且为true，说明修改成功
            if (liveUpdateChannelAuthResponse !=null && liveUpdateChannelAuthResponse) {
                log.info("测试设置白名单观看条件成功");
            }
            return ResultCode.SUCCESS;
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            log.error(e.getMessage(), e);
            // 异常返回做B端异常的业务逻辑，记录log 或者 上报到ETL 或者回滚事务
            throw e;
        } catch (Exception e) {
            log.error("设置指定频道的观看条件接口调用异常", e);
            return null;
        }
    }

    //添加白名单
    public void testCreateChannelWhiteList() throws Exception, NoSuchAlgorithmException {
        LiveCreateChannelWhiteListRequest liveCreateChannelWhiteListRequest = new LiveCreateChannelWhiteListRequest();
        Boolean liveCreateChannelWhiteListResponse;
        try {
            liveCreateChannelWhiteListRequest.setRank(1)
                    .setCode(String.valueOf(System.currentTimeMillis()))
                    .setName("学员1");
            liveCreateChannelWhiteListResponse = new LiveWebAuthServiceImpl().createChannelWhiteList(
                    liveCreateChannelWhiteListRequest);
//            Assert.assertNotNull(liveCreateChannelWhiteListResponse);
            if (liveCreateChannelWhiteListResponse) {
                //to do something ......
                log.debug("测试添加单个白名单-全局白名单成功");
            }
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            log.error(e.getMessage(), e);
            // 异常返回做B端异常的业务逻辑，记录log 或者 上报到ETL 或者回滚事务
            throw e;
        } catch (Exception e) {
            log.error("SDK调用异常", e);
            throw e;
        }
    }

    //通过文件新增白名单
    public void testUploadWhiteList(String channelId, File file) throws Exception, NoSuchAlgorithmException {
        LiveUploadWhiteListRequest liveUploadWhiteListRequest = new LiveUploadWhiteListRequest();
        Boolean liveUploadWhiteListResponse;
        try {
            //path设置为模板文件路径(已填写完数据)
            String path = getClass().getResource("/file/WhiteListTemplate.xls").getPath();
            liveUploadWhiteListRequest.setChannelId(channelId)
                    .setRank(1)
                    .setFile(file);
            liveUploadWhiteListResponse = new LiveWebAuthServiceImpl().uploadWhiteList(liveUploadWhiteListRequest);
//            Assert.assertTrue(liveUploadWhiteListResponse);
            if (liveUploadWhiteListResponse) {
                //to do something ......
                log.debug("测试新增白名单成功");
            }
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            log.error(e.getMessage(), e);
            // 异常返回做B端异常的业务逻辑，记录log 或者 上报到ETL 或者回滚事务
            throw e;
        } catch (Exception e) {
            log.error("SDK调用异常", e);
            throw e;
        }
    }



    /**
     * 修改名字和封面图
     *
     * @param channelId 需要设置观看条件的频道ID
     * @return 响应字符串
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public void testUpdateChannel(String channelId) throws IOException, NoSuchAlgorithmException {
        LiveUpdateChannelRequest liveUpdateChannelRequest = new LiveUpdateChannelRequest();
        Boolean liveUpdateChannelResponse;
        try {
            liveUpdateChannelRequest.setChannelId(channelId)
                    .setName("换名字啦")
                    .setSplashImg("");
            liveUpdateChannelResponse = new LiveChannelOperateServiceImpl().updateChannel(liveUpdateChannelRequest);

            if (liveUpdateChannelResponse!=null && liveUpdateChannelResponse) {
                //to do something ......
                log.debug("测试修改频道设置成功");
            }
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            log.error(e.getMessage(), e);
            // 异常返回做B端异常的业务逻辑，记录log 或者 上报到ETL 或者回滚事务
            throw e;
        } catch (Exception e) {
            log.error("SDK调用异常", e);
            throw e;
        }
    }

    //创建角色
      public void testCreateAccount(String channelId) throws Exception {
        LiveCreateAccountRequest liveCreateAccountRequest = new LiveCreateAccountRequest();
        LiveCreateAccountResponse liveCreateAccountResponse;
        try {
            liveCreateAccountRequest.setChannelId(channelId)
                    .setRole("Assistant")
                    .setActor("助教boy")
                    .setNickName("王助教")
                    .setPurviewList(Arrays.asList(new LiveCreateAccountRequest.Purview().setCode(
                            LiveConstant.RolePurview.CHAT_LIST_ENABLED.getCode())
                            .setEnabled(LiveConstant.Flag.YES.getFlag())));
            liveCreateAccountResponse = new LiveChannelOperateServiceImpl().createAccount(liveCreateAccountRequest);

            if (liveCreateAccountResponse != null) {
                //to do something ......
                log.debug("测试创建角色成功 {}", JSON.toJSONString(liveCreateAccountResponse));
                //TODO 此处创建完成后删除了角色，正式使用需删除该语句
            }
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            log.error(e.getMessage(), e);
            // 异常返回做B端异常的业务逻辑，记录log 或者 上报到ETL 或者回滚事务
            throw e;
        } catch (Exception e) {
            log.error("SDK调用异常", e);
            throw e;
        }
    }

    //查询该助教角色信息
    public void testGetSonChannelInfo(String channelId) throws Exception, NoSuchAlgorithmException {
        LiveSonChannelInfoRequest liveSonChannelInfoRequest = new LiveSonChannelInfoRequest();
        LiveSonChannelInfoResponse liveSonChannelInfoResponse;
        try {
            //准备测试数据
            liveSonChannelInfoRequest.setAccount(channelId).setChannelId(channelId);
            liveSonChannelInfoResponse = new LiveChannelOperateServiceImpl().getSonChannelInfo(
                    liveSonChannelInfoRequest);
            if (liveSonChannelInfoResponse != null) {
                //to do something ......
                log.debug("测试查询角色信息成功{}", JSON.toJSONString(liveSonChannelInfoResponse));
                liveSonChannelInfoResponse.getPushUrl();
            }
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage(),B
            log.error(e.getMessage(), e);
            // 异常返回做B端异常的业务逻辑，记录log 或者 上报到ETL 或者回滚事务
            throw e;
        } catch (Exception e) {
            log.error("SDK调用异常", e);
            throw e;
        }
    }


    public void testUpdateChannelPlaybackSetting(String channelId) throws Exception, NoSuchAlgorithmException {
        LiveChannelPlaybackSettingRequest liveChannelPlaybackSettingRequest;
        Boolean liveChannelPlaybackSettingResponse;
        try {

            //videoId可通过new LiveChannelPlaybackServiceImpl().listChannelVideoLibrary()获取
            List<String> videoIds = new ArrayList<>();
            videoIds.add(channelId);
            liveChannelPlaybackSettingRequest = new LiveChannelPlaybackSettingRequest();
            liveChannelPlaybackSettingRequest.setChannelId(channelId)
                    .setPlaybackEnabled("Y")
                    .setType("single")
                    .setOrigin("playback")
                    .setVideoId(videoIds.get(0))
                    .setSectionEnabled("N")
                    .setChatPlaybackEnabled("N");
            liveChannelPlaybackSettingResponse = new LiveChannelPlaybackServiceImpl().updateChannelPlaybackSetting(
                    liveChannelPlaybackSettingRequest);
//            Assert.assertNotNull(liveChannelPlaybackSettingResponse);
            if (liveChannelPlaybackSettingResponse) {
                //to do something ......
                log.debug("设置频道回放设置成功");
            }
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            log.error(e.getMessage(), e);
            // 异常返回做B端异常的业务逻辑，记录log 或者 上报到ETL 或者回滚事务
            throw e;
        } catch (Exception e) {
            log.error("SDK调用异常", e);
            throw e;
        }
    }


    public void testMergeMp4Record(String channelId) throws Exception, NoSuchAlgorithmException {
        LiveMergeMp4RecordRequest liveMergeMp4RecordRequest = new LiveMergeMp4RecordRequest();
        LiveMergeMp4RecordResponse liveMergeMp4RecordResponse;
        try {

            liveMergeMp4RecordRequest.setChannelId(channelId)
                    .setStartTime(new Date())
                    .setEndTime(new Date())
                    .setCallbackUrl(null)
                    .setFileName("testMergeMp4");
            liveMergeMp4RecordResponse = new LiveChannelPlaybackServiceImpl().mergeMp4Record(liveMergeMp4RecordRequest);
//            Assert.assertNotNull(liveMergeMp4RecordResponse);
            if (liveMergeMp4RecordResponse != null) {
                //to do something ......
                log.debug("测试导出合并的录制文件并回调mp4下载地址成功,{}", JSON.toJSONString(liveMergeMp4RecordResponse));
                liveMergeMp4RecordResponse.getFileUrl();
                File file=new File("");
//                minioService.uploadStreamToMinio(inputStream,fileName,diyBucketName);
            }
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            log.error(e.getMessage(), e);
            // 异常返回做B端异常的业务逻辑，记录log 或者 上报到ETL 或者回滚事务
            throw e;
        } catch (Exception e) {
            log.error("SDK调用异常", e);
            throw e;
        }
    }






    /**
     * 设置指定直播间是否开启回放
     * @param channelId
     * @param playBack
     * @param ident 该参数用来指定回放采用点播 还是 直播缓存
     * @return
     */
    public boolean setPlayBack(String channelId, boolean playBack, boolean ident){
        ChannelInfoData channelInfoData = new ChannelInfoData();
        if(playBack){
            // 开启回放

            if(ident) {
                // 直播缓存

                channelInfoData.setChannelId(channelId);
                channelInfoData.setGlobalSettingEnabled("N");
                channelInfoData.setPlaybackEnabled("Y");
                channelInfoData.setType("list");
//        channelInfoData.setVideoId("27b07c2dc999caefedb9d3e4fb685471_2");
                channelInfoData.setOrigin("playback");
            }else {
                channelInfoData.setChannelId(channelId);
                channelInfoData.setGlobalSettingEnabled("N");
                channelInfoData.setPlaybackEnabled("Y");
                channelInfoData.setType("list");
//        channelInfoData.setVideoId("27b07c2dc999caefedb9d3e4fb685471_2");
                channelInfoData.setOrigin("vod");
            }

        }else{
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
            if(channelPlayBackInfoResponse.getCode().equals(200)){
                log.info("设置成功");
                return true;
            }else{
                return false;
            }
        }catch (Exception e){
            log.error("设置 (" + channelId + ") 的频道回放信息失败 " + e.toString());
        }
        return false;
    }

    /**
     * 获取直播间的状态
     * @param channelId
     * @return
     */
    public boolean getPlayBackState(String channelId){
        LiveChannelPlaybackEnabledInfoRequest liveChannelPlaybackEnabledInfoRequest =
                new LiveChannelPlaybackEnabledInfoRequest();
        String liveChannelPlaybackEnabledInfoResponse;
        try {
            liveChannelPlaybackEnabledInfoRequest.setChannelId(channelId);
            liveChannelPlaybackEnabledInfoResponse = new LiveChannelPlaybackServiceImpl().getChannelPlayBackEnabledInfo(
                    liveChannelPlaybackEnabledInfoRequest);
            if(liveChannelPlaybackEnabledInfoResponse != null){
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
