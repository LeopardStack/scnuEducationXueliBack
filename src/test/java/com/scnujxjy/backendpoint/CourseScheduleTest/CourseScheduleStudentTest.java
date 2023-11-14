package com.scnujxjy.backendpoint.CourseScheduleTest;

import cn.hutool.core.io.FileUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.bo.teaching_process.CourseScheduleStudentExcelBO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

@SpringBootTest
@Slf4j
public class CourseScheduleStudentTest {
    @Resource
    CourseScheduleMapper courseScheduleMapper;

    @Resource
    CourseInformationMapper courseInformationMapper;

    @Resource
    ClassInformationMapper classInformationMapper;

    @Resource
    StudentStatusMapper studentStatusMapper;

    @Test
    public void test1() {
        CourseInformationPO courseInformationPO = new CourseInformationPO();
        courseInformationPO.setGrade("2023");
        courseInformationPO.setMajorName("计算机科学与技术");
        courseInformationPO.setStudyForm("函授");
        courseInformationPO.setLevel("专升本");
        courseInformationPO.setAdminClass("深伴我学2");
        courseInformationPO.setCourseName("JAVA语言程序设计");

        List<CourseInformationPO> courseInformationPOS = courseInformationMapper.selectList(new LambdaQueryWrapper<CourseInformationPO>().
                eq(CourseInformationPO::getGrade, courseInformationPO.getGrade()).
                eq(CourseInformationPO::getMajorName, courseInformationPO.getMajorName()).
                eq(CourseInformationPO::getAdminClass, courseInformationPO.getAdminClass()).
                eq(CourseInformationPO::getCourseName, courseInformationPO.getCourseName())
        );

        List<ClassInformationPO> classInformationPOS = classInformationMapper.selectList(new LambdaQueryWrapper<ClassInformationPO>().
                eq(ClassInformationPO::getGrade, courseInformationPO.getGrade()).
                eq(ClassInformationPO::getMajorName, courseInformationPO.getMajorName()).
                eq(ClassInformationPO::getLevel, courseInformationPO.getLevel()).
                eq(ClassInformationPO::getStudyForm, courseInformationPO.getStudyForm()).
                eq(ClassInformationPO::getClassName, courseInformationPO.getAdminClass())
        );


        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(new LambdaQueryWrapper<CourseSchedulePO>().
                eq(CourseSchedulePO::getMajorName, courseInformationPO.getMajorName()).
                eq(CourseSchedulePO::getGrade, courseInformationPO.getGrade()).
                eq(CourseSchedulePO::getAdminClass, courseInformationPO.getAdminClass()).
                eq(CourseSchedulePO::getCourseName, courseInformationPO.getCourseName())
        );


        List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectList(new LambdaQueryWrapper<StudentStatusPO>().
                eq(StudentStatusPO::getClassIdentifier, "230502")
        );


        log.info("\n" + courseInformationPOS);
        log.info("\n" + courseSchedulePOS);
        log.info("\n" + classInformationPOS);
        log.info("\n" + studentStatusPOS);
    }

    /**
     * 指定学生获取其排课一级页面 也就是多少门要直播的课程
     */
    @Test
    public void test2() {
        String sfzh = "44512219951212376X";
        List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectList(new LambdaQueryWrapper<StudentStatusPO>().
                eq(StudentStatusPO::getIdNumber, sfzh));
        for (StudentStatusPO studentStatusPO : studentStatusPOS) {
            String classIdentifier = studentStatusPO.getClassIdentifier();
            try {
                ClassInformationPO classInformationPO = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>().

                        eq(ClassInformationPO::getClassIdentifier, classIdentifier));
                String className = classInformationPO.getClassName();
                String grade = classInformationPO.getGrade();
                String majorName = classInformationPO.getMajorName();
                List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(new LambdaQueryWrapper<CourseSchedulePO>().
                        eq(CourseSchedulePO::getGrade, grade).
                        eq(CourseSchedulePO::getAdminClass, className).
                        eq(CourseSchedulePO::getMajorName, majorName));
                Set<String> courseNames = new HashSet<>();
                for (CourseSchedulePO courseSchedulePO : courseSchedulePOS) {
                    courseNames.add(courseSchedulePO.getCourseName());
                }
                log.info("\n" + sfzh + " 的直播课程包括 " + grade + " 年 " + courseNames);
            } catch (Exception e) {
                log.error(sfzh + " 获取班级信息失败 " + e.toString());
            }
        }
    }

    /**
     * 学生点击一门课程，显示其排课表，其实就是一个筛选
     */
    @Test
    public void test3() {
        String sfzh = "44512219951212376X";
        String courseName = "JAVA语言程序设计";

        Map<String, List<CourseSchedulePO>> courseSchedules = new HashMap<>();

        List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectList(new LambdaQueryWrapper<StudentStatusPO>().
                eq(StudentStatusPO::getIdNumber, sfzh));
        for (StudentStatusPO studentStatusPO : studentStatusPOS) {
            String classIdentifier = studentStatusPO.getClassIdentifier();
            try {
                ClassInformationPO classInformationPO = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>().

                        eq(ClassInformationPO::getClassIdentifier, classIdentifier));
                String className = classInformationPO.getClassName();
                String grade = classInformationPO.getGrade();
                String majorName = classInformationPO.getMajorName();
                List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(new LambdaQueryWrapper<CourseSchedulePO>().
                        eq(CourseSchedulePO::getGrade, grade).
                        eq(CourseSchedulePO::getAdminClass, className).
                        eq(CourseSchedulePO::getMajorName, majorName));
                List<CourseSchedulePO> returnCourseSchedules = new ArrayList<>();
                for (CourseSchedulePO courseSchedulePO : courseSchedulePOS) {
                    if (courseSchedulePO.getCourseName().equals(courseName)) {
                        returnCourseSchedules.add(courseSchedulePO);
                    }
                }

                courseSchedules.put(grade, returnCourseSchedules);

            } catch (Exception e) {
                log.error(sfzh + " 获取班级信息失败 " + e.toString());
            }
        }

        log.info("\n" + sfzh + " 的排课表包括 " + courseSchedules);
    }

    @Test
    public void testStudentInformationBatchIndex() {
        Set<CourseScheduleStudentExcelBO> studentInformationMapList = new HashSet<>(courseScheduleMapper.getStudentInformationBatchIndex(53L));
        File file = FileUtil.file("./test.xlsx");
        EasyExcel.write(file, CourseScheduleStudentExcelBO.class)
                .sheet("模板")
                .doWrite(() -> studentInformationMapList);
        System.out.println(file.getAbsolutePath());
    }

}
