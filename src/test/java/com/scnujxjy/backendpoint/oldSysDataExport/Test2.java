package com.scnujxjy.backendpoint.oldSysDataExport;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfo;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatus;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getStudentInfos;

@SpringBootTest
@Slf4j
public class Test2 {

    @Autowired(required = false)
    private StudentStatusMapper studentStatusMapper;

    @Autowired(required = false)
    private PersonalInfoMapper personalInfoMapper;

    /**
     * 获取旧系统中的在籍学生数据
     */
    @Test
    public void test1() {
        ArrayList<HashMap<String, String>> studentInfos = getStudentInfos("2022");
        ArrayList<HashMap<String, String>> studentInfos1 = getStudentInfos("2021");
        ArrayList<HashMap<String, String>> studentInfos2 = getStudentInfos("2020");
        ArrayList<HashMap<String, String>> studentInfos3 = getStudentInfos("2019");
        ArrayList<HashMap<String, String>> studentInfos4 = getStudentInfos("2018");

        studentInfos.addAll(studentInfos1);
        studentInfos.addAll(studentInfos2);
        studentInfos.addAll(studentInfos3);
        studentInfos.addAll(studentInfos4);
        log.info(String.valueOf(studentInfos.size()));

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy/MM/dd");

        for (HashMap<String, String> studentData : studentInfos) {
            StudentStatus studentStatus = new StudentStatus();
            PersonalInfo personalInfo = new PersonalInfo();

            // 请根据实际的字段名和数据类型调整以下代码
            studentStatus.setStudentNumber(studentData.get("XH"));
            studentStatus.setGrade(studentData.get("NJ"));
            studentStatus.setCollege(studentData.get("XSH"));

            // 教学点 BH，去掉末尾的数字
            String teachingPoint = "佛山天天3";
            teachingPoint = teachingPoint.replaceAll("\\d+$", "教学点");
            studentStatus.setTeachingPoint(teachingPoint);

            studentStatus.setMajorName(studentData.get("ZYMC"));
            studentStatus.setStudyForm(studentData.get("XXXS"));
            studentStatus.setLevel(studentData.get("CC"));
            studentStatus.setStudyDuration(studentData.get("XZ"));
            studentStatus.setAdmissionNumber(studentData.get("KSH"));
            studentStatus.setAcademicStatus(studentData.get("ZT"));

            personalInfo.setGender(studentData.get("XB"));

            String birthDateString = studentData.get("CSRQ");
            Date birthDate = null;
            try {
                birthDate = dateFormat1.parse(birthDateString);
            } catch (ParseException e) {
                try {
                    birthDate = dateFormat2.parse(birthDateString);
                } catch (ParseException e2) {
                    try {
                        birthDate = dateFormat3.parse(birthDateString);
                    } catch (ParseException e3) {
                        log.error(birthDateString);
                        log.error(e3.getMessage());
                    }
                }
            }
            if (birthDate != null) {
                personalInfo.setBirthDate(birthDate);
            }

            personalInfo.setEthnicity(studentData.get("MZ"));
            personalInfo.setIdType(studentData.get("idType"));
            personalInfo.setIdNumber(studentData.get("SFZH"));
            personalInfo.setEntrancePhoto(studentData.get("RXPIC"));

            studentStatusMapper.insert(studentStatus);
            personalInfoMapper.insert(personalInfo);
        }
    }


}
