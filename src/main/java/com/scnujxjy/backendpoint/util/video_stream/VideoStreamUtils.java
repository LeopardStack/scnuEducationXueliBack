package com.scnujxjy.backendpoint.util.video_stream;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.obs.shade.okhttp3.*;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.inverter.video_stream.VideoStreamInverter;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelRequestBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.SonChannelRequestBO;
import lombok.extern.slf4j.Slf4j;
import net.polyv.live.v1.config.LiveGlobalConfig;
import net.polyv.live.v1.entity.channel.operate.LiveChannelBasicInfoRequest;
import net.polyv.live.v1.entity.channel.operate.LiveChannelBasicInfoResponse;
import net.polyv.live.v1.entity.channel.operate.LiveCreateSonChannelListRequest;
import net.polyv.live.v1.entity.channel.viewdata.LiveListChannelViewlogRequest;
import net.polyv.live.v1.entity.channel.viewdata.LiveListChannelViewlogResponse;
import net.polyv.live.v1.entity.quick.QuickCreateChannelResponse;
import net.polyv.live.v1.entity.quick.QuickCreatePPTChannelRequest;
import net.polyv.live.v1.service.channel.impl.LiveChannelOperateServiceImpl;
import net.polyv.live.v1.service.channel.impl.LiveChannelViewdataServiceImpl;
import net.polyv.live.v1.service.quick.impl.LiveChannelQuickCreatorServiceImpl;
import net.polyv.live.v1.util.LiveSignUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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
     * 根据频道ID生成讲师的单点登录链接
     *
     * @param channelId 频道ID
     * @return 单点登录链接
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public String generateTeacherSSOLink(String channelId) throws IOException, NoSuchAlgorithmException {
        // 公共参数,填写自己的实际参数
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        long beijingTimestamp1 = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp1);

        // 自定义的token，只能使用一次，且10秒内有效
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        String url = String.format("http://api.polyv.net/live/v2/channels/%s/set-token", channelId);



        // 1、设置频道单点登录token
        OkHttpClient client = new OkHttpClient();
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        requestMap.put("timestamp", String.valueOf(beijingTimestamp));
        requestMap.put("token", token);

        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("appId", appId)
                .add("timestamp", timestamp)
                .add("token", token)
                .add("sign", LiveSignUtil.getSign(requestMap, appSecret));
        Request request = new Request.Builder()
                .url(url)
                .post(formBuilder.build())
                .build();
        Response response = client.newCall(request).execute();
        // TODO: 判断response返回是否成功
        log.info("单点登录返回值 " + response.toString());
        // 2、生成讲师授权登录地址
        String redirectUrl = "https://console.polyv.net/web-start/?channelId=" + channelId;
        String authURL = "https://console.polyv.net/teacher/auth-login";
        authURL += "?channelId=" + channelId + "&token=" + token + "&redirect=" + URLEncoder.encode(redirectUrl, "utf-8");
        log.info("讲师单点登录地址设置成功，跳转地址为：{}", authURL);

        return authURL;
    }

    /**
     * 查询指定频道号下的所有角色信息
     * @param channelId 频道ID
     * @return 角色信息的JSON字符串
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public String getAccountsByChannelId(String channelId) throws IOException, NoSuchAlgorithmException {
        OkHttpClient client = new OkHttpClient();
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", LiveGlobalConfig.getAppId());

        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        requestMap.put("timestamp", String.valueOf(beijingTimestamp));

        requestMap.put("sign", LiveSignUtil.getSign(requestMap, LiveGlobalConfig.getAppSecret()));

        HttpUrl.Builder urlBuilder = HttpUrl.parse(String.format("https://api.polyv.net/live/v2/channelAccount/%s/accounts", channelId)).newBuilder();
        for (Map.Entry<String, String> entry : requestMap.entrySet()) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }

        Request request = new Request.Builder()
                .url(urlBuilder.build())
                .get()
                .build();

        log.info("\n请求信息为 " + request.toString());
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                log.info("\n获取指定频道 " + channelId + " 的角色信息 " + response.toString());
                return response.body().string();
            } else {
                throw new IOException("Unexpected code " + response.toString());
            }
        }
    }

}
