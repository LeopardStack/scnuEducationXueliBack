package com.scnujxjy.backendpoint.videoStreamTest;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelDetail;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelInfoResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.updateChannelInfo.AuthSetting;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.TutorInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.bo.video_stream.teacher_sso_information.PolyvRoleInformationResponseBO;
import com.scnujxjy.backendpoint.model.vo.video_stream.StudentWhiteListVO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamAllUrlInformationVO;
import com.scnujxjy.backendpoint.service.SingleLivingService;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingServiceImpl;
import com.scnujxjy.backendpoint.service.video_stream.VideoStreamRecordService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.polyv.HttpUtil;
import com.scnujxjy.backendpoint.util.polyv.LiveSignUtil;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.polyv.common.v1.exception.PloyvSdkException;
import net.polyv.live.v1.config.LiveGlobalConfig;
import net.polyv.live.v1.constant.LiveConstant;
import net.polyv.live.v1.entity.channel.operate.LiveChannelSettingRequest;
import net.polyv.live.v1.entity.web.auth.*;
import net.polyv.live.v1.service.web.impl.LiveWebAuthServiceImpl;
import net.polyv.live.v2.entity.channel.operate.account.LiveCreateAccountRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private VideoStreamUtils videoStreamUtils;

    @Resource
    private TutorInformationMapper tutorInformationMapper;

    @Resource
    private SingleLivingService singleLivingService;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private VideoStreamRecordService videoStreamRecordService;

    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;

    /**
     * 創建頻道
     * //
     */
    public static void main(String[] args) {
        // 定义要操作的文件路径和工作表名
        String templateFilePath = "bbb.xls";

        List<StudentWhiteListVO> StudentWhiteListVOS = new ArrayList<>();
        StudentWhiteListVO studentWhiteListVO1 = new StudentWhiteListVO();
        studentWhiteListVO1.setCode("333");
        studentWhiteListVO1.setName("gagaga");
        StudentWhiteListVOS.add(studentWhiteListVO1);
        EasyExcel.write(templateFilePath, StudentWhiteListVO.class).sheet("Sheet1").doWrite(StudentWhiteListVOS);
        File file = new File(templateFilePath);
        file.delete();

//        excelWriter.fill(map, writeSheet);
        //关闭文件流
    }

    @Test
    public void test2() throws IOException, NoSuchAlgorithmException {
        ChannelCreateRequestBO channelCreateRequestBO = new ChannelCreateRequestBO();
        channelCreateRequestBO.setLivingRoomTitle("直播间标题哈哈哈");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        channelCreateRequestBO.setStartDate(calendar.getTime());

        calendar.add(Calendar.HOUR_OF_DAY, 1);
        // 获取计算后的时间
        Date oneHourLater = calendar.getTime();

        channelCreateRequestBO.setEndDate(oneHourLater);
        channelCreateRequestBO.setPlayRollback(true);
        channelCreateRequestBO.setPureRtcEnabled("Y");
//        singleLivingService.createChannel(channelCreateRequestBO);
    }

    @Test
    public void test3() throws Exception {
        SingleLivingServiceImpl sing = new SingleLivingServiceImpl();
        ChannelInfoRequest request = new ChannelInfoRequest();
        request.setChannelId("4417168");
        sing.setWatchCondition("4440091");
//        sing.testConcurrence();
//        sing.viewlogDetailTest("430221198412180829");
//        request.setCurrentDay("2023-11-04");
//        request.setStartTime("2023-11-04 23:00");
//        request.setEndTime("2023-11-04 23:50");
//        sing.getChannelCardPush(request);

//        String[] a = new String[]{"4360979", "4360976"};
//        ChannelInfoRequest request = new ChannelInfoRequest();
//        request.setPlaybackEnabled("Y");
//        request.setChannelId("4368180");
//        TutorInformation tutorInformation=new TutorInformation();
//        tutorInformation.setChannelId("123456");
//        tutorInformation.setTutorName("123");
//        tutorInformation.setTutorUrl("456");
//        tutorInformation.setUserId("789");
//        tutorInformationMapper.insert(tutorInformation);
//        sing.getChannelDetail();
//        sing.testUploadWhiteList();
//        sing.createTutor("4389634", "汤姆");
//        sing.testUploadWhiteList("4368180");
//        sing.testMergeChannelVideoAsync();
//        sing.testMergeMp4Record("4368180");
//        sing.GetStudentChannelUrl("4360331");
//        sing.testGetChannelInfo("4368180");
//        sing.deleteChannel(a);
//        sing.setWatchCondition("4368180");
//        sing.testCreateChannelWhiteList(Integer.valueOf("4368180"));
    }

    @Test
    public void testGetCategoryList() throws IOException, NoSuchAlgorithmException {
        //公共参数,填写自己的实际
        String appId = LiveGlobalConfig.getAppId();
        String appSecret = LiveGlobalConfig.getAppSecret();
        String userId = "27b07c2dc9";
        String timestamp = String.valueOf(System.currentTimeMillis());
        //业务参数
        String url = "http://api.polyv.net/live/v3/user/category/list";

        //http 调用逻辑
        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appId", appId);
        requestMap.put("timestamp", timestamp);
        requestMap.put("sign", LiveSignUtil.getSign(requestMap, appSecret));
        String response = HttpUtil.postFormBody(url, requestMap);
        log.info("测试查询直播分类，返回值：{}", response);
        //do somethings

    }


    @Test
    public void testGetChannelWhiteList() throws Exception, NoSuchAlgorithmException {
        LiveChannelWhiteListRequest liveChannelWhiteListRequest = new LiveChannelWhiteListRequest();
        LiveChannelWhiteListResponse liveChannelWhiteListResponse;
        try {
            liveChannelWhiteListRequest.setChannelId("4368180")
                    .setRank(1)
                    .setKeyword(null)
                    .setPageSize(1);
            liveChannelWhiteListResponse = new LiveWebAuthServiceImpl().getChannelWhiteList(
                    liveChannelWhiteListRequest);
//            Assert.assertNotNull(liveChannelWhiteListResponse);
            if (liveChannelWhiteListResponse != null) {
                //to do something ......
                log.info("测试查询频道观看白名单列表成功,{}", JSON.toJSONString(liveChannelWhiteListResponse));
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

    @Test
    public void testCreateChannelWhiteList() throws Exception, NoSuchAlgorithmException {
        LiveCreateChannelWhiteListRequest liveCreateChannelWhiteListRequest = new LiveCreateChannelWhiteListRequest();
        liveCreateChannelWhiteListRequest.setName("");
        Boolean liveCreateChannelWhiteListResponse;
        try {
            liveCreateChannelWhiteListRequest.setRank(1)
                    .setChannelId("4368180")
                    .setCode("778899")
                    .setName("LT");
            liveCreateChannelWhiteListResponse = new LiveWebAuthServiceImpl().createChannelWhiteList(
                    liveCreateChannelWhiteListRequest);
//            Assert.assertNotNull(liveCreateChannelWhiteListResponse);
            if (liveCreateChannelWhiteListResponse) {
                //to do something ......
                log.info("测试添加单个白名单-全局白名单成功");
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

    @Test
    public void testUploadWhiteList() throws Exception, NoSuchAlgorithmException {
        LiveUploadWhiteListRequest liveUploadWhiteListRequest = new LiveUploadWhiteListRequest();
        Boolean liveUploadWhiteListResponse;
        try {
            //path设置为模板文件路径(已填写完数据)
            String path = "aaa.xls";
            liveUploadWhiteListRequest.setChannelId("4392355")
                    .setRank(1)
                    .setFile(new File(path));
            liveUploadWhiteListResponse = new LiveWebAuthServiceImpl().uploadWhiteList(liveUploadWhiteListRequest);

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

    @Test
    public void testPageViewlog() throws IOException, NoSuchAlgorithmException {
//        //公共参数,填写自己的实际参数
//        String appId = super.appId;
//        String appSecret = super.appSecret;
//        String userId = super.userId;
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        //业务参数
//        String url = String.format("http://api.polyv.net/live/v2/statistics/%s/viewlog","1965681");
//        String currentDay = "2021-1-20";
//        String page = "1";
//        String pageSize = "10";
//        String startTime = "1611072000000";
//        String endTime = "1611158400000";
//        String param1 = "1b448be323";
//        String param2 = "管理员";
//        String param3 = "live";
//        //http 调用逻辑
//        Map<String,String> requestMap = new HashMap<>();
//        requestMap.put("appId", appId);
//        requestMap.put("timestamp",timestamp);
//        requestMap.put("currentDay",currentDay);
//        requestMap.put("page",page);
//        requestMap.put("pageSize",pageSize);
//        requestMap.put("startTime",startTime);
//        requestMap.put("endTime",endTime);
//        requestMap.put("param1",param1);
//        requestMap.put("param2",param2);
//        requestMap.put("param3",param3);
//        requestMap.put("sign",LiveSignUtil.getSign(requestMap, appSecret));
//        String response = HttpUtil.get(url, requestMap);
//        log.info("测试分页查询频道直播观看详情数据，返回值：{}",response);
//        //do somethings

    }

    /**
     * 获取指定频道号的角色信息
     */
    @Test
    public void test1() {
        CourseSchedulePO courseSchedulePO = new CourseSchedulePO();
        courseSchedulePO.setCourseName("幼儿音乐教育");//230751 class
        courseSchedulePO.setGrade("2023");
        courseSchedulePO.setMajorName("学前教育");
        courseSchedulePO.setStudyForm("函授");
        courseSchedulePO.setLevel("专升本1");
        List<Map<String, String>> scheduleClassStudent = studentStatusMapper.getScheduleClassStudent(courseSchedulePO);
        System.out.println(scheduleClassStudent);
    }

    @Test
    void testSelectChannelAllRoleInformation() throws IOException, NoSuchAlgorithmException {
        List<PolyvRoleInformationResponseBO> roleInformationBOS = videoStreamUtils.selectChannelAllRoleInformation("4422426");
        roleInformationBOS.forEach(System.out::println);
    }

    @Test
    void testCreateChannelRole() throws IOException, NoSuchAlgorithmException {
        PolyvRoleInformationResponseBO roleInformation = videoStreamUtils.createRoleInformation(LiveCreateAccountRequest.builder()
                .role("Guest")
                .channelId("4422426")
                .nickName("测试嘉宾")
                .build());
        log.info("新增角色信息：{}", roleInformation);
    }

    @Test
    void testWatch() throws IOException, NoSuchAlgorithmException {
        String link = videoStreamUtils.getIndependentAuthorizationLink("4422426", "440782200111216519", "测试", null);
        log.info("生成的链接为：{}", link);
    }

    @Test
    void testGetChannelInfo() throws IOException, NoSuchAlgorithmException {
        ChannelInfoResponse channelInfoResponse = videoStreamUtils.getChannelInfo("4422426");
        log.info("频道信息: {}", channelInfoResponse);
        ChannelDetail channelDetail = channelInfoResponse.getData();
        log.info("频道详细信息: {}", channelDetail);
        List<AuthSetting> authSettings = channelDetail.getAuthSettings();
        for (AuthSetting authSetting : authSettings) {
            log.info("频道其他设置: {}", authSetting);
        }
    }

    @Test
    void testChannelWatchInfo() throws IOException, NoSuchAlgorithmException {
        List<AuthSetting> channelWatchCondition = videoStreamUtils.getChannelWatchCondition("4422426");
        for (AuthSetting authSetting : channelWatchCondition) {
            log.info("{}", authSetting);
        }
    }

    @Test
    void testCreateChannelWatchInfo() throws IOException, NoSuchAlgorithmException {
        LiveChannelSettingRequest request = new LiveChannelSettingRequest();
        request.setChannelId("4422426");
        ArrayList<LiveChannelSettingRequest.AuthSetting> authSettings = new ArrayList<>();
        LiveChannelSettingRequest.AuthSetting authSetting = new LiveChannelSettingRequest.AuthSetting();
        authSetting.setRank(2)
                .setEnabled("Y")
                .setAuthType("direct")
                .setDirectKey(RandomUtil.randomString(8));
        authSettings.add(authSetting);
        request.setAuthSettings(authSettings);
        Boolean isSuccess = videoStreamUtils.createWatchCondition(request);
        log.info("{}", isSuccess);
        List<AuthSetting> channelWatchCondition = videoStreamUtils.getChannelWatchCondition("4422426");
        for (AuthSetting setting : channelWatchCondition) {
            log.info("频道其他设置:{}", setting);
        }
    }

    @Test
    void testAllChannel() throws IOException, NoSuchAlgorithmException {
        VideoStreamAllUrlInformationVO videoStreamAllUrlInformationVO = videoStreamRecordService.selectChannelAllUrl("4422426");
        log.info("频道链接信息：{}", videoStreamAllUrlInformationVO);
    }

    @Test
    void testSetWatching(){
       List<String> channelIds = videoStreamRecordsMapper.selectDistinctChannelIds();

        List<String> success=new ArrayList<>();
        List<String> fail=new ArrayList<>();
        for (String channelId: channelIds) {
            LiveUpdateChannelAuthRequest liveUpdateChannelAuthRequest = new LiveUpdateChannelAuthRequest();
            Boolean liveUpdateChannelAuthResponse;
            LiveChannelSettingRequest.AuthSetting authSetting2 = new LiveChannelSettingRequest.AuthSetting().setAuthType(
                    LiveConstant.AuthType.DIRECT.getDesc())
                    .setRank(2)
                    .setEnabled("Y")
                    .setDirectKey(RandomUtil.randomString(8));

            List<LiveChannelSettingRequest.AuthSetting> authSettings = new ArrayList<>();
            authSettings.add(authSetting2);
            liveUpdateChannelAuthRequest.setChannelId(channelId)
                    .setAuthSettings(authSettings);

            try {
                liveUpdateChannelAuthResponse = new LiveWebAuthServiceImpl().updateChannelAuth(
                        liveUpdateChannelAuthRequest);
                //如果返回结果不为空并且为true，说明修改成功
                if (liveUpdateChannelAuthResponse != null && liveUpdateChannelAuthResponse) {
                    log.info(channelId + "更新次要条件成功");
                    success.add(channelId);
                }
            }catch (Exception e ){
                e.printStackTrace();
                fail.add(channelId);
            }

        }

        System.out.println("成功更新的频道号位"+success);
        System.out.println("更新失败的频道号为"+fail);

    
    }

}
