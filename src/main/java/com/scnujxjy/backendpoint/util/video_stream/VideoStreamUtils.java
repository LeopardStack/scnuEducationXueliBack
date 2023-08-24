package com.scnujxjy.backendpoint.util.video_stream;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.inverter.video_stream.VideoStreamInverter;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelRequestBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.SonChannelRequestBO;
import lombok.extern.slf4j.Slf4j;
import net.polyv.live.v1.config.LiveGlobalConfig;
import net.polyv.live.v1.entity.channel.operate.LiveCreateSonChannelListRequest;
import net.polyv.live.v1.entity.channel.viewdata.LiveListChannelViewlogRequest;
import net.polyv.live.v1.entity.channel.viewdata.LiveListChannelViewlogResponse;
import net.polyv.live.v1.entity.quick.QuickCreateChannelResponse;
import net.polyv.live.v1.entity.quick.QuickCreatePPTChannelRequest;
import net.polyv.live.v1.service.channel.impl.LiveChannelViewdataServiceImpl;
import net.polyv.live.v1.service.quick.impl.LiveChannelQuickCreatorServiceImpl;
import net.polyv.live.v1.util.LiveSignUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@Component
@Slf4j
public class VideoStreamUtils {

    private final static LiveChannelViewdataServiceImpl liveChannelViewdataService = new LiveChannelViewdataServiceImpl();
    private final static LiveChannelQuickCreatorServiceImpl liveChannelQuickCreatorService = new LiveChannelQuickCreatorServiceImpl();
    @Resource
    private VideoStreamInverter videoStreamInverter;

    public static final String URL_FORMAT = "http://api.polyv.net/live/v2/channels/%s/delete";

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
            return null;
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
        Map<String, String> signRequest = new HashMap<>();
        signRequest.put("appId", appId);
        signRequest.put("appSecret", appSecret);
        signRequest.put("userId", userId);
        signRequest.put("timestamp", time);
        try {
            signRequest.put("sign", LiveSignUtil.getSign(signRequest, appSecret));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            log.error("获取保利威请求签名失败，表单：{}", signRequest);
            return null;
        }
        Map<String, Object> request = new HashMap<>(signRequest);
        String response = HttpUtil.post(url, request);
        return JSONObject.toJavaObject(JSONObject.parseObject(response), Map.class);
    }


}
