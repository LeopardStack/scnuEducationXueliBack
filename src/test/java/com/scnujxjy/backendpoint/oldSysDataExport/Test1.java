package com.scnujxjy.backendpoint.oldSysDataExport;

import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import static com.scnujxjy.backendpoint.util.DataImportScnuOldSys.getStudentLuqus;

@SpringBootTest
@Slf4j
public class Test1 {
    @Autowired(required = false)
    private AdmissionInformationMapper admissionInformationMapper;

    // 用于保存失败的学生数据
    private ArrayList<HashMap<String, String>> failedStudents = new ArrayList<>();


    private void insertLuquStudents(int insertGrade){
        String grade = String.valueOf(insertGrade + 1);
        if(insertGrade == -1){
            grade = "2023";
        }
        ArrayList<HashMap<String, String>> studentLuqus = getStudentLuqus(insertGrade);
        log.info(String.valueOf(studentLuqus.size()));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai"); // 设置为北京时间
        dateFormat1.setTimeZone(timeZone);
        dateFormat.setTimeZone(timeZone);


        for (HashMap<String, String> studentData : studentLuqus) {
            AdmissionInformationPO admissionInformation = new AdmissionInformationPO();

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
            if(graduatedDateString.trim().length() > 0) {
                try {
                    Date graduatedDate = dateFormat.parse(graduatedDateString);
                    admissionInformation.setGraduationDate(graduatedDate);
                } catch (ParseException e) {
                    try {
                        Date graduatedDate = dateFormat1.parse(graduatedDateString);
                        admissionInformation.setGraduationDate(graduatedDate);
                    } catch (Exception e1) {
                        log.error("毕业日期解析失败 " + graduatedDateString);
                        studentData.put("插入失败原因", "毕业日期解析失败 " + e.toString());
                        failedStudents.add(studentData);
                        continue;
                    }
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
                    studentData.put("插入失败原因", "出生日期解析失败 " + e.toString());
                    failedStudents.add(studentData);
                    continue;
                }
            }
            admissionInformation.setAddress(studentData.get("TXDZ"));
            admissionInformation.setPostalCode(studentData.get("YZBM"));
            admissionInformation.setEthnicity(studentData.get("MINZU"));
            admissionInformation.setPoliticalStatus(studentData.get("ZZMM"));
            admissionInformation.setAdmissionNumber(studentData.get("ZKZH"));
            admissionInformation.setShortStudentNumber(studentData.get("KSH"));
            admissionInformation.setGrade(grade);

            try {
                admissionInformationMapper.insert(admissionInformation);
            } catch (Exception e) {
                log.error("插入失败的学生：" + studentData.get("KSH"));
                studentData.put("插入失败原因", "数据库插入失败 " + e.toString());
                failedStudents.add(studentData);
            }
        }
    }

    // 使用Apache POI将失败的学生数据写入Excel
    private void writeFailedStudentsToExcel() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Failed Students");
        int rowNum = 0;

        // 创建表头
        Row header = sheet.createRow(rowNum++);
        String[] headers = {"学号", "姓名", "失败原因"}; // 定义您的表头
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        // 填充数据
        for (HashMap<String, String> student : failedStudents) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(student.get("KSH"));
            row.createCell(1).setCellValue(student.get("XM"));
            row.createCell(1).setCellValue(student.get("插入失败原因"));
            // ... 填充其他字段
        }

        // 写入文件
        try (FileOutputStream outputStream = new FileOutputStream("导入旧系统录取新生数据失败的数据.xlsx")) {
            workbook.write(outputStream);
        } catch (IOException e) {
            log.error("写入Excel失败", e);
        }
    }


    @Test
    public void test1() throws ParseException {
//        for(int i = 2021; i >= 2009; i--){
//            insertLuquStudents(i);
//        }
        insertLuquStudents(-1);
//        writeFailedStudentsToExcel();
    }
}
