package com.scnujxjy.backendpoint.oldSysDataExport;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.GraduationInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.OriginalEducationInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.GraduationInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.OriginalEducationInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    /**
     * 根据身份证号码判别港澳台身份证
     * @param id
     * @return
     */
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
     * 根据准考证号码来判别年份
     * @param code
     * @return
     */
    public static int getYearFromCode(String code) {
        if (code == null || code.length() < 2) {
            throw new IllegalArgumentException("Code must have at least 2 characters.");
        }

        int threshold = 50;  // 假设从 50 开始是 21 世纪
        int prefix = Integer.parseInt(code.substring(0, 2));

        if (prefix < threshold) {
            return 2000 + prefix;
        } else {
            return 1900 + prefix;
        }
    }

    public void insertXHStudents(Set<String> undefinedJxd, Map<String, String> jxd_jc, int grade,
                                 List<HashMap<String, String>> failedStudents) {
        ArrayList<HashMap<String, String>> studentInfos = getStudentInfos(String.valueOf(grade));
        log.info(String.valueOf(studentInfos.size()));

        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy/MM");
        SimpleDateFormat dateFormat5 = new SimpleDateFormat("yyyy.MM");
        SimpleDateFormat dateFormat6 = new SimpleDateFormat("yyyy.MM.dd");

        dateFormat4.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateFormat5.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateFormat6.setTimeZone(TimeZone.getTimeZone("GMT"));

        for (HashMap<String, String> studentData : studentInfos) {
            if(studentData.get("XXXS").contains("文凭")){
                // 澳门培训的文凭班学生直接跳过
                continue;
            }
            try {
                StudentStatusPO studentStatusPO = new StudentStatusPO();
                PersonalInfoPO personalInfoPO = new PersonalInfoPO();
                OriginalEducationInfoPO originalEducationInfoPO = new OriginalEducationInfoPO();
                GraduationInfoPO graduationInfoPO = new GraduationInfoPO();

                // 请根据实际的字段名和数据类型调整以下代码
                studentStatusPO.setStudentNumber(studentData.get("XH"));
                studentStatusPO.setGrade(studentData.get("NJ"));
                studentStatusPO.setCollege(studentData.get("XSH"));

                // 教学点 BH，去掉末尾的数字
                String teachingPoint = studentData.get("BH");
                teachingPoint = teachingPoint.replaceAll("\\d+$", "");
                if (!jxd_jc.containsKey(teachingPoint)) {
//                log.error(teachingPoint + " 没有在集合内");
                    undefinedJxd.add(teachingPoint);
                    if (teachingPoint.contains("校内")) {
                        teachingPoint = teachingPoint;
                    } else {
                        teachingPoint = teachingPoint + "教学点";
                    }

                } else {
                    teachingPoint = jxd_jc.get(teachingPoint);
                }

                studentStatusPO.setTeachingPoint(teachingPoint);

                studentStatusPO.setMajorName(studentData.get("ZYMC"));
                studentStatusPO.setStudyForm(studentData.get("XXXS"));
                studentStatusPO.setLevel(studentData.get("CC"));
                studentStatusPO.setStudyDuration(studentData.get("XZ"));
                studentStatusPO.setAdmissionNumber(studentData.get("KSH"));
                studentStatusPO.setAcademicStatus(studentData.get("ZT"));

                String enrollDateString = studentData.get("RXRQ");
                Date enrollDate = null;
                enrollDate = dateFormat5.parse(enrollDateString);
                studentStatusPO.setEnrollmentDate(enrollDate);

                personalInfoPO.setGender(studentData.get("XB"));

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
                            try{
                                birthDate = dateFormat6.parse(birthDateString);
                            }catch (ParseException e4){
                                studentData.put("插入失败原因", "出生日期解析失败 " +e3.getMessage());
                                failedStudents.add(studentData);
                                continue;
//                            log.error(birthDateString);
//                            log.error(e3.getMessage());
                            }
                        }
                    }
                }
                if (birthDate != null) {
                    personalInfoPO.setBirthDate(birthDate);
                }

                // 根据考生号来获取新生数据中的个人信息
                String ksh = studentData.get("KSH");
                AdmissionInformationPO student = null;

                QueryWrapper<AdmissionInformationPO> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("admission_number", ksh);
                List<AdmissionInformationPO> admissionInformationPOS = admissionInformationMapper.selectList(queryWrapper);
                if(admissionInformationPOS.size() == 1){
                    student = admissionInformationMapper.selectOne(queryWrapper);
                }else if(admissionInformationPOS.size() > 1){
                    for(int i = 0; i < admissionInformationPOS.size(); i++){
                        AdmissionInformationPO admissionInformationPO = admissionInformationPOS.get(i);
                        String grade1 = admissionInformationPO.getGrade();
                        if(grade1.equals(getYearFromCode(ksh)+"")){
                            student = admissionInformationPO;
                            break;
                        }
                    }
                }
                if(student == null){
                    studentData.put("插入失败原因", "从录取表中获取不到学生的个人信息 ");
                    failedStudents.add(studentData);
                    continue;
                }

                personalInfoPO.setGender(student.getGender());
                personalInfoPO.setBirthDate(student.getBirthDate());
                personalInfoPO.setPoliticalStatus(student.getPoliticalStatus());
                if (!studentData.get("MZ").equals(student.getEthnicity())) {
                    studentData.put("插入失败原因", "民族信息与新生数据中不同 ");
                    failedStudents.add(studentData);
//                    log.error("民族信息与新生数据中不同 " + ksh);
                }
                personalInfoPO.setEthnicity(studentData.get("MZ"));
                personalInfoPO.setIdType(identifyID(studentData.get("SFZH")));
                personalInfoPO.setIdNumber(studentData.get("SFZH"));
                personalInfoPO.setPostalCode(student.getPostalCode());
                personalInfoPO.setPhoneNumber(student.getPhoneNumber());
                personalInfoPO.setAddress(student.getAddress());
                personalInfoPO.setEntrancePhoto(studentData.get("RXPIC"));
                personalInfoPO.setGrade(studentData.get("NJ"));


                originalEducationInfoPO.setGrade(studentData.get("NJ"));
                originalEducationInfoPO.setIdNumber(studentData.get("SFZH"));
                originalEducationInfoPO.setGraduationSchool(student.getGraduationSchool());
                originalEducationInfoPO.setOriginalEducation(student.getOriginalEducation());
                originalEducationInfoPO.setGraduationDate(student.getGraduationDate());

                graduationInfoPO.setGrade(studentData.get("NJ"));
                graduationInfoPO.setIdNumber(studentData.get("SFZH"));
                graduationInfoPO.setStudentNumber(studentData.get("XH"));
                graduationInfoPO.setGraduationNumber(studentData.get("BYZH"));


                String graduateDateString = studentData.get("BYRQ");
                if(graduateDateString != null && !graduateDateString.equals("NULL")){
                    Date graduateDate = null;
                    graduateDate = dateFormat5.parse(graduateDateString);
                    graduationInfoPO.setGraduationDate(graduateDate);

                    graduationInfoPO.setGraduationPhoto(studentData.get("BYPIC"));
                    graduationInfoMapper.insert(graduationInfoPO);
                }

                studentStatusMapper.insert(studentStatusPO);
                personalInfoMapper.insert(personalInfoPO);
                originalEducationInfoMapper.insert(originalEducationInfoPO);
            }catch (ParseException p){
                studentData.put("插入失败原因", "其他日期解析失败 " +p.getMessage());
                failedStudents.add(studentData);
                continue;
//                log.error("日期解析失败 " + studentData.get("KSH"));
            }catch (Exception e){
                studentData.put("插入失败原因", "插入学生数据失败 " +e.getMessage());
                failedStudents.add(studentData);
                continue;
//                log.error("插入学生数据失败 " + studentData.get("KSH"));
            }
        }
    }

    public static void writeToExcel(List<HashMap<String, String>> failedStudents, String filePath) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Failed Students");

        // Creating header
        Row header = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue("考生号");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue("插入失败的原因"); // 根据你的数据调整
        headerCell.setCellStyle(headerStyle);

        // ... 为每一个字段添加header

        // Writing data
        for (int i = 0; i < failedStudents.size(); i++) {
            Row row = sheet.createRow(i + 1);
            HashMap<String, String> student = failedStudents.get(i);

            row.createCell(0).setCellValue(student.get("KSH"));
            row.createCell(1).setCellValue(student.get("插入失败原因"));
            // ... 添加更多的学生信息到每个单元格中
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
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

//        Set<String> undefinedJxd = new HashSet<>();
//
//        for(int i = 2023; i >=2020; i--){
//            List<HashMap<String, String>> failedStudents = new ArrayList<>();
//            insertXHStudents(undefinedJxd, jxd_jc, i, failedStudents);
//            writeToExcel(failedStudents, i + " 数据插入失败的学生学籍数据.xlsx");
//        }
        Set<String> undefinedJxd = Collections.synchronizedSet(new HashSet<>());

        int startYear = 1995;
        int endYear = 1999;

        ExecutorService executorService = Executors.newFixedThreadPool(endYear - startYear + 1);
        List<Future<Void>> futures = new ArrayList<>();

        for (int i = endYear; i >= startYear; i--) {
            final int year = i;
            futures.add(executorService.submit(() -> {
                List<HashMap<String, String>> failedStudents = Collections.synchronizedList(new ArrayList<>());
                insertXHStudents(undefinedJxd, jxd_jc, year, failedStudents);
                writeToExcel(failedStudents, year + " 数据插入失败的学生学籍数据.xlsx");
                return null;
            }));
        }

        // 等待所有任务完成
        for (Future<Void> future : futures) {
            future.get();
        }

        executorService.shutdown();
    log.info("未定义的教学点包括 " + undefinedJxd.toString());
    }
}
