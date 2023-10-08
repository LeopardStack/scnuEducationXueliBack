package com.scnujxjy.backendpoint.util;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.GraduationInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.GraduationInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
    private ScoreInformationMapper scoreInformationMapper;

    @Resource
    private CourseInformationMapper courseInformationMapper;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private GraduationInfoMapper graduationInfoMapper;

    @Resource
    private OldDataSynchronize oldDataSynchronize;

    @Resource
    private MessageSender messageSender;

    /**
     * 每 600秒 轮询一次
     * 主要是校验学籍数据、成绩数据、教学计划、班级数据
     */
    @Scheduled(fixedRate = 600000)
    public void checkCourseSchedules() {
        log.info("每10分钟扫描一下班级信息表 更新一下班级各种人数记录");
    }

    @Value("${spring.rabbitmq.queue1}")
    private String queue1;

    @Scheduled(cron = "0 51 0 * * ?")
    public void executeAt1AM1520() {
        // 每晚 11点 校对新旧系统数据
        log.info("旧系统数据更新中...");
        messageSender.send(queue1, "数据同步");
    }
}
