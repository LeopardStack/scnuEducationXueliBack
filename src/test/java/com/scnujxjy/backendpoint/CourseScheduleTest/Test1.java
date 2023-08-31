package com.scnujxjy.backendpoint.CourseScheduleTest;

import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class Test1 {

    @Autowired(required = false)
    private CourseScheduleMapper courseScheduleMapper;

    @Test
    public void test1(){

    }
}
