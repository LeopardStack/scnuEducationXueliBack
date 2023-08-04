package com.scnujxjy.backendpoint.oldSysDataExport;

import com.scnujxjy.backendpoint.entity.admission_information.AdmissionInformation;
import com.scnujxjy.backendpoint.mapper.admission_information.AdmissionInformationMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getStudentLuqus;

@SpringBootTest
@Slf4j
public class Test1 {
    @Autowired(required = false)
    private AdmissionInformationMapper admissionInformationMapper;

    @Test
    public void test1() throws ParseException {
        String grade = "2021";
        ArrayList<HashMap<String, String>> studentLuqus = getStudentLuqus(2020);
        log.info(String.valueOf(studentLuqus.size()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");

        for (HashMap<String, String> studentData : studentLuqus) {
            AdmissionInformation admissionInformation = new AdmissionInformation();

            // 请根据实际的字段名和数据类型调整以下代码
            admissionInformation.setStudentNumber(studentData.get("KSH"));
            admissionInformation.setName(studentData.get("XM"));
            admissionInformation.setGender(studentData.get("XBDM"));
            admissionInformation.setTotalScore(Integer.valueOf(studentData.get("PXZF")));
            admissionInformation.setMajorCode(studentData.get("LQZY"));
            admissionInformation.setMajorName(studentData.get("ZYMC"));
            admissionInformation.setLevel(studentData.get("PYCC"));
            admissionInformation.setStudyForm(studentData.get("XXXS"));
            admissionInformation.setOriginalEducation(studentData.get("WHCDDM"));
            admissionInformation.setGraduationSchool(studentData.get("BYXX"));

            String graduatedDateString = studentData.get("BYRQ");
            try {
                Date graduatedDate = dateFormat.parse(graduatedDateString);
                admissionInformation.setGraduationDate(graduatedDate);
            }catch (ParseException e){
                try{
                    Date graduatedDate = dateFormat1.parse(graduatedDateString);
                    admissionInformation.setGraduationDate(graduatedDate);
                }catch (Exception e1){
                    log.error("毕业日期解析失败 " + graduatedDateString);
                }
            }

            admissionInformation.setPhoneNumber(studentData.get("LXDH"));
            admissionInformation.setIdCardNumber(studentData.get("SFZH"));

            String birthDateString = studentData.get("CSRQ");
            try {
                Date birthDate = dateFormat.parse(birthDateString);
                admissionInformation.setBirthDate(birthDate);
            }catch (ParseException e){
                try{
                    Date birthDate = dateFormat1.parse(birthDateString);
                    admissionInformation.setBirthDate(birthDate);
                }catch (Exception e1){
                    log.error("出生日期解析失败 " + birthDateString);
                }
            }
            admissionInformation.setAddress(studentData.get("TXDZ"));
            admissionInformation.setPostalCode(studentData.get("YZBM"));
            admissionInformation.setEthnicity(studentData.get("MINZU"));
            admissionInformation.setPoliticalStatus(studentData.get("ZZMM"));
            admissionInformation.setAdmissionNumber(studentData.get("ZKZH"));
            admissionInformation.setShortStudentNumber(studentData.get("KSH"));
            admissionInformation.setGrade(grade);

            admissionInformationMapper.insert(admissionInformation);
        }
    }
}
