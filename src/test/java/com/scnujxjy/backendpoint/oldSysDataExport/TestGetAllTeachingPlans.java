package com.scnujxjy.backendpoint.oldSysDataExport;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getTeachingPlans;

@SpringBootTest
@Slf4j
public class TestGetAllTeachingPlans {

    @Autowired(required = false)
    private ClassInformationMapper classInformationMapper;

    @Autowired(required = false)
    private CourseInformationMapper courseInformationMapper;

    @Test
    public void test1(){
        ArrayList<HashMap<String, String>> teachingPlans = getTeachingPlans();
        log.info("教学计划总数 " + teachingPlans.size());

        // 记录文凭班教学计划数量
        int wpCount = 0;

        for(HashMap<String, String> hashMap : teachingPlans){
            CourseInformationPO courseInformationPO = new CourseInformationPO();
            String classIdentifier = hashMap.get("BSHI");

            QueryWrapper<ClassInformationPO> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("class_identifier", classIdentifier);
            ClassInformationPO classInformationPO = classInformationMapper.selectOne(queryWrapper);

            if(classInformationPO == null){
                if(classIdentifier.startsWith("WP")){
                    wpCount += 1;
                }
                log.error(hashMap.toString() + " 找不到对应的班级");
                continue;
            }



            courseInformationPO.setGrade(classInformationPO.getGrade());
            courseInformationPO.setMajorName(classInformationPO.getMajorName());
            courseInformationPO.setLevel(classInformationPO.getLevel());
            courseInformationPO.setStudyForm(classInformationPO.getStudyForm());
            courseInformationPO.setAdminClass(hashMap.get("BSHI"));
            courseInformationPO.setCourseName(hashMap.get("KCHM"));
            courseInformationPO.setStudyHours(Integer.valueOf(hashMap.get("KCHH")));
            courseInformationPO.setAssessmentType(hashMap.get("FSHI"));
            courseInformationPO.setTeachingMethod("线下");
            courseInformationPO.setCourseType(hashMap.get("TYPES"));
            courseInformationPO.setTeachingSemester(hashMap.get("XQI"));
            courseInformationPO.setCourse_code(hashMap.get("KCHH"));

            courseInformationMapper.insert(courseInformationPO);


        }
        log.info("文凭班总数为 " + wpCount);
    }
}
