package com.scnujxjy.backendpoint.service.TeachingProcess;

import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.service.teaching_process.CourseInformationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class CourseInformationServiceTest1 {

    @Resource
    private CourseInformationService courseInformationService;

    @Test
    public void test1(){
        List<CourseInformationRO> studentTeachingPlan = courseInformationService.getStudentTeachingPlan("2");
        log.info(studentTeachingPlan.toString());
    }
}
