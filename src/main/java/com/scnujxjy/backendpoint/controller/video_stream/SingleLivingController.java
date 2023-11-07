package com.scnujxjy.backendpoint.controller.video_stream;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ViewLogFirstResponse;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.service.SingleLivingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;

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
    @Resource
    private CourseScheduleMapper courseScheduleMapper;

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

    @PostMapping("/edit/getChannelView")
    public SaResult getChannelView(@RequestBody ChannelInfoRequest channelInfoRequest) throws IOException, NoSuchAlgorithmException {
        if (StrUtil.isBlank(channelInfoRequest.getChannelId()) || StrUtil.isBlank(channelInfoRequest.getCurrentDay()) ||
                StrUtil.isBlank(channelInfoRequest.getStartTime()) || StrUtil.isBlank(channelInfoRequest.getEndTime())) {
            throw dataMissError();
        }
        return singleLivingService.getChannelCardPush(channelInfoRequest);
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

    //返回学生登录链接
    @PostMapping("/edit/getStudentChannelUrl")
    public SaResult getStudentChannelUrl(String channelId){
        if (StrUtil.isBlank(channelId)) {
            throw dataMissError();
        }
        return singleLivingService.getStudentChannelUrl(channelId);
    }

    //返回助教单点登录链接
    @PostMapping("/edit/getTutorChannelUrl")
    public  SaResult getTutorChannelUrl(String channelId,String userId){
        if (StrUtil.isBlank(channelId) || StrUtil.isBlank(userId)) {
            throw dataMissError();
        }
        return singleLivingService.getTutorChannelUrl(channelId,userId);
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
        if (StrUtil.isBlank(request.getChannelId()) || StrUtil.isBlank(request.getCode()) || StrUtil.isBlank(request.getChannelName())) {
            throw dataMissError();
        }

        return singleLivingService.addChannelWhiteStudent(request);
    }
}

