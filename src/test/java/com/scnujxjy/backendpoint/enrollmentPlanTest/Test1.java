package com.scnujxjy.backendpoint.enrollmentPlanTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.admission_information.EnrollmentPlanPO;
import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.scnujxjy.backendpoint.service.admission_information.EnrollmentPlanService;
import com.scnujxjy.backendpoint.service.basic.GlobalConfigService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private EnrollmentPlanService enrollmentPlanService;

    @Resource
    private GlobalConfigService globalConfigService;

    @Test
    public void test1(){
        GlobalConfigPO globalConfigPO = globalConfigService.
                getGlobalConfigInfo("招生计划申报");
        log.info("招生计划申报配置 \n" + globalConfigPO);

        EnrollmentPlanPO enrollmentPlanPO = new EnrollmentPlanPO()
                .setYear(2024)
                .setMajorName("学前教育")
                .setStudyForm("函授")
                .setEducationLength("3")
                .setTrainingLevel("高起专")
                .setEnrollmentNumber(100)
                .setTargetStudents("dafsadf")
                .setEnrollmentRegion("广州达德")
                .setSchoolLocation("广州市")
                .setContactNumber("9521233434")
                .setCollege("计算机学院")
                .setTeachingLocation("广州达德教学点")
                .setEnrollmentSubject("文史类")
                .setTuition(BigDecimal.valueOf(3000.0))
                ;
        enrollmentPlanService.getBaseMapper().insert(enrollmentPlanPO);
    }
}
