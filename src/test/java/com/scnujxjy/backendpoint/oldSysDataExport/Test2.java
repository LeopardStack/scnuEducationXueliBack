package com.scnujxjy.backendpoint.oldSysDataExport;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.entity.admission_information.AdmissionInformation;
import com.scnujxjy.backendpoint.entity.registration_record_card.GraduationInfo;
import com.scnujxjy.backendpoint.entity.registration_record_card.OriginalEducationInfo;
import com.scnujxjy.backendpoint.entity.registration_record_card.PersonalInfo;
import com.scnujxjy.backendpoint.entity.registration_record_card.StudentStatus;
import com.scnujxjy.backendpoint.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.mapper.registration_record_card.GraduationInfoMapper;
import com.scnujxjy.backendpoint.mapper.registration_record_card.OriginalEducationInfoMapper;
import com.scnujxjy.backendpoint.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.mapper.registration_record_card.StudentStatusMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getStudentInfos;

@SpringBootTest
@Slf4j
public class Test2 {

    @Autowired(required = false)
    private StudentStatusMapper studentStatusMapper;

    @Autowired(required = false)
    private PersonalInfoMapper personalInfoMapper;

    @Autowired(required = false)
    private AdmissionInformationMapper admissionInformationMapper;

    @Autowired(required = false)
    private OriginalEducationInfoMapper originalEducationInfoMapper;

    @Autowired(required = false)
    private GraduationInfoMapper graduationInfoMapper;

    public String identifyID(String id) {
        if (id == null) {
            return null;
        }

        if (id.length() == 18 || id.length() == 15) {
            // Mainland ID
            return "中华人民共和国居民身份证";
        } else if (id.length() == 8 || id.length() == 10) {
            // Hong Kong ID
            return "港澳台证件";
        } else if (id.matches("^[A-Z][0-9]{9}$")) {
            // Taiwan ID
            return "港澳台证件";
        } else if (id.matches("^[157][0-9]{6}\\([0-9Aa]\\)$")) {
            // Macau ID
            return "港澳台证件";
        } else {
            return "非法证件";
        }
    }



