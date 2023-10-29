package com.scnujxjy.backendpoint.controller.video_stream;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseExtraInformationPO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseExtraInformationRO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordVO;
import com.scnujxjy.backendpoint.service.SingleLivingService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 直播记录管理
 *
 * @author leopard
 * @since 2023-08-21
 */
@RestController
@RequestMapping("/SingleLiving")
public class SingleLivingController {


    @Resource
    private SingleLivingService singleLivingService;

    /**
     * 批量添加直播间
     *
     * @param
     * @return 添加后的频道信息
     */
//    @PostMapping("/createChannel")
//    public SaResult createChannel(@RequestBody ChannelCreateRequestBO channelCreateRequestBO) {
//        if (CollUtil.isEmpty(videoStreamRecordROS)) {
//            throw dataMissError();
//        }
//        singleLivingService.createChannel()
//        if (CollUtil.isEmpty(generateVideoStream)) {
//            return SaResult.error().setMsg("创建失败，请联系管理员");
//        }
//        return SaResult.data(generateVideoStream);
//    }

    /**
     * 根据id查询直播间信息
     *
     * @param id 直播间id
     * @return 直播间信息
     */
//    @GetMapping("/detail")
//    public SaResult detailById(Long id) {
//        if (Objects.isNull(id)) {
//            throw dataMissError();
//        }
//        VideoStreamRecordVO videoStreamRecordVO = videoStreamRecordService.detailById(id);
//        if (Objects.isNull(videoStreamRecordVO)) {
//            throw dataNotFoundError();
//        }
//        return SaResult.data(videoStreamRecordVO);
//    }

    /**
     * 根据channelId查询直播间信息
     *
     * @param channelId 频道id
     * @return
     */
//    @GetMapping("/detail/channelId")
//    public SaResult detailByChannelId(String channelId) {
//        if (StrUtil.isBlank(channelId)) {
//            throw dataMissError();
//        }
//        VideoStreamRecordVO videoStreamRecordVO = videoStreamRecordService.detailByChannelId(channelId);
//        if (Objects.isNull(videoStreamRecordVO)) {
//            throw dataNotFoundError();
//        }
//        return SaResult.data(videoStreamRecordVO);
//    }

    /**
     * 开启或关闭直播间
     *
     * @param
     * @param
     * @return
     */
//    @GetMapping("/edit/status")
//    public SaResult closeByChannelId(String channelId, Integer type) {
//        if (StrUtil.isBlank(channelId) || Objects.isNull(type)) {
//            throw dataMissError();
//        }
//        Boolean ok = videoStreamRecordService.closeVideoStream(channelId, type);
//        return SaResult.data(ok);
//    }


    @PostMapping("/edit/deleteChannels")
    public SaResult deleteChannels(@RequestBody String[] channelIds) throws IOException, NoSuchAlgorithmException {
        if (channelIds.length<1) {
            throw dataMissError();
        }
        return singleLivingService.deleteChannel(channelIds);
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

    //返回教师单点登录链接
    @PostMapping("/edit/getTeacherChannelUrl")
    public SaResult getTeacherChannelUrl(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            throw dataMissError();
        }
        return singleLivingService.getTeacherChannelUrl(channelId);
    }

    //返回学生单点登录链接
    @PostMapping("/edit/getStudentChannelUrl")
    public SaResult getStudentChannelUrl(String channelId){
        if (StrUtil.isBlank(channelId)) {
            throw dataMissError();
        }
        return singleLivingService.getStudentChannelUrl(channelId);
    }

    //返回助教单点登录链接
    @PostMapping("/edit/getTutorChannelUrl")
    public  SaResult getTutorChannelUrl(String channelId){
        if (StrUtil.isBlank(channelId)) {
            throw dataMissError();
        }
        return singleLivingService.getTutorChannelUrl(channelId);
    }

    @PostMapping("/edit/UpdateChannelNameAndImg")
    public SaResult UpdateChannelNameAndImg(@RequestBody ChannelInfoRequest request) {
        // 校验参数
        if (StrUtil.isBlank(request.getChannelId()) || StrUtil.isBlank(request.getImgUrl())) {
            throw dataMissError();
        }

        return singleLivingService.UpdateChannelNameAndImg(request);
    }

}

