package com.scnujxjy.backendpoint.videoStreamTest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.scnujxjy.backendpoint.dao.entity.video_stream.TutorInformation;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.TutorInformationMapper;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.vo.video_stream.StudentWhiteListVO;
import com.scnujxjy.backendpoint.service.SingleLivingService;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingServiceImpl;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.polyv.common.v1.exception.PloyvSdkException;
import net.polyv.live.v1.entity.web.auth.LiveChannelWhiteListRequest;
import net.polyv.live.v1.entity.web.auth.LiveChannelWhiteListResponse;
import net.polyv.live.v1.entity.web.auth.LiveCreateChannelWhiteListRequest;
import net.polyv.live.v1.entity.web.auth.LiveUploadWhiteListRequest;
import net.polyv.live.v1.service.web.impl.LiveWebAuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private VideoStreamUtils videoStreamUtils;

    @Resource
    private TutorInformationMapper tutorInformationMapper;

    @Resource
    private SingleLivingService singleLivingService;

    /**
     * 获取指定频道号的角色信息
     */
    @Test
    public void test1() {
        try {
            videoStreamUtils.getAccountsByChannelId("4252181");
        } catch (Exception e) {
            log.error(e.toString());
        }

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
        singleLivingService.createChannel(channelCreateRequestBO);
    }

    @Test
    public void test3() throws Exception {
        SingleLivingServiceImpl sing = new SingleLivingServiceImpl();
        String[] a = new String[]{"4360979", "4360976"};
        ChannelInfoRequest request = new ChannelInfoRequest();
        request.setPlaybackEnabled("Y");
        request.setChannelId("4368180");
        TutorInformation tutorInformation=new TutorInformation();
        tutorInformation.setChannelId("123456");
        tutorInformation.setTutorName("123");
        tutorInformation.setTutorUrl("456");
        tutorInformation.setUserId("789");
        tutorInformationMapper.insert(tutorInformation);
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


    /**
     * 創建頻道
//     */
    public static void main(String[] args) {
        // 定义要操作的文件路径和工作表名
        String templateFilePath = "bbb.xls";

        List<StudentWhiteListVO> StudentWhiteListVOS=new ArrayList<>();
        StudentWhiteListVO studentWhiteListVO1=new StudentWhiteListVO();
        studentWhiteListVO1.setCode("333");
        studentWhiteListVO1.setName("gagaga");
        StudentWhiteListVOS.add(studentWhiteListVO1);
        EasyExcel.write(templateFilePath, StudentWhiteListVO.class).sheet("Sheet1").doWrite(StudentWhiteListVOS);
        File file=new File(templateFilePath);
        file.delete();

//        excelWriter.fill(map, writeSheet);
        //关闭文件流
    }


}
