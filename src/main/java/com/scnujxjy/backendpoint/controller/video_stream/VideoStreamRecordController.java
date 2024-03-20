package com.scnujxjy.backendpoint.controller.video_stream;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.scnujxjy.backendpoint.constant.enums.LiveStatusEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelInfoResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ChannelResponseData;
import com.scnujxjy.backendpoint.model.bo.teaching_process.CourseScheduleStudentExcelBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ChannelSetRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.video_stream.VideoStreamRecordRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamAllUrlInformationVO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordVO;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingService;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import com.scnujxjy.backendpoint.service.video_stream.VideoStreamRecordService;
import com.scnujxjy.backendpoint.util.MessageSender;
import com.scnujxjy.backendpoint.util.filter.CourseScheduleFilter;
import com.scnujxjy.backendpoint.util.tool.ScnuTimeInterval;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import com.scnujxjy.backendpoint.util.video_stream.SingleLivingSetting;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.polyv.live.v1.entity.channel.operate.LiveChannelSettingRequest;
import org.apache.tika.utils.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.STUDENT;
import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * 直播记录管理
 *
 * @author leopard
 * @since 2023-08-21
 */
@RestController
@RequestMapping("/video-stream-record")
@Slf4j
public class VideoStreamRecordController {

    @Resource
    private VideoStreamRecordService videoStreamRecordService;

    @Resource
    private SingleLivingSetting singleLivingSetting;

    @Resource
    private VideoStreamUtils videoStreamUtils;

    @Resource
    private TeacherInformationService teacherInformationService;


    @Resource
    private CourseScheduleService courseScheduleService;

    @Resource
    private SingleLivingService singleLivingService;

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private MessageSender messageSender;

    @Resource
    private CourseScheduleFilter courseScheduleFilter;


    /**
     * 批量添加直播间
     *
     * @param videoStreamRecordROS 直播间信息
     * @return 添加后的频道信息
     */
    @PostMapping("/create/page")
    public SaResult createVideoStream(@RequestBody List<VideoStreamRecordRO> videoStreamRecordROS) {
        if (CollUtil.isEmpty(videoStreamRecordROS)) {
            throw dataMissError();
        }
        List<List<VideoStreamRecordVO>> generateVideoStream = videoStreamRecordService.generateVideoStream(videoStreamRecordROS);
        if (CollUtil.isEmpty(generateVideoStream)) {
            return SaResult.error().setMsg("创建失败，请联系管理员");
        }
        return SaResult.data(generateVideoStream);
    }

