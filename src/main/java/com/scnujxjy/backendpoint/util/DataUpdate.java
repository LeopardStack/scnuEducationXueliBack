package com.scnujxjy.backendpoint.util;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.constant.enums.LiveStatusEnum;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelInfoResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ChannelResponseData;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

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
    private SingleLivingSetting singleLivingSetting;

    @Resource
    private GraduationInfoMapper graduationInfoMapper;

    @Resource
    private OldDataSynchronize oldDataSynchronize;

    @Resource
    private MessageSender messageSender;

    @Transactional(rollbackFor = Exception.class)
    boolean updateCourseScheduleInfoByVideo(CourseSchedulePO courseSchedulePO, String videoId){
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

    /**
     * 定时的去开启删除直播间
     */
//    @Scheduled(fixedRate = 60000)
    public void dealWithLivingRooms(){
        // 获取距离现在只有 1小时的排课表
        List<CourseSchedulePO> recordsWithinCertainHour = courseScheduleMapper.findRecordsWithinCertainHour(1);
        for(CourseSchedulePO courseSchedulePO: recordsWithinCertainHour){
            // date 类型自带时间
            Date teachingDate = courseSchedulePO.getTeachingDate();
            /// 16:30 - 18:30
            String teachingTime = courseSchedulePO.getTeachingTime();

            Date start = null;
            Date end = null;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(teachingDate);
            // 提取时间部分，例如 "16:30 - 18:30"
            String[] timeParts = teachingTime.split("-");

            String startTimeStr = timeParts[0];
            int startHour = Integer.parseInt(startTimeStr.split(":")[0]);
            int startMinute = Integer.parseInt(startTimeStr.split(":")[1]);
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            start = calendar.getTime();

            String endTimeStr = timeParts[1];
            int endHour = Integer.parseInt(endTimeStr.split(":")[0]);
            int endMinute = Integer.parseInt(endTimeStr.split(":")[1]);
            calendar.set(Calendar.HOUR_OF_DAY, endHour);
            calendar.set(Calendar.MINUTE, endMinute);
            end = calendar.getTime();

            String videoId = courseSchedulePO.getOnlinePlatform();
            if (videoId == null) {
                // 如果该排课表没有开启直播 现在就给她开通

                try {
                    ApiResponse channel = singleLivingSetting.createChannel(courseSchedulePO.getCourseName(), start, end, false, "N");
                    log.info("创建直播间参数包括 " + start + "  " + end  +  "创建直播间返回值 " + channel.toString());
                    if(channel.getCode().equals(200)){
                        ChannelResponseData channelResponseData = channel.getData();
                        VideoStreamRecordPO videoStreamRecordPO = new VideoStreamRecordPO();
                        videoStreamRecordPO.setChannelId("" + channelResponseData.getChannelId());
                        videoStreamRecordPO.setChannelPasswd("" + channelResponseData.getChannelPasswd());
                        int insert = videoStreamRecordsMapper.insert(videoStreamRecordPO);

                        ChannelInfoResponse channelInfoByChannelId1 = videoStreamUtils.getChannelInfoByChannelId("" + channelResponseData.getChannelId());
                        log.info("频道信息包括 " + channelInfoByChannelId1);
                        if(channelInfoByChannelId1.getCode().equals(200) && channelInfoByChannelId1.getSuccess()){
                            log.info("创建频道成功");
                        }else{
                            log.info("创建失败");
                        }
                        if(insert > 0){
                            List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(new LambdaQueryWrapper<CourseSchedulePO>()
                                    .eq(CourseSchedulePO::getTeachingDate, courseSchedulePO.getTeachingDate())
                                    .eq(CourseSchedulePO::getTeachingTime, courseSchedulePO.getTeachingTime())
                                    .eq(CourseSchedulePO::getTeacherUsername, courseSchedulePO.getTeacherUsername())
                                    .eq(CourseSchedulePO::getCourseName, courseSchedulePO.getCourseName())
                            );
                            for(CourseSchedulePO courseSchedulePO1: courseSchedulePOS){
                                boolean b = updateCourseScheduleInfoByVideo(courseSchedulePO1, "" + videoStreamRecordPO.getId());
                                log.info("直播间创建成功！" + courseSchedulePO);
                            }


                        }
                        log.info(channel.toString());
                        log.info("创建的直播间频道 " + channelResponseData.getChannelId() + " 频道密码 " + channelResponseData.getChannelPasswd());
                    }
                }catch (Exception e){
                    log.error("创建直播间失败 " + courseSchedulePO);
                }
            }else{
                log.info("删除直播间暂时不做 videoStreamRecordId " + videoId);
                // 获取当前的东八区时间
//                Calendar currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Shanghai"));
//                Date currentTime = currentCalendar.getTime();
//
//                // 将 end 时间加上30分钟
//                Calendar endCalendar = Calendar.getInstance();
//                endCalendar.setTime(end);
//                endCalendar.add(Calendar.MINUTE, 30);
//
//                // 判断是否超过30分钟
//                if (endCalendar.getTime().before(currentTime)) {
//                    // 超过30分钟，执行删除直播间的操作
//                    VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(new LambdaQueryWrapper<VideoStreamRecordPO>()
//                            .eq(VideoStreamRecordPO::getId, Long.parseLong(courseSchedulePO.getOnlinePlatform())));
//
//                    if (videoStreamRecordPO != null) {
//                        Map<String, Object> stringObjectMap = videoStreamUtils.deleteView(videoStreamRecordPO.getChannelId());
//                        log.info("删除直播间 " + videoStreamRecordPO);
//                    }
//                }
            }
        }
    }


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

    @Scheduled(cron = "0 00 22 * * ?")
    public void executeAt1AM1520() {
        // 每晚 10 点 校对新旧系统数据
        log.info("旧系统数据更新中...");
        messageSender.send(queue1, "数据同步");
    }

    /**
     * 检测一下 直播间的状态
     */
    @Scheduled(fixedRate = 300000)
    public void checkLivingRoomStatus(){
        log.info("执行直播间扫描");
        List<VideoStreamRecordPO> videoStreamRecordPOS = videoStreamRecordsMapper.selectList(null);
        for(VideoStreamRecordPO videoStreamRecordPO: videoStreamRecordPOS){
            log.info("扫描 " + videoStreamRecordPO);
            String channelId = videoStreamRecordPO.getChannelId();
            if(channelId == null){
                int i = videoStreamRecordsMapper.deleteById(videoStreamRecordPO.getId());
            }else{
                ChannelResponseBO channelBasicInfo = videoStreamUtils.getChannelBasicInfo(channelId);
                if(videoStreamRecordPO.getWatchStatus() != null && videoStreamRecordPO.getWatchStatus().equals(LiveStatusEnum.OVER.status)){
                    // 彻底终止状态，直播间完全不会再用
                    continue;
                }
                if(videoStreamRecordPO.getWatchStatus() != null && channelBasicInfo.getWatchStatus().equals(videoStreamRecordPO.getWatchStatus())){

                }else{
                    videoStreamRecordPO.setWatchStatus(LiveStatusEnum.get(channelBasicInfo.getWatchStatus()));
                    int i = videoStreamRecordsMapper.updateById(videoStreamRecordPO);
                    log.info("更新直播间状态 " + i + " " + channelBasicInfo);
                }
            }
        }
    }
}
