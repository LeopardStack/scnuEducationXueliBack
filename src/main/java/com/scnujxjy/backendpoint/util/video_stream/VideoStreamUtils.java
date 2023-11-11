package com.scnujxjy.backendpoint.util.video_stream;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ChannelResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.LiveRequestBody;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelDetail;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelInfoResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelInfoData;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelPlayBackInfoResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.roleCreate.CreateMainResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.roleCreate.CreateRoleRequestBody;
import com.scnujxjy.backendpoint.dao.entity.video_stream.updateChannelInfo.AuthSetting;
import com.scnujxjy.backendpoint.dao.entity.video_stream.updateChannelInfo.BasicSetting;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.inverter.video_stream.VideoStreamInverter;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelRequestBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.SonChannelRequestBO;
import com.scnujxjy.backendpoint.util.polyv.LiveSignUtil;
import com.scnujxjy.backendpoint.util.polyv.PolyvHttpUtil;
import lombok.extern.slf4j.Slf4j;
import net.polyv.common.v1.exception.PloyvSdkException;
import net.polyv.live.v1.config.LiveGlobalConfig;
import net.polyv.live.v1.entity.channel.operate.*;
import net.polyv.live.v1.entity.channel.viewdata.LiveListChannelViewlogRequest;
import net.polyv.live.v1.entity.channel.viewdata.LiveListChannelViewlogResponse;
import net.polyv.live.v1.entity.quick.QuickCreateChannelResponse;
import net.polyv.live.v1.entity.quick.QuickCreatePPTChannelRequest;
import net.polyv.live.v1.service.channel.impl.LiveChannelOperateServiceImpl;
import net.polyv.live.v1.service.channel.impl.LiveChannelViewdataServiceImpl;
import net.polyv.live.v1.service.quick.impl.LiveChannelQuickCreatorServiceImpl;
import net.polyv.live.v2.entity.channel.operate.account.LiveUpdateAccountRequest;
import net.polyv.live.v2.entity.channel.operate.account.LiveUpdateAccountResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class VideoStreamUtils {

    private final static LiveChannelViewdataServiceImpl liveChannelViewdataService = new LiveChannelViewdataServiceImpl();
    private final static LiveChannelQuickCreatorServiceImpl liveChannelQuickCreatorService = new LiveChannelQuickCreatorServiceImpl();
    @Resource
    private VideoStreamInverter videoStreamInverter;
    /**
     * 新建直播频道链接
     */
    private static final String URL_FORMAT = "http://api.polyv.net/live/v2/channels/%s/delete";

    /**
     * 关闭直播间链接
     */
    private static final String CLOSE_URL_FORMAT = "http://api.polyv.net/live/v2/stream/%s/cutoff";

    /**
     * 恢复直播链接
     */
    private static final String RESUME_URL_FORMAT = "http://api.polyv.net/live/v2/stream/%s/resume";

    /**
     * 根据频道请求信息生成直播间
     *
     * @param channelRequestBO 频道信息
     * @return 直播间信息
     */
    public ChannelResponseBO createTeachChannel(ChannelRequestBO channelRequestBO) {
        if (Objects.isNull(channelRequestBO)) {
            log.error("参数缺失");
            return null;
        }
        QuickCreatePPTChannelRequest request = videoStreamInverter.channelRequestBO2Polyv(channelRequestBO);
        if (Objects.isNull(request)) {
            log.error("参数转换失败，数据：{}", channelRequestBO);
            return null;
        }
        LiveCreateSonChannelListRequest sonRequest = new LiveCreateSonChannelListRequest();
        List<LiveCreateSonChannelListRequest.SonChannel> sonChannels = setSonChannelInfo(channelRequestBO);
        if (CollUtil.isEmpty(sonChannels)) {
            log.error("设立子频道错误，数据：{}", channelRequestBO);
            return null;
        }
        sonRequest.setSonChannels(sonChannels);
        try {
            QuickCreateChannelResponse response = liveChannelQuickCreatorService.quickCreatePPTSence(request, sonRequest);
            if (Objects.isNull(response)) {
                log.error("响应为空，请求频道数据：{}，子频道数据：{}", request, sonRequest);
                return null;
            }
            log.info("频道创建成功，频道信息：{}", response);
            return videoStreamInverter.polyv2ChannelResponseBO(response);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("获取直播间失败，请求频道数据：{}，子频道数据：{}", request, sonRequest, e);
            throw new BusinessException(e);
        }

    }

    /**
     * 根据频道信息中的子频道请求信息生成自频道信息
     *
     * @param channelRequestBO 频道信息
     * @return 子频道信息列表
     */
    private List<LiveCreateSonChannelListRequest.SonChannel> setSonChannelInfo(ChannelRequestBO channelRequestBO) {
        if (Objects.isNull(channelRequestBO)) {
            log.error("参数缺失");
            return null;
        }
        List<SonChannelRequestBO> sonChannelRequestBOS = channelRequestBO.getSonChannelRequestBOS();
        if (CollUtil.isEmpty(sonChannelRequestBOS)) {
            // 当没有设置子频道信息时，设置一个助教，助教的role不需要传递
            String passwd = RandomUtil.randomString(11);
            log.info("助教频道密码：{}", passwd);
            sonChannelRequestBOS = ListUtil.of(SonChannelRequestBO.builder()
                    .nickname(channelRequestBO.getPublisher())
                    .passwd(passwd)
                    .build());
        }
        return videoStreamInverter.sonRequestBO2Polyv(sonChannelRequestBOS);
    }

    /**
     * 根据频道id查询频道日志
     *
     * @param channelId 频道id
     * @param startTime 日志开始时间
     * @param endTime   日志结束时间
     * @return 返回日志Response的Map
     */
    private Map<String, Object> liveViewLog(String channelId, Date startTime, Date endTime) {
        LiveListChannelViewlogRequest request = new LiveListChannelViewlogRequest();
        // 构建查询参数
        request.setChannelId(channelId)
                .setStartTime(startTime)
                .setEndTime(endTime);
        try {
            LiveListChannelViewlogResponse response = liveChannelViewdataService.listChannelViewlog(request);
            if (Objects.isNull(response)) {
                return null;
            }
            return BeanUtil.beanToMap(response, false, true);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("无法查询到 channelId：{} 下的直播日志信息", channelId, e);
            throw new BusinessException(e);
        }
    }

    /**
     * 根据频道id删除直播间
     *
     * @param channelId 频道id
     * @return Restful风格
     * <p>code - 响应码</p>
     * <p>data - 数据</p>
     * <p>message - 具体的信息</p>
     * <p>status - success 成功 error 失败</p>
     */
    public Map<String, Object> deleteView(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            log.error("参数缺失");
            return null;
        }
        // 获取请求参数
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        String userId = LiveGlobalConfig.getUserId();
        String time = String.valueOf(System.currentTimeMillis());

        // 构建请求链接
        String url = String.format(URL_FORMAT, channelId);

        // 构建表单
        Map<String, String> signRequest = getRequestMap();
        String response = HttpUtil.post(url, new HashMap<>(signRequest));
        return JSONObject.toJavaObject(JSONObject.parseObject(response), Map.class);
    }

    /**
     * 关闭直播间
     *
     * @param channelId 频道id
     * @return 响应信息
     */
    public Map<String, Object> videoStreamClose(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            log.error("参数缺失");
            return null;
        }
        String url = String.format(CLOSE_URL_FORMAT, channelId);
        Map<String, String> requestMap = getRequestMap();
        String response = HttpUtil.post(url, new HashMap<>(requestMap));
        log.info("关闭直播间响应：{}", response);
        return JSONObject.toJavaObject(JSONObject.parseObject(response), Map.class);
    }

    /**
     * 封装请求参数map
     *
     * @return
     */
    private Map<String, String> getRequestMap() {
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", LiveGlobalConfig.getAppId());
        requestMap.put("appSecret", LiveGlobalConfig.getAppSecret());
        requestMap.put("userId", LiveGlobalConfig.getUserId());
        requestMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        try {
            requestMap.put("sign", LiveSignUtil.getSign(requestMap, LiveGlobalConfig.getAppSecret()));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            log.error("获取保利威请求签名失败，表单：{}", requestMap);
            throw new BusinessException("获取签名失败");
        }
        return requestMap;
    }

    /**
     * 恢复直播间
     *
     * @param channelId
     * @return
     */
    public Map<String, Object> videoStreamResume(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            log.error("参数缺失");
            return null;
        }

        String url = String.format(RESUME_URL_FORMAT, channelId);
        String response = HttpUtil.post(url, new HashMap<>(getRequestMap()));
        return JSONObject.toJavaObject(JSONObject.parseObject(response), Map.class);
    }

    /**
     * 获取频道基本信息
     *
     * @param channelId 频道id
     * @return
     */
    public ChannelResponseBO getChannelBasicInfo(String channelId) {
        LiveChannelBasicInfoRequest request = new LiveChannelBasicInfoRequest();
        request.setChannelId(channelId);
        try {
            LiveChannelBasicInfoResponse response = new LiveChannelOperateServiceImpl().getChannelBasicInfo(request);
            log.info("频道信息：{}", response);
            return videoStreamInverter.liveChannelBasicInfoResponse2ChannelResponseBO(response);
        } catch (IOException | NoSuchAlgorithmException e) {
            log.error("获取频道信息失败：{}", request);
            throw new BusinessException(e);
        }
    }

    /**
     * 根据频道ID生成讲师的 登录链接
     *
     * @param channelId 频道ID
     * @return 单点登录链接
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public String generateTeacherSSOLink(String channelId) throws IOException, NoSuchAlgorithmException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        //频道号
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        //自定义的token，只能使用一次，且10秒内有效
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        String url = String.format("http://api.polyv.net/live/v2/channels/%s/set-token", channelId);

        //1、设置频道单点登录token
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        requestMap.put("timestamp", timestamp);
        requestMap.put("token", token);
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));
        String response = com.scnujxjy.backendpoint.util.polyv.HttpUtil.postFormBody(url, requestMap);
        //TODO 判断response返回是否成功

        //2、生成讲师授权登录地址
        String redirectUrl = "https://console.polyv.net/web-start/?channelId=" + channelId;
        String authURL = "https://console.polyv.net/teacher/auth-login";
        authURL +=
                "?channelId=" + channelId + "&token=" + token + "&redirect=" + URLEncoder.encode(redirectUrl, "utf-8");
        log.info("讲师单点登录地址设置成功，跳转地址为：{}", authURL);
        return authURL;
    }


    /**
     * 根据频道ID生成助教的单点登录链接
     *
     * @param channelId 频道ID
     * @return 单点登录链接
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public String generateTutorSSOLink(String channelId, String accountId) throws IOException, NoSuchAlgorithmException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        //频道号
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        //自定义的token，只能使用一次，且10秒内有效
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        //助教账号
        String url = String.format("http://api.polyv.net/live/v2/channels/%s/set-account-token", accountId);

        //1、设置助教单点登录token
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        requestMap.put("timestamp", timestamp);
        requestMap.put("token", token);
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));
        String response = com.scnujxjy.backendpoint.util.polyv.HttpUtil.postFormBody(url, requestMap);
        //TODO 判断response返回是否成功

        //2、生成助教授权登录地址
        String redirectUrl = "https://console.polyv.net/assistant/?accountId=" + accountId;
        String authURL = "https://console.polyv.net/teacher/auth-login";
        authURL +=
                "?channelId=" + accountId + "&token=" + token + "&redirect=" + URLEncoder.encode(redirectUrl, "utf-8");
        log.info("助教单点登录设置成功，跳转地址为：{}", authURL);
        return authURL;
    }

    /**
     * 生成学生单点登录链接
     *
     * @param channelId  频道 id
     * @param userId     用户 id
     * @param username   用户昵称
     * @param avatarPath 用户头像地址
     * @return 单点登录链接
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public String getStudentSSOLink(String channelId, String userId, String username, String avatarPath) throws IOException, NoSuchAlgorithmException {
        ChannelInfoResponse channelInfoByChannelId = getChannelInfoByChannelId(channelId);
        if (Objects.isNull(channelInfoByChannelId)) {
            return null;
        }
        ChannelDetail channelDetail = channelInfoByChannelId.getData();
        if (Objects.isNull(channelDetail)) {
            return null;
        }
        List<AuthSetting> authSettings = channelDetail.getAuthSettings();
        List<String> secretKeys = authSettings.stream()
                .map(AuthSetting::getDirectKey)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(secretKeys)) {
            return null;
        }
        String secretKey = secretKeys.get(0);
        StringBuilder url = new StringBuilder("https://live.polyv.cn/watch/");
        String ts = String.valueOf(System.currentTimeMillis());
        String sign = MD5.create().digestHex(secretKey + userId + secretKey + ts);
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("userId", userId);
        requestMap.put("ts", ts);
        requestMap.put("sign", sign);
        requestMap.put("nickname", Base64.encode(username));
        if (StrUtil.isNotBlank(avatarPath)) {
            requestMap.put("avatar", avatarPath);
        }
        String query = URLUtil.buildQuery(requestMap, StandardCharsets.UTF_8);
        url.append(channelId).append("?").append(query);
        return url.toString();
    }

    /**
     * 生成嘉宾（管理员）查看链接
     *
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public String getAdminSSOLink() throws IOException, NoSuchAlgorithmException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        String accountId = StpUtil.getLoginIdAsString().replaceAll("T", "");
        String url = String.format("http://api.polyv.net/live/v2/channels/%s/set-account-token", accountId);
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", LiveGlobalConfig.getAppId());
        requestMap.put("timestamp", timestamp);
        requestMap.put("token", token);
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, LiveGlobalConfig.getAppSecret()));
        String response = com.scnujxjy.backendpoint.util.polyv.HttpUtil.postFormBody(url, requestMap);
        log.info("生成嘉宾响应信息：{}", response);
        // 生成嘉宾授权地址
        String redirectUrl = "https://console.polyv.net/web-start/?channelId=" + accountId;
        String authURL = "https://console.polyv.net/teacher/auth-login";
        authURL += "?channelId=" + accountId + "&token=" + token + "&redirect=" + URLEncoder.encode(redirectUrl, "utf-8");
        log.info("嘉宾单点登录设置成功，跳转地址为：{}", authURL);
        return authURL;
    }

    /**
     * 查询指定频道号下的所有角色信息
     *
     * @param channelId 频道ID
     * @return 角色信息的JSON字符串
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ChannelResponse getAccountsByChannelId(String channelId) throws IOException, NoSuchAlgorithmException {
        String url = "https://api.polyv.net/live/v2/channelAccount/%s/accounts";

        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", LiveGlobalConfig.getAppId());
        requestMap.put("timestamp", timestamp);
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, LiveGlobalConfig.getAppSecret()));

        url = String.format(url, channelId);
        ChannelResponse channelResponse = null;
        try {
            String response = PolyvHttpUtil.get(url, requestMap);
            // 解析响应为 ChannelResponse POJO
            channelResponse = JSON.parseObject(response, new TypeReference<ChannelResponse>() {
            });
        } catch (Exception e) {
            log.error("获取频道 (" + channelId + ") 下的角色信息失败 " + e.toString());
        }

        return channelResponse;
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
    public String setWatchCondition(String channelId, String body) throws IOException, NoSuchAlgorithmException {
        String url = "http://api.polyv.net/live/v3/channel/auth/update";
        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", LiveGlobalConfig.getAppId());
        requestMap.put("timestamp", timestamp);
        requestMap.put("channelId", channelId);
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, LiveGlobalConfig.getAppSecret()));

        url = PolyvHttpUtil.appendUrl(url, requestMap);

        String response = PolyvHttpUtil.postJsonBody(url, body, null);

        log.info("设置频道 {} 的观看条件，接口返回值：{}", channelId, response);

        return response;
    }

    /**
     * 创建频道
     *
     * @param liveRequestBody
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ApiResponse createChannel(LiveRequestBody liveRequestBody) throws IOException, NoSuchAlgorithmException {
        //公共参数,填写自己的实际
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

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
        String response = PolyvHttpUtil.postJsonBody(url, body, null);

        return JSON.parseObject(response, ApiResponse.class);
    }

    /**
     * 增加助教或嘉宾账号
     *
     * @param createRoleRequestBody
     * @param channelId
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public CreateMainResponse addChannelAccount(CreateRoleRequestBody createRoleRequestBody,
                                                String channelId) throws IOException,
            NoSuchAlgorithmException {
        // 获取公共参数
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // 设置业务URL
        String url = "http://api.polyv.net/live/v4/channel/account/create";

        // 构建请求参数
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        requestMap.put("timestamp", timestamp);
        requestMap.put("channelId", channelId);

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("role", createRoleRequestBody.getRole());
        bodyMap.put("actor", createRoleRequestBody.getActor());
        bodyMap.put("nickName", createRoleRequestBody.getNickName());
        bodyMap.put("avatar", createRoleRequestBody.getAvatar());
        bodyMap.put("passwd", createRoleRequestBody.getPasswd());
        bodyMap.put("purviewList", createRoleRequestBody.getPurviewList());

        // 生成签名
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));

        // 调用HTTP POST方法
        url = PolyvHttpUtil.appendUrl(url, requestMap);
        String response = PolyvHttpUtil.postJsonBody(url, JSON.toJSONString(bodyMap), null);

        log.info("创建角色成功：{}", response);

        return JSON.parseObject(response, CreateMainResponse.class);
    }

    /**
     * 修改直播开始时间和结束时间
     *
     * @param channelId 频道ID
     * @param endTime   结束时间
     * @param startTime 开始时间
     * @return ApiResponse
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ChannelResponse setLivingTime(String channelId, String startTime, String endTime) throws IOException, NoSuchAlgorithmException {
        // 获取公共参数
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // 设置业务URL
        String url = String.format("http://api.polyv.net/live/v2/channelSetting/%s/set-countdown", channelId);

        // 构建请求参数
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        requestMap.put("timestamp", timestamp);
        requestMap.put("endTime", endTime);
        requestMap.put("startTime", startTime);

        // 生成签名
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));

        // 调用HTTP POST方法
        String response = PolyvHttpUtil.postFormBody(url, requestMap);

        log.info("修改直播倒计时设置成功：{}", response);

        return JSON.parseObject(response, ChannelResponse.class);
    }

    /**
     * 批量修改频道弹幕开关
     *
     * @param closeDanmu           是否关闭弹幕功能
     * @param showDanmuInfoEnabled 是否显示弹幕信息开关
     * @param channelIds           需要修改弹幕开关的频道号，多个频道号用逗号隔开
     * @return ApiResponse
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ChannelResponse batchUpdateDanmu(String closeDanmu, String showDanmuInfoEnabled,
                                            String channelIds) throws IOException,
            NoSuchAlgorithmException {
        // 获取公共参数
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // 设置业务URL
        String url = "http://api.polyv.net/live/v3/channel/basic/batchUpdateDanmu";

        // 构建请求参数
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        requestMap.put("timestamp", timestamp);
        requestMap.put("closeDanmu", closeDanmu);
        requestMap.put("showDanmuInfoEnabled", showDanmuInfoEnabled);
        requestMap.put("channelIds", channelIds);

        // 生成签名
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));

        // 调用HTTP POST方法
        String response = PolyvHttpUtil.postFormBody(url, requestMap);

        log.info("批量修改频道弹幕开关成功：{}", response);

        return JSON.parseObject(response, ChannelResponse.class);
    }

    /**
     * 修改频道主持人姓名
     *
     * @param publisherName 主持人姓名
     * @param channelId     频道号，如果不提供或传-1，则修改该用户的所有频道号的主持人姓名
     * @return ApiResponse
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ChannelResponse setPublisherName(String publisherName, String channelId) throws IOException, NoSuchAlgorithmException {
        // 获取公共参数
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        String userId = LiveGlobalConfig.getUserId();
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // 设置业务URL
        String url = String.format("http://api.polyv.net/live/v2/channelSetting/%s/setPublisher", userId);

        // 构建请求参数
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        requestMap.put("timestamp", timestamp);
        requestMap.put("publisher", publisherName);
        requestMap.put("channelId", channelId);

        // 生成签名
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));

        // 调用HTTP POST方法
        String response = PolyvHttpUtil.postFormBody(url, requestMap);

        log.info("修改频道主持人姓名成功：{}", response);

        return JSON.parseObject(response, ChannelResponse.class);
    }

    /**
     * 修改频道名称
     *
     * @param channelName 修改后的频道名称
     * @param channelId   频道号
     * @return ChannelResponse
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ChannelResponse updateChannelName(String channelName, String channelId) throws IOException, NoSuchAlgorithmException {
        // 获取公共参数
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        String userId = LiveGlobalConfig.getUserId();
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // 设置业务URL
        String url = String.format("http://api.polyv.net/live/v2/channels/%s/update", channelId);

        // 构建请求参数
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        requestMap.put("timestamp", timestamp);
        requestMap.put("name", channelName);

        // 生成签名
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));

        // 调用HTTP POST方法
        String response = PolyvHttpUtil.postFormBody(url, requestMap);

        log.info("修改频道名称成功：{}", response);

        return JSON.parseObject(response, ChannelResponse.class);
    }

    /**
     * 修改频道信息 比如设置连麦人数、直播介绍、是否无延迟直播、弹幕是否打开、开始时间、结束时间、主持人名称
     * 频道密码、频道名称
     *
     * @param channelId
     * @param basicSetting
     * @param authSettings
     * @return
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ChannelResponse updateChannelDetailSetting(String channelId, BasicSetting basicSetting,
                                                      List<AuthSetting> authSettings)
            throws IOException, NoSuchAlgorithmException {
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        // 获取当前时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // 构造URL
        String url = "http://api.polyv.net/live/v3/channel/basic/update";

        // 构造请求参数
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        requestMap.put("timestamp", timestamp);
        requestMap.put("channelId", channelId);
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));
        url = PolyvHttpUtil.appendUrl(url, requestMap);

        // 将POJO转为JSON字符串
        String json = generateJsonString(basicSetting, authSettings);

        // 发起请求
        String response = PolyvHttpUtil.postJsonBody(url, json, null);
        log.info("修改频道信息，返回值：{}", response);

        return JSON.parseObject(response, ChannelResponse.class);
    }

    private String generateJsonString(BasicSetting basicSetting, List<AuthSetting> authSettings) {
        JSONObject root = new JSONObject();
        root.put("basicSetting", JSON.toJSON(basicSetting));
        root.put("authSettings", JSON.toJSON(authSettings));
        return root.toJSONString();
    }

    /**
     * 查询指定频道号下的频道完整信息
     *
     * @param channelId 频道ID
     * @return 角色信息的JSON字符串
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ChannelInfoResponse getChannelInfoByChannelId(String channelId) throws IOException, NoSuchAlgorithmException {
        String url = "http://api.polyv.net/live/v4/channel/basic/get";

        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", LiveGlobalConfig.getAppId());
        requestMap.put("timestamp", timestamp);
        requestMap.put("channelId", channelId);
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, LiveGlobalConfig.getAppSecret()));

        ChannelInfoResponse channelResponse = null;
        try {
            String response = PolyvHttpUtil.get(url, requestMap);
            log.info("频道信息返回值 \n" + response);
            // 解析响应为 ChannelResponse POJO
            channelResponse = JSON.parseObject(response, new TypeReference<ChannelInfoResponse>() {
            });
        } catch (Exception e) {
            log.error("获取频道 (" + channelId + ") 下的角色信息失败 " + e.toString());
        }

        return channelResponse;
    }


    /**
     * 获取指定频道下的角色信息
     *
     * @throws Exception
     * @throws NoSuchAlgorithmException
     */
    public LiveSonChannelInfoListResponse getRoleInfo(String channelId) throws Exception, NoSuchAlgorithmException {
        LiveSonChannelInfoListRequest liveSonChannelInfoListRequest = new LiveSonChannelInfoListRequest();
        LiveSonChannelInfoListResponse liveSonChannelInfoResponse;
        try {
            //准备测试数据
            liveSonChannelInfoListRequest.setChannelId(channelId);
            liveSonChannelInfoResponse = new LiveChannelOperateServiceImpl().getSonChannelInfoList(
                    liveSonChannelInfoListRequest);
            if (liveSonChannelInfoResponse != null) {
                //to do something ......
                log.info("查询频道号下所有角色信息成功{}", JSON.toJSONString(liveSonChannelInfoResponse));
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

        return liveSonChannelInfoResponse;
    }


    /**
     * 更新默认助教的权限信息
     *
     * @param channelId
     */
    public boolean generateTutor(String channelId, String name, String password) {
        try {
            // Step 1: 获取角色信息
            LiveSonChannelInfoListResponse roleInfo = getRoleInfo(channelId);
            if (roleInfo != null && !roleInfo.getSonChannelInfos().isEmpty()) {
                LiveSonChannelInfoResponse liveSonChannelInfoResponse = roleInfo.getSonChannelInfos().get(0);// 假设助教是列表中的第一个角色
                String account1 = liveSonChannelInfoResponse.getAccount();
                // Step 2: 创建更新请求对象
                LiveUpdateAccountRequest updateRequest = new LiveUpdateAccountRequest();
                updateRequest.setChannelId(channelId)
                        .setAccount(account1)
                        .setPasswd(password)  // 你可以选择更新密码或保留原密码
                        .setNickName("name")
                        .setPurviewList(Arrays.asList(
                                new LiveUpdateAccountRequest.Purview().setCode("chatListEnabled").setEnabled("Y"),
                                new LiveUpdateAccountRequest.Purview().setCode("pageTurnEnabled").setEnabled("Y"),
                                new LiveUpdateAccountRequest.Purview().setCode("chatAuditEnabled").setEnabled("Y")
                        ));  // 设置新的权限

                // Step 3: 调用 updateAccount 方法更新助教的权限
                LiveUpdateAccountResponse updateResponse = new LiveChannelOperateServiceImpl().updateAccount(updateRequest);
                if (updateResponse != null) {
                    log.info("更新助教权限成功: {}", JSON.toJSONString(updateResponse));
                    return true;

                } else {
                    log.error("更新助教权限失败");
                }
            } else {
                log.error("未找到任何助教信息");
            }
        } catch (PloyvSdkException e) {
            log.error("参数校验失败或服务器处理异常: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("SDK调用异常: {}", e.getMessage(), e);
        }
        return false;
    }


    /**
     * 查询指定频道号的回放设置
     *
     * @param channelId 频道ID
     * @return 回放设置的JSON字符串
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ChannelPlayBackInfoResponse getPlaybackSettingByChannelId(String channelId) throws IOException, NoSuchAlgorithmException {
        // Endpoint URL for fetching playback settings
        String url = "http://api.polyv.net/live/v3/channel/playback/get-setting";

        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // Constructing the request parameters
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", LiveGlobalConfig.getAppId());
        requestMap.put("timestamp", timestamp);
        requestMap.put("channelId", channelId);
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, LiveGlobalConfig.getAppSecret()));

        ChannelPlayBackInfoResponse playbackResponse = null;
        try {
            String response = PolyvHttpUtil.get(url, requestMap);  // assuming PolyvHttpUtil can be used here
            log.info("回放设置返回值 \n" + response);
            // 解析响应为 PlaybackSettingResponse POJO
            playbackResponse = JSON.parseObject(response, new TypeReference<ChannelPlayBackInfoResponse>() {
            });
        } catch (Exception e) {
            log.error("获取频道 (" + channelId + ") 的回放设置失败 " + e.toString());
        }

        return playbackResponse;
    }

    /**
     * 修改频道回放设置
     *
     * @param playbackSetting 请求参数
     * @return 返回的JSON字符串
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public ChannelResponse setRecordSetting(ChannelInfoData playbackSetting) throws IOException, NoSuchAlgorithmException {
        String url = "http://api.polyv.net/live/v3/channel/playback/set-setting";

        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // Constructing the request parameters
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", LiveGlobalConfig.getAppId());
        requestMap.put("timestamp", timestamp);
        requestMap.put("channelId", playbackSetting.getChannelId() == null ? "" : playbackSetting.getChannelId());
        requestMap.put("globalSettingEnabled", playbackSetting.getGlobalSettingEnabled() == null ? "" : playbackSetting.getGlobalSettingEnabled());
        requestMap.put("crontabType", playbackSetting.getCrontType() == null ? "" : playbackSetting.getCrontType());
        requestMap.put("startTime", playbackSetting.getStartTime() == null ? "" : "" + playbackSetting.getStartTime());
        requestMap.put("endTime", playbackSetting.getEndTime() == null ? "" : "" + playbackSetting.getEndTime());
        requestMap.put("playbackEnabled", playbackSetting.getPlaybackEnabled() == null ? "" : playbackSetting.getPlaybackEnabled());
        requestMap.put("type", playbackSetting.getType() == null ? "" : playbackSetting.getType());
        requestMap.put("origin", playbackSetting.getOrigin() == null ? "" : playbackSetting.getOrigin());
        requestMap.put("videoId", playbackSetting.getVideoId() == null ? "" : playbackSetting.getVideoId());
        requestMap.put("sectionEnabled", playbackSetting.getSectionEnabled() == null ? "" : playbackSetting.getSectionEnabled());
        requestMap.put("chatPlaybackEnabled", playbackSetting.getChatPlaybackEnabled() == null ? "" : playbackSetting.getChatPlaybackEnabled());
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
            log.error("设置频道 (" + playbackSetting.getChannelId() + ") 的回放参数失败 " + e.toString());
        }

        return playbackResponse;

    }


    public List<String> getAllChannels() {
        List<String> channelIdList = new ArrayList<>();

        try {
            String appId = LiveGlobalConfig.getAppId();
            String appSecret = LiveGlobalConfig.getAppSecret();
            String userId = LiveGlobalConfig.getUserId();
            String timestamp = String.valueOf(System.currentTimeMillis());

            // 业务参数
            String url = "http://api.polyv.net/live/v3/user/channels";


            // HTTP 调用逻辑
            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("appId", appId);
            requestMap.put("timestamp", timestamp);

            requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));
            String response = com.scnujxjy.backendpoint.util.polyv.HttpUtil.get(url, requestMap);
            log.info("测试查询频道列表，返回值：{}", response);

            // 解析 JSON 响应
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("code") && rootNode.get("code").asInt() == 200) {
                JsonNode channelsNode = rootNode.get("data").get("channels");

                if (channelsNode.isArray()) {
                    for (JsonNode channelNode : channelsNode) {
                        channelIdList.add(channelNode.asText());
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取所有直播间 ID 失败 " + e.toString());
        }

        return channelIdList;
    }


}
