package com.scnujxjy.backendpoint.controller.video_stream;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.dao.entity.video_stream.TutorAllInformation;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelViewRequest;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelViewStudentRequest;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

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

    //不能获取不存在的直播间会报错
    @PostMapping("/edit/getChannelStatus")
    public SaResult deleteChannels(@RequestBody ChannelInfoRequest channelInfoRequest) {
        if (channelInfoRequest.getChannelIds()==null ||channelInfoRequest.getChannelIds().size()==0) {
            throw dataMissError();
        }
        return singleLivingService.getChannelStatus(channelInfoRequest.getChannelIds());
    }

    //获取频道下或具体场次下的，观众观看数据详情，详情，详情
    @GetMapping("/edit/getChannelView")
    public SaResult getChannelView(@RequestBody ChannelViewRequest channelViewRequest) throws IOException, NoSuchAlgorithmException {
        //要么传getCurrentDay，要么传getStartTime和getEndTime。不能三者同时为空
        if (StrUtil.isBlank(channelViewRequest.getChannelId()) ||
       (StrUtil.isBlank(channelViewRequest.getCurrentDay()) && StrUtil.isBlank(channelViewRequest.getStartTime()) && StrUtil.isBlank(channelViewRequest.getEndTime()))) {
            throw dataMissError();
        }
        return singleLivingService.getChannelCardPush(channelViewRequest);
    }

    //导出考勤表接口
    @PostMapping("/edit/exportStudentSituation")
    public SaResult exportStudentSituation(@RequestParam Long sectionId, HttpServletResponse response) {
        // 校验参数
        if (Objects.isNull(sectionId)) {
            throw dataMissError();
        }

        return singleLivingService.exportStudentSituation(sectionId,response);
    }



    @PostMapping("/edit/exportAllStudentSituation")
    public void exportAllStudentSituation(@RequestParam String[] courseId, HttpServletResponse response) {
        // 校验参数
        if (courseId.length==0) {
            throw dataMissError();
        }
        singleLivingService.exportAllCourseSituation(courseId,response);
        return;
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
    public  SaResult getTutorChannelUrl(String channelId){
        Object userId = StpUtil.getLoginId();
        if (StrUtil.isBlank(channelId) || Objects.isNull(userId)) {
            throw dataMissError();
        }
        return singleLivingService.getTutorChannelUrl(channelId,userId.toString());
    }

    //创建助教并返回单点登录链接
    @PostMapping("/edit/createTutorChannel")
    public  SaResult createTutorChannel(String channelId){
        Object userId = StpUtil.getLoginId();
        if (StrUtil.isBlank(channelId) || Objects.isNull(userId)) {
            throw dataMissError();
        }
        return singleLivingService.createTutorChannel(channelId,userId.toString());
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
        if (StrUtil.isBlank(request.getChannelId())) {
            throw dataMissError();
        }

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

