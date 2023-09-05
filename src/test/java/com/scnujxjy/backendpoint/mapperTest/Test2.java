package com.scnujxjy.backendpoint.mapperTest;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 检测排课表中的班级是否在旧系统中的班级中
 * 因为存在人工录入的错误
 */
@SpringBootTest
@Slf4j
public class Test2 {
    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Test
    public void test1(){
        Set<String> errorClassName = new HashSet<>();

        List<CourseSchedulePO> courseInformationPOS = courseScheduleMapper.selectList(null);
        for(CourseSchedulePO courseSchedulePO: courseInformationPOS){
            String grade = courseSchedulePO.getGrade();
            String major_name = courseSchedulePO.getMajorName();
            String level = courseSchedulePO.getLevel();
            String study_form = courseSchedulePO.getStudyForm();
            List<ClassInformationPO> classInformationPOS = classInformationMapper.selectClassByCondition1(grade, major_name, level, study_form);

            boolean ident = false;
            for(ClassInformationPO classInformationPO: classInformationPOS){
                String className = classInformationPO.getClassName();
                if(className.equals(courseSchedulePO.getAdminClass())){
                    ident = true;
                }
            }
            if(!ident){

                errorClassName.add(courseSchedulePO.getAdminClass() );
            }
        }
//        log.info(courseInformationPOS.toString());
        log.info("错误的排课表行政班别包括\n" + errorClassName.toString());
    }
}
