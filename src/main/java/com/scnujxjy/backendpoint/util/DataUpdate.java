package com.scnujxjy.backendpoint.util;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//    @Scheduled(fixedRate = 600000)
    public void checkCourseSchedules() {
        log.info("每10分钟扫描一下班级信息表 更新一下班级各种人数记录");

        int grade = 2023;
        Map<String, Integer> studentNumber = new HashMap<>();

        List<ClassInformationPO> classInformationPOS = classInformationMapper.selectList(new LambdaQueryWrapper<ClassInformationPO>().
                eq(ClassInformationPO::getGrade, "" + grade));
        for(ClassInformationPO classInformationPO: classInformationPOS){
            String classIdentifier = classInformationPO.getClassIdentifier();
            Integer integer = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>().
                    eq(StudentStatusPO::getClassIdentifier, classIdentifier));
            log.info(classInformationPO.getClassName() + " 班 " + grade + " 年在籍学生 " + integer);
            if(studentNumber.containsKey(classIdentifier)){
                log.error("出现了重复的班级信息 " + classIdentifier);
            }else{
                studentNumber.put(classIdentifier, integer);
            }
        }

        log.info("每10分钟查看一下近 一周要开课的教学班，提前开好直播间给他们 ");
        List<CourseSchedulePO> recentRecords = courseScheduleMapper.findRecentRecords(7);

        Map<String, List<CourseSchedulePO>> teachingClassToSchedules = new HashMap<>();

        for (CourseSchedulePO record : recentRecords) {
            String teachingClass = record.getTeachingClass();

            // 如果字典中还没有这个教学班的键，就创建一个新的列表
            if (!teachingClassToSchedules.containsKey(teachingClass)) {
                teachingClassToSchedules.put(teachingClass, new ArrayList<>());
            }

            // 将当前的排课记录添加到对应的教学班的列表中
            teachingClassToSchedules.get(teachingClass).add(record);
        }
        log.info("近七天的教学班 数量为 " + teachingClassToSchedules.keySet().size() + "\n" + teachingClassToSchedules.keySet());

        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.findminRecords();
        log.info("距离今天最近的一天的全部排课记录 \n" + courseSchedulePOS.toString());


        int totalStudentCount = 0; // 用于计算总学生人数
        Map<String, Integer> teachingClassStudentCountMap = new HashMap<>(); // 用于存储各教学班的学生人数

        for (CourseSchedulePO record : recentRecords) {
            LambdaQueryWrapper<ClassInformationPO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(ClassInformationPO::getGrade, record.getGrade())
                    .eq(ClassInformationPO::getStudyForm, record.getStudyForm())
                    .eq(ClassInformationPO::getLevel, record.getLevel())
                    .eq(ClassInformationPO::getClassName, record.getAdminClass())
                    .eq(ClassInformationPO::getMajorName, record.getMajorName());

            ClassInformationPO matchedClass = classInformationMapper.selectOne(queryWrapper);
            if (matchedClass == null) {
                log.error("没有找到与排课记录匹配的班级记录!");
                continue;
            }

            if(teachingClassStudentCountMap.containsKey(record.getTeachingClass())) {
                teachingClassStudentCountMap.put(record.getTeachingClass(),
                        teachingClassStudentCountMap.get(record.getTeachingClass()) + matchedClass.getTotalCount());
            } else {
                teachingClassStudentCountMap.put(record.getTeachingClass(), matchedClass.getTotalCount());
            }

            totalStudentCount += matchedClass.getTotalCount();
        }

        log.info("近七天的行政班一起参与排课的总学生人数：" + totalStudentCount);
        for (Map.Entry<String, Integer> entry : teachingClassStudentCountMap.entrySet()) {
            log.info("教学班 " + entry.getKey() + " 的近七天排课学生人数：" + entry.getValue());
        }


    }

    @Value("${spring.rabbitmq.queue1}")
    private String queue1;

//    @Scheduled(cron = "0 35 8 * * ?")
//    public void executeAt1AM1520() {
//        // 每晚 11点 校对新旧系统数据
//        log.info("旧系统数据更新中...");
//        messageSender.send(queue1, "数据同步");
//    }
}
