package com.scnujxjy.backendpoint.util.video_stream;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ChannelResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.LiveRequestBody;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ChannelResponseData;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelInfoData;
import com.scnujxjy.backendpoint.model.bo.course_learning.StudentWhiteListInfoBO;
import com.scnujxjy.backendpoint.model.vo.video_stream.StudentWhiteListVO;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.polyv.LiveSignUtil;
import com.scnujxjy.backendpoint.util.polyv.PolyvHttpUtil;
import lombok.extern.slf4j.Slf4j;
import net.polyv.common.v1.exception.PloyvSdkException;
import net.polyv.live.v1.config.LiveGlobalConfig;
import net.polyv.live.v1.constant.LiveConstant;
import net.polyv.live.v1.entity.channel.operate.LiveChannelSettingRequest;
import net.polyv.live.v1.entity.channel.operate.LiveSonChannelInfoListResponse;
import net.polyv.live.v1.entity.channel.playback.LiveChannelPlaybackEnabledInfoRequest;
import net.polyv.live.v1.entity.web.auth.LiveCreateChannelWhiteListRequest;
import net.polyv.live.v1.entity.web.auth.LiveUpdateChannelAuthRequest;
import net.polyv.live.v1.entity.web.auth.LiveUploadWhiteListRequest;
import net.polyv.live.v1.service.channel.impl.LiveChannelPlaybackServiceImpl;
import net.polyv.live.v1.service.web.impl.LiveWebAuthServiceImpl;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

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
        liveRequestBody.setCategoryId(520488);//设置直播分类为2024学历教育520488。 510210是非学历培训，测试510211,默认分类486269
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
                //默认开启回放，需要再调接口关闭
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


    public SaResult addExtraChannelWhiteStudent(String channelId, List<StudentWhiteListInfoBO> studentWhiteListInfoBOList) {
        log.info("调用批量新增白名单接口，请求入参为:{}", studentWhiteListInfoBOList);
        SaResult saResult = new SaResult();
        Boolean liveCreateChannelWhiteListResponse;
        List<StudentWhiteListVO> successList = new ArrayList<>();
        List<StudentWhiteListVO> failList = new ArrayList<>();

        // Convert StudentWhiteListInfoBO to StudentWhiteListVO
        for (StudentWhiteListInfoBO infoBO : studentWhiteListInfoBOList) {
            StudentWhiteListVO studentWhite = new StudentWhiteListVO();
            studentWhite.setCode(infoBO.getIdNumber());
            studentWhite.setName(infoBO.getName());
            failList.add(studentWhite);
        }

        Iterator<StudentWhiteListVO> iterator = failList.iterator();
        try {
            while (iterator.hasNext()) {
                LiveCreateChannelWhiteListRequest liveCreateChannelWhiteListRequest = new LiveCreateChannelWhiteListRequest();
                StudentWhiteListVO studentWhite = iterator.next();
                liveCreateChannelWhiteListRequest
                        .setRank(1)
                        .setChannelId(channelId)
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


    public SaResult addChannelWhiteStudent(String channelId, List<StudentWhiteListInfoBO> students) {
        log.info("调用批量新增白名单接口，请求入参为: channelId={}, students={}", channelId, students);
        SaResult saResult = new SaResult();

        // 在内存中准备数据
        List<StudentWhiteListVO> excelDataList = new ArrayList<>();
        for (StudentWhiteListInfoBO student : students) {
            StudentWhiteListVO vo = new StudentWhiteListVO();
            vo.setCode(student.getIdNumber());
            vo.setName(student.getName());
            excelDataList.add(vo);
        }

        // 使用 EasyExcel 将数据写入到 ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        EasyExcel.write(outputStream, StudentWhiteListVO.class).sheet("Sheet1").doWrite(excelDataList);

        // 指定保存文件的路径
        String saveDirPath = "D:\\ScnuWork\\xueli\\xueliBackEnd\\src\\main\\resources\\data\\保利威白名单";
        Path saveDir = Paths.get(saveDirPath);
        if (!Files.exists(saveDir)) {
            try {
                Files.createDirectories(saveDir);
            } catch (IOException e) {
                log.error("创建保存目录失败", e);
                saResult.setCode(ResultCode.FAIL.getCode());
                saResult.setMsg("创建保存目录失败: " + e.getMessage());
                return saResult;
            }
        }
        String uniqueFileName = "whiteList-" + UUID.randomUUID().toString() + ".xlsx";
        Path filePath = saveDir.resolve(uniqueFileName);

        try {
            // 将 ByteArrayOutputStream 的数据写入文件
            Files.write(filePath, outputStream.toByteArray());

            // 创建上传请求对象并上传文件
            LiveUploadWhiteListRequest request = new LiveUploadWhiteListRequest();
            request.setChannelId(channelId)
                    .setRank(1)
                    .setFile(filePath.toFile());
            Boolean uploadResult = new LiveWebAuthServiceImpl().uploadWhiteList(request);

            if (uploadResult) {
                saResult.setCode(ResultCode.SUCCESS.getCode());
                saResult.setMsg("上传白名单成功，文件路径：" + filePath.toString());
            } else {
                saResult.setCode(ResultCode.FAIL.getCode());
                saResult.setMsg("上传白名单失败");
            }
        } catch (Exception e) {
            log.error("创建或上传文件时发生错误", e);
            saResult.setCode(ResultCode.FAIL.getCode());
            saResult.setMsg("创建或上传文件时发生错误: " + e.getMessage());
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                log.error("关闭输出流时发生错误", e);
            }
        }

        return saResult;
    }



}
