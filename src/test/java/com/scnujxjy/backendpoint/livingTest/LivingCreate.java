package com.scnujxjy.backendpoint.livingTest;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSON;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.service.video_stream.VideoStreamRecordService;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.polyv.common.v1.exception.PloyvSdkException;
import net.polyv.live.v1.entity.channel.operate.LiveBatchUpdateBarrageRequest;
import net.polyv.live.v1.entity.channel.operate.LiveDeleteChannelRequest;
import net.polyv.live.v1.entity.web.setting.LiveUploadImageRequest;
import net.polyv.live.v1.entity.web.setting.LiveUploadImageResponse;
import net.polyv.live.v1.service.channel.impl.LiveChannelOperateServiceImpl;
import net.polyv.live.v1.service.web.impl.LiveWebSettingServiceImpl;
import net.polyv.live.v2.entity.channel.operate.LiveChannelV2Request;
import net.polyv.live.v2.entity.channel.operate.LiveChannelV2Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class LivingCreate {

    public static void assertNotEmpty(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("The object is null!");
        }
    }

    @Autowired
    private VideoStreamUtils videoStreamUtils;


    /**
     * 简单创建直播间
     */
    @Test
    public void test1() {
        try {
            LiveChannelV2Request liveChannelRequest = new LiveChannelV2Request();
            liveChannelRequest.setNewScene("alone")
                    .setTemplate("alone")
                    .setName("Spring 知识精讲") //设置频道主题信息
                    .setChannelPasswd("666888");   //设置频道密码
            //调用SDK请求保利威服务器
            LiveChannelV2Response liveChannelResponse = new LiveChannelOperateServiceImpl().createChannelV2(
                    liveChannelRequest);
            assertNotEmpty(liveChannelResponse);
            //正常返回做B端正常的业务逻辑
            // to do something ......
            log.info("频道创建成功{}", JSON.toJSONString(liveChannelResponse));
            log.info("网页开播地址：https://live.polyv.net/web-start/login?channelId={}  , 登录密码： {}", liveChannelResponse.getChannelId(), liveChannelRequest.getChannelPasswd());
            log.info("网页观看地址：https://live.polyv.cn/watch/{} ", liveChannelResponse.getChannelId());
        } catch (
                PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            log.error(e.getMessage(), e);
            // 异常返回做B端异常的业务逻辑，记录log 或者 上报到ETL 或者回滚事务
        } catch (Exception e) {
            log.error("SDK调用异常", e);
        }
    }

    /**
     * 创建直播时可以设置 直播间名称、直播场景、模板、讲师登录密码
     * 直播延迟、封面图片、开始时间、结束时间、连麦人数限制
     */
    @Test
    public void test2() {
        try {
            // 指定资源文件路径，注意路径前面不要加上斜杠
            String imagePath = "pics/img.png";

            // 创建ClassPathResource对象
            Resource resource = new ClassPathResource(imagePath);
            String absolutePath = "//liveimages.videocc.net/uploaded/images/2023/07/gmtfdvvp20.png";
            log.info("图片绝对地址为 " + absolutePath);

            // 传入一个特定的时间字符串
            String dateString = "2023-07-16 18:00:00";
            String dateString1 = "2023-07-16 20:00:00";

            // 定义日期时间格式
            String pattern = "yyyy-MM-dd HH:mm:ss";

            // 创建SimpleDateFormat对象
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

            LiveChannelV2Request liveChannelRequest = new LiveChannelV2Request();
            liveChannelRequest.setNewScene("topclass")     // 直播场景 (topclass-大班课 、 double-双师课（该场景需开通） 、 train-企业培训 、 seminar-研讨会 、 alone-活动营销)
                    .setTemplate("ppt")   //直播模板 (ppt-三分屏(横屏) 、 portrait_ppt-三分屏(竖屏) 、 alone-纯视频(横屏) 、portrait_alone-纯视频(竖屏) 、 topclass-纯视频-极速(横屏) 、 portrait_topclass-纯视频-极速(竖屏) 、 seminar-研讨会、guide（导播，该场景需开通）)
                    .setName("Spring 知识精讲") //设置频道主题信息
                    .setChannelPasswd("666888")  //设置讲师频道密码
                    .setPureRtcEnabled("Y") //   直播延迟 Y无延时 N普通延迟
                    .setSplashImg(absolutePath)   // 封面图片地址，非保利威域名下的图片需调用上传频道所有装修图片素材上传
                    .setLinkMicLimit(0) // 连麦人数限制，最多16人
                    .setStartTime(dateFormat.parse(dateString))   // 开始时间，格式：yyyy-MM-dd HH:mm:ss【注：仅做直播前倒计时显示，不对讲师开播操作产生影响】
                    .setEndTime(dateFormat.parse(dateString1)) // 结束时间，格式：yyyy-MM-dd HH:mm:ss【注：仅做未开播时直播状态判断显示，不对讲师开播操作产生影响】
            ;

            //调用SDK请求保利威服务器
            LiveChannelV2Response liveChannelResponse = new LiveChannelOperateServiceImpl().createChannelV2(
                    liveChannelRequest);
            assertNotEmpty(liveChannelResponse);
            //正常返回做B端正常的业务逻辑
            // to do something ......
            log.info("频道创建成功{}", JSON.toJSONString(liveChannelResponse));
            log.info("网页开播地址：https://live.polyv.net/web-start/login?channelId={}  , 登录密码： {}", liveChannelResponse.getChannelId(), liveChannelRequest.getChannelPasswd());
            log.info("网页观看地址：https://live.polyv.cn/watch/{} ", liveChannelResponse.getChannelId());
        } catch (
                PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            log.error(e.getMessage(), e);
            // 异常返回做B端异常的业务逻辑，记录log 或者 上报到ETL 或者回滚事务
        } catch (Exception e) {
            log.error("SDK调用异常", e);
        }
    }

    /**
     * 根据频道号 删除直播间
     *
     * @throws Exception
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void testDeleteChannel() throws Exception, NoSuchAlgorithmException {
        LiveDeleteChannelRequest liveDeleteChannelRequest = new LiveDeleteChannelRequest();
        Boolean liveDeleteChannelResponse;
        try {
            //准备测试数据
            String channelId = "4113947";
            liveDeleteChannelRequest.setChannelId(channelId);
            liveDeleteChannelResponse = new LiveChannelOperateServiceImpl().deleteChannel(liveDeleteChannelRequest);
            assertNotEmpty(liveDeleteChannelResponse);
            if (liveDeleteChannelResponse) {
                //to do something ......
                log.debug("删除直播频道成功");
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
    }

    /**
     * 上传图片图片资源到保利威
     *
     * @throws Exception
     * @throws NoSuchAlgorithmException
     */
    @Test
    public void testUploadImage() throws Exception, NoSuchAlgorithmException {
        LiveUploadImageRequest liveUploadImageRequest = new LiveUploadImageRequest();
        LiveUploadImageResponse liveUploadImageResponse;
        try {
            String path = "D:\\MyProject\\feixueliPlatform\\BackEndpoint\\src\\main\\resources\\pics\\img.png";
            List<File> fileList = new ArrayList<File>();
            fileList.add(new File(path));
            liveUploadImageRequest.setType("coverImage")
                    .setFile(fileList);
            liveUploadImageResponse = new LiveWebSettingServiceImpl().uploadImage(
                    liveUploadImageRequest);
            assertNotEmpty(liveUploadImageResponse);
            //to do something ......
            log.info("测试上传图片资源成功,{}", liveUploadImageResponse);
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
     * 创建直播时可以设置 直播间名称、直播场景、模板、讲师登录密码
     * 直播延迟、封面图片、开始时间、结束时间、连麦人数限制
     * 除此以外 关闭弹幕功能
     */
    @Test
    public void test4() {
        try {
            // 指定资源文件路径，注意路径前面不要加上斜杠
            String imagePath = "pics/img.png";

            // 创建ClassPathResource对象
            Resource resource = new ClassPathResource(imagePath);
            String absolutePath = "//liveimages.videocc.net/uploaded/images/2023/07/gmtfdvvp20.png";
            log.info("图片绝对地址为 " + absolutePath);

            // 传入一个特定的时间字符串
            String dateString = "2023-07-16 18:00:00";
            String dateString1 = "2023-07-16 20:00:00";

            // 定义日期时间格式
            String pattern = "yyyy-MM-dd HH:mm:ss";

            // 创建SimpleDateFormat对象
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

            LiveChannelV2Request liveChannelRequest = new LiveChannelV2Request();
            liveChannelRequest.setNewScene("topclass")     // 直播场景 (topclass-大班课 、 double-双师课（该场景需开通） 、 train-企业培训 、 seminar-研讨会 、 alone-活动营销)
                    .setTemplate("ppt")   //直播模板 (ppt-三分屏(横屏) 、 portrait_ppt-三分屏(竖屏) 、 alone-纯视频(横屏) 、portrait_alone-纯视频(竖屏) 、 topclass-纯视频-极速(横屏) 、 portrait_topclass-纯视频-极速(竖屏) 、 seminar-研讨会、guide（导播，该场景需开通）)
                    .setName("Spring 知识精讲") //设置频道主题信息
                    .setChannelPasswd("666888")  //设置讲师频道密码
                    .setPureRtcEnabled("Y") //   直播延迟 Y无延时 N普通延迟
                    .setSplashImg(absolutePath)   // 封面图片地址，非保利威域名下的图片需调用上传频道所有装修图片素材上传
                    .setLinkMicLimit(0) // 连麦人数限制，最多16人
                    .setStartTime(dateFormat.parse(dateString))   // 开始时间，格式：yyyy-MM-dd HH:mm:ss【注：仅做直播前倒计时显示，不对讲师开播操作产生影响】
                    .setEndTime(dateFormat.parse(dateString1)) // 结束时间，格式：yyyy-MM-dd HH:mm:ss【注：仅做未开播时直播状态判断显示，不对讲师开播操作产生影响】
            ;

            //调用SDK请求保利威服务器
            LiveChannelV2Response liveChannelResponse = new LiveChannelOperateServiceImpl().createChannelV2(
                    liveChannelRequest);
            assertNotEmpty(liveChannelResponse);
            //正常返回做B端正常的业务逻辑
            // to do something ......
            log.info("频道创建成功{}", JSON.toJSONString(liveChannelResponse));
            log.info("网页开播地址：https://live.polyv.net/web-start/login?channelId={}  , 登录密码： {}", liveChannelResponse.getChannelId(), liveChannelRequest.getChannelPasswd());
            log.info("网页观看地址：https://live.polyv.cn/watch/{} ", liveChannelResponse.getChannelId());

            //准备测试数据
            LiveBatchUpdateBarrageRequest liveBatchUpdateBarrageRequest = new LiveBatchUpdateBarrageRequest();
            Boolean liveBatchUpdateBarrageResponse;
//            String channelIds = String.format("%s,%s", super.getAloneChannelId(), super.createChannel());
            liveBatchUpdateBarrageRequest.setChannelIds(liveChannelResponse.getChannelId()).setCloseBarrage("N").setShowBarrageInfoEnabled("N");
            liveBatchUpdateBarrageResponse = new LiveChannelOperateServiceImpl().batchUpdateBarrage(
                    liveBatchUpdateBarrageRequest);
            Assert.isTrue(liveBatchUpdateBarrageResponse);
            //to do something ......
            log.info("测试批量修改频道弹幕开关成功");
        } catch (
                PloyvSdkException e) {
            //参数校验不合格 或者 请求服务器端500错误，错误信息见PloyvSdkException.getMessage()
            log.error(e.getMessage(), e);
            // 异常返回做B端异常的业务逻辑，记录log 或者 上报到ETL 或者回滚事务
        } catch (Exception e) {
            log.error("SDK调用异常", e);
        }
    }

    @Test
    void testDeleteView() {
        Map<String, Object> map = videoStreamUtils.deleteView("123");
        log.info("response:{}", map);
    }


    @Autowired
    private VideoStreamRecordService videoStreamRecordsService;

    @Test
    void testSelect() {
        List<VideoStreamRecordPO> videoStreamRecordPOS = videoStreamRecordsService.
                getBaseMapper().selectBatchIds(ListUtil.of(1L, 2L, 3L, 4L));
        log.info("查询的数据是:{}",videoStreamRecordPOS);
    }


    // 查询频道的基本信息
    @Test
    public void test6(){
        try {
            ChannelResponseBO channelBasicInfo = videoStreamUtils.getChannelBasicInfo("4401417");
            if(channelBasicInfo != null && channelBasicInfo.getChannelId() != null){
                log.info("直播间存在" + channelBasicInfo.getChannelId());
            }
            log.info(channelBasicInfo.toString());
        }catch (Exception e){
            log.info("直播间不存在" + e);
        }
    }


}
