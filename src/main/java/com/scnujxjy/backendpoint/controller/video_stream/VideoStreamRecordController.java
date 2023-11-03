package com.scnujxjy.backendpoint.controller.video_stream;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.scnujxjy.backendpoint.constant.enums.LiveStatusEnum;
import com.scnujxjy.backendpoint.constant.enums.PolyvEnum;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelInfoResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ChannelResponseData;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.video_stream.VideoStreamRecordRO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordVO;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import com.scnujxjy.backendpoint.service.video_stream.VideoStreamRecordService;
import com.scnujxjy.backendpoint.util.tool.ScnuTimeInterval;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import com.scnujxjy.backendpoint.util.video_stream.SingleLivingSetting;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.polyv.live.v1.entity.channel.operate.LiveSonChannelInfoListResponse;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    private ScnuXueliTools scnuXueliTools;

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
        VideoStreamRecordVO videoStreamRecordVO = videoStreamRecordService.detailById(id);
        if (Objects.isNull(videoStreamRecordVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(videoStreamRecordVO);
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
    public SaResult createLivingRoom(@RequestBody CourseInformationRO courseInformationRO) {
        if(courseInformationRO == null){
            return SaResult.error("创建直播间失败");
        }


        try {
            CourseSchedulePO courseSchedulePO = courseScheduleService.getBaseMapper().selectOne(new LambdaQueryWrapper<CourseSchedulePO>()
                    .eq(CourseSchedulePO::getId, courseInformationRO.getId()));
            ScnuTimeInterval timeInterval = scnuXueliTools.getTimeInterval(courseSchedulePO.getTeachingDate(), courseSchedulePO.getTeachingTime());

            ApiResponse channel = singleLivingSetting.createChannel(courseSchedulePO.getCourseName(), timeInterval.getStart(), timeInterval.getEnd(),
                    false, "N");
            log.info("保利威创建直播间" + channel);
            if(channel.getCode().equals(200)){
                ChannelResponseData channelResponseData = channel.getData();
                VideoStreamRecordPO videoStreamRecordPO = new VideoStreamRecordPO();
                videoStreamRecordPO.setChannelId("" + channelResponseData.getChannelId());
                videoStreamRecordPO.setChannelPasswd("" + channelResponseData.getChannelPasswd());

                ChannelInfoResponse channelInfoByChannelId1 = videoStreamUtils.getChannelInfoByChannelId("" + channelResponseData.getChannelId());
                log.info("频道信息包括 " + channelInfoByChannelId1);
                if(channelInfoByChannelId1.getCode().equals(200) && channelInfoByChannelId1.getSuccess()){
                    log.info("创建频道成功");
                    videoStreamRecordPO.setWatchStatus(LiveStatusEnum.get(channelInfoByChannelId1.getData().getWatchStatus()));
                    int insert = videoStreamRecordService.getBaseMapper().insert(videoStreamRecordPO);
                    // 更新排课表的在线平台资源
                    courseSchedulePO.setOnlinePlatform(String.valueOf(videoStreamRecordPO.getId()));
                    boolean b = courseScheduleService.updateById(courseSchedulePO);


                    log.info(channel.toString());
                    log.info("创建的直播间频道 " + channelResponseData.getChannelId() + " 频道密码 " + channelResponseData.getChannelPasswd());
                    if(insert > 0){
                        log.info("直播间信息插入成功");
                    }

                    return SaResult.ok("创建频道成功");
                }else{
                    log.error("创建直播间失败 " + channelInfoByChannelId1);
                    return SaResult.error("创建直播间失败");
                }

            }
        }catch (Exception e){
            log.error("创建直播间失败 " + e.toString());
            return SaResult.error("创建直播间失败");
        }
        return SaResult.error("创建直播间失败");
    }

    /**
     * 单个删除直播间
     *
     * @param id 排课表id
     * @return 添加后的频道信息
     */
    @DeleteMapping("/delete_living_room")
//    @SaCheckPermission("强制删除直播间")
    public SaResult deleteLivingRoom(@RequestParam("id") Long id) {
        log.info("获取到了 排课表 ID" + id);
        CourseSchedulePO courseSchedulePO = courseScheduleService.getBaseMapper().selectById((id));
        if(courseSchedulePO == null){
            return SaResult.error("删除直播间失败, 该 id 找不到排课信息").setCode(2000);
        }else{
            String onlinePlatform = courseSchedulePO.getOnlinePlatform();
            if(onlinePlatform == null){
                return SaResult.ok("直播已删除，不需要重复删除");
            }else{
                try {
                    VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordService.getBaseMapper().selectById(Long.parseLong(onlinePlatform));
                    if(videoStreamRecordPO != null && videoStreamRecordPO.getChannelId() != null){
                        String channelId = videoStreamRecordPO.getChannelId();
                        Map<String, Object> stringObjectMap = videoStreamUtils.deleteView(channelId);
                        int i = videoStreamRecordService.getBaseMapper().deleteById(videoStreamRecordPO.getId());
                    }
                }catch (Exception e){
                    log.info("找不到该直播间信息，删除失败" + e);
                }
                courseSchedulePO.setOnlinePlatform("");
                int i = courseScheduleService.getBaseMapper().updateById(courseSchedulePO);

                UpdateWrapper<CourseSchedulePO> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("online_platform", null).eq("id", courseSchedulePO.getId());
                int update = courseScheduleService.getBaseMapper().update(null, updateWrapper);
                log.info("update的值为"+update);

                return SaResult.ok("删除成功 " + i);
            }
        }
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
        }catch (Exception e){
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
        try {
            CourseSchedulePO courseSchedulePO = courseScheduleService.getBaseMapper().selectById(id);
            String onlinePlatform = courseSchedulePO.getOnlinePlatform();
            VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordService.getBaseMapper().selectById(onlinePlatform);
            String channelId = videoStreamRecordPO.getChannelId();

            LiveSonChannelInfoListResponse roleInfo = videoStreamUtils.getRoleInfo(channelId);
            // 获取这个唯一助教的账号
            String account = roleInfo.getSonChannelInfos().get(0).getAccount();
            String loginId = StpUtil.getLoginIdAsString();
            TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                    .eq(TeacherInformationPO::getTeacherUsername, loginId));
            boolean b = videoStreamUtils.generateTutor(channelId, teacherInformationPO.getName(), "123456");
            if (b) {
                log.info("生成助教成功!");
                String s = videoStreamUtils.generateTutorSSOLink(channelId, account);
                log.info("助教的单点登录链接为 " + s);
                return SaResult.ok(s);
            }
            return SaResult.error("生成助教直播单点登录链接失败").setCode(2000);
        }catch (Exception e){
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

            boolean isExist = true;
            if(channelId == null){
                return SaResult.error("获取直播失败，频道不存在").setCode(2000);
            }
            ChannelResponseBO channelBasicInfo = null;
            try {
                channelBasicInfo = videoStreamUtils.getChannelBasicInfo(channelId);
                if(channelBasicInfo.getChannelId() != null){
                    return SaResult.ok(PolyvEnum.WATCH_URL.getKey() + channelId);
                }
            }catch (Exception e){
                log.info("获取观众链接 保利威返回值 " + channelBasicInfo);
            }
            return SaResult.error("获取直播失败，请联系管理员").setCode(2000);
        }catch (Exception e){
            log.error("获取观众链接失败 " + e.toString());
            return SaResult.error("获取直播失败，请联系管理员").setCode(2000);
        }
    }
}

