package com.scnujxjy.backendpoint.CourseScheduleTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@SpringBootTest
@Slf4j
public class Test3 {
    @Data
    @AllArgsConstructor
    static class BatchInfo {
        private String teacherName;
        private String courseName;
        private Set<String> classes;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BatchInfo batchInfo = (BatchInfo) o;
            return Objects.equals(teacherName, batchInfo.teacherName) &&
                    Objects.equals(courseName, batchInfo.courseName) &&
                    Objects.equals(classes, batchInfo.classes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(teacherName, courseName, classes);
        }
    }
    /**
     * 将系统里已有的排课表 按照老师、课程、合班的班级 建立批次信息
     */

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private CourseScheduleService courseScheduleService;

    @Resource
    private ManagerFilter  managerFilter;

    @Test
    public void test1(){
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(null);


        Map<BatchInfo, List<CourseSchedulePO>> batches = new HashMap<>();

        for (CourseSchedulePO schedule : courseSchedulePOS) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = schedule.getTeachingDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            String formattedDate = localDate.format(formatter);

            List<CourseSchedulePO> courseSchedulePOS2 = courseScheduleMapper.selectList(new LambdaQueryWrapper<CourseSchedulePO>()
                    .eq(CourseSchedulePO::getMainTeacherName, schedule.getMainTeacherName())
                    .eq(CourseSchedulePO::getCourseName, schedule.getCourseName())
                    .eq(CourseSchedulePO::getTeachingDate, schedule.getTeachingDate())
                    .eq(CourseSchedulePO::getTeachingTime, schedule.getTeachingTime())
            );
            Map<String, Integer> classCountMap = new HashMap<>();
            for (CourseSchedulePO schedulePO : courseSchedulePOS2) {
                String className = schedulePO.getAdminClass();
                if (classCountMap.containsKey(className)) {
                    log.error("Duplicate class name found: " + className);
                } else {
                    classCountMap.put(className, 1);
                }
            }

            // At this point, no duplicates were found, so you can safely create a set of class names
            Set<String> classSet = classCountMap.keySet();

            BatchInfo key = new BatchInfo(schedule.getMainTeacherName(), schedule.getCourseName(), classSet);
            if(batches.containsKey(key)){
                batches.get(key).add(schedule);
            }else{
                batches.put(key, new ArrayList<>());
                batches.get(key).add(schedule);
            }
        }


        log.info(batches.toString());

        // 写入批次 ID
        long batchId = 1;
        int countD = 0;
        for(BatchInfo key: batches.keySet()){
            List<CourseSchedulePO> courseSchedulePOS2 = batches.get(key);
            LocalDate currentDate = LocalDate.now();

            // Get the current year as a string
            String yearNow = String.valueOf(currentDate.getYear());
            int count = 1;
            String teachingClassName = yearNow + "-" + key.teacherName + "-" + key.courseName + "-" + count + "班";
            while(true){
                List<CourseSchedulePO> courseSchedulePOS3 = courseScheduleMapper.selectList(
                        new LambdaQueryWrapper<CourseSchedulePO>().eq(CourseSchedulePO::getTeachingClass, teachingClassName));
                if(!courseSchedulePOS3.isEmpty()){
                    count += 1;
                    teachingClassName = yearNow + "-" + key.teacherName + "-" + key.courseName + "-" + count + "班";
                }else{
                    break;
                }
            }

            for(CourseSchedulePO courseSchedulePO: courseSchedulePOS2){
                countD += 1;
                courseSchedulePO.setTeachingClass(teachingClassName);
                courseSchedulePO.setBatchIndex(batchId);
                int i = courseScheduleMapper.updateById(courseSchedulePO);
                log.info(key + " \n更新 " + i);
            }
            batchId += 1;
        }

        log.info("共计获取 " + countD + " 条排课表记录");
    }


    /**
     * 获取指定 筛选器的返回值
     */
    @Test
    public void test2(){
        PageRO<CourseScheduleFilterRO> pageVO = new PageRO<>();
        pageVO.setEntity(new CourseScheduleFilterRO());
        pageVO.setPageNumber(1L);
        pageVO.setPageSize(10L);
        FilterDataVO scheduleCourses = courseScheduleService.getScheduleCourses(pageVO, managerFilter);
        log.info(scheduleCourses.toString());
    }

}
