package com.scnujxjy.backendpoint.controller.video_stream;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.TutorAllInformation;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelViewRequest;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelViewStudentRequest;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingService;
import com.scnujxjy.backendpoint.util.MessageSender;
import com.scnujxjy.backendpoint.util.ResultCode;
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

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private TeacherInformationService teacherInformationService;

    @Resource
    private MessageSender messageSender;

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
        boolean send = messageSender.sendExportStudentSituation(sectionId,(String)StpUtil.getLoginId(),1);
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
        boolean send = messageSender.sendExportStudentSituation(courseId,(String)StpUtil.getLoginId(),2);
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

