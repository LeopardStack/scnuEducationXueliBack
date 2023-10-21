package com.scnujxjy.backendpoint.CourseScheduleTest;

import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScheduleCourseInformationVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class Test2 {

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Test
    public void testCourses(){
        List<ScheduleCourseInformationVO> scheduleCourseInformationVOS =
                courseScheduleMapper.selectCoursesInformation(new CourseScheduleFilterRO(), 1, 10);
        log.info("课程信息包括 " + scheduleCourseInformationVOS.toString());
        for(ScheduleCourseInformationVO scheduleCourseInformationVO: scheduleCourseInformationVOS){
            log.info(scheduleCourseInformationVO.toString());
        }
    }
}
