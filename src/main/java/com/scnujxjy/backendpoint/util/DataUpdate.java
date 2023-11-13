package com.scnujxjy.backendpoint.util;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.constant.enums.LiveStatusEnum;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.GraduationInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import com.scnujxjy.backendpoint.util.video_stream.SingleLivingSetting;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class DataUpdate {

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Resource
    private VideoStreamUtils videoStreamUtils;

    @Resource
    protected RedisTemplate<String, Object> redisTemplate;

    @Resource
    private ScoreInformationMapper scoreInformationMapper;

    @Resource
    private CourseInformationMapper courseInformationMapper;

    @Resource
    private StudentStatusMapper studentStatusMapper;
    @Resource
    private SingleLivingSetting singleLivingSetting;

    @Resource
    private GraduationInfoMapper graduationInfoMapper;

    @Resource
    private OldDataSynchronize oldDataSynchronize;

    @Resource
    private MessageSender messageSender;

    @Value("${run.checkLivingStatusScan}")
    private boolean checkLivingStatusScan;

    @Value("${run.schedule}")
    private boolean schedule;

    @Value("${run.oldDataSynchronizeStatus}")
    private boolean oldDataSynchronizeStatus;

    @Transactional(rollbackFor = Exception.class)
    public boolean updateCourseScheduleInfoByVideo(CourseSchedulePO courseSchedulePO, String videoId){
        // 获取所有在同一个教学班、同一门课程、同一个时间点的排课记录，即合班一起上的课
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(new LambdaQueryWrapper<CourseSchedulePO>()
                .eq(CourseSchedulePO::getTeachingClass, courseSchedulePO.getTeachingClass())
                .eq(CourseSchedulePO::getCourseName, courseSchedulePO.getCourseName())
                .eq(CourseSchedulePO::getTeachingDate, courseSchedulePO.getTeachingDate())
                .eq(CourseSchedulePO::getTeachingTime, courseSchedulePO.getTeachingTime()));

        for(CourseSchedulePO courseSchedulePO1: courseSchedulePOS){
            // 更新时间
            courseSchedulePO1.setOnlinePlatform(videoId);

            int update = courseScheduleMapper.update(courseSchedulePO1, new LambdaQueryWrapper<CourseSchedulePO>().eq(CourseSchedulePO::getId, courseSchedulePO1.getId()));
            if(update <= 0){
                throw new RuntimeException("Failed to update record: " + courseSchedulePO1.getId());
            }
        }
        return true;
    }


    @Value("${spring.rabbitmq.queue1}")
    private String queue1;

    @Scheduled(cron = "0 00 22 * * ?", zone="Asia/Shanghai")
    public void executeAt1AM1520() {
        if (!oldDataSynchronizeStatus) {
            // 如果配置为 false，直接返回
            return;
        }
        // 每晚 10 点 校对新旧系统数据
        log.info("旧系统数据更新中...");
        messageSender.send(queue1, "数据同步");
    }

    /**
     * 检测一下 直播间的状态
     */
    @Scheduled(fixedRate = 300000)
    public void checkLivingRoomStatus(){
        if (!checkLivingStatusScan) {
            // 如果配置为 false，直接返回
            return;
        }
        log.info("执行直播间扫描");
        List<VideoStreamRecordPO> videoStreamRecordPOS = videoStreamRecordsMapper.selectList(null);
        for(VideoStreamRecordPO videoStreamRecordPO: videoStreamRecordPOS){
//            log.info("扫描 " + videoStreamRecordPO);
            String channelId = videoStreamRecordPO.getChannelId();
            if(channelId == null){
                int i = videoStreamRecordsMapper.deleteById(videoStreamRecordPO.getId());
            }else{
                ChannelResponseBO channelBasicInfo = videoStreamUtils.getChannelBasicInfo(channelId);
                if(videoStreamRecordPO.getWatchStatus() != null && videoStreamRecordPO.getWatchStatus().equals(LiveStatusEnum.OVER.status)){
                    // 彻底终止状态，直播间完全不会再用
                    continue;
                }
                if(videoStreamRecordPO.getWatchStatus() != null && LiveStatusEnum.get(channelBasicInfo.getWatchStatus()).equals(videoStreamRecordPO.getWatchStatus())){

                }else{
                    videoStreamRecordPO.setWatchStatus(LiveStatusEnum.get(channelBasicInfo.getWatchStatus()));
                    int i = videoStreamRecordsMapper.updateById(videoStreamRecordPO);
                    log.info("更新直播间状态 " + i + " " + channelBasicInfo);
                }
            }
        }
    }

    @Scheduled(fixedRate = 1800000) // 每30分钟执行一次
    public void checkAndUpdateOnlineUsers() {
        List<String> onlineUsers = StpUtil.searchSessionId("", 0, -1, false);
        for (String sessionId : onlineUsers) {
            if (!StpUtil.isLogin(sessionId)) {
                // 如果用户不再登录状态（可能因为令牌过期）
                // 获取用户的角色信息并更新在线人数
                if(!StpUtil.getRoleList().isEmpty()) {
                    String roleName = StpUtil.getRoleList().get(0); // 根据sessionId获取角色名称
                    redisTemplate.opsForValue().decrement("onlineCount:" + roleName);
                    redisTemplate.opsForValue().decrement("totalOnlineCount");
                }
            }
        }
    }
}