    /**
     * 获取旧系统中的在籍学生数据
     */
    @Test
    public void test1() throws Exception {
        Map<String, String> jxd_jc = new HashMap<>();
        jxd_jc.put("深圳中鹏", "深圳中鹏教学点");
        jxd_jc.put("深圳华智", "深圳华智教学点");
        jxd_jc.put("东莞师华", "东莞师华教学点");
        jxd_jc.put("深圳爱华", "深圳爱华教学点");
        jxd_jc.put("佛山华泰", "佛山华泰教学点");
        jxd_jc.put("深圳燕荣", "深圳燕荣教学点");
        jxd_jc.put("惠州岭南", "惠州岭南教学点");
        jxd_jc.put("中山火炬", "中山火炬职院教学点");
        jxd_jc.put("广州海珠蓝星", "广州海珠蓝星教学点");
        jxd_jc.put("梅州启航", "梅州启航教学点");
        jxd_jc.put("广州达德", "广州达德教学点");
        jxd_jc.put("深圳伴我学", "深圳伴我学教学点");
        jxd_jc.put("华成理工", "广州华成理工教学点");
        jxd_jc.put("增城职大", "广州增城职大教学点");
        jxd_jc.put("佛山七天", "佛山七天教学点");
        jxd_jc.put("英富教育", "江门英富教学点");
        jxd_jc.put("南方人才", "广州南方人才教学点");
        jxd_jc.put("汕头龙湖", "汕头龙湖教学点");
        jxd_jc.put("清远敦敏", "清远敦敏教学点");
        jxd_jc.put("深圳华信", "深圳华信教学点");
        jxd_jc.put("深圳宝安职训", "深圳宝安教学点");
        jxd_jc.put("佛山天天", "佛山天天教学点");

        ArrayList<HashMap<String, String>> studentInfos = getStudentInfos("2023");
        ArrayList<HashMap<String, String>> studentInfos1 = getStudentInfos("2022");
        ArrayList<HashMap<String, String>> studentInfos2 = getStudentInfos("2021");
        ArrayList<HashMap<String, String>> studentInfos3 = getStudentInfos("2020");
        ArrayList<HashMap<String, String>> studentInfos4 = getStudentInfos("2019");
        ArrayList<HashMap<String, String>> studentInfos5 = getStudentInfos("2018");

        studentInfos.addAll(studentInfos1);
        studentInfos.addAll(studentInfos2);
        studentInfos.addAll(studentInfos3);
        studentInfos.addAll(studentInfos4);
        studentInfos.addAll(studentInfos5);
        log.info(String.valueOf(studentInfos.size()));

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy/MM");
        SimpleDateFormat dateFormat5 = new SimpleDateFormat("yyyy.MM");

        for (HashMap<String, String> studentData : studentInfos) {
            StudentStatus studentStatus = new StudentStatus();
            PersonalInfo personalInfo = new PersonalInfo();
            OriginalEducationInfo originalEducationInfo = new OriginalEducationInfo();
            GraduationInfo graduationInfo = new GraduationInfo();

            // 请根据实际的字段名和数据类型调整以下代码
            studentStatus.setStudentNumber(studentData.get("XH"));
            studentStatus.setGrade(studentData.get("NJ"));
            studentStatus.setCollege(studentData.get("XSH"));

            // 教学点 BH，去掉末尾的数字
            String teachingPoint = studentData.get("BH");
            teachingPoint = teachingPoint.replaceAll("\\d+$", "");
            if(!jxd_jc.containsKey(teachingPoint)){
                log.error(teachingPoint + " 没有在集合内");
                teachingPoint = teachingPoint;
            }
            else{
                teachingPoint = jxd_jc.get(teachingPoint);
            }

            studentStatus.setTeachingPoint(teachingPoint);

            studentStatus.setMajorName(studentData.get("ZYMC"));
            studentStatus.setStudyForm(studentData.get("XXXS"));
            studentStatus.setLevel(studentData.get("CC"));
            studentStatus.setStudyDuration(studentData.get("XZ"));
            studentStatus.setAdmissionNumber(studentData.get("KSH"));
            studentStatus.setAcademicStatus(studentData.get("ZT"));

            String enrollDateString = studentData.get("RXRQ");
            Date enrollDate = null;
            enrollDate = dateFormat5.parse(enrollDateString);
            studentStatus.setEnrollmentDate(enrollDate);

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

            // 根据考生号来获取新生数据中的个人信息
            String ksh = studentData.get("KSH");
            QueryWrapper<AdmissionInformation> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("admission_number", ksh);
            AdmissionInformation student = admissionInformationMapper.selectOne(queryWrapper);

            personalInfo.setGender(student.getGender());
            personalInfo.setBirthDate(student.getBirthDate());
            personalInfo.setPoliticalStatus(student.getPoliticalStatus());
            if(studentData.get("MZ").equals(student.getEthnicity())){
                throw new Exception("民族信息与新生数据中不同 " + ksh);
            }
            personalInfo.setEthnicity(studentData.get("MZ"));
            personalInfo.setIdType(identifyID(studentData.get("SFZH")));
            personalInfo.setIdNumber(studentData.get("SFZH"));
            personalInfo.setPostalCode(student.getPostalCode());
            personalInfo.setPhoneNumber(student.getPhoneNumber());
            personalInfo.setAddress(student.getAddress());
            personalInfo.setEntrancePhoto(studentData.get("RXPIC"));
            personalInfo.setGrade(studentData.get("NJ"));


            originalEducationInfo.setGrade(studentData.get("NJ"));
            originalEducationInfo.setIdNumber(studentData.get("SFZH"));
            originalEducationInfo.setGraduationSchool(student.getGraduationSchool());
            originalEducationInfo.setOriginalEducation(student.getOriginalEducation());
            originalEducationInfo.setGraduationDate(student.getGraduationDate());

            graduationInfo.setGrade(studentData.get("NJ"));
            graduationInfo.setIdNumber(studentData.get("SFZH"));
            graduationInfo.setStudentNumber(studentData.get("XH"));
            graduationInfo.setGraduationNumber(studentData.get("BYZH"));


            String graduateDateString = studentData.get("BYRQ");
            Date graduateDate = null;
            graduateDate = dateFormat5.parse(graduateDateString);
            graduationInfo.setGraduationDate(graduateDate);

            graduationInfo.setGraduationPhoto(studentData.get("BYPIC"));

            studentStatusMapper.insert(studentStatus);
            personalInfoMapper.insert(personalInfo);
            originalEducationInfoMapper.insert(originalEducationInfo);
            graduationInfoMapper.insert(graduationInfo);
        }
    }


}
