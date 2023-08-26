package com.scnujxjy.backendpoint.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.inverter.video_stream.VideoStreamInverter;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelRequestBO;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.ro.video_stream.VideoStreamRecordRO;
import com.scnujxjy.backendpoint.model.vo.video_stream.VideoStreamRecordVO;
import com.scnujxjy.backendpoint.service.video_stream.VideoStreamRecordsService;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CourseScheduleTask {

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private VideoStreamUtils videoStreamUtils;

    @Resource
    private VideoStreamInverter videoStreamInverter;

    @Resource
    private VideoStreamRecordsService videoStreamRecordsService;

    /**
     * 每天上午六点到晚上十一点之间每个小时执行一次
     */
//    @Scheduled(cron = " 0 6-23 * * * *")
    public void doTask() {
        // 获取当天xx:00:01 ～ xx + 1:00:00的课程
        LocalDateTime now = LocalDateTimeUtil.now();
        log.info("目前时间是：{}，开始生成直播间链接", LocalDateTimeUtil.format(now, DatePattern.NORM_DATETIME_FORMATTER));
        LambdaQueryWrapper<CourseSchedulePO> wrapper = Wrappers.<CourseSchedulePO>lambdaQuery()
                .between(CourseSchedulePO::getTeachingDate, LocalDateTimeUtil.beginOfDay(now), LocalDateTimeUtil.endOfDay(now))
                .between(CourseSchedulePO::getTeachingTime, now.withHour(0).withMinute(0).withSecond(0).withNano(0).plusSeconds(1),
                        now.withHour(1).withMinute(0).withSecond(0).withNano(0))
                .eq(CourseSchedulePO::getTeachingMethod, "直播");
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(wrapper);
        if (CollUtil.isEmpty(courseSchedulePOS)) {
            log.error("今日没有直播课程需要上");
            return;
        }
        // 生成直播链接
        for (CourseSchedulePO courseSchedulePO : courseSchedulePOS) {
            String channelPasswd = RandomUtil.randomString(11);
            log.info("直播间密码密码为：{}", channelPasswd);
            ChannelRequestBO channelRequestBO = ChannelRequestBO.builder()
                    .name(courseSchedulePO.getCourseName())
                    .channelPasswd(channelPasswd)
                    .linkMicLimit(-1)
                    .publisher(courseSchedulePO.getTutorName())
                    .startTime(courseSchedulePO.getTeachingDate().getTime())
                    .desc("测试用直播间")
                    .nickname(courseSchedulePO.getTeachingTime())
                    .build();
            ChannelResponseBO channelResponseBO = videoStreamUtils.createTeachChannel(channelRequestBO);
            if (Objects.isNull(channelResponseBO)) {
                log.error("生成直播链接失败，排课表信息：{}", courseSchedulePO);
                // todo 通知管理员
                continue;
            }
            // 将生成的信息入库
            VideoStreamRecordRO mainRecordRO = videoStreamInverter.channelResponseBO2RO(channelResponseBO);
            // 子频道信息入库
            List<VideoStreamRecordRO> videoStreamRecordROS = videoStreamInverter.sonChannelResponseBO2RO(channelResponseBO.getSonChannelResponseBOS());
            if (CollUtil.isNotEmpty(videoStreamRecordROS)) {
                List<VideoStreamRecordVO> sonRecordVOS = videoStreamRecordsService.createBatch(videoStreamRecordROS);
                if (CollUtil.isNotEmpty(sonRecordVOS)) {
                    // 子频道id给到主频道
                    List<Long> sonIds = sonRecordVOS.stream().map(VideoStreamRecordVO::getId).collect(Collectors.toList());
                    mainRecordRO.setSonId(sonIds);
                }
            }
            // 主频道信息入库
            VideoStreamRecordVO videoStreamRecordVO = videoStreamRecordsService.create(mainRecordRO);
            if (Objects.isNull(videoStreamRecordVO)) {
                log.error("频道信息入库失败");
                continue;
            }
            // todo 通知教师
        }
        log.info("课程信息生成完成");
    }
}
