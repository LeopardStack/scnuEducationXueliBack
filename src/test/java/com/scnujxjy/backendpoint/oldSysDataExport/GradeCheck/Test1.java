package com.scnujxjy.backendpoint.oldSysDataExport.GradeCheck;

import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private ScoreInformationMapper scoreInformationMapper;

    @Test
    public void test1(){
        ScoreInformationPO scoreInformationPO = new ScoreInformationPO()    ;
//        scoreInformationPO.setStudentId("235307016043");
//        scoreInformationPO.setClassIdentifier("230701");
//        scoreInformationPO.setGrade("2023");
//        scoreInformationPO.setCollege("教育科学学院");
//        scoreInformationPO.setMajorName("小学教育");
//        scoreInformationPO.setSemester("3");
//        scoreInformationPO.setCourseName("学校管理学");
//        scoreInformationPO.setCourseCode("20");
//        scoreInformationPO.setCourseType("必修");
//        scoreInformationPO.setAssessmentType("考试");

        scoreInformationPO.setStudentId("155307186014");
        scoreInformationPO.setClassIdentifier("150718");
        scoreInformationPO.setGrade("2015");
        scoreInformationPO.setCollege("教育科学学院");
        scoreInformationPO.setMajorName("教育学(教育管理方向)");
        scoreInformationPO.setSemester("5");
        scoreInformationPO.setCourseName("课程开发与管理");
        scoreInformationPO.setCourseCode("17");
        scoreInformationPO.setCourseType("必修");
        scoreInformationPO.setAssessmentType("考查");
        scoreInformationPO.setFinalScore("92");
//        int i = scoreInformationMapper.countByAttributesExceptId(scoreInformationPO);
//        int i1 = scoreInformationMapper.countBySelectedAttributes(scoreInformationPO);
//        int i2 = scoreInformationMapper.updateBySelectedAttributes(scoreInformationPO);
//        log.info("相等的条目数 " + i);
//        log.info("相等的条目数1 " + i1);
//        log.info("更新相等的条目数 " + i2);
        List<ScoreInformationPO> scoreInformationPOS = scoreInformationMapper.selectDuplicateRecordsByGrade("2023");
        log.info(scoreInformationPOS.toString());
    }
}
