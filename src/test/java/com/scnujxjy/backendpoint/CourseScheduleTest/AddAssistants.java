package com.scnujxjy.backendpoint.CourseScheduleTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.TeachingAssistantsCourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.TeachingAssistantsCourseScheduleMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class AddAssistants {
    @Resource
    CourseScheduleMapper courseScheduleMapper;

    @Resource
    TeachingAssistantsCourseScheduleMapper teachingAssistantsCourseScheduleMapper;

    @Resource
    TeacherInformationMapper teacherInformationMapper;


    @Test
    public void test1(){
        String mainName = "刘晶" +
                "";
        String tutorName = "黄敏丰";
        TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getName, tutorName));
        TeacherInformationPO mainTeacher = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getName, mainName));
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(new LambdaQueryWrapper<CourseSchedulePO>()
                .eq(CourseSchedulePO::getTeacherUsername, mainTeacher.getTeacherUsername()));
        Set<Long> uniqueBatchIndexes = courseSchedulePOS.stream()
                .map(CourseSchedulePO::getBatchIndex) // 将每个 CourseSchedulePO 映射为其 batchIndex
                .collect(Collectors.toSet()); // 收集结果到一个 Set 中以去除重复项
        for(Long batchIndex: uniqueBatchIndexes){
            TeachingAssistantsCourseSchedulePO teachingAssistantsCourseSchedulePO = new TeachingAssistantsCourseSchedulePO();
            teachingAssistantsCourseSchedulePO.setUsername(teacherInformationPO.getTeacherUsername());
            teachingAssistantsCourseSchedulePO.setBatchId(batchIndex);
            if(!teachingAssistantsCourseScheduleMapper.selectList(new LambdaQueryWrapper<TeachingAssistantsCourseSchedulePO>()
                    .eq(TeachingAssistantsCourseSchedulePO::getUsername, teacherInformationPO.getTeacherUsername())
                    .eq(TeachingAssistantsCourseSchedulePO::getBatchId, batchIndex)
            ).isEmpty()){
                // 重复记录 不用管它
                log.info("该助教已存在");
            }else{
                int insert = teachingAssistantsCourseScheduleMapper.insert(teachingAssistantsCourseSchedulePO);
                log.info(mainName + " 老师增加了一位新的助教" + tutorName + " 插入结果 " + insert);
            }

        }
        if(uniqueBatchIndexes.isEmpty()){
            log.info("该老师没有在本平台上课");
        }
    }
}
