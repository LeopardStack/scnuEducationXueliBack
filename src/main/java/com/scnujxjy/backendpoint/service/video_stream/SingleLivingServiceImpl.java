package com.scnujxjy.backendpoint.service.video_stream;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ChannelResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.LiveRequestBody;
import com.scnujxjy.backendpoint.dao.entity.video_stream.TutorInformation;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelInfoData;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.TutorInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoResponse;
import com.scnujxjy.backendpoint.model.vo.video_stream.StudentWhiteListVO;
import com.scnujxjy.backendpoint.service.SingleLivingService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.polyv.LiveSignUtil;
import com.scnujxjy.backendpoint.util.polyv.PolyvHttpUtil;
import lombok.extern.slf4j.Slf4j;
import net.polyv.common.v1.exception.PloyvSdkException;
import net.polyv.live.v1.config.LiveGlobalConfig;
import net.polyv.live.v1.constant.LiveConstant;
import net.polyv.live.v1.entity.channel.operate.LiveChannelInfoRequest;
import net.polyv.live.v1.entity.channel.operate.LiveChannelInfoResponse;
import net.polyv.live.v1.entity.channel.operate.LiveChannelSettingRequest;
import net.polyv.live.v1.entity.channel.operate.LiveDeleteChannelRequest;
import net.polyv.live.v1.entity.channel.playback.LiveMergeChannelVideoAsyncRequest;
import net.polyv.live.v1.entity.channel.playback.LiveMergeMp4RecordRequest;
import net.polyv.live.v1.entity.channel.playback.LiveMergeMp4RecordResponse;
import net.polyv.live.v1.entity.web.auth.LiveCreateChannelWhiteListRequest;
import net.polyv.live.v1.entity.web.auth.LiveUpdateChannelAuthRequest;
import net.polyv.live.v1.entity.web.auth.LiveUploadWhiteListRequest;
import net.polyv.live.v1.service.channel.impl.LiveChannelOperateServiceImpl;
import net.polyv.live.v1.service.channel.impl.LiveChannelPlaybackServiceImpl;
import net.polyv.live.v1.service.web.impl.LiveWebAuthServiceImpl;
import net.polyv.live.v2.entity.channel.account.LiveChannelBasicInfoV2Request;
import net.polyv.live.v2.entity.channel.account.LiveChannelBasicInfoV2Response;
import net.polyv.live.v2.entity.channel.operate.LiveUpdateChannelRequest;
import net.polyv.live.v2.entity.channel.operate.account.LiveCreateAccountRequest;
import net.polyv.live.v2.entity.channel.operate.account.LiveCreateAccountResponse;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
@Slf4j
public class SingleLivingServiceImpl implements SingleLivingService {

    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;
    @Resource
    private TutorInformationMapper tutorInformationMapper;

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