    /**
     * 根据id查询直播间信息
     *
     * @param id 直播间id
     * @return 直播间信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        String loginIdAsString = StpUtil.getLoginIdAsString();
        TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getTeacherUsername, loginIdAsString));
        String teacherType2 = teacherInformationPO.getTeacherType2();
        VideoStreamRecordVO videoStreamRecordVO = videoStreamRecordService.detailById(id);
        if (teacherType2.equals("主讲教师")) {

            if (Objects.isNull(videoStreamRecordVO)) {
                return SaResult.error("直播间信息获取失败 " + loginIdAsString).setCode(2000);
            }
            return SaResult.data(videoStreamRecordVO);

        } else {
            SaResult tutorChannelUrl = singleLivingService.getTutorChannelUrl(videoStreamRecordVO.getChannelId(), loginIdAsString);
            // 假设 tutorChannelUrl 中有一个方法 getData() 返回 URL 字符串
            String url = tutorChannelUrl.getData().toString();

            // 使用正则表达式匹配 URL 中的 channelId
            Pattern pattern = Pattern.compile("channelId=([\\d\\w]+)");
            Matcher matcher = pattern.matcher(url);

            String tutorUrl = null;
            if (matcher.find()) {
                String accountId = matcher.group(1); // 提取 channelId
                try {
                    tutorUrl = videoStreamUtils.generateTutorSSOLink(videoStreamRecordVO.getChannelId(), accountId);
                } catch (Exception e) {
                    log.error("获取助教链接失败 " + e.toString());
                    return SaResult.error("获取助教链接失败 " + loginIdAsString).setCode(2001);
                }

                // 使用 channelId
            } else {
                // URL 中没有找到 channelId
            }
            return SaResult.data(tutorUrl).setCode(201);
        }
    }

    /**
     * 根据channelId查询直播间信息
     *
     * @param channelId 频道id
     * @return
     */
    @GetMapping("/detail/channelId")
    public SaResult detailByChannelId(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            throw dataMissError();
        }
        VideoStreamRecordVO videoStreamRecordVO = videoStreamRecordService.detailByChannelId(channelId);
        if (Objects.isNull(videoStreamRecordVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(videoStreamRecordVO);
    }

    /**
     * 开启或关闭直播间
     *
     * @param channelId 频道id
     * @param type      状态：0-关闭，1-开启
     * @return
     */
    @GetMapping("/edit/status")
    public SaResult closeByChannelId(String channelId, Integer type) {
        if (StrUtil.isBlank(channelId) || Objects.isNull(type)) {
            throw dataMissError();
        }
        Boolean ok = videoStreamRecordService.closeVideoStream(channelId, type);
        return SaResult.data(ok);
    }


    /**
     * 单个添加直播间
     *
     * @param courseInformationRO 排课表id
     * @return 添加后的频道信息
     */
    @PostMapping("/create_living_room")
    @SaCheckPermission("添加直播间")
    public SaResult createLivingRoom(@RequestBody CourseInformationRO courseInformationRO) {
        if (courseInformationRO == null) {
            return SaResult.error("创建直播间失败");
        }


        try {
            CourseSchedulePO courseSchedulePO = courseScheduleService.getBaseMapper().selectOne(new LambdaQueryWrapper<CourseSchedulePO>()
                    .eq(CourseSchedulePO::getId, courseInformationRO.getId()));

            ScnuTimeInterval timeInterval = scnuXueliTools.getTimeInterval(courseSchedulePO.getTeachingDate(), courseSchedulePO.getTeachingTime());

            ApiResponse channel = singleLivingSetting.createChannel(courseSchedulePO.getCourseName(), timeInterval.getStart(), timeInterval.getEnd(),
                    false, "N");
            log.info("保利威创建直播间" + channel);
            if (channel.getCode().equals(200)) {
                // 将频道的 ID 与 courseID 绑定在一起 存储到 LivingResources
                ChannelResponseData channelResponseData = channel.getData();
                VideoStreamRecordPO videoStreamRecordPO = new VideoStreamRecordPO();
                videoStreamRecordPO.setChannelId("" + channelResponseData.getChannelId());
                videoStreamRecordPO.setChannelPasswd("" + channelResponseData.getChannelPasswd());
                videoStreamRecordPO.setName(courseSchedulePO.getCourseName());
                videoStreamRecordPO.setStartTime(timeInterval.getStart());
                videoStreamRecordPO.setEndTime(timeInterval.getEnd());

                ChannelInfoResponse channelInfoByChannelId1 = videoStreamUtils.getChannelInfo("" + channelResponseData.getChannelId());
                log.info("频道信息包括 " + channelInfoByChannelId1);
                if (channelInfoByChannelId1.getCode().equals(200) && channelInfoByChannelId1.getSuccess()) {
                    log.info("创建频道成功");
                    videoStreamRecordPO.setWatchStatus(LiveStatusEnum.get(channelInfoByChannelId1.getData().getWatchStatusText()));
                    int insert = videoStreamRecordService.getBaseMapper().insert(videoStreamRecordPO);
                    // 更新排课表的在线平台资源 要考虑合班
                    List<CourseSchedulePO> courseSchedulePOS = courseScheduleService.getBaseMapper().selectList(new LambdaQueryWrapper<CourseSchedulePO>()
                            .eq(CourseSchedulePO::getTeachingDate, courseSchedulePO.getTeachingDate())
                            .eq(CourseSchedulePO::getTeachingTime, courseSchedulePO.getTeachingTime())
                            .eq(CourseSchedulePO::getTeacherUsername, courseSchedulePO.getTeacherUsername())
                            .eq(CourseSchedulePO::getCourseName, courseSchedulePO.getCourseName())
                            .eq(CourseSchedulePO::getBatchIndex, courseSchedulePO.getBatchIndex())
                    );
                    Long id = videoStreamRecordPO.getId();
                    if (id == null) {
                        return SaResult.error("创建直播间失败，插入数据库失败").setCode(2000);
                    }
                    for (CourseSchedulePO courseSchedulePO1 : courseSchedulePOS) {

                        courseSchedulePO.setOnlinePlatform(String.valueOf(courseSchedulePO1.getId()));
                        UpdateWrapper<CourseSchedulePO> updateWrapper = new UpdateWrapper<>();
                        updateWrapper.set("online_platform", id).eq("id", courseSchedulePO1.getId());

                        int update = courseScheduleService.getBaseMapper().update(null, updateWrapper);

//                        boolean b = courseScheduleService.updateById(courseSchedulePO1);
                        if (insert > 0 && update > 0) {
                            log.info("新增直播间，直播间信息插入成功 " + courseSchedulePO1);
                        }
                    }


                    log.info(channel.toString());
                    log.info("创建的直播间频道 " + channelResponseData.getChannelId() + " 频道密码 " + channelResponseData.getChannelPasswd());


                    return SaResult.ok("创建频道成功");
                } else {
                    log.error("创建直播间失败 " + channelInfoByChannelId1);
                    return SaResult.error("创建直播间失败").setCode(2000);
                }

            }
        } catch (Exception e) {
            log.error("创建直播间失败 " + e.toString());
            return SaResult.error("创建直播间失败").setCode(2000);
        }
        return SaResult.error("创建直播间失败").setCode(2000);
    }

    /**
     * 单个删除直播间
     *
     * @param id 排课表id
     * @return 添加后的频道信息
     */
    @DeleteMapping("/delete_living_room")
    @SaCheckPermission("强制删除直播间")
    public SaResult deleteLivingRoom(@RequestParam("id") Long id) {
        log.info("获取到了 排课表 ID" + id);
        CourseSchedulePO courseSchedulePO = courseScheduleService.getBaseMapper().selectById((id));
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleService.getBaseMapper().selectList(new LambdaQueryWrapper<CourseSchedulePO>()
                .eq(CourseSchedulePO::getTeachingDate, courseSchedulePO.getTeachingDate())
                .eq(CourseSchedulePO::getTeachingTime, courseSchedulePO.getTeachingTime())
                .eq(CourseSchedulePO::getTeacherUsername, courseSchedulePO.getTeacherUsername())
                .eq(CourseSchedulePO::getCourseName, courseSchedulePO.getCourseName())
                .eq(CourseSchedulePO::getBatchIndex,courseSchedulePO.getBatchIndex())
        );
        int count = 0;
        for (CourseSchedulePO courseSchedulePO1 : courseSchedulePOS) {
            if (courseSchedulePO1 == null) {
                return SaResult.error("删除直播间失败, 该 id 找不到排课信息").setCode(2000);
            } else {
                String onlinePlatform = courseSchedulePO1.getOnlinePlatform();
                if (StrUtil.isBlank(onlinePlatform)) {
                    return SaResult.ok("直播已删除，不需要重复删除");
                } else {

//                        VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordService.getBaseMapper().selectById(Long.parseLong(onlinePlatform));
//                        if (videoStreamRecordPO != null && videoStreamRecordPO.getChannelId() != null) {
//                            String channelId = videoStreamRecordPO.getChannelId();
////                            Map<String, Object> stringObjectMap = videoStreamUtils.deleteView(channelId);//无需对后台直播间操作
//                            int i = videoStreamRecordService.getBaseMapper().deleteById(videoStreamRecordPO.getId());
//                            if (i > 0) {
//                                log.info("删除直播间表成功,频道号为：" + channelId);
//                            }
//                        }
//                    } catch (Exception e) {
//                        log.info("找不到该直播间信息，删除失败" + e);
//                    }

                    courseSchedulePO1.setOnlinePlatform(null);

                    Long i = courseScheduleService.getBaseMapper().updateOnlinePlatformToNull(courseSchedulePO1.getId());

                    count += i;
                }
            }
        }

        return SaResult.ok("强制删除直播间成功 " + count);

    }

    /**
     * 获取教师单点登录链接
     *
     * @param id 排课表id
     * @return 添加后的频道信息
     */
    @GetMapping("/get_tutor_link")
    public SaResult getTeahcerLink(@RequestParam("id") Long id) {
        try {
            CourseSchedulePO courseSchedulePO = courseScheduleService.getBaseMapper().selectById(id);
            String onlinePlatform = courseSchedulePO.getOnlinePlatform();
            VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordService.getBaseMapper().selectById(onlinePlatform);
            String channelId = videoStreamRecordPO.getChannelId();

            String s = videoStreamUtils.generateTeacherSSOLink(channelId);
            return SaResult.ok(s);
        } catch (Exception e) {
            return SaResult.error("获取直播链接失败").setCode(2000);
        }
    }

    /**
     * 获取助教单点登录链接
     *
     * @param id 排课表id
     * @return 添加后的频道信息
     */
    @GetMapping("/get_teacher_link")
    public SaResult getTutorLink(@RequestParam("id") Long id) {
        String loginIdAsString = StpUtil.getLoginIdAsString();
        try {
            CourseScheduleVO courseScheduleVO = courseScheduleService.detailById(id);



            if(StringUtils.isBlank(courseScheduleVO.getOnlinePlatform())){
                return SaResult.error("巡视直播间失败，直播间未生成").setCode(2001);
            }
            VideoStreamRecordVO videoStreamRecordVO = videoStreamRecordService.detailById(Long.valueOf(courseScheduleVO.getOnlinePlatform()));
            SaResult tutorChannelUrl = singleLivingService.getTutorChannelUrl(videoStreamRecordVO.getChannelId(), loginIdAsString);
            // 假设 tutorChannelUrl 中有一个方法 getData() 返回 URL 字符串
            String url = tutorChannelUrl.getData().toString();

            // 使用正则表达式匹配 URL 中的 channelId
            Pattern pattern = Pattern.compile("channelId=([\\d\\w]+)");
            Matcher matcher = pattern.matcher(url);

            String tutorUrl = null;
            if (matcher.find()) {
                String accountId = matcher.group(1); // 提取 channelId
                try {
                    tutorUrl = videoStreamUtils.generateTutorSSOLink(videoStreamRecordVO.getChannelId(), accountId);
                } catch (Exception e) {
                    log.error("获取助教链接失败 " + e.toString());
                    return SaResult.error("获取助教链接失败 " + loginIdAsString).setCode(2001);
                }

                // 使用 channelId
            } else {
                // URL 中没有找到 channelId
            }
            return SaResult.data(tutorUrl).setCode(201);
        } catch (Exception e) {
            log.error("获取助教链接失败 " + e.toString());
            return SaResult.error("获取直播链接失败").setCode(2000);
        }
    }


    /**
     * 获取观众链接
     *
     * @param id 排课表id
     * @return 添加后的频道信息
     */
    @GetMapping("/get_watcher_link")
    public SaResult getWatcherLink(@RequestParam("id") Long id) {
        try {
            CourseSchedulePO courseSchedulePO = courseScheduleService.getBaseMapper().selectById(id);
            String onlinePlatform = courseSchedulePO.getOnlinePlatform();
            VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordService.getBaseMapper().selectById(onlinePlatform);
            String channelId = videoStreamRecordPO.getChannelId();


            String userId = StpUtil.getLoginIdAsString();
            PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new
                    LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUsername, userId));
            if (Objects.isNull(platformUserPO)) {
                return SaResult.code(2000).setData("用户信息为空");
            }
            // 非学生nickname=身份-name
            String nickname = platformUserPO.getName();
            if (CollUtil.isNotEmpty(StpUtil.getRoleList()) && !StpUtil.getRoleList().contains(STUDENT.getRoleName())) {
                nickname = StpUtil.getRoleList().get(0);
                if (StrUtil.isNotBlank(platformUserPO.getName())) {
                    nickname += "-" + platformUserPO.getName();
                }
            }
            try {
                String url = videoStreamUtils.getIndependentAuthorizationLink(channelId, String.valueOf(userId), nickname, platformUserPO.getAvatarImagePath());
                return SaResult.data(url);
            } catch (IOException | NoSuchAlgorithmException e) {
                return SaResult.code(2000).setData("获取直播间信息失败");
            }

        } catch (Exception e) {
            log.error("获取观众链接失败 " + e.toString());
            return SaResult.error("获取直播失败，请联系管理员").setCode(2000);
        }
    }


    /**
     * 设置回放
     *
     * @param channelSetRO 排课表id
     * @return 添加后的频道信息
     */
    @PostMapping("/set_living_args")
    @SaCheckPermission("设置回放")
    public SaResult createLivingRoom(@RequestBody ChannelSetRO channelSetRO) {
        if (channelSetRO == null) {
            return SaResult.error("创建直播间失败").setCode(2000);
        }
        if ("Y".equals(channelSetRO.getPlayBack())) {
            boolean b = singleLivingSetting.setPlayBack(channelSetRO.getChannelId(), true, true);
            if (b) {
                return SaResult.ok("设置回放成功");
            } else {
                return SaResult.error("设置回放失败").setCode(2000);
            }
        } else {
            boolean b = singleLivingSetting.setPlayBack(channelSetRO.getChannelId(), false, true);
            if (b) {
                return SaResult.ok("关闭回放成功");
            } else {
                return SaResult.error("关闭回放失败").setCode(2000);
            }
        }
    }


    /**
     * 获取直播回放状态
     *
     * @param channelId 频道id
     * @return
     */
    @GetMapping("/get_channel_playback")
    public SaResult getChannelPlayBackState(@RequestParam("channelId") String channelId) {
        if (StrUtil.isBlank(channelId)) {
            return SaResult.error("获取回放失败").setCode(2000);
        }
        boolean playBackState = singleLivingSetting.getPlayBackState(channelId);
        return SaResult.ok().setData(playBackState);
    }


    /**
     * 获取独立授权地址
     *
     * @param channelId 直播间 ID
     * @return
     */
    @GetMapping("/create-sso-link")
    public SaResult createIndependentAuthorizationLink(String channelId) {
        if (StrUtil.isBlank(channelId)) {
            throw dataMissError();
        }
        String userId = StpUtil.getLoginIdAsString();
        PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new
                LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUsername, userId));
        if (Objects.isNull(platformUserPO)) {
            return SaResult.code(2000).setData("用户信息为空");
        }
        // 非学生nickname=身份-name
        String nickname = platformUserPO.getName();
        if (CollUtil.isNotEmpty(StpUtil.getRoleList()) && !StpUtil.getRoleList().contains(STUDENT.getRoleName())) {
            nickname = StpUtil.getRoleList().get(0);
            if (StrUtil.isNotBlank(platformUserPO.getName())) {
                nickname += "-" + platformUserPO.getName();
            }
        }
        try {
            Boolean directOpen = videoStreamRecordService.checkDirectOpen(channelId);
            if (!directOpen) {
                LiveChannelSettingRequest request = new LiveChannelSettingRequest();
                LiveChannelSettingRequest.AuthSetting authSetting = new LiveChannelSettingRequest.AuthSetting();
                authSetting.setRank(2)
                        .setEnabled("Y")
                        .setAuthType("direct")
                        .setDirectKey(RandomUtil.randomString(8));
                request.setChannelId(channelId);
                request.setAuthSettings(ListUtil.of(authSetting));
                Boolean isCreate = videoStreamUtils.createWatchCondition(request);
                if (!isCreate) {
                    return SaResult.code(2000).setMsg("打开独立授权失败，请联系管理员");
                }
            }
            String url = videoStreamUtils.getIndependentAuthorizationLink(channelId, String.valueOf(userId), nickname, platformUserPO.getAvatarImagePath());
            return SaResult.data(url);
        } catch (IOException | NoSuchAlgorithmException e) {
            return SaResult.code(2000).setData("获取直播间信息失败");
        }
    }

    @GetMapping("/detail-watch-information")
    public SaResult getWatchInformation(@RequestParam("channelId")String channelId) {
        if (StrUtil.isBlank(channelId)) {
            throw dataMissError();
        }
        try {
            VideoStreamAllUrlInformationVO videoStreamAllUrlInformationVO = videoStreamRecordService.selectChannelAllUrl(channelId);
            if (Objects.isNull(videoStreamAllUrlInformationVO)) {
                return SaResult.code(2000).setMsg("获取观看信息为空");
            }
            return SaResult.data(videoStreamAllUrlInformationVO);
        } catch (IOException | NoSuchAlgorithmException e) {
            return SaResult.code(2000).setMsg("获取观看信息失败");
        }
    }

    @PostMapping("/export-student-batch-index")
    public SaResult exportStudentInformationBatchIndex(@RequestBody PageRO<CourseScheduleStudentExcelBO> courseScheduleStudentExcelBOPageRO) {
        if (Objects.isNull(courseScheduleStudentExcelBOPageRO)
                || Objects.isNull(courseScheduleStudentExcelBOPageRO.getEntity())
                || Objects.isNull(courseScheduleStudentExcelBOPageRO.getEntity().getBatchIndex())) {
            throw dataMissError();
        }
        String userId = StpUtil.getLoginIdAsString();
        boolean isSend = messageSender.sendExportMsg(courseScheduleStudentExcelBOPageRO, courseScheduleFilter, userId);
        if (!isSend) {
            return SaResult.error("导出数据失败");
        }
        return SaResult.ok("导出数据成功");
    }

}

