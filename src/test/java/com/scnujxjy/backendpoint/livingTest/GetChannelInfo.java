package com.scnujxjy.backendpoint.livingTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@Slf4j
public class GetChannelInfo {
    @Resource
    CourseScheduleMapper courseScheduleMapper;

    @Resource
    ClassInformationMapper classInformationMapper;

    @Resource
    VideoStreamRecordsMapper videoStreamRecordsMapper;

    @Test
    public void test1(){
        Set<String> channels = new HashSet<>();
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(null);
        for(CourseSchedulePO courseSchedulePO: courseSchedulePOS){
            String grade = courseSchedulePO.getGrade();
            String level = courseSchedulePO.getLevel();
            String studyForm = courseSchedulePO.getStudyForm();
            String majorName = courseSchedulePO.getMajorName();
            String className = courseSchedulePO.getAdminClass();

            ClassInformationPO classInformationPO = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                    .eq(ClassInformationPO::getGrade, grade)
                    .eq(ClassInformationPO::getLevel, level)
                    .eq(ClassInformationPO::getStudyForm, studyForm)
                    .eq(ClassInformationPO::getMajorName, majorName)
                    .eq(ClassInformationPO::getClassName, className)
            );
            if(classInformationPO == null){
                log.error("存在排课记录找不到班级 " + courseSchedulePO);
            }
            if(classInformationPO.getCollege().equals("文学院")){
                String onlinePlatform = courseSchedulePO.getOnlinePlatform();
                if (onlinePlatform != null && !onlinePlatform.isEmpty()) {
                    // 正则表达式匹配纯数字字符串
                    String regex = "^-?\\d+$";
                    if (onlinePlatform.matches(regex)) {
                        try {
                            long onlinePlatformLong = Long.parseLong(onlinePlatform);
                            // 在这里使用onlinePlatformLong，例如查询VideoStreamRecord
                            VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(
                                    new LambdaQueryWrapper<VideoStreamRecordPO>()
                                            .eq(VideoStreamRecordPO::getId, onlinePlatformLong)
                            );
                            channels.add(videoStreamRecordPO.getChannelId());
                        } catch (NumberFormatException e) {
                            // handle the exception if the number is out of range for a long
                            log.error("The provided string is too large to fit in a long.");
                        }
                    } else {
                        log.error("The provided string does not represent a valid long number.");
                    }
                } else {
                    log.error("The provided string is null or empty.");
                }

            }
        }

        log.info("文学院的直播间包括 \n" + channels);
    }
}
