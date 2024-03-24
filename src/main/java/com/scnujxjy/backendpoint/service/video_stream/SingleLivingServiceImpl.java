package com.scnujxjy.backendpoint.service.video_stream;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.LiveResourcesPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.*;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ViewStudentResponse.Content;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ViewStudentResponse.ViewFirstStudentResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelInfoData;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesLearningMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.LiveResourceMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.TutorInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.inverter.video_stream.VideoStreamInverter;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.*;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.model.vo.video_stream.AttendanceVO;
import com.scnujxjy.backendpoint.model.vo.video_stream.StudentWhiteListVO;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.polyv.HttpUtil;
import com.scnujxjy.backendpoint.util.polyv.LiveSignUtil;
import com.scnujxjy.backendpoint.util.polyv.PolyvHttpUtil;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.polyv.common.v1.exception.PloyvSdkException;
import net.polyv.live.v1.config.LiveGlobalConfig;
import net.polyv.live.v1.constant.LiveConstant;
import net.polyv.live.v1.entity.channel.operate.LiveChannelSettingRequest;
import net.polyv.live.v1.entity.channel.operate.LiveDeleteChannelRequest;
import net.polyv.live.v1.entity.channel.playback.LiveListChannelSessionInfoRequest;
import net.polyv.live.v1.entity.channel.playback.LiveListChannelSessionInfoResponse;
import net.polyv.live.v1.entity.web.auth.*;
import net.polyv.live.v1.service.channel.impl.LiveChannelOperateServiceImpl;
import net.polyv.live.v1.service.channel.impl.LiveChannelPlaybackServiceImpl;
import net.polyv.live.v1.service.web.impl.LiveWebAuthServiceImpl;
import net.polyv.live.v2.entity.channel.account.LiveChannelBasicInfoV2Request;
import net.polyv.live.v2.entity.channel.account.LiveChannelBasicInfoV2Response;
import net.polyv.live.v2.entity.channel.operate.LiveUpdateChannelRequest;
import net.polyv.live.v2.entity.channel.operate.account.LiveCreateAccountRequest;
import net.polyv.live.v2.entity.channel.operate.account.LiveCreateAccountResponse;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SingleLivingServiceImpl implements SingleLivingService {

    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;
    @Resource
    private TutorInformationMapper tutorInformationMapper;
    @Resource
    private StudentStatusMapper studentStatusMapper;
    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private TeacherInformationMapper teacherInformationMapper;

    @Resource
    private VideoStreamInverter videoStreamInverter;

    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private VideoStreamUtils videoStreamUtils;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Resource
    private CoursesLearningMapper coursesLearningMapper;

    @Resource
    private LiveResourceMapper liveResourceMapper;


    @Override
    public SaResult createChannel(ChannelCreateRequestBO channelCreateRequestBO, CourseSchedulePO courseSchedulePO) throws IOException, NoSuchAlgorithmException {
        SaResult saResult = new SaResult();
        LiveRequestBody liveRequestBody = new LiveRequestBody();
        liveRequestBody.setName(channelCreateRequestBO.getLivingRoomTitle());
        liveRequestBody.setNewScene("topclass");
        liveRequestBody.setTemplate("ppt");
        liveRequestBody.setCategoryId(510209);//设置直播分类为学历教育。 510210是非学历培训，默认分类486269
        // 设置是否开启无延迟
        liveRequestBody.setPureRtcEnabled(channelCreateRequestBO.getPureRtcEnabled());

        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // 密码都不设置 让保利威自行设置
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
            if (channel.getCode() == 200 && Objects.nonNull(channel.getData().getChannelId())) {
                String channelId = channel.getData().getChannelId().toString();

                File whiteListFile = createWhiteListFile(courseSchedulePO);
                SaResult saResult1 = UploadWhiteList(whiteListFile, channelId);
                if (saResult1.getCode() == 200) {
                    //设置指定频道的观看条件为白名单
                    log.info("批量添加白名单成功");
                    if (Objects.nonNull(channel.getData().getChannelId())) {
                        SaResult saResult2 = setWatchCondition(channel.getData().getChannelId().toString());
                        if (saResult2.getCode() == 200) {
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
            log.error("调用创建直播间异常，异常信息为" + e);
        }

        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
    }


    @Override
    public SaResult getChannelCardPush(ChannelViewRequest channelViewRequest) throws IOException, NoSuchAlgorithmException {
        log.info("获取请求观看数据接口入参为:{}", channelViewRequest);
        SaResult saResult = new SaResult();

        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        String timestamp = String.valueOf(System.currentTimeMillis());
        //业务参数
        String url = String.format("http://api.polyv.net/live/v2/statistics/%s/viewlog", channelViewRequest.getChannelId());

        //http 调用逻辑
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        requestMap.put("timestamp", timestamp);
        if (StrUtil.isNotBlank(channelViewRequest.getCurrentDay())) {
            String currentDay = channelViewRequest.getCurrentDay();
            requestMap.put("currentDay", currentDay);
        }
        if (StrUtil.isNotBlank(channelViewRequest.getPage())) {
            requestMap.put("page", channelViewRequest.getPage());
        }
        if (StrUtil.isNotBlank(channelViewRequest.getPageSize())) {
            requestMap.put("pageSize", channelViewRequest.getPageSize());
        }
        if (StrUtil.isNotBlank(channelViewRequest.getStartTime()) && StrUtil.isNotBlank(channelViewRequest.getEndTime())) {
            //对时间数据进行处理
            String startTime = channelViewRequest.getStartTime();
            LocalDateTime startDateTime = LocalDateTime.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            long startTimestamp = startDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
            String endTime = channelViewRequest.getEndTime();
            LocalDateTime endDateTime = LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            long endTimestamp = endDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();

            requestMap.put("startTime", String.valueOf(startTimestamp));
            requestMap.put("endTime", String.valueOf(endTimestamp));
        }
        if (StrUtil.isNotBlank(channelViewRequest.getParam1())) {
            requestMap.put("param1", channelViewRequest.getParam1());
        }
        if (StrUtil.isNotBlank(channelViewRequest.getParam2())) {
            requestMap.put("param2", channelViewRequest.getParam2());
        }
        if (StrUtil.isNotBlank(channelViewRequest.getParam3())) {
            requestMap.put("param3", channelViewRequest.getParam3());
        }
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));

        String response = HttpUtil.get(url, requestMap);
        ViewLogFirstResponse viewLogFirstResponse = JSON.parseObject(response, ViewLogFirstResponse.class);
//        log.info("分页查询频道直播观看详情数据，返回值：{}", viewLogFirstResponse);
        List<ViewLogResponse> viewLogResponseList = new ArrayList<>();
        if (viewLogFirstResponse != null && viewLogFirstResponse.getCode() == 200) {
            List<ViewLogThirdResponse> contents = viewLogFirstResponse.getData().getContents();

            for (ViewLogThirdResponse viewLogThirdResponse : contents) {
                ViewLogResponse viewLogResponse = new ViewLogResponse();
                viewLogResponse.setChannelId(channelViewRequest.getChannelId());
                viewLogResponse.setParam1(viewLogThirdResponse.getParam1());
                viewLogResponse.setParam2(viewLogThirdResponse.getParam2());
                viewLogResponse.setPlayDuration(viewLogThirdResponse.getPlayDuration());
                viewLogResponse.setFirstActiveTime(viewLogThirdResponse.getFirstActiveTime());
                viewLogResponse.setLastActiveTime(viewLogThirdResponse.getLastActiveTime());
                viewLogResponse.setSessionId(viewLogThirdResponse.getSessionId());
                viewLogResponse.setParam3(viewLogThirdResponse.getParam3());
                viewLogResponseList.add(viewLogResponse);
            }

            saResult.setCode(ResultCode.SUCCESS.getCode());
            saResult.setMsg(ResultCode.SUCCESS.getMessage());
            saResult.setData(viewLogResponseList);
            return saResult;
        }
        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
    }


    private File createWhiteListFile(CourseSchedulePO courseSchedulePO) {
        String templateFilePath = "temporaryWhiteList.xls";
        List<StudentWhiteListVO> StudentWhiteListVOS = new ArrayList<>();

        //查出排课表的所有排课信息
        QueryWrapper<CourseSchedulePO> courseQueryWrapper = new QueryWrapper<>();
        courseQueryWrapper.eq("course_name", courseSchedulePO.getCourseName())
                .eq("main_teacher_name", courseSchedulePO.getMainTeacherName());

        List<CourseSchedulePO> schedulePOList = courseScheduleMapper.selectList(courseQueryWrapper);

        for (CourseSchedulePO schedulePO : schedulePOList) {
            List<Map<String, String>> scheduleClassStudent = studentStatusMapper.getScheduleClassStudent(schedulePO);
            for (Map<String, String> sc : scheduleClassStudent) {
                StudentWhiteListVO studentWhiteListVO = new StudentWhiteListVO();
                studentWhiteListVO.setName(sc.get("name"));
                studentWhiteListVO.setCode(sc.get("id_number"));
                StudentWhiteListVOS.add(studentWhiteListVO); // 将studentWhiteListVO添加到集合中
            }
        }

        EasyExcel.write(templateFilePath, StudentWhiteListVO.class).sheet("Sheet1").doWrite(StudentWhiteListVOS);
        return new File(templateFilePath);
    }

    @Override
    public SaResult deleteChannel(String channelId) {
        //先把直播间设为无效，再去保利威删除该直播间
        try {
            UpdateWrapper<LiveResourcesPO> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("valid", "N")
                    .eq("channel_id", channelId);
            int update = liveResourceMapper.update(null, updateWrapper);

            LiveDeleteChannelRequest liveDeleteChannelRequest = new LiveDeleteChannelRequest();
            Boolean liveDeleteChannelResponse;
            liveDeleteChannelRequest.setChannelId(channelId);
            liveDeleteChannelResponse = new LiveChannelOperateServiceImpl().deleteChannel(liveDeleteChannelRequest);
            if (liveDeleteChannelResponse != null && liveDeleteChannelResponse) {
                return SaResult.ok("删除频道直播间成功");
            }
        } catch (Exception e) {
            log.error("调用删除直播间接口异常" + channelId, e);
        }
        return SaResult.error("删除直播间异常，请联系管理员");
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
                    .setEnabled("Y")
                    .setAuthTips("请输入你的身份证号码");

            LiveChannelSettingRequest.AuthSetting authSetting2 = new LiveChannelSettingRequest.AuthSetting().setAuthType(
                    LiveConstant.AuthType.DIRECT.getDesc())
                    .setRank(2)
                    .setEnabled("Y")
                    .setDirectKey(RandomUtil.randomString(8));

            List<LiveChannelSettingRequest.AuthSetting> authSettings = new ArrayList<>();
            authSettings.add(authSetting);
            authSettings.add(authSetting2);

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
        channelInfoData.setType("list");
//        channelInfoData.setVideoId("27b07c2dc999caefedb9d3e4fb685471_2");
        channelInfoData.setOrigin("playback");//默认列表回放，使用回放列表

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
//        requestMap.put("crontabType", channelInfoData.getCrontType() == null ? null : channelInfoData.getCrontType());
//        requestMap.put("startTime", channelInfoData.getStartTime() == null ? null : "" + channelInfoData.getStartTime());
//        requestMap.put("endTime", channelInfoData.getEndTime() == null ? null : "" + channelInfoData.getEndTime());
        requestMap.put("playbackEnabled", channelInfoData.getPlaybackEnabled() == null ? "" : channelInfoData.getPlaybackEnabled());
        requestMap.put("type", channelInfoData.getType() == null ? "" : channelInfoData.getType());
        requestMap.put("origin", channelInfoData.getOrigin() == null ? "" : channelInfoData.getOrigin());
        requestMap.put("videoId", channelInfoData.getVideoId() == null ? "" : channelInfoData.getVideoId());
//        requestMap.put("sectionEnabled", channelInfoData.getSectionEnabled() == null ? null : channelInfoData.getSectionEnabled());
//        requestMap.put("chatPlaybackEnabled", channelInfoData.getChatPlaybackEnabled() == null ? null : channelInfoData.getChatPlaybackEnabled());
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, LiveGlobalConfig.getAppSecret()));

        log.info("请求参数  " + requestMap);
        ChannelResponse playbackResponse = null;
        try {
            String response = PolyvHttpUtil.postFormBody(url, requestMap);  // assuming PolyvHttpUtil can be used here
            log.info("回放设置返回值 \n" + response);
            playbackResponse = JSON.parseObject(response, ChannelResponse.class);
            saResult.setCode(playbackResponse.getCode());
            saResult.setMsg(playbackResponse.getMessage());
            saResult.setData(playbackResponse.getData());
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
        try {
            String tutorSSOLink = videoStreamUtils.generateTeacherSSOLink(channelId);
            return SaResult.data(tutorSSOLink);
        } catch (Exception e) {
            log.error("讲师单点登录失败，频道id为" + channelId, e);
        }
        return SaResult.error("开启直播失败，请联系管理员");
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
    public SaResult getTutorChannelUrl(String channelId, String userId) {
        SaResult saResult = new SaResult();
        ChannelInfoResponse channelInfoResponse = new ChannelInfoResponse();
        TutorInformation tutorInformation;

        try {
            QueryWrapper<TutorInformation> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("channel_id", channelId);
            queryWrapper.eq("user_id", userId);
            Integer integer = tutorInformationMapper.selectCount(queryWrapper);
            if (integer == 0) {//说明该用户助教信息没被返回过
                QueryWrapper<TutorInformation> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("channel_id", channelId)
                        .and(wrapper -> wrapper.isNull("user_id").or().eq("user_id", ""));
                List<TutorInformation> tutorInformations = tutorInformationMapper.selectList(queryWrapper1);
                tutorInformation = tutorInformations.get(0);

                if (tutorInformation == null) {//如果找不到该频道已经找不到userId为空的助教了，表示用完了那就返回错误联系管理员
                    saResult.setCode(ResultCode.GET_TUTOR_FAIL.getCode());
                    saResult.setMsg(ResultCode.GET_TUTOR_FAIL.getMessage());
                    return saResult;
                }

                //同时将该userId更新到该条助教信息中
                UpdateWrapper<TutorInformation> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("user_id", userId)
                        .eq("id", tutorInformation.getId());
                int update = tutorInformationMapper.update(null, updateWrapper);
                if (update > 0) {
                    log.info("更新助教userId成功");
                }
            } else {//说明查过，该助教信息中有userId
                tutorInformation = tutorInformationMapper.selectOne(queryWrapper);
            }

            if (StrUtil.isNotBlank(tutorInformation.getTutorUrl())) {
                channelInfoResponse.setUrl(tutorInformation.getTutorUrl());
            }
            if (StrUtil.isNotBlank(tutorInformation.getTutorPassword())) {
                channelInfoResponse.setPassword(tutorInformation.getTutorPassword());
            }
            if (StrUtil.isNotBlank(tutorInformation.getAccount())) {
                channelInfoResponse.setAccount(tutorInformation.getAccount());
            }


        } catch (Exception e) {
            e.printStackTrace();
            saResult.setCode(ResultCode.FAIL.getCode());
            saResult.setMsg(ResultCode.FAIL.getMessage());
            return saResult;
        }
        saResult.setCode(ResultCode.SUCCESS.getCode());
        saResult.setMsg(ResultCode.SUCCESS.getMessage());
        saResult.setData(channelInfoResponse);
        return saResult;
    }

    @Override
    public SaResult createTutorChannel(String channelId, String userId) {

        LiveCreateAccountRequest liveCreateAccountRequest = new LiveCreateAccountRequest();
        LiveCreateAccountResponse liveCreateAccountResponse;
        try {
            QueryWrapper<TutorInformation> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("channel_id", channelId);
            queryWrapper.eq("user_id", userId);
            List<TutorInformation> tutorInformations = tutorInformationMapper.selectList(queryWrapper);
            if (tutorInformations.size() > 0) {
                //说明该角色有助教，直接返回单点登录链接即可
                String tutorSSOLink = videoStreamUtils.generateTutorSSOLink(channelId, tutorInformations.get(0).getAccount());
                return SaResult.data(tutorSSOLink);
            }

            //说明该用户没有创建过助教。
            PlatformUserPO platformUserPO = platformUserMapper.selectById(userId);
            liveCreateAccountRequest.setChannelId(channelId)
                    .setRole("Assistant")
                    .setActor("助教")
                    .setNickName(platformUserPO.getUsername())
                    .setPurviewList(Arrays.asList(new LiveCreateAccountRequest.Purview().setCode(
                            LiveConstant.RolePurview.CHAT_LIST_ENABLED.getCode())
                            .setEnabled(LiveConstant.Flag.YES.getFlag())));
            liveCreateAccountResponse = new LiveChannelOperateServiceImpl().createAccount(liveCreateAccountRequest);

            if (liveCreateAccountResponse != null) {
                log.info("创建助教角色成功 {}", JSON.toJSONString(liveCreateAccountResponse));
                String tutorSSOLink = videoStreamUtils.generateTutorSSOLink(channelId, liveCreateAccountResponse.getAccount());

                //同时插入助教表
                TutorInformation tutorInformation = new TutorInformation();
                tutorInformation.setTutorUrl("https://console.polyv.net/live/login.html?channelId=" + liveCreateAccountResponse.getAccount());
                tutorInformation.setTutorName(platformUserPO.getUsername());
                tutorInformation.setUserId(userId);
                tutorInformation.setChannelId(channelId);
                tutorInformation.setTutorPassword(liveCreateAccountResponse.getPasswd());
                tutorInformation.setAccount(liveCreateAccountResponse.getAccount());
                int insert = tutorInformationMapper.insert(tutorInformation);

                return SaResult.data(tutorSSOLink);
            }
        } catch (Exception e) {
            log.error("创建助教接口调用异常,入参为" + channelId + "" + userId, e);
        }
        return SaResult.error("返回助教登录链接失败，请联系管理员");
    }

    //创建直播间时默认创建普通高级的助教老师
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
//                            .setCode(LiveConstant.RolePurview.CHAT_AUDIT.getCode())
                            .setEnabled(LiveConstant.Flag.YES.getFlag())));
            liveCreateAccountResponse = new LiveChannelOperateServiceImpl().createAccount(liveCreateAccountRequest);
            if (liveCreateAccountResponse != null) {
//                https://console.polyv.net/live/login.html?channelId=0024368180
                log.info("创建角色成功 {}", JSON.toJSONString(liveCreateAccountResponse));
                channelInfoResponse.setPassword(liveCreateAccountResponse.getPasswd());
                channelInfoResponse.setUrl("https://console.polyv.net/live/login.html?channelId=" + liveCreateAccountResponse.getAccount());
                //返回助教信息，同时插入助教表
                TutorInformation tutorInformation = new TutorInformation();
                tutorInformation.setTutorUrl("https://console.polyv.net/live/login.html?channelId=" + liveCreateAccountResponse.getAccount());
                tutorInformation.setTutorName(tutorName);
                tutorInformation.setChannelId(channelId);
                tutorInformation.setTutorPassword(liveCreateAccountResponse.getPasswd());
                tutorInformation.setAccount(liveCreateAccountResponse.getAccount());

                int insert = tutorInformationMapper.insert(tutorInformation);
                if (insert > 0) {
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

    public SaResult UploadWhiteList(File file, String channelId) {
        SaResult saResult = new SaResult();
        LiveUploadWhiteListRequest liveUploadWhiteListRequest = new LiveUploadWhiteListRequest();
        Boolean liveUploadWhiteListResponse;
        try {
            liveUploadWhiteListRequest.setChannelId(channelId)
                    .setRank(1)
                    .setFile(file);
            liveUploadWhiteListResponse = new LiveWebAuthServiceImpl().uploadWhiteList(liveUploadWhiteListRequest);

            if (liveUploadWhiteListResponse != null && liveUploadWhiteListResponse) {
                boolean delete = file.delete();
                if (delete) {
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

    @Override
    public SaResult getChannelWhiteList(ChannelInfoRequest channelInfoRequest) {
        SaResult saResult = new SaResult();
        if (channelInfoRequest.getPageSize() != null && channelInfoRequest.getPageSize() > 1000) {
            saResult.setCode(ResultCode.FAIL.getCode());
            saResult.setMsg("每页的最大数量不能超过1000");
            return saResult;
        }

        LiveChannelWhiteListRequest liveChannelWhiteListRequest = new LiveChannelWhiteListRequest();
        LiveChannelWhiteListResponse liveChannelWhiteListResponse;
        try {
            liveChannelWhiteListRequest.setChannelId(channelInfoRequest.getChannelId())
                    .setRank(1)
                    .setKeyword(channelInfoRequest.getKeyword())
                    .setCurrentPage(channelInfoRequest.getCurrentPage())
                    .setPageSize(channelInfoRequest.getPageSize());
            liveChannelWhiteListResponse = new LiveWebAuthServiceImpl().getChannelWhiteList(
                    liveChannelWhiteListRequest);
            if (liveChannelWhiteListResponse != null) {
                log.info("测试查询频道观看白名单列表成功,{}", JSON.toJSONString(liveChannelWhiteListResponse));
                saResult.setCode(ResultCode.SUCCESS.getCode());
                saResult.setMsg(ResultCode.SUCCESS.getMessage());
                saResult.setData(liveChannelWhiteListResponse.getContents());
                return saResult;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("调用查询白名单接口异常", e);
        }
        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
    }


    //添加单个白名单
    @Override
    public SaResult addChannelWhiteStudent(ChannelInfoRequest channelInfoRequest) {
        log.info("调用批量新增白名单接口，请求入参为:{}", channelInfoRequest);
        SaResult saResult = new SaResult();
        Boolean liveCreateChannelWhiteListResponse;
        List<StudentWhiteListVO> successList = new ArrayList<>();
        List<StudentWhiteListVO> failList = channelInfoRequest.getStudentWhiteList();
        Iterator<StudentWhiteListVO> iterator = failList.iterator();
        try {
            while (iterator.hasNext()) {
                LiveCreateChannelWhiteListRequest liveCreateChannelWhiteListRequest = new LiveCreateChannelWhiteListRequest();
                StudentWhiteListVO studentWhite = iterator.next();
                liveCreateChannelWhiteListRequest
                        .setRank(1)
                        .setChannelId(channelInfoRequest.getChannelId())
                        .setCode(studentWhite.getCode())
                        .setName(studentWhite.getName());
                liveCreateChannelWhiteListResponse = new LiveWebAuthServiceImpl().createChannelWhiteList(
                        liveCreateChannelWhiteListRequest);
                if (liveCreateChannelWhiteListResponse != null && liveCreateChannelWhiteListResponse) {
                    successList.add(studentWhite);
                    iterator.remove(); // 删除元素使用 iterator.remove()
                }
            }
            if (failList.size() != 0) {
                log.info("新增部分白名单成功" + successList);
                saResult.setCode(ResultCode.PARTIALSUCCESS.getCode());
                saResult.setMsg(ResultCode.PARTIALSUCCESS.getMessage());
                saResult.setData(failList);
                return saResult;
            } else {
                saResult.setCode(ResultCode.SUCCESS.getCode());
                saResult.setMsg(ResultCode.SUCCESS.getMessage());
                return saResult;
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("添加白名单接口调用异常", e);
        }
        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        saResult.setData(failList);
        return saResult;

    }

    @Override
    public SaResult addChannelWhiteStudentByFile(ChannelInfoRequest channelInfoRequest) {
        log.info("调用批量新增白名单通过文件接口，请求入参为:{}", channelInfoRequest);
        SaResult saResult = new SaResult();

        try {
            String templateFilePath = "暂存白名单文件.xls";
            EasyExcel.write(templateFilePath, StudentWhiteListVO.class).sheet("Sheet1").doWrite(channelInfoRequest.getStudentWhiteList());
            File file = new File(templateFilePath);
            LiveUploadWhiteListRequest liveUploadWhiteListRequest = new LiveUploadWhiteListRequest();
            Boolean liveUploadWhiteListResponse;
            liveUploadWhiteListRequest.setChannelId(channelInfoRequest.getChannelId())
                    .setRank(1)
                    .setFile(file);
            liveUploadWhiteListResponse = new LiveWebAuthServiceImpl().uploadWhiteList(liveUploadWhiteListRequest);
            if (liveUploadWhiteListResponse != null && liveUploadWhiteListResponse) {
                //上传白名单成功
                boolean delete = file.delete();
                if (delete) {
                    log.info("删除文件成功");
                }
            }

        } catch (Exception e) {
            log.error("通过文件新增白名单接口调用异常", e);
            saResult.setCode(ResultCode.FAIL.getCode());
            saResult.setMsg(ResultCode.FAIL.getMessage());
            return saResult;
        }
        saResult.setCode(ResultCode.SUCCESS.getCode());
        saResult.setMsg(ResultCode.SUCCESS.getMessage());
        return saResult;
    }


    //删除白名单
    @Override
    public SaResult deleteChannelWhiteStudent(ChannelInfoRequest channelInfoRequest) {
        log.info("调用批量删除白名单接口，请求入参为:{}", channelInfoRequest);
        SaResult saResult = new SaResult();
        Boolean liveDeleteChannelWhiteListResponse;
        List<String> successList = new ArrayList<>();
        List<String> failList = channelInfoRequest.getDeleteCodeList();
        Iterator<String> iterator = failList.iterator();
        try {
            //遍历需要删除的白名单list，成功的装进successList,删除失败的放入failList
            while (iterator.hasNext()) {
                String code = iterator.next();
                LiveDeleteChannelWhiteListRequest liveDeleteChannelWhiteListRequest = new LiveDeleteChannelWhiteListRequest();
                liveDeleteChannelWhiteListRequest
                        .setRank(1)
                        .setChannelId(channelInfoRequest.getChannelId())
                        .setIsClear(channelInfoRequest.getIsClear())
                        .setCode(code);
                liveDeleteChannelWhiteListResponse = new LiveWebAuthServiceImpl().deleteChannelWhiteList(
                        liveDeleteChannelWhiteListRequest);
                if (liveDeleteChannelWhiteListResponse != null && liveDeleteChannelWhiteListResponse) {
                    successList.add(code);
                    iterator.remove(); // 删除元素使用 iterator.remove()
                }
            }

            if (failList.size() != 0) {
                log.info("删除部分白名单成功" + successList);
                saResult.setCode(ResultCode.PARTIALSUCCESS.getCode());
                saResult.setMsg(ResultCode.PARTIALSUCCESS.getMessage());
                saResult.setData(failList);
                return saResult;
            } else {
                saResult.setCode(ResultCode.SUCCESS.getCode());
                saResult.setMsg(ResultCode.SUCCESS.getMessage());
                return saResult;
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.error("删除白名单接口调用异常", e);
        }
        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg("该直播间不含该会员码，无需删除");
        saResult.setData(failList);
        return saResult;

    }

    public Date minusAddOneHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        return calendar.getTime();
    }

    public Date minusSubtractOneHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        return calendar.getTime();
    }

    @Override
    public SaResult exportStudentSituation(Long courseId, HttpServletResponse response) {
        log.info("传入的排课表id为" + courseId);
        SaResult saResult = new SaResult();
        try {
            //获取该排课表的频道直播间id

//            LiveResourcesPO live = liveResourceMapper.query(courseId);
//            if (live ==null || StrUtil.isBlank(live.getChannelId())) {
//                return SaResult.data("该排课还未创建直播间");
//            }
//            coursesLearningMapper.selectList()

            CourseSchedulePO schedulePO = courseScheduleMapper.selectById(courseId);
            if (StrUtil.isBlank(schedulePO.getOnlinePlatform())) {
                saResult.setCode(ResultCode.FAIL.getCode());
                saResult.setMsg("该排课表还未创建直播间");
                return saResult;
            }
            VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectById(schedulePO.getOnlinePlatform());
            //开始和结束时间应该是该排课的，而不是直播记录表的。
            String teachingTime = schedulePO.getTeachingTime().replace("-", "—");//14:30—17:00, 2:00-5:00
            String courseStartTime = teachingTime.substring(0, teachingTime.indexOf("—"));//获取14:30, 2:00
            String courseEndTime = teachingTime.substring(teachingTime.indexOf("—") + 1);//获取17:00, 5:00

            String datePattern = "yyyy-MM-dd";
            SimpleDateFormat sdf1 = new SimpleDateFormat(datePattern);
            String pattern = "yyyy-MM-dd HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);

            videoStreamRecordPO.setStartTime(sdf.parse(sdf1.format(schedulePO.getTeachingDate()) + " " + courseStartTime));
            videoStreamRecordPO.setEndTime(sdf.parse(sdf1.format(schedulePO.getTeachingDate()) + " " + courseEndTime));

            SaResult channelCardPush = getStudentViewLog(videoStreamRecordPO);
            List<ViewLogResponse> viewLogResponseList = (List<ViewLogResponse>) channelCardPush.getData();
            if (viewLogResponseList.size() == 0) {
                saResult.setCode(ResultCode.FAIL.getCode());
                saResult.setMsg("该排课时间段内没有学生观看数据，请联系管理员");
                return saResult;
            }
            log.info("学生观看数据获取成功" + viewLogResponseList);
            //将观看数据根据param1字段聚合后playDuration相加，再去重。
            Map<String, List<ViewLogResponse>> groupByParam1 = viewLogResponseList.stream()
                    .collect(Collectors.groupingBy(ViewLogResponse::getParam1));

            // 对每个学生分组做操作，有的学生一场课多次进入，观看时长需要累加
            Map<String, Integer> viewResult = new HashMap<>();
            for (Map.Entry<String, List<ViewLogResponse>> entry : groupByParam1.entrySet()) {
                int totalPlayDuration = entry.getValue().stream()
                        .mapToInt(ViewLogResponse::getPlayDuration)
                        .sum();
                viewResult.put(entry.getKey(), totalPlayDuration);
            }
            //这样就获取到了每个学生的观看时长数据viewResult。key是身份证号，value是时长

            //去重后的观众观看数据，同时对观看时长更新下
            List<ViewLogResponse> distinctViewLogResponse = groupByParam1.values()
                    .stream()
                    .map(subList -> subList.get(0))
                    .collect(Collectors.toList());
            for (ViewLogResponse viewLogResponse : distinctViewLogResponse) {
                viewLogResponse.setPlayDuration(viewResult.get(viewLogResponse.getParam1()));
            }

            //考勤导出attendanceVOList,拥有出勤的所有学生数据
            List<AttendanceVO> attendanceVOList = new ArrayList<>();
            for (ViewLogResponse viewLogResponse : distinctViewLogResponse) {
                AttendanceVO attendanceVO = new AttendanceVO();
                attendanceVO.setGrade(schedulePO.getGrade());
                attendanceVO.setLevel(schedulePO.getLevel());
                attendanceVO.setMajorName(schedulePO.getMajorName());
                attendanceVO.setStudyForm(schedulePO.getStudyForm());
                attendanceVO.setTeachingTime(sdf1.format(schedulePO.getTeachingDate()) + " " + teachingTime);
                attendanceVO.setName(viewLogResponse.getParam2());
                attendanceVO.setPlayDuration(viewLogResponse.getPlayDuration().toString());
                attendanceVO.setAttendance("是");
                StudentStatusVO studentStatusVO = studentStatusMapper.selectStudentByidNumberGrade(viewLogResponse.getParam1(), schedulePO.getGrade());
                if (studentStatusVO != null) {
                    attendanceVO.setCode(studentStatusVO.getStudentNumber());
                    QueryWrapper<ClassInformationPO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("class_identifier", studentStatusVO.getClassIdentifier());
                    List<ClassInformationPO> classInformationPOS = classInformationMapper.selectList(queryWrapper);
                    if (classInformationPOS.size() != 0) {
                        attendanceVO.setClassName(classInformationPOS.get(0).getClassName());//根据身份证拿到学生的学号，班别。
                    }
                }
                attendanceVOList.add(attendanceVO);
            }
            Collections.sort(attendanceVOList, Comparator.comparingInt(a -> Integer.parseInt(((AttendanceVO) a).getPlayDuration())).reversed());
            log.info("获取所有观看的学生数据并降序排序完成" + attendanceVOList);

            //拿到直播间所有学生白名单数据whiteLists
            List<LiveChannelWhiteListResponse.ChannelWhiteList> whiteLists = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                LiveChannelWhiteListRequest liveChannelWhiteListRequest = new LiveChannelWhiteListRequest();
                liveChannelWhiteListRequest.setChannelId(videoStreamRecordPO.getChannelId())
                        .setRank(1)
                        .setCurrentPage(i)
                        .setPageSize(1000);
                LiveChannelWhiteListResponse liveChannelWhiteListResponse = new LiveWebAuthServiceImpl().getChannelWhiteList(liveChannelWhiteListRequest);
                if (liveChannelWhiteListResponse.getContents().size() != 0) {
                    List<LiveChannelWhiteListResponse.ChannelWhiteList> contents = liveChannelWhiteListResponse.getContents();
                    whiteLists.addAll(contents);
                } else {
                    break;
                }
            }

            List<LiveChannelWhiteListResponse.ChannelWhiteList> noAttendList = new ArrayList<>();
            for (LiveChannelWhiteListResponse.ChannelWhiteList channel : whiteLists) {
                boolean found = false;
                for (ViewLogResponse viewLogResponse : distinctViewLogResponse) {
                    // 假设有一个名为id的属性用于比较
                    if (channel.getPhone().equals(viewLogResponse.getParam1())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    noAttendList.add(channel);
                }
            }
            //这样就拿到了没有出勤的学生数据noAttendList

            for (LiveChannelWhiteListResponse.ChannelWhiteList channelWhiteList : noAttendList) {
                AttendanceVO attendanceVO = new AttendanceVO();
                attendanceVO.setGrade(schedulePO.getGrade());
                attendanceVO.setLevel(schedulePO.getLevel());
                attendanceVO.setMajorName(schedulePO.getMajorName());
                attendanceVO.setStudyForm(schedulePO.getStudyForm());
                attendanceVO.setTeachingTime(sdf1.format(schedulePO.getTeachingDate()) + " " + teachingTime);

                attendanceVO.setName(channelWhiteList.getName());
                attendanceVO.setPlayDuration("0");
                attendanceVO.setAttendance("否");
                StudentStatusVO studentStatusVO = studentStatusMapper.selectStudentByidNumberGrade(channelWhiteList.getPhone(), schedulePO.getGrade());
                if (studentStatusVO != null) {
                    attendanceVO.setCode(studentStatusVO.getStudentNumber());
                    QueryWrapper<ClassInformationPO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("class_identifier", studentStatusVO.getClassIdentifier());
                    List<ClassInformationPO> classInformationPOS = classInformationMapper.selectList(queryWrapper);
                    if (classInformationPOS.size() != 0) {
                        attendanceVO.setClassName(classInformationPOS.get(0).getClassName());//根据身份证拿到学生的学号，班别。
                    }
                }
                attendanceVOList.add(attendanceVO);
            }
            log.info("获取所有未观看的学生数据完成" + noAttendList);
            log.info("获取{}该堂课的学生数据完成", courseId);
            downloadExportFile(response, attendanceVOList);
            saResult.setCode(ResultCode.SUCCESS.getCode());
            saResult.setMsg(ResultCode.SUCCESS.getMessage());
            return saResult;

        } catch (Exception e) {
            log.error("调用导出考勤表接口失败，该堂排课表id为:{}", courseId, e);
        }

        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
    }

    @Override
    public void exportAllCourseSituation(String[] courseId, HttpServletResponse response) {

        try {
            List<AttendanceVO> exportAttendanceVOList = new ArrayList<>();
            for (String id : courseId) {
                //获取该排课表的频道直播间id
                CourseSchedulePO schedulePO = courseScheduleMapper.selectById(id);
                if (StrUtil.isBlank(schedulePO.getOnlinePlatform())) {
                    log.error("该排课表还未创建直播间");
                    return;
                }

                VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectById(schedulePO.getOnlinePlatform());
                //开始和结束时间应该是该排课的，而不是直播记录表的。
                String teachingTime = schedulePO.getTeachingTime().replace("-", "—");//14:30—17:00, 2:00-5:00
                String courseStartTime = teachingTime.substring(0, teachingTime.indexOf("—"));//获取14:30, 2:00
                String courseEndTime = teachingTime.substring(teachingTime.indexOf("—") + 1);//获取17:00, 5:00

                String datePattern = "yyyy-MM-dd";
                SimpleDateFormat sdf1 = new SimpleDateFormat(datePattern);
                String pattern = "yyyy-MM-dd HH:mm";
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);

                videoStreamRecordPO.setStartTime(sdf.parse(sdf1.format(schedulePO.getTeachingDate()) + " " + courseStartTime));
                videoStreamRecordPO.setEndTime(sdf.parse(sdf1.format(schedulePO.getTeachingDate()) + " " + courseEndTime));

                SaResult channelCardPush = getStudentViewLog(videoStreamRecordPO);
                List<ViewLogResponse> viewLogResponseList = (List<ViewLogResponse>) channelCardPush.getData();
                if (viewLogResponseList.size() == 0) {
                    log.error("该排课时间段内没有学生观看数据，请联系管理员");
                    return;
                }

                log.info("学生观看数据获取成功" + viewLogResponseList);
                //将观看数据根据param1字段聚合后playDuration相加，再去重。
                Map<String, List<ViewLogResponse>> groupByParam1 = viewLogResponseList.stream()
                        .collect(Collectors.groupingBy(ViewLogResponse::getParam1));

                // 对每个学生分组做操作，有的学生一场课多次进入，观看时长需要累加
                Map<String, Integer> viewResult = new HashMap<>();
                for (Map.Entry<String, List<ViewLogResponse>> entry : groupByParam1.entrySet()) {
                    int totalPlayDuration = entry.getValue().stream()
                            .mapToInt(ViewLogResponse::getPlayDuration)
                            .sum();
                    viewResult.put(entry.getKey(), totalPlayDuration);
                }
                //这样就获取到了每个学生的观看时长数据viewResult。key是身份证号，value是时长

                //去重后的观众观看数据，同时对观看时长更新下
                List<ViewLogResponse> distinctViewLogResponse = groupByParam1.values()
                        .stream()
                        .map(subList -> subList.get(0))
                        .collect(Collectors.toList());
                for (ViewLogResponse viewLogResponse : distinctViewLogResponse) {
                    viewLogResponse.setPlayDuration(viewResult.get(viewLogResponse.getParam1()));
                }

                //考勤导出attendanceVOList,拥有出勤的所有学生数据
                List<AttendanceVO> attendanceVOList = new ArrayList<>();
                for (ViewLogResponse viewLogResponse : distinctViewLogResponse) {
                    AttendanceVO attendanceVO = new AttendanceVO();
                    attendanceVO.setGrade(schedulePO.getGrade());
                    attendanceVO.setLevel(schedulePO.getLevel());
                    attendanceVO.setMajorName(schedulePO.getMajorName());
                    attendanceVO.setStudyForm(schedulePO.getStudyForm());
                    attendanceVO.setTeachingTime(sdf1.format(schedulePO.getTeachingDate()) + " " + teachingTime);

                    attendanceVO.setName(viewLogResponse.getParam2());
                    attendanceVO.setPlayDuration(viewLogResponse.getPlayDuration().toString());
                    attendanceVO.setAttendance("是");
                    StudentStatusVO studentStatusVO = studentStatusMapper.selectStudentByidNumberGrade(viewLogResponse.getParam1(), schedulePO.getGrade());
                    if (studentStatusVO != null) {
                        attendanceVO.setCode(studentStatusVO.getStudentNumber());
                        QueryWrapper<ClassInformationPO> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("class_identifier", studentStatusVO.getClassIdentifier());
                        List<ClassInformationPO> classInformationPOS = classInformationMapper.selectList(queryWrapper);
                        if (classInformationPOS.size() != 0) {
                            attendanceVO.setClassName(classInformationPOS.get(0).getClassName());//根据身份证拿到学生的学号，班别。
                        }
                    }
                    attendanceVOList.add(attendanceVO);
                }
                Collections.sort(attendanceVOList, Comparator.comparingInt(a -> Integer.parseInt(((AttendanceVO) a).getPlayDuration())).reversed());
                log.info("获取所有观看的学生数据并降序排序完成" + attendanceVOList);

                //拿到直播间所有学生白名单数据whiteLists
                List<LiveChannelWhiteListResponse.ChannelWhiteList> whiteLists = new ArrayList<>();
                for (int i = 1; i < 10; i++) {
                    LiveChannelWhiteListRequest liveChannelWhiteListRequest = new LiveChannelWhiteListRequest();
                    liveChannelWhiteListRequest.setChannelId(videoStreamRecordPO.getChannelId())
                            .setRank(1)
                            .setCurrentPage(i)
                            .setPageSize(1000);
                    LiveChannelWhiteListResponse liveChannelWhiteListResponse = new LiveWebAuthServiceImpl().getChannelWhiteList(liveChannelWhiteListRequest);
                    if (liveChannelWhiteListResponse.getContents().size() != 0) {
                        List<LiveChannelWhiteListResponse.ChannelWhiteList> contents = liveChannelWhiteListResponse.getContents();
                        whiteLists.addAll(contents);
                    } else {
                        break;
                    }
                }

                List<LiveChannelWhiteListResponse.ChannelWhiteList> noAttendList = new ArrayList<>();
                for (LiveChannelWhiteListResponse.ChannelWhiteList channel : whiteLists) {
                    boolean found = false;
                    for (ViewLogResponse viewLogResponse : distinctViewLogResponse) {
                        // 假设有一个名为id的属性用于比较
                        if (channel.getPhone().equals(viewLogResponse.getParam1())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        noAttendList.add(channel);
                    }
                }
                //这样就拿到了没有出勤的学生数据noAttendList

                for (LiveChannelWhiteListResponse.ChannelWhiteList channelWhiteList : noAttendList) {
                    AttendanceVO attendanceVO = new AttendanceVO();
                    attendanceVO.setGrade(schedulePO.getGrade());
                    attendanceVO.setLevel(schedulePO.getLevel());
                    attendanceVO.setMajorName(schedulePO.getMajorName());
                    attendanceVO.setStudyForm(schedulePO.getStudyForm());
                    attendanceVO.setTeachingTime(sdf1.format(schedulePO.getTeachingDate()) + " " + teachingTime);

                    attendanceVO.setName(channelWhiteList.getName());
                    attendanceVO.setPlayDuration("0");
                    attendanceVO.setAttendance("否");
                    StudentStatusVO studentStatusVO = studentStatusMapper.selectStudentByidNumberGrade(channelWhiteList.getPhone(), schedulePO.getGrade());
                    if (studentStatusVO != null) {
                        attendanceVO.setCode(studentStatusVO.getStudentNumber());
                        QueryWrapper<ClassInformationPO> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("class_identifier", studentStatusVO.getClassIdentifier());
                        List<ClassInformationPO> classInformationPOS = classInformationMapper.selectList(queryWrapper);
                        if (classInformationPOS.size() != 0) {
                            attendanceVO.setClassName(classInformationPOS.get(0).getClassName());//根据身份证拿到学生的学号，班别。
                        }
                    }
                    attendanceVOList.add(attendanceVO);
                }

                exportAttendanceVOList.addAll(attendanceVOList);
            }

            downloadExportFile(response, exportAttendanceVOList);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("");
        }

    }


    public SaResult getStudentViewLog(VideoStreamRecordPO videoStreamRecordPO) throws IOException, NoSuchAlgorithmException {
        //学生的学号、姓名、班别、观看时长
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date exportStartTime = minusSubtractOneHour(videoStreamRecordPO.getStartTime());
        Date exportEndTime = minusAddOneHour(videoStreamRecordPO.getEndTime());
        String startTime = format.format(exportStartTime);
        String endTime = format.format(exportEndTime);

        ChannelViewRequest channelViewRequest = new ChannelViewRequest();
        channelViewRequest.setChannelId(videoStreamRecordPO.getChannelId());
        channelViewRequest.setStartTime(startTime);
        channelViewRequest.setEndTime(endTime);
        channelViewRequest.setParam3("live");
        channelViewRequest.setPageSize("10000");
        log.info("获取指定条件下的观看数据请求为：" + channelViewRequest);
        SaResult channelCardPush = getChannelCardPush(channelViewRequest);
        return channelCardPush;
    }

    private void downloadExportFile(HttpServletResponse response, List<AttendanceVO> attendanceVOList) throws IOException {
        String templateFilePath = "考勤数据.xls";
        EasyExcel.write(templateFilePath, AttendanceVO.class).sheet("Sheet1").doWrite(attendanceVOList);
        File file = new File(templateFilePath);
        OutputStream outputStream = response.getOutputStream();
        FileInputStream fileInputStream = new FileInputStream(file);

        // 设置响应内容的类型,响应头部信息，指定文件名
        try {
//                LocalDateTime now = LocalDateTime.now();
//                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//                String currentTime = now.format(formatter);
            response.setContentType("application/vnd.ms-excel");
//                response.setHeader("Content-Disposition", "attachment; filename=\"" +currentTime+ "考勤数据.xls" + "\"");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.flush();
        } finally {
            fileInputStream.close();
            outputStream.close();
        }
        boolean delete = file.delete();
        if (delete) {
            log.info("删除文件成功");
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


    //live：直播中 playback：回放中 end：已结束 waiting：等待中 unStart：未开始
    @Override
    public SaResult GetChannelDetail(String channelId) {
        SaResult saResult = new SaResult();
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

    @Override
    public SaResult getChannelSessionInfo(ChannelInfoRequest channelInfoRequest) {
        SaResult saResult = new SaResult();
        LiveListChannelSessionInfoRequest liveListChannelSessionInfoRequest = new LiveListChannelSessionInfoRequest();
        LiveListChannelSessionInfoResponse liveListChannelSessionInfoResponse;
        try {
            liveListChannelSessionInfoRequest.setChannelId(channelInfoRequest.getChannelId());
            if (Objects.nonNull(channelInfoRequest.getStartDate())) {
                liveListChannelSessionInfoRequest.setStartDate(channelInfoRequest.getStartDate());
            }
            if (Objects.nonNull(channelInfoRequest.getEndDate())) {
                liveListChannelSessionInfoRequest.setEndDate(channelInfoRequest.getEndDate());
            }
            if (Objects.nonNull(channelInfoRequest.getCurrentPage())) {
                liveListChannelSessionInfoRequest.setCurrentPage(channelInfoRequest.getCurrentPage());
            }
            if (Objects.nonNull(channelInfoRequest.getPageSize())) {
                liveListChannelSessionInfoRequest.setPageSize(channelInfoRequest.getPageSize());
            }
            liveListChannelSessionInfoResponse = new LiveChannelPlaybackServiceImpl().listChannelSessionInfo(
                    liveListChannelSessionInfoRequest);
            if (liveListChannelSessionInfoResponse != null) {
                saResult.setCode(ResultCode.SUCCESS.getCode());
                saResult.setMsg(ResultCode.SUCCESS.getMessage());
                saResult.setData(liveListChannelSessionInfoResponse);
                log.info("测试查询频道直播场次信息成功{}", JSON.toJSONString(liveListChannelSessionInfoResponse));
                return saResult;
            }
        } catch (PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            e.printStackTrace();
        } catch (Exception e) {
            log.error("调用查询频道号的场次信息接口异常", e);
        }
        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
    }

    @Override
    public SaResult getTotalTeachingTime(String courseId) {
        SaResult saResult = new SaResult();
        CourseSchedulePO schedulePO = courseScheduleMapper.selectById(courseId);
        if (schedulePO == null) {
            saResult.setCode(ResultCode.FAIL.getCode());
            saResult.setMsg("找不到该排课信息，请联系管理员");
            return saResult;
        }
        String teacherName = schedulePO.getMainTeacherName();
        String workId = schedulePO.getMainTeacherId();
        TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectByNameAndWorkNumber(teacherName, workId);
        //找不到老师，直接返回失败
        if (teacherInformationPO == null) {
            saResult.setCode(ResultCode.FAIL.getCode());
            saResult.setMsg("找不到该课程对应的老师信息，请联系管理员");
            return saResult;
        }
        //获取到该老师的所有直播间
        List<String> videoIdList = courseScheduleMapper.selectByNameAndWorkNumber(teacherName, workId);
        if (videoIdList.isEmpty()) {
            saResult.setCode(ResultCode.FAIL.getCode());
            saResult.setMsg("找不到该课程与老师对应的直播间信息，请联系管理员");
            return saResult;
        }

        List<String> channelIdList = videoStreamRecordsMapper.selectChannelIds(videoIdList);

        try {
            Long totalTime = 0L;
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date startTime = format.parse("2023-01-01 00:00:00");
//            Date endTime = format.parse("2099-01-01 00:00:00");
            for (String channelId : channelIdList) {
                LiveListChannelSessionInfoRequest liveListChannelSessionInfoRequest = new LiveListChannelSessionInfoRequest();
                LiveListChannelSessionInfoResponse liveListChannelSessionInfoResponse;
                liveListChannelSessionInfoRequest.setChannelId(channelId);
                liveListChannelSessionInfoRequest.setPageSize(1000);
//                liveListChannelSessionInfoRequest.setStartDate(startTime);
//                liveListChannelSessionInfoRequest.setEndDate("2099-01-01 00:00:00");

                liveListChannelSessionInfoResponse = new LiveChannelPlaybackServiceImpl().listChannelSessionInfo(
                        liveListChannelSessionInfoRequest);
                if (liveListChannelSessionInfoResponse != null) {
                    List<LiveListChannelSessionInfoResponse.ChannelSessionInfo> contents = liveListChannelSessionInfoResponse.getContents();
                    if (contents.size() == 0) {
                        continue;
                    }
                    for (LiveListChannelSessionInfoResponse.ChannelSessionInfo channelSessionInfo : contents) {
                        long startTimeMillis = channelSessionInfo.getStartTime().getTime();
                        long endTimeMillis = channelSessionInfo.getEndTime().getTime();
                        long timeDiffMillis = (endTimeMillis - startTimeMillis) / 1000;
                        totalTime += timeDiffMillis;
                    }
                }
            }
            //这样将所有直播间的所有场次时间都加起来。
            saResult.setCode(ResultCode.SUCCESS.getCode());
            saResult.setMsg(ResultCode.SUCCESS.getMessage());
            saResult.setData(String.format("%.2f", totalTime / 3600.0));//转化为小时
            return saResult;

        } catch (Exception e) {
            log.error("调用获取老师直播总时长接口异常", e);
            saResult.setCode(ResultCode.FAIL.getCode());
            saResult.setMsg(ResultCode.FAIL.getMessage());
            return saResult;
        }

    }

    public static void main(String[] args) {
        Long totalTime = 86990L;
        System.out.println(totalTime / 3600.0);
    }

    @Override
    public SaResult getStudentViewlogDetail(ChannelViewStudentRequest channelViewStudentRequest) throws IOException, NoSuchAlgorithmException {
        SaResult saResult = new SaResult();
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();

        String timestamp = String.valueOf(System.currentTimeMillis());
        String url = "http://api.polyv.net/live/v4/user/viewlog/detail";

        //http 调用逻辑
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        requestMap.put("timestamp", timestamp);
        requestMap.put("viewerId", channelViewStudentRequest.getViewerId());
        if (StrUtil.isNotBlank(channelViewStudentRequest.getStartDate())) {
            requestMap.put("startDate", channelViewStudentRequest.getStartDate());
        }
        if (StrUtil.isNotBlank(channelViewStudentRequest.getEndDate())) {
            requestMap.put("endDate", channelViewStudentRequest.getEndDate());
        }
        if (StrUtil.isNotBlank(channelViewStudentRequest.getPageNumber())) {
            requestMap.put("pageNumber", channelViewStudentRequest.getPageNumber());
        }
        if (StrUtil.isNotBlank(channelViewStudentRequest.getPageSize())) {
            requestMap.put("pageSize", channelViewStudentRequest.getPageSize());
        }
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));

        String response = HttpUtil.get(url, requestMap);
        ViewFirstStudentResponse viewLogFirstResponse = JSON.parseObject(response, ViewFirstStudentResponse.class);
        if (viewLogFirstResponse.getSuccess() && "success".equals(viewLogFirstResponse.getStatus())) {
            log.info("查询观众的所有直播场次观看信息成功:{}", response);
            Content[] contents = viewLogFirstResponse.getData().getContents();
            saResult.setCode(ResultCode.SUCCESS.getCode());
            saResult.setMsg(ResultCode.SUCCESS.getMessage());
            saResult.setData(viewLogFirstResponse);
            return saResult;
        }

        saResult.setCode(ResultCode.FAIL.getCode());
        saResult.setMsg(ResultCode.FAIL.getMessage());
        return saResult;
    }

    @Override
    public List<TutorAllInformation> selectTutorInformationByBatchIndex(Long batchIndex) {
        if (Objects.isNull(batchIndex)) {
            return null;
        }
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(Wrappers.<CourseSchedulePO>lambdaQuery().eq(CourseSchedulePO::getBatchIndex, batchIndex));
        if (CollUtil.isEmpty(courseSchedulePOS)) {
            return null;
        }
        Set<String> videoStreamRecordIdSet = courseSchedulePOS.stream()
                .map(CourseSchedulePO::getOnlinePlatform)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(videoStreamRecordIdSet)) {
            return null;
        }
        List<VideoStreamRecordPO> videoStreamRecordPOS = videoStreamRecordsMapper.selectList(Wrappers.<VideoStreamRecordPO>lambdaQuery().in(VideoStreamRecordPO::getId, videoStreamRecordIdSet));
        if (CollUtil.isEmpty(videoStreamRecordPOS)) {
            return null;
        }
        Set<String> channelIdSet = videoStreamRecordPOS.stream()
                .map(VideoStreamRecordPO::getChannelId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(channelIdSet)) {
            return null;
        }
        List<TutorInformation> tutorInformationList = tutorInformationMapper.selectList(Wrappers.<TutorInformation>lambdaQuery().in(TutorInformation::getChannelId, channelIdSet));
        List<TutorAllInformation> tutorAllInformationList = tutorInformationList.stream()
                .map(ele -> {
                    String userId = ele.getUserId();
                    if (StrUtil.isBlank(userId)) {
                        return null;
                    }
                    TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectById(userId);
                    return videoStreamInverter.tutorInformationTeacherInformation2TutorAllInformation(ele, teacherInformationPO);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return tutorAllInformationList;
    }
}