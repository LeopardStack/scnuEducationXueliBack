package com.scnujxjy.backendpoint.task;

import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class CourseScheduleTask {

    @Resource
    private CourseScheduleService courseScheduleService;

    @Scheduled(cron = " 0 0 0 0 0 0 0")
    private void doTask() {

    }
}
