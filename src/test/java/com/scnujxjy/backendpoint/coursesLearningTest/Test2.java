package com.scnujxjy.backendpoint.coursesLearningTest;

import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseNotificationsPO;
import com.scnujxjy.backendpoint.service.courses_learning.CourseMaterialsService;
import com.scnujxjy.backendpoint.service.courses_learning.CourseNotificationsService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

@SpringBootTest
@Slf4j
public class Test2 {
    @Resource
    private CourseNotificationsService courseNotificationsService;

    @Resource
    private CourseMaterialsService courseMaterialsService;

    /**
     * 生成一个课程通知
     */
    @Test
    public void test1(){
        CourseNotificationsPO courseNotificationsPO = new CourseNotificationsPO()
                .setCourseId(127L)
                .setNotificationTitle("课程第一次通知 请同学们完成作业一")
                .setNotificationContent("懂法守法大法师发的发生大发是")
                .setIsPinned(true)
                .setNotificationAttachment(Arrays.asList(1L, 2L))
                ;
        int insert = courseNotificationsService.getBaseMapper().insert(courseNotificationsPO);
    }

    /**
     * 更新通知
     */
    @Test
    public void test2(){
        CourseNotificationsPO courseNotificationsPO = courseNotificationsService.getBaseMapper().selectById(1L);
        CourseNotificationsPO courseNotificationsPO1 = courseNotificationsPO.setNotificationAttachment(Arrays.asList(3L, 4L));
        boolean b = courseNotificationsService.updateById(courseNotificationsPO1);
        if(b){
            log.info("\n 更新成功");
        }else{
            log.error("\n 更新失败");
        }
    }
}
