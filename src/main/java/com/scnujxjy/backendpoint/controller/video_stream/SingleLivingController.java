package com.scnujxjy.backendpoint.controller.video_stream;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.SectionsPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.StudentRecords;
import com.scnujxjy.backendpoint.dao.entity.video_stream.TutorAllInformation;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoInformation;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.SectionsMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.StudentRecordsMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoInformationMapper;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelViewRequest;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelViewStudentRequest;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingService;
import com.scnujxjy.backendpoint.util.MessageSender;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.polyv.HttpUtil;
import com.scnujxjy.backendpoint.util.polyv.LiveSignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;

/**
 * 直播记录管理
 *
 * @author leopard
 * @since 2023-08-21
 */
@RestController
@Slf4j
@RequestMapping("/SingleLiving")
public class SingleLivingController {

    @Resource
    private SingleLivingService singleLivingService;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private TeacherInformationService teacherInformationService;

    @Resource
    private MessageSender messageSender;

    @Resource
    private VideoInformationMapper videoInformationMapper;

    @Resource
    private SectionsMapper sectionsMapper;

    @Resource
    private StudentRecordsMapper studentRecordsMapper;

    @PostMapping("/countStudentVideo")
    public SaResult countStudentVideo(@RequestBody ChannelInfoRequest channelInfoRequest) {
        if (channelInfoRequest.getStudentNumber() == null || channelInfoRequest.getVideoId() == null || channelInfoRequest.getWatched() == null) {
            return SaResult.error("缺少必要参数");
        }

        try {

            StudentRecords studentRecords1 = studentRecordsMapper.selectByNumberAndVideoId(channelInfoRequest.getStudentNumber(), channelInfoRequest.getVideoId());
            if (studentRecords1 != null){
                //说明之前已经观看过，无需插入了
                return SaResult.ok("该学生本堂课已观看过");
            }

            StudentRecords studentRecords = new StudentRecords();
            studentRecords.setStudentNumber(channelInfoRequest.getStudentNumber());
            studentRecords.setVideoId(channelInfoRequest.getVideoId());
            studentRecords.setWatched(channelInfoRequest.getWatched());
            studentRecordsMapper.insert(studentRecords);
            return SaResult.ok("记录该学生本堂课观看成功");
        } catch (Exception e) {
            log.error("记录学生是否观看过该视频异常，入参为" + channelInfoRequest, e);
            return SaResult.error("记录学生是否观看该视频异常，请联系管理员");
        }

    }

    @PostMapping("/queryVideoInformation")
    public SaResult queryVideoInformation(@RequestBody ChannelInfoRequest channelInfoRequest) {

        if (channelInfoRequest.getCourseId() != null) {
            if (channelInfoRequest.getPageSize() == null || channelInfoRequest.getCurrentPage() == null) {
                return SaResult.error("分页参数缺失");
            }

            List<SectionsPO> sectionsPOS = sectionsMapper.selectSectionsByCourseId(channelInfoRequest.getCourseId());
            List<Long> idList = sectionsPOS.stream()
                    .map(SectionsPO::getId)
                    .collect(Collectors.toList());

            QueryWrapper<VideoInformation> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("status", 1)
                    .in("section_id", idList)
                    .orderByAsc("video_information.section_id");

            int offset = (channelInfoRequest.getCurrentPage() - 1) * channelInfoRequest.getPageSize();
            int limit = channelInfoRequest.getPageSize();
            queryWrapper.last("LIMIT " + offset + "," + limit);
            List<VideoInformation> videoInformations = videoInformationMapper.selectList(queryWrapper);

            return SaResult.data(videoInformations);

        } else if (channelInfoRequest.getSectionId() != null) {
            VideoInformation videoInformation = videoInformationMapper.selectBySectionId(channelInfoRequest.getSectionId());
            return SaResult.data(videoInformation);
        }

        return SaResult.error("缺少必要参数课程id或者节点id");

    }

