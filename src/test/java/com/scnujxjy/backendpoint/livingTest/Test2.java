package com.scnujxjy.backendpoint.livingTest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.LiveResourcesPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.SectionsPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ChannelResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.LiveRequestBody;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoInformation;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelInfoResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ChannelResponseData;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelInfoData;
import com.scnujxjy.backendpoint.dao.entity.video_stream.playback.ChannelPlayBackInfoResponse;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.LiveResourceMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.SectionsMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.util.polyv.HttpUtil;
import com.scnujxjy.backendpoint.util.polyv.LiveSignUtil;
import com.scnujxjy.backendpoint.util.video_stream.SingleLivingSetting;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.polyv.live.v1.constant.LiveConstant;
import net.polyv.live.v1.entity.channel.operate.LiveChannelSettingRequest;
import net.polyv.live.v1.entity.channel.operate.LiveSonChannelInfoListResponse;
import net.polyv.live.v1.entity.web.auth.LiveCreateChannelWhiteListRequest;
import net.polyv.live.v1.entity.web.auth.LiveUpdateChannelAuthRequest;
import net.polyv.live.v1.service.web.impl.LiveWebAuthServiceImpl;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class Test2 {
    @Resource
    private VideoStreamUtils videoStreamUtils;

    @Resource
    private SingleLivingSetting singleLivingSetting;

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;

    @Resource
    private LiveResourceMapper liveResourceMapper;

    @Resource
    private VideoInformationMapper videoInformationMapper;

    @Resource
    private SectionsMapper sectionsMapper;

    /**
     * 获取指定频道 id 下的所有角色信息
     */
    @Test
    public void test1() {
        String channelID = "4247418";
        try {
            ChannelResponse accountsByChannelId = videoStreamUtils.getAccountsByChannelId(channelID);
            log.info(channelID + " 下的角色信息为 \n" + accountsByChannelId.toString());
        } catch (Exception e) {
            log.error("获取频道(" + channelID + ") 下的角色信息失败 " + e.toString());
        }
    }

    /**
     * 创建直播间
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void testBasicCreateV4() throws IOException, NoSuchAlgorithmException {

        LiveRequestBody liveRequestBody = new LiveRequestBody();
        liveRequestBody.setName("有延迟学历教育第一次直播测试");
        liveRequestBody.setNewScene("topclass");
        liveRequestBody.setTemplate("ppt");

        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);

        // 将时间戳转换为Date对象
        Date currentDate = Date.from(Instant.ofEpochMilli(beijingTimestamp + 60 * 60 * 1000L));

        // 计算2小时后的时间
        Date twoHoursLaterDate = Date.from(Instant.ofEpochMilli(beijingTimestamp + 2 * 60 * 60 * 1000L));
        // 密码都不设置 让 保利威自行设置
        liveRequestBody.setStartTime(currentDate.getTime());
        liveRequestBody.setEndTime(twoHoursLaterDate.getTime());
        liveRequestBody.setType("normal");


        ApiResponse response = videoStreamUtils.createChannel(liveRequestBody);

        log.info("测试创建单个频道，返回值：{}", response);

        //do other test related things if needed
    }

    /**
     * 默认创建的直播间是没有开启回放的
     */
    @Test
    public void test3() {
        Map<String, Object> stringObjectMap = videoStreamUtils.deleteView("4276203");
        log.info("删除直播间 " + stringObjectMap);
    }

    /**
     * 查询指定频道的详细信息
     */
    @Test
    public void test4() {
        String channelID = "4417539";
        String channelID1 = "4400659";
        try {
            ChannelInfoResponse channelInfoByChannelId = videoStreamUtils.getChannelInfo(channelID);
            ChannelInfoResponse channelInfoByChannelId1 = videoStreamUtils.getChannelInfo(channelID1);
            log.info("频道信息包括 " + channelInfoByChannelId);
            log.info("频道信息包括1 " + channelInfoByChannelId1);
            if (channelInfoByChannelId1.getCode().equals(200) && channelInfoByChannelId1.getSuccess()) {
                log.info("创建频道成功");
            }
        } catch (Exception e) {
            log.error("获取 (" + channelID + ") 的频道信息失败 " + e.toString());
        }
    }

    /**
     * 查询指定频道的回放信息
     */
    @Test
    public void test5() {
        String channelID = "4276449";
        try {
            ChannelPlayBackInfoResponse channelPlayBackInfoResponse = videoStreamUtils.getPlaybackSettingByChannelId(channelID);
            log.info("频道回放信息包括 " + channelPlayBackInfoResponse);
        } catch (Exception e) {
            log.error("获取 (" + channelID + ") 的频道信息失败 " + e.toString());
        }
    }

    /**
     * 获取讲师的单点登录链接
     */
    @Test
    public void testTeacherLogin() {
        String channelID = "4400659";
        try {
            String s = videoStreamUtils.generateTeacherSSOLink(channelID);
            ChannelInfoResponse channelInfoByChannelId = videoStreamUtils.getChannelInfo(channelID);
            log.info("频道信息" + channelInfoByChannelId);
            log.info("单点登录链接" + s);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * 获取指定频道下的角色信息
     */
    @Test
    public void testGetChannelRoleInfo() {
        String channelID = "4401416";
        try {
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
            log.info("角色信息" + roleInfo);
        } catch (Exception e) {
            log.error(e.toString());
        }
    }

    /**
     * 修改指定频道的回放信息
     * <p>
     * Y list playback 可以设置回放为 回放列表
     * Y list vod 可以设置回放为 点播列表，不要设置 vid
     * Y single 则可以选择 record、playback 和 vod
     */
    @Test
    public void test6() {
        String channelID = "4416383";
        ChannelInfoData channelInfoData = new ChannelInfoData();
        channelInfoData.setChannelId(channelID);
        channelInfoData.setGlobalSettingEnabled("N");
        channelInfoData.setPlaybackEnabled("Y");
        channelInfoData.setType("single");
//        channelInfoData.setVideoId("27b07c2dc999caefedb9d3e4fb685471_2");
        channelInfoData.setOrigin("record");

        try {
            ChannelResponse channelPlayBackInfoResponse =
                    videoStreamUtils.setRecordSetting(channelInfoData);
            log.info("频道回放信息包括 " + channelPlayBackInfoResponse);
            if (channelPlayBackInfoResponse.getCode().equals(200)) {
                log.info("设置成功");
            } else {
                log.info("设置失败");
            }
        } catch (Exception e) {
            log.error("设置 (" + channelID + ") 的频道回放信息失败 " + e.toString());
        }
    }

    @Test
    public void testLivingRootCreate() throws IOException, NoSuchAlgorithmException {
        // 获取北京时间的时间戳
        long beijingTimestamp = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        String timestamp = String.valueOf(beijingTimestamp);
        // 将时间戳转换为Date对象
        Date currentDate = Date.from(Instant.ofEpochMilli(beijingTimestamp + 60 * 60 * 1000L));

        // 计算2小时后的时间
        Date twoHoursLaterDate = Date.from(Instant.ofEpochMilli(beijingTimestamp + 2 * 60 * 60 * 1000L));
        try {
            ApiResponse channel = singleLivingSetting.createChannel("测试简单直播间", currentDate, twoHoursLaterDate, true,
                    "N");
            log.info(channel.toString());
            if (channel.getCode().equals(200)) {
                ChannelResponseData channelResponseData = channel.getData();
                log.info("创建的直播间频道 " + channelResponseData.getChannelId() + " 频道密码 " + channelResponseData.getChannelPasswd());
            }
        } catch (Exception e) {
            log.info("创建简单直播间失败 " + e.toString());
        }
    }

    @Test
    public void testDeleteLivingRoom() {
        Map<String, Object> stringObjectMap = videoStreamUtils.deleteView("4327258");
        if (stringObjectMap.containsKey("code") && stringObjectMap.get("code").equals(200)) {
            log.info("删除直播间成功");
        } else {
            log.info("删除直播间失败");
        }
        log.info(stringObjectMap.toString());
    }

    @Test
    public void TestWhiteList() {
        String channelId = "4327520";

        LiveUpdateChannelAuthRequest liveUpdateChannelAuthRequest = new LiveUpdateChannelAuthRequest();
        LiveCreateChannelWhiteListRequest liveCreateChannelWhiteListRequest = new LiveCreateChannelWhiteListRequest();
        Boolean liveUpdateChannelAuthResponse;
        try {
            liveCreateChannelWhiteListRequest.setCode("454467");
            liveCreateChannelWhiteListRequest.setName("阿龙");
            LiveChannelSettingRequest.AuthSetting authSetting = new LiveChannelSettingRequest.AuthSetting().setAuthType(
                    LiveConstant.AuthType.CODE.getDesc())
                    .setRank(1)
                    .setEnabled("Y")
                    .setAuthType("phone")
                    .setAuthTips("请输入你的身份证号码")
                    .setQcodeImg("https://live.polyv.net/static/images/live-header-logo.png");
            List<LiveChannelSettingRequest.AuthSetting> authSettings =
                    new ArrayList<LiveChannelSettingRequest.AuthSetting>();
            authSettings.add(authSetting);

            liveUpdateChannelAuthRequest.setChannelId(channelId)
                    .setAuthSettings(authSettings);
            liveUpdateChannelAuthResponse = new LiveWebAuthServiceImpl().updateChannelAuth(
                    liveUpdateChannelAuthRequest);
            if (liveUpdateChannelAuthResponse) {
                log.info("测试设置观看条件成功");
            } else {
                log.info("测试设置观看条件失败");
            }
            log.info(liveUpdateChannelAuthResponse.toString());
        } catch (Exception e) {
            log.error(e.toString());
        }
    }


    /**
     * 分析 videoStreamRecord 表里 有哪些直播间被删除了
     */
    @Test
    public void test11() {

        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(null);
        List<VideoStreamRecordPO> videoStreamRecordPOS = new ArrayList<>();
        for (CourseSchedulePO courseSchedulePO : courseSchedulePOS) {
            String onlinePlatform = courseSchedulePO.getOnlinePlatform();
            try {
                VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(new LambdaQueryWrapper<VideoStreamRecordPO>()
                        .eq(VideoStreamRecordPO::getChannelId, Long.parseLong(onlinePlatform)));
                videoStreamRecordPOS.add(videoStreamRecordPO);
            } catch (Exception e) {
                log.error(e.toString());
            }

        }


        List<String> uniqueChannelIds = videoStreamRecordPOS.stream()
                .map(VideoStreamRecordPO::getChannelId) // 将 VideoStreamRecordPO 映射到它的 channelId
                .distinct() // 去除重复的 channelId
                .collect(Collectors.toList()); // 收集到一个新的 List 中

        for (String channelID : uniqueChannelIds) {
            try {
                ChannelInfoResponse channelInfoByChannelId = videoStreamUtils.getChannelInfo(channelID);
                log.info("频道信息包括 " + channelInfoByChannelId);
                if (channelInfoByChannelId.getCode().equals(200) && channelInfoByChannelId.getSuccess()) {

                } else {
                    log.info("该频道不存在 " + channelID);
                }
            } catch (Exception e) {
                log.error("获取 (" + channelID + ") 的频道信息失败 " + e.toString());
            }
        }

    }

    @Test
    public void putAllVideoInformation() throws IOException, NoSuchAlgorithmException, ParseException {

        //先获取所有频道id
        List<String> channelIds = liveResourceMapper.selectAllChannelId();
        List<String> sessionIds = videoInformationMapper.selectAllSession();

        for (String channelId : channelIds) {

            LiveResourcesPO liveResourcesPO = liveResourceMapper.queryCourseId(channelId);
            Long courseId = liveResourcesPO.getCourseId();

            String timestamp = String.valueOf(System.currentTimeMillis());
            String url = "http://api.polyv.net/live/v2/channels/%s/recordFiles";
            String startDate = "2024-03-01";
            String endDate = "2024-07-31";
            url = String.format(url, channelId);

            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("appId", "gj95rpxjhf");
            requestMap.put("timestamp", timestamp);
            requestMap.put("startDate", startDate);
            requestMap.put("endDate", endDate);
            requestMap.put("userId", "27b07c2dc9");
            requestMap.put("sign", LiveSignUtil.getSign(requestMap, "a642eb8a7e8f425995d9aead5bdd83ea"));
            String response = HttpUtil.get(url, requestMap);
            log.info("查询频道录制视频信息，返回值：{}", response);
            JSONObject jsonObject = JSON.parseObject(response, JSONObject.class);

            if (200 == jsonObject.getInteger("code")) {
                JSONArray data = jsonObject.getJSONArray("data");
                Iterator<Object> iterator = data.iterator();
                while (iterator.hasNext()) {
                    JSONObject object = (JSONObject) iterator.next();
                    if ("complete".equals(object.getString("recordFileType"))) {
                        if (sessionIds.contains(object.getString("channelSessionId"))) {
                            continue;
                        }
                        String startTime = object.getString("startTime");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = sdf.parse(startTime);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.HOUR_OF_DAY, -2);
                        Date previousHour = calendar.getTime();
                        calendar.add(Calendar.HOUR_OF_DAY, 4); // 加4小时，因为前面减了2小时，这里需要补回来
                        Date nextHour = calendar.getTime();
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String previousHourStr = outputFormat.format(previousHour);
                        String nextHourStr = outputFormat.format(nextHour);

                        SectionsPO sectionsPOS = sectionsMapper.selectSectionsByTime(previousHourStr, nextHourStr,courseId);

                        VideoInformation videoInformation = new VideoInformation();
                        if (sectionsPOS!=null){
                            videoInformation.setSectionId(sectionsPOS.getId());
                        }

                        //只有完整回放才需要下载哈
                        videoInformation.setChannelId(channelId);
                        videoInformation.setSessionId(object.getString("channelSessionId"));
                        videoInformation.setStatus(0);
                        videoInformation.setCreateTime(new Date());
                        videoInformation.setUrl(object.getString("url"));
                        videoInformationMapper.insert(videoInformation);
                    }

                }

            }
        }


    }


    @Test
    public void updateAllVideoInformation() throws IOException, NoSuchAlgorithmException, ParseException {

        List<String> channelIds = liveResourceMapper.selectAllChannelId();
        int count=0;
        for (String channelId : channelIds) {

            LiveResourcesPO liveResourcesPO = liveResourceMapper.queryCourseId(channelId);
            Long courseId = liveResourcesPO.getCourseId();

            String timestamp = String.valueOf(System.currentTimeMillis());
            String url = "http://api.polyv.net/live/v2/channels/%s/recordFiles";
            String startDate = "2024-03-01";
            String endDate = "2024-07-31";
            url = String.format(url, channelId);

            Map<String, String> requestMap = new HashMap<>();
            requestMap.put("appId", "gj95rpxjhf");
            requestMap.put("timestamp", timestamp);
            requestMap.put("startDate", startDate);
            requestMap.put("endDate", endDate);
            requestMap.put("userId", "27b07c2dc9");
            requestMap.put("sign", LiveSignUtil.getSign(requestMap, "a642eb8a7e8f425995d9aead5bdd83ea"));
            String response = HttpUtil.get(url, requestMap);
            log.info("查询频道录制视频信息，返回值：{}", response);
            JSONObject jsonObject = JSON.parseObject(response, JSONObject.class);

            if (200 == jsonObject.getInteger("code")) {
                JSONArray data = jsonObject.getJSONArray("data");
                Iterator<Object> iterator = data.iterator();
                while (iterator.hasNext()) {
                    JSONObject object = (JSONObject) iterator.next();
                    if ("complete".equals(object.getString("recordFileType"))) {

                        String startTime = object.getString("startTime");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                        Date date = sdf.parse(startTime);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.add(Calendar.HOUR_OF_DAY, -2);
                        Date previousHour = calendar.getTime();
                        calendar.add(Calendar.HOUR_OF_DAY, 4); // 加2小时，因为前面减了1小时，这里需要补回来
                        Date nextHour = calendar.getTime();
                        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String previousHourStr = outputFormat.format(previousHour);
                        String nextHourStr = outputFormat.format(nextHour);

                        //找出该堂课的时间节点
                        SectionsPO sectionsPOS = sectionsMapper.selectSectionsByTime(previousHourStr, nextHourStr,courseId);

                        if (sectionsPOS!=null) {
                            UpdateWrapper<VideoInformation> updateWrapper = new UpdateWrapper<>();
                            updateWrapper.set("section_id", sectionsPOS.getId())
                                    .set("update_time",new Date())
                                    .eq("session_id", object.getString("channelSessionId"));
                            int update = videoInformationMapper.update(null, updateWrapper);
                            if (update>0) {
                                count++;
                            }
                        }
//                        videoInformationMapper.update()
//                        //只有完整回放才需要下载哈
//                        videoInformation.setChannelId(channelId);
//                        videoInformation.setSessionId(object.getString("channelSessionId"));
//                        videoInformation.setStatus(0);
//                        videoInformation.setCreateTime(new Date());
//                        videoInformation.setUrl(object.getString("url"));
//                        videoInformationMapper.insert(videoInformation);
                    }

                }

            }

            log.info("更新数量为"+count);
        }


    }



    @Test
    public void addRecordTask() throws Exception {
//
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        String url = "http://api.polyv.net/live/v2/channels/%s/recordFiles";
//        String startDate = "2024-03-01";
//        String endDate = "2024-07-31";
//        url = String.format(url, "4766896");
//
//        Map<String, String> requestMap = new HashMap<>();
//        requestMap.put("appId", "gj95rpxjhf");
//        requestMap.put("timestamp", timestamp);
//        requestMap.put("startDate", startDate);
//        requestMap.put("endDate", endDate);
//        requestMap.put("userId", "27b07c2dc9");
//        requestMap.put("sign", LiveSignUtil.getSign(requestMap, "a642eb8a7e8f425995d9aead5bdd83ea"));
//        String response = HttpUtil.get(url, requestMap);
//        log.info("查询频道录制视频信息，返回值：{}", response);
//        JSONObject jsonObject = JSON.parseObject(response, JSONObject.class);

//        String appId = "gj95rpxjhf";
//        String appSecret ="a642eb8a7e8f425995d9aead5bdd83ea";
//        String userId = "27b07c2dc9";
//        String timestamp = String.valueOf(System.currentTimeMillis());
//
//        //http 调用逻辑
//        String url = "http://api.polyv.net/live/v3/channel/pptRecord/addRecordTask";
//        String channelId = "4766896";
//        String videoId = "ca155d63f8b7b63312e0b76761645cb6";
//
//        //http 调用逻辑
//        Map<String, String> requestMap = new HashMap<>();
//        requestMap.put("appId", appId);
//        requestMap.put("timestamp", timestamp);
//        requestMap.put("channelId", channelId);
//        requestMap.put("videoId", videoId);
//        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));
//        String response = HttpUtil.postFormBody(url, requestMap);
//        log.info("测试创建重制课件任务，返回值：{}", response);
        //fileId -> d6a3f2fa2c514e1058a3b70189153611 fileId -> ca155d63f8b7b63312e0b76761645cb6

        String url = "https://api.polyv.net/live/v3/user/playback/list";
        String categoryIds="340019,345134";
        String page = "2";
        String pageSize="2";
        String order="timeDesc";
        String listType="playback";

        //http 调用逻辑
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", "gj95rpxjhf");
        requestMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
        requestMap.put("categoryIds", categoryIds);
        requestMap.put("page", page);
        requestMap.put("pageSize", pageSize);
        requestMap.put("order", order);
        requestMap.put("listType", listType);
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, "a642eb8a7e8f425995d9aead5bdd83ea"));
        String response = HttpUtil.get(url, requestMap);
        log.info("测试查询所有频道的回放视频，返回值：{}", response);
    }

    @Test
    public void GetChannelRecordInfo() throws InterruptedException {

//        String uuid = UUID.randomUUID().toString();
//        uuid = uuid.replace("-", "");
//        String traceId = uuid.substring(0, 10);
//        log.info(traceId + "下载视频开始，本次下载视频数量最大为" + size, "视频存入地址为:" + savePath);

        List<VideoInformation> videoInformations = videoInformationMapper.seletctVideoUrl(10);
        ExecutorService executor = Executors.newFixedThreadPool(10);
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
                            Path filePath = Paths.get("/home/video", fileName);
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
                            queryWrapper.eq("id", videoInformation.getId()).set("status", 1).set("update_time", new Date());
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
        log.info("下载视频完成");


    }
}
