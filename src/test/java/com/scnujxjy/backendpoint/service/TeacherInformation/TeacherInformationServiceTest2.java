package com.scnujxjy.backendpoint.service.TeacherInformation;

import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class TeacherInformationServiceTest2 {
    @Resource
    private TeacherInformationService teacherInformationService;

    /**
     * 检查是否有同名同性的老师，但是工号、身份证号码、手机号码不同的老师
     */
    @Test
    public void test1(){
        List<TeacherInformationPO> teachers = teacherInformationService.getBaseMapper().selectList(null);

        Map<String, List<TeacherInformationPO>> groupedByName = teachers.stream()
                .collect(Collectors.groupingBy(TeacherInformationPO::getName));

        for (Map.Entry<String, List<TeacherInformationPO>> entry : groupedByName.entrySet()) {
            List<TeacherInformationPO> sameNameTeachers = entry.getValue();

            Set<String> uniqueIdentifiers = new HashSet<>();

            for (TeacherInformationPO teacher : sameNameTeachers) {
                String identifier = teacher.getWorkNumber() + "|" + teacher.getIdCardNumber() + "|" + teacher.getPhone();
                uniqueIdentifiers.add(identifier);
            }

            if (uniqueIdentifiers.size() == sameNameTeachers.size() && uniqueIdentifiers.size() > 1) {
                System.out.println("找到同名同姓但工号、身份证号码、手机号码都不同的老师: " + entry.getKey());
                for (TeacherInformationPO teacher : sameNameTeachers) {
                    System.out.println("工号: " + teacher.getWorkNumber() + ", 身份证号码: " + teacher.getIdCardNumber() + ", 手机号码: " + teacher.getPhone());
                }
                System.out.println("-------------------------------");
            }
        }
    }

    /**
     * 输出所有同名同姓的老师
     */
    @Test
    public void testPrintSameNameTeachers(){
        List<TeacherInformationPO> teachers = teacherInformationService.getBaseMapper().selectList(null);

        Map<String, List<TeacherInformationPO>> groupedByName = teachers.stream()
                .collect(Collectors.groupingBy(TeacherInformationPO::getName));

        for (Map.Entry<String, List<TeacherInformationPO>> entry : groupedByName.entrySet()) {
            List<TeacherInformationPO> sameNameTeachers = entry.getValue();

            if (sameNameTeachers.size() > 1) {
                System.out.println("同名同姓的老师: " + entry.getKey());
                for (TeacherInformationPO teacher : sameNameTeachers) {
                    System.out.println("工号: " + teacher.getWorkNumber() + ", 身份证号码: " + teacher.getIdCardNumber() + ", 手机号码: " + teacher.getPhone());
                }
                System.out.println("-------------------------------");
            }
        }
    }
}