    @PostMapping("/downloadCZ")
    public void addRecordTask(String savePath,String channelId) throws Exception {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        String traceId = uuid.substring(0, 10);

        String url="http://api.polyv.net/live/v3/channel/pptRecord/list";
        String status="success";
        String page=String.valueOf(1);
        String pageSize=String.valueOf(100);
        String timestamp = String.valueOf(System.currentTimeMillis());
        String appSecret ="a642eb8a7e8f425995d9aead5bdd83ea";

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", "gj95rpxjhf");
        requestMap.put("timestamp", timestamp);
        requestMap.put("channelId", channelId);
        requestMap.put("status", status);
        requestMap.put("page", page);
        requestMap.put("pageSize", pageSize);
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));
        String response1 = HttpUtil.get(url, requestMap);
        log.info(traceId+"查询重制课件任务列表，返回值：{}", response1);

        JSONObject jsonObject = JSON.parseObject(response1, JSONObject.class);
        //说明查询成功
        if (200==jsonObject.getInteger("code")){
            JSONObject jsonObject1=jsonObject.getJSONObject("data");
            JSONArray data = jsonObject1.getJSONArray("contents");
            if(data.isEmpty()){
                return;
            }

            Iterator<Object> iterator = data.iterator();
            ExecutorService executor = Executors.newFixedThreadPool(10);
            PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
            cm.setMaxTotal(20);
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(cm)
                    .build();

            while (iterator.hasNext()) {
                JSONObject object = (JSONObject) iterator.next();
                String sessionId = object.getString("sessionId");
                String downloadUrl = object.getString("url");
                executor.submit(() -> {
                    try {
                        String fileName = channelId+ "_" + sessionId + "_CZ"+".mp4";
                        HttpGet httpGet = new HttpGet(downloadUrl);
                        RequestConfig requestConfig = RequestConfig.custom()
                                .setConnectTimeout(10 * 1000) // 10 seconds connect timeout
                                .build();
                        httpGet.setConfig(requestConfig);

                        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                            HttpEntity entity = response.getEntity();
                            if (entity != null) {
                                Path filePath = Paths.get(savePath, fileName);
                                try (InputStream in = entity.getContent();
                                     FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                                    byte[] buffer = new byte[4096];
                                    int bytesRead;
                                    while ((bytesRead = in.read(buffer)) != -1) {
                                        out.write(buffer, 0, bytesRead);
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                UpdateWrapper<VideoInformation> queryWrapper = new UpdateWrapper<>();
                                queryWrapper.eq("sessionId", sessionId)
                                        .set("update_time", new Date())
                                        .set("cdn_url", "https://w-gdou.webtrncdn.com/livevod/cdn/cce/" + fileName);
                                int update = videoInformationMapper.update(null, queryWrapper);

                            }
                        }
                    } catch (IOException e) {
                        log.error(e+traceId+"下载视频发生错误" + channelId + " " + sessionId);
                    }
                });
            }
            log.info(traceId+"等待所有视频完成中");
            executor.shutdown(); // Shut down executor
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info(traceId+"下载视频完成");

        }


    }


    @PostMapping("/download")
    public void downloadFile(String savePath, Integer size) throws InterruptedException {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        String traceId = uuid.substring(0, 10);
        log.info(traceId + "下载视频开始，本次下载视频数量最大为" + size, "视频存入地址为:" + savePath);

        List<VideoInformation> videoInformations = videoInformationMapper.seletctVideoUrl(size);
        ExecutorService executor = Executors.newFixedThreadPool(size);
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(20);
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        for (VideoInformation videoInformation : videoInformations) {
            executor.submit(() -> {
                try {
                    String fileName = videoInformation.getChannelId() + "_" + videoInformation.getSessionId() + ".mp4";
                    HttpGet httpGet = new HttpGet(videoInformation.getUrl());
                    RequestConfig requestConfig = RequestConfig.custom()
                            .setConnectTimeout(10 * 1000) // 10 seconds connect timeout
                            .build();
                    httpGet.setConfig(requestConfig);

                    try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                        HttpEntity entity = response.getEntity();
                        if (entity != null) {
                            Path filePath = Paths.get(savePath, fileName);
                            try (InputStream in = entity.getContent();
                                 FileOutputStream out = new FileOutputStream(filePath.toFile())) {
                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = in.read(buffer)) != -1) {
                                    out.write(buffer, 0, bytesRead);
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            UpdateWrapper<VideoInformation> queryWrapper = new UpdateWrapper<>();
                            queryWrapper.eq("id", videoInformation.getId()).set("status", 1)
                                    .set("update_time", new Date())
                                    .set("cdn_url", "https://w-gdou.webtrncdn.com/livevod/cdn/cce/" + fileName);
                            int update = videoInformationMapper.update(null, queryWrapper);
                        }
                    }
                } catch (IOException e) {
                    log.error("下载视频发生错误" + videoInformation.getChannelId() + " " + videoInformation.getSessionId());
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown(); // Shut down executor
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info(traceId + "下载视频完成");

    }

    /**
     * 刪除直播间
     *
     * @return
     */
    @PostMapping("/edit/deleteChannel")
    public SaResult deleteChannels(String channelId) throws IOException, NoSuchAlgorithmException {
        if (StrUtil.isBlank(channelId)) {
            throw dataMissError();
        }
        return singleLivingService.deleteChannel(channelId);
    }

    @PostMapping("/edit/getChannelInformation")
    public SaResult getChannelInformation(Long sectionId) {
        if (Objects.isNull(sectionId)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        return singleLivingService.getChannelInformation(sectionId);
    }

    @PostMapping("/edit/createTeacherAndTutorUrl")
    public SaResult createTeacherAndTutorUrl(String channelId) {
        Object loginId = StpUtil.getLoginId();
        return singleLivingService.createTeacherAndTutorUrl(channelId, loginId.toString());
    }

    @PostMapping("/edit/getChannelBasicInformation")
    public SaResult getChannelInformation(String channelId) {
        if (Objects.isNull(channelId)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        return singleLivingService.getChannelBasicInformation(channelId);
    }

    //不能获取不存在的直播间会报错
    @PostMapping("/edit/getChannelStatus")
    public SaResult deleteChannels(@RequestBody ChannelInfoRequest channelInfoRequest) {
        if (channelInfoRequest.getChannelIds() == null || channelInfoRequest.getChannelIds().size() == 0) {
            throw dataMissError();
        }
        return singleLivingService.getChannelStatus(channelInfoRequest.getChannelIds());
    }

    //获取频道下或具体场次下的，观众观看数据详情，详情，详情
    @PostMapping("/edit/getChannelView")
    public SaResult getChannelView(@RequestBody ChannelViewRequest channelViewRequest) throws IOException, NoSuchAlgorithmException {
        //要么传getCurrentDay，要么传getStartTime和getEndTime。不能三者同时为空
        if (StrUtil.isBlank(channelViewRequest.getChannelId()) ||
                (StrUtil.isBlank(channelViewRequest.getCurrentDay()) && StrUtil.isBlank(channelViewRequest.getStartTime()) && StrUtil.isBlank(channelViewRequest.getEndTime()))) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        return singleLivingService.getChannelCardPush(channelViewRequest);
    }

    //导出考勤表接口
    @PostMapping("/edit/exportStudentSituation")
//    @SaCheckPermission("导出考勤表")
    public SaResult exportStudentSituation(@RequestParam Long sectionId) {
        // 校验参数
        if (Objects.isNull(sectionId)) {
            throw dataMissError();
        }
        boolean send = messageSender.sendExportStudentSituation(sectionId, (String) StpUtil.getLoginId(), 1);
        if (send) {
            return SaResult.ok("导出该堂课考勤表成功");
        }
        return SaResult.error("导出该堂课考勤表失败");
    }


    @PostMapping("/edit/exportAllStudentSituation")
    public SaResult exportAllStudentSituation(@RequestParam Long courseId) {
        // 校验参数
        if (Objects.isNull(courseId)) {
            throw dataMissError();
        }
        boolean send = messageSender.sendExportStudentSituation(courseId, (String) StpUtil.getLoginId(), 2);
        if (send) {
            return SaResult.ok("导出该门课所有考勤信息成功");
        }
        return SaResult.error("导出该门课所有考勤信息失败");
    }

    /**
     * 设置直播间是否回放
     *
     * @return
     */
    @PostMapping("/edit/setRecordSetting")
    public SaResult setWatchCondition(@RequestBody ChannelInfoRequest request) throws IOException, NoSuchAlgorithmException {
        if (StrUtil.isBlank(request.getChannelId()) || StrUtil.isBlank(request.getPlaybackEnabled())) {
            throw dataMissError();
        }
        return singleLivingService.setRecordSetting(request);
    }

    @PostMapping("/edit/getRecordSetting")
    public SaResult getWatchCondition(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            throw dataMissError();
        }
        return singleLivingService.getRecordSetting(channelId);
    }

    //返回教师单点登录链接
    // 要区分助教
    @PostMapping("/edit/getTeacherChannelUrl")
    public SaResult getTeacherChannelUrl(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            throw dataMissError();
        }

        List<String> roleList = StpUtil.getRoleList();
        if (roleList.contains("教师")) {
            String loginIdAsString = StpUtil.getLoginIdAsString();
            Long userIdByUsername = platformUserService.getUserIdByUsername(loginIdAsString);
            TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                    .eq(TeacherInformationPO::getTeacherUsername, loginIdAsString));
            if (teacherInformationPO.getTeacherType2().equals("主讲教师")) {
                return singleLivingService.getTeacherChannelUrl(channelId);
            } else {
                return singleLivingService.createTutorChannel(channelId, String.valueOf(userIdByUsername));
            }
        }

        return singleLivingService.getTeacherChannelUrl(channelId);
    }

    //返回学生登录链接
    @PostMapping("/edit/getStudentChannelUrl")
    public SaResult getStudentChannelUrl(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            throw dataMissError();
        }
        return singleLivingService.getStudentChannelUrl(channelId);
    }

    //返回助教单点登录链接
    @PostMapping("/edit/getTutorChannelUrl")
    public SaResult getTutorChannelUrl(String channelId) {
        Object userId = StpUtil.getLoginId();
        if (StrUtil.isBlank(channelId) || Objects.isNull(userId)) {
            throw dataMissError();
        }
        return singleLivingService.getTutorChannelUrl(channelId, userId.toString());
    }

    //创建助教并返回单点登录链接
    @PostMapping("/edit/createTutorChannel")
    public SaResult createTutorChannel(String channelId) {
        Object userId = StpUtil.getLoginId();
        if (StrUtil.isBlank(channelId) || Objects.isNull(userId)) {
            throw dataMissError();
        }
        return singleLivingService.createTutorChannel(channelId, userId.toString());
    }


    //创建助教并返回单点登录链接
    @PostMapping("/edit/createTutorChannel1")
    public SaResult createTutorChannel1(String channelId) {
        String loginIdAsString = StpUtil.getLoginIdAsString();
        Long userId = platformUserService.getUserIdByUsername(loginIdAsString);
        return singleLivingService.createTutorChannel(channelId, String.valueOf(userId));
    }

    @PostMapping("/edit/UpdateChannelNameAndImg")
    public SaResult UpdateChannelNameAndImg(@RequestBody ChannelInfoRequest request) {
        // 校验参数
        if (StrUtil.isBlank(request.getChannelId()) || StrUtil.isBlank(request.getImgUrl())) {
            throw dataMissError();
        }

        return singleLivingService.UpdateChannelNameAndImg(request);
    }


    @PostMapping("/edit/addChannelWhiteStudent")
    public SaResult addChannelWhiteStudent(@RequestBody ChannelInfoRequest request) {
        // 校验参数
        if (StrUtil.isBlank(request.getChannelId()) || request.getStudentWhiteList().isEmpty()) {
            throw dataMissError();
        }
//        return singleLivingService.addChannelWhiteStudent(request);
        return singleLivingService.addChannelWhiteStudentByFile(request);
    }

    @PostMapping("/edit/queryChannelWhiteStudent")
    public SaResult queryChannelWhiteStudent(@RequestBody ChannelInfoRequest request) {
        // 校验参数

        return singleLivingService.getChannelWhiteList(request);
    }

    @PostMapping("/edit/deleteChannelWhiteStudent")
    public SaResult deleteChannelWhiteStudent(@RequestBody ChannelInfoRequest request) {

        return singleLivingService.deleteChannelWhiteStudent(request);
    }

    //获取老师的总观看时长
    @PostMapping("/edit/getTotalTeachingTime")
    public SaResult getTotalTeachingTime(String courseId) {
        // 校验参数  默认返回1,页20条。
        if (StrUtil.isBlank(courseId)) {
            throw dataMissError();
        }

        return singleLivingService.getTotalTeachingTime(courseId);
    }

    //获取频道下的场次信息
    @PostMapping("/edit/getChannelSessionInfo")
    public SaResult getChannelSessionInfo(@RequestBody ChannelInfoRequest request) {
        // 校验参数  默认返回1,页20条。
        if (StrUtil.isBlank(request.getChannelId())) {
            throw dataMissError();
        }

        return singleLivingService.getChannelSessionInfo(request);
    }

    //获取观众的所有频道场次观看详情信息
    @GetMapping("/edit/getStudentViewlogDetail")
    public SaResult getStudentViewlogDetail(@RequestBody ChannelViewStudentRequest channelViewStudentRequest) throws IOException, NoSuchAlgorithmException {
        //观众观看记录可能很多，最好传入startDate和endDate
        if (StrUtil.isBlank(channelViewStudentRequest.getViewerId())) {
            throw dataMissError();
        }

        return singleLivingService.getStudentViewlogDetail(channelViewStudentRequest);
    }

    @PostMapping("/detail-tutor-batch-index")
    public SaResult getTutorBatchIndex(Long batchIndex) {
        if (Objects.isNull(batchIndex)) {
            throw dataMissError();
        }
        List<TutorAllInformation> tutorAllInformationList = singleLivingService.selectTutorInformationByBatchIndex(batchIndex);
        if (CollUtil.isEmpty(tutorAllInformationList)) {
            return SaResult.code(2000).setMsg("查询数据为空");
        }
        return SaResult.data(tutorAllInformationList);
    }
}