        //设置频道直播间白名单，只有添加了白名单，后续才能设置观看条件为白名单。
        try {
            if (channel.getCode()==200 && Objects.nonNull(channel.getData().getChannelId())) {
                String channelId=channel.getData().getChannelId().toString();
                File whiteListFile = createWhiteListFile();
                SaResult saResult1 = UploadWhiteList(whiteListFile,channelId);
                if (saResult1.getCode()==200){
                    //设置指定频道的观看条件为白名单
                    log.info("批量添加白名单成功");
                    if (Objects.nonNull(channel.getData().getChannelId())) {
                        SaResult saResult2 = setWatchCondition(channel.getData().getChannelId().toString());
                        if (saResult2.getCode()==200){
                            log.info("设置白名单观看条件成功");
                            saResult.setCode(ResultCode.SUCCESS.getCode());
                            saResult.setMsg(ResultCode.SUCCESS.getMessage());
                            saResult.setData(channel);
                            return saResult;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
    }

    private File createWhiteListFile() {
        String templateFilePath = "temporaryWhiteList.xls";
//        studentStatusMapper.getScheduleClassStudent();//检查sql后采用查询的返回的List
        List<StudentWhiteListVO> StudentWhiteListVOS=new ArrayList<>();
        StudentWhiteListVO studentWhiteListVO1=new StudentWhiteListVO();
        studentWhiteListVO1.setCode("111");
        studentWhiteListVO1.setName("aaaa");
        StudentWhiteListVO studentWhiteListVO2=new StudentWhiteListVO();
        studentWhiteListVO2.setCode("222");
        studentWhiteListVO2.setName("bbbb");
        StudentWhiteListVOS.add(studentWhiteListVO1);
        StudentWhiteListVOS.add(studentWhiteListVO2);
        EasyExcel.write(templateFilePath, StudentWhiteListVO.class).sheet("Sheet1").doWrite(StudentWhiteListVOS);
        return new File(templateFilePath);
    }

    @Override
    public SaResult deleteChannel(String channelId) throws IOException, NoSuchAlgorithmException {
        SaResult saResult = new SaResult();
        LiveDeleteChannelRequest liveDeleteChannelRequest = new LiveDeleteChannelRequest();
        Boolean liveDeleteChannelResponse;
        try {
            liveDeleteChannelRequest.setChannelId(channelId);
            liveDeleteChannelResponse = new LiveChannelOperateServiceImpl().deleteChannel(liveDeleteChannelRequest);
            if (liveDeleteChannelResponse != null && liveDeleteChannelResponse) {
                log.info("批量删除频道成功");
                saResult.setCode(ResultCode.SUCCESS.getCode());
                saResult.setMsg(ResultCode.SUCCESS.getMessage());
                return saResult;
            }
        } catch (PloyvSdkException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error("调用批量删除接口异常", e);
            throw e;
        }
        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
    }

    //    /**
//     * 设置指定频道的观看条件
//     *
//     * @param channelId 需要设置观看条件的频道ID
//     * @return 响应字符串
//     * @throws IOException
//     * @throws NoSuchAlgorithmException
//     */
//    @Override
    public SaResult setWatchCondition(String channelId) {
        SaResult saResult = new SaResult();
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
                saResult.setMsg(ResultCode.SUCCESS.getMessage());
                saResult.setCode(ResultCode.SUCCESS.getCode());
                return saResult;
            }
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            e.printStackTrace();
        } catch (Exception e) {
            log.error("设置频道的白名单接口调用异常", e);
        }
        saResult.setMsg(ResultCode.FAIL.getMessage());
        saResult.setCode(ResultCode.FAIL.getCode());
        return saResult;
    }

    /**
     * 修改频道回放设置
     *
     * @param
     * @return 返回的JSON字符串
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Override
    public SaResult setRecordSetting(ChannelInfoRequest request) throws IOException, NoSuchAlgorithmException {
        SaResult saResult = new SaResult();
        ChannelInfoData channelInfoData = new ChannelInfoData();
        channelInfoData.setChannelId(request.getChannelId());
        channelInfoData.setGlobalSettingEnabled("N");
        channelInfoData.setPlaybackEnabled(request.getPlaybackEnabled());
        channelInfoData.setType("single");
//        channelInfoData.setVideoId("27b07c2dc999caefedb9d3e4fb685471_2");
        channelInfoData.setOrigin("record");

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
            saResult.setCode(ResultCode.SUCCESS.getCode());
            saResult.setMsg(ResultCode.SUCCESS.getMessage());
            saResult.setData(playbackResponse);
            return saResult;
        } catch (Exception e) {
            log.error("设置频道 (" + channelInfoData.getChannelId() + ") 的回放参数失败 " + e);
        }
        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        saResult.setData(playbackResponse);
        return saResult;

    }

    @Override
    public SaResult getTeacherChannelUrl(String channelId) {
        SaResult saResult = new SaResult();
        ChannelInfoResponse channelInfoResponse = new ChannelInfoResponse();
        LiveChannelInfoRequest liveChannelInfoRequest = new LiveChannelInfoRequest();
        LiveChannelInfoResponse liveChannelInfoResponse;
        try {
            liveChannelInfoRequest.setChannelId(channelId);
            liveChannelInfoResponse = new LiveChannelOperateServiceImpl().getChannelInfo(liveChannelInfoRequest);
            if (liveChannelInfoResponse != null) {
                //to do something ......K4zqkS
                log.debug("查询频道信息成功{}", JSON.toJSONString(liveChannelInfoResponse));
                saResult.setCode(ResultCode.SUCCESS.getCode());
                saResult.setMsg(ResultCode.SUCCESS.getMessage());
                channelInfoResponse.setUrl("https://live.polyv.net/web-start/login?channelId=" + channelId);
                channelInfoResponse.setPassword(liveChannelInfoResponse.getChannelPasswd());
                saResult.setData(channelInfoResponse);
                return saResult;
            }
        } catch (PloyvSdkException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error("调用教师单点登录链接接口异常", e);
        }
        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
    }

    @Override
    public SaResult getStudentChannelUrl(String channelId) {
        SaResult saResult = new SaResult();
        ChannelInfoResponse channelInfoResponse = new ChannelInfoResponse();
        channelInfoResponse.setUrl("https://live.polyv.cn/watch/" + channelId);
        saResult.setCode(ResultCode.SUCCESS.getCode());
        saResult.setMsg(ResultCode.SUCCESS.getMessage());
        saResult.setData(channelInfoResponse);
        return saResult;
    }

    @Override
    public SaResult getTutorChannelUrl(String channelId) {
        SaResult saResult = new SaResult();
        ChannelInfoResponse channelInfoResponse = new ChannelInfoResponse();

        QueryWrapper<VideoStreamRecordPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("channel_id", channelId);
        VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(queryWrapper);
        if (StrUtil.isNotBlank(videoStreamRecordPO.getTutorUrl())) {
            channelInfoResponse.setUrl(videoStreamRecordPO.getTutorUrl());
        }
        if (StrUtil.isNotBlank(videoStreamRecordPO.getTutorPasswd())) {
            channelInfoResponse.setPassword(videoStreamRecordPO.getTutorPasswd());
        }
        saResult.setCode(ResultCode.SUCCESS.getCode());
        saResult.setMsg(ResultCode.SUCCESS.getMessage());
        saResult.setData(channelInfoResponse);
        return saResult;
    }

    //创建直播间时默认创建一个最高权限的助教老师
    @Override
    public SaResult createTutor(String channelId, String tutorName) {
        SaResult saResult = new SaResult();
        LiveCreateAccountRequest liveCreateAccountRequest = new LiveCreateAccountRequest();
        LiveCreateAccountResponse liveCreateAccountResponse;
        ChannelInfoResponse channelInfoResponse = new ChannelInfoResponse();
        try {
            liveCreateAccountRequest.setChannelId(channelId)
                    .setRole("Assistant")
                    .setActor("助教")
                    .setNickName(tutorName)
//            chatListEnabled：在线列表（仅支持助教）
//            pageTurnEnabled：翻页（仅支持助教，且仅能设置一个助教有翻页权限）
//            monitorEnabled：监播（仅支持助教，且仅能设置一个助教有监播权限）
//            chatAuditEnabled：聊天审核（仅支持助教）


                    .setPurviewList(Arrays.asList(new LiveCreateAccountRequest.Purview().setCode(
                            LiveConstant.RolePurview.CHAT_LIST_ENABLED.getCode())
//                            .setCode(LiveConstant.RolePurview.PAGE_TURN_ENABLED.getCode())
                            .setEnabled(LiveConstant.Flag.YES.getFlag())));
            liveCreateAccountResponse = new LiveChannelOperateServiceImpl().createAccount(liveCreateAccountRequest);
            if (liveCreateAccountResponse != null) {
//                https://console.polyv.net/live/login.html?channelId=0024368180
                log.info("创建角色成功 {}", JSON.toJSONString(liveCreateAccountResponse));
                channelInfoResponse.setPassword(liveCreateAccountResponse.getPasswd());
                channelInfoResponse.setUrl("https://console.polyv.net/live/login.html?channelId=" + liveCreateAccountResponse.getAccount());
                //返回助教信息，同时插入助教表
                TutorInformation tutorInformation=new TutorInformation();
                tutorInformation.setTutorUrl("https://console.polyv.net/live/login.html?channelId=" + liveCreateAccountResponse.getAccount());
                tutorInformation.setTutorName(tutorName);
                tutorInformation.setChannelId(channelId);
                tutorInformation.setUserId("123123");
                int insert = tutorInformationMapper.insert(tutorInformation);
                if (insert>0) {
                    saResult.setCode(ResultCode.SUCCESS.getCode());
                    saResult.setMsg(ResultCode.SUCCESS.getMessage());
                    saResult.setData(channelInfoResponse);
                    return saResult;
                }
            }
        } catch (PloyvSdkException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error("创建助教接口调用异常", e);
        }
        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
    }

    public SaResult UploadWhiteList(File file,String channelId) {
        SaResult saResult=new SaResult();
        LiveUploadWhiteListRequest liveUploadWhiteListRequest = new LiveUploadWhiteListRequest();
        Boolean liveUploadWhiteListResponse;
        try {
            liveUploadWhiteListRequest.setChannelId(channelId)
                    .setRank(1)
                    .setFile(file);
            liveUploadWhiteListResponse = new LiveWebAuthServiceImpl().uploadWhiteList(liveUploadWhiteListRequest);

            if (liveUploadWhiteListResponse!=null && liveUploadWhiteListResponse) {
                boolean delete = file.delete();
                if (delete){
                    log.info("删除文件成功");
                }
                saResult.setCode(ResultCode.SUCCESS.getCode());
                saResult.setMsg(ResultCode.SUCCESS.getMessage());
                return saResult;
            }
        } catch (PloyvSdkException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error("调用批量增加白名单接口异常", e);
        }
        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
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


    /**
     * 修改名字和封面图
     *
     * @param
     * @return 响应字符串
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Override
    public SaResult UpdateChannelNameAndImg(ChannelInfoRequest channelInfoRequest) {
        SaResult saResult = new SaResult();
        LiveUpdateChannelRequest liveUpdateChannelRequest = new LiveUpdateChannelRequest();
        Boolean liveUpdateChannelResponse;
        try {
            liveUpdateChannelRequest.setChannelId(channelInfoRequest.getChannelId())
                    .setName(channelInfoRequest.getChannelName())
                    .setSplashImg(channelInfoRequest.getImgUrl());
            liveUpdateChannelResponse = new LiveChannelOperateServiceImpl().updateChannel(liveUpdateChannelRequest);

            if (liveUpdateChannelResponse != null && liveUpdateChannelResponse) {
                log.info("测试修改频道名字和封面图设置成功");
                saResult.setCode(ResultCode.SUCCESS.getCode());
                saResult.setMsg(ResultCode.SUCCESS.getMessage());
                return saResult;
            }
        } catch (PloyvSdkException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error("调用修改名字和封面图接口调用异常", e);
        }
        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
    }

    public void testMergeMp4Record(String channelId) {
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
                String fileUrl = liveMergeMp4RecordResponse.getFileUrl();
                File file = new File(fileUrl);
//                minioService.uploadStreamToMinio(inputStream,fileName,diyBucketName);
            }
        } catch (PloyvSdkException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error("导出合并的录制文件接口调用异常", e);
        }
    }

    public void testMergeChannelVideoAsync() throws Exception, NoSuchAlgorithmException {
        LiveMergeChannelVideoAsyncRequest liveMergeChannelVideoAsyncRequest = new LiveMergeChannelVideoAsyncRequest();
        Boolean liveMergeChannelVideoAsyncResponse;
        try {
            liveMergeChannelVideoAsyncRequest.setChannelId("4368180")
                    .setFileIds("gq2or4951o")
                    .setFileName("测试合并-可删除")
                    .setCallbackUrl(null)
                    .setAutoConvert("Y")
                    .setMergeMp4("Y");
            liveMergeChannelVideoAsyncResponse = new LiveChannelPlaybackServiceImpl().mergeChannelVideoAsync(
                    liveMergeChannelVideoAsyncRequest);
            if (liveMergeChannelVideoAsyncResponse != null && liveMergeChannelVideoAsyncResponse) {
                log.info("测试异步合并直播录制文件,具体是否成功以回调为准");
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

    //live：直播中 playback：回放中 end：已结束 waiting：等待中 unStart：未开始
    @Override
    public SaResult GetChannelDetail(String channelId) {
        SaResult saResult=new SaResult();
        LiveChannelBasicInfoV2Request liveChannelBasicInfoV2Request = new LiveChannelBasicInfoV2Request();
        LiveChannelBasicInfoV2Response liveChannelBasicInfoV2Response;
        try {
            liveChannelBasicInfoV2Request.setChannelId(channelId);
            liveChannelBasicInfoV2Response = new LiveChannelOperateServiceImpl().getChannelDetail(
                    liveChannelBasicInfoV2Request);
            if (liveChannelBasicInfoV2Response != null) {
                log.info("观看夜返回码" + liveChannelBasicInfoV2Response.getWatchStatus());
                log.info("观看夜返回描述" + liveChannelBasicInfoV2Response.getWatchStatusText());
                saResult.setCode(ResultCode.SUCCESS.getCode());
                saResult.setMsg(ResultCode.SUCCESS.getMessage());
                return saResult;
            }
        } catch (PloyvSdkException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error("查询调用异常", e);
        }
        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
    }

}
