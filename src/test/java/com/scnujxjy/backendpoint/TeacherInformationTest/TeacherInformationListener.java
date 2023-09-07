package com.scnujxjy.backendpoint.TeacherInformationTest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationExcelImportVO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationVO;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
public class TeacherInformationListener extends AnalysisEventListener<TeacherInformationExcelImportVO> {

    private TeacherInformationMapper teacherInformationMapper;

    private int dataCount = 0; // 添加一个计数变量

    // 记录导入失败的数据
    private List<TeacherInformationErrorRecord> errorRecords = new ArrayList<>();

    // 添加一个列表来存储所有读取的教师信息
    private List<TeacherInformationExcelImportVO> allTeachers = new ArrayList<>();



    public TeacherInformationListener(TeacherInformationMapper teacherInformationMapper) {
        this.teacherInformationMapper = teacherInformationMapper;
    }

    public int getDataCount() {
        return dataCount;
    }


    public Date convertBirthDate(String birthDateStr) {
        SimpleDateFormat format;

        // 根据字符串长度判断日期格式
        if (birthDateStr.endsWith("年")) {
            if (birthDateStr.contains("月")) {
                format = new SimpleDateFormat("yyyy年MM月");
            } else {
                format = new SimpleDateFormat("yyyy年");
            }
        } else if (birthDateStr.contains(".")) {
            format = new SimpleDateFormat("yyyy.MM");
        } else if (birthDateStr.matches("\\d{4}")) {  // 判断是否为4位数字
            format = new SimpleDateFormat("yyyy");
        } else {
            return null;  // 如果格式不匹配，返回null或者你可以选择抛出异常
        }

        try {
            return format.parse(birthDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取身份证号码中的出生年月信息
     * @param idCardNumber 身份证号码
     * @return
     */
    public Date convertBirthDateFromIdCard(String idCardNumber) {
        if (idCardNumber == null) {
            return null;
        }
        String birthDateStr;
        SimpleDateFormat format;
        if (idCardNumber.length() == 15) {
            birthDateStr = "19" + idCardNumber.substring(6, 12); // 对于15位身份证，需要加上"19"作为世纪
            format = new SimpleDateFormat("yyyyMMdd");
        } else if (idCardNumber.length() == 18) {
            birthDateStr = idCardNumber.substring(6, 14);
            format = new SimpleDateFormat("yyyyMMdd");
        } else {
            return null;
        }
        try {
            return format.parse(birthDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static TeacherInformationPO from(TeacherInformationExcelImportVO vo) {
        TeacherInformationListener listener = new TeacherInformationListener(null);
        Date birthDate = null;
        if(vo.getIdCardNumber() != null && !vo.getIdCardNumber().isEmpty()){
            birthDate = listener.convertBirthDateFromIdCard(vo.getIdCardNumber());
        }
        if (birthDate == null && vo.getBirthDate() != null) {
            birthDate = listener.convertBirthDate(vo.getBirthDate());
        }


        return TeacherInformationPO.builder()
                .userId(vo.getUserId())
                .name(vo.getName())
                .gender(vo.getGender())
                .birthDate(birthDate)
                .politicalStatus(vo.getPoliticalStatus())
                .education(vo.getEducation())
                .degree(vo.getDegree())
                .professionalTitle(vo.getProfessionalTitle())
                .titleLevel(vo.getTitleLevel())
                .graduationSchool(vo.getGraduationSchool())
                .currentPosition(vo.getCurrentPosition())
                .collegeId(vo.getCollegeId())
                .teachingPoint(vo.getTeachingPoint())
                .administrativePosition(vo.getAdministrativePosition())
                .workNumber(vo.getWorkNumber())
                .idCardNumber(vo.getIdCardNumber())
                .phone(vo.getPhone())
                .email(vo.getEmail())
                .startTerm(vo.getStartTerm())
                .teacherType1(vo.getTeacherType1())
                // 请注意，我没有为teacherType2提供值，因为VO中没有这个字段，您可能需要进行进一步的处理或提供一个默认值
                .build();
    }


    private String checkDuplicate(TeacherInformationExcelImportVO data) {
        boolean idCardExists = teacherInformationMapper.selectByIdCardNumber(data.getIdCardNumber()).size() > 0;
        boolean workNumberExists = teacherInformationMapper.selectByWorkNumber(data.getWorkNumber()).size() > 0;
        boolean phoneExists = teacherInformationMapper.selectByPhone(data.getPhone()).size() > 0;

        // 如果身份证号码和工号都为空，只检查手机号码
        if (data.getIdCardNumber() == null && data.getWorkNumber() == null) {
            if (phoneExists) {
                return "数据库中存在该教师，手机号码相同";
            }
            return null;
        }

        // 如果身份证号码或工号在数据库中出现，但另一个没有出现，允许插入
        if ((idCardExists && !workNumberExists) || (!idCardExists && workNumberExists)) {
            return null;
        }

        // 如果身份证号码和工号都在数据库中出现，不允许插入
        if (idCardExists && workNumberExists) {
            return "数据库中存在该教师，身份证号码和工号都相同";
        }

        // 如果只有身份证号码在数据库中出现
        if (idCardExists) {
            return "数据库中存在该教师，身份证号码相同";
        }

        // 如果只有工号在数据库中出现
        if (workNumberExists) {
            return "数据库中存在该教师，工号相同";
        }

        return null;
    }

    /**
     * 检查导入的教师是否与数据库中的教师信息存在同名同姓 但是身份证号码、工号、手机号码没有一个能区分的
     * @param data
     * @return
     */
    private boolean isDuplicateTeacher(TeacherInformationExcelImportVO data) {
        List<TeacherInformationPO> sameNameTeachers = teacherInformationMapper.selectByName(data.getName());

        for (TeacherInformationPO teacher : sameNameTeachers) {
            // 如果两者都为null或空，认为它们相同；否则，如果其中一个为null或空，认为它们不同
            boolean sameIdCard = (data.getIdCardNumber() == null || data.getIdCardNumber().isEmpty()) && (teacher.getIdCardNumber() == null || teacher.getIdCardNumber().isEmpty())
                    || Objects.equals(data.getIdCardNumber(), teacher.getIdCardNumber());

            boolean samePhone = (data.getPhone() == null || data.getPhone().isEmpty()) && (teacher.getPhone() == null || teacher.getPhone().isEmpty())
                    || Objects.equals(data.getPhone(), teacher.getPhone());

            boolean sameWorkNumber = (data.getWorkNumber() == null || data.getWorkNumber().isEmpty()) && (teacher.getWorkNumber() == null || teacher.getWorkNumber().isEmpty())
                    || Objects.equals(data.getWorkNumber(), teacher.getWorkNumber());

            if (sameIdCard && samePhone && sameWorkNumber) {
                return true;
            }
        }

        return false;
    }

    private TeacherInformationPO findMatchingTeacher(TeacherInformationExcelImportVO data) {
        List<TeacherInformationPO> sameNameTeachers = teacherInformationMapper.selectByName(data.getName());

        for (TeacherInformationPO teacher : sameNameTeachers) {
            boolean sameIdCard = (data.getIdCardNumber() == null || data.getIdCardNumber().isEmpty()) ?
                    (teacher.getIdCardNumber() == null || teacher.getIdCardNumber().isEmpty()) :
                    Objects.equals(data.getIdCardNumber(), teacher.getIdCardNumber());

            boolean samePhone = (data.getPhone() == null || data.getPhone().isEmpty()) ?
                    (teacher.getPhone() == null || teacher.getPhone().isEmpty()) :
                    Objects.equals(data.getPhone(), teacher.getPhone());

            boolean sameWorkNumber = (data.getWorkNumber() == null || data.getWorkNumber().isEmpty()) ?
                    (teacher.getWorkNumber() == null || teacher.getWorkNumber().isEmpty()) :
                    Objects.equals(data.getWorkNumber(), teacher.getWorkNumber());

            if (sameIdCard && samePhone && sameWorkNumber) {
                return teacher;
            }
        }

        return null;
    }


    @Override
    public void invoke(TeacherInformationExcelImportVO data, AnalysisContext context) {
        try {
            TeacherInformationPO matchingTeacher = findMatchingTeacher(data);
            if (matchingTeacher != null) {
                log.error("导入失败，数据库中存在与此相同或相似的老师: " + data.toString());
                errorRecords.add(TeacherInformationErrorRecord.builder()
                        .userId(data.getUserId())
                        .name(data.getName())
                        .gender(data.getGender())
                        .birthDate(data.getBirthDate())
                        .politicalStatus(data.getPoliticalStatus())
                        .education(data.getEducation())
                        .degree(data.getDegree())
                        .professionalTitle(data.getProfessionalTitle())
                        .titleLevel(data.getTitleLevel())
                        .graduationSchool(data.getGraduationSchool())
                        .currentPosition(data.getCurrentPosition())
                        .collegeId(data.getCollegeId())
                        .teachingPoint(data.getTeachingPoint())
                        .administrativePosition(data.getAdministrativePosition())
                        .workNumber(data.getWorkNumber())
                        .idCardNumber(data.getIdCardNumber())
                        .phone(data.getPhone())
                        .email(data.getEmail())
                        .startTerm(data.getStartTerm())
                        .teacherType1(data.getTeacherType1())
                        .errorDescription("导入失败，数据库中存在与此相同或相似的老师: " + matchingTeacher.toString())
                        .build());
            } else {
                TeacherInformationPO teacherInformationPO = from(data);

                // 插入数据库
                teacherInformationMapper.insert(teacherInformationPO);

                dataCount++;
            }
        } catch (Exception e){
            log.error("插入数据失败 " + data.toString() + "\n" + e.toString());
            errorRecords.add(TeacherInformationErrorRecord.builder()
                    .userId(data.getUserId())
                    .name(data.getName())
                    .gender(data.getGender())
                    .birthDate(data.getBirthDate())
                    .politicalStatus(data.getPoliticalStatus())
                    .education(data.getEducation())
                    .degree(data.getDegree())
                    .professionalTitle(data.getProfessionalTitle())
                    .titleLevel(data.getTitleLevel())
                    .graduationSchool(data.getGraduationSchool())
                    .currentPosition(data.getCurrentPosition())
                    .collegeId(data.getCollegeId())
                    .teachingPoint(data.getTeachingPoint())
                    .administrativePosition(data.getAdministrativePosition())
                    .workNumber(data.getWorkNumber())
                    .idCardNumber(data.getIdCardNumber())
                    .phone(data.getPhone())
                    .email(data.getEmail())
                    .startTerm(data.getStartTerm())
                    .teacherType1(data.getTeacherType1())
                    .errorDescription(e.getMessage())
                    .build());
        }
        // 将每个读取的教师信息添加到列表中
        allTeachers.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("总共读入了 " + dataCount + " 条数据");

        if (!errorRecords.isEmpty()) {
            log.error("存在导入失败的数据 " + errorRecords.size() + " 条");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/teacherInformation";
            String errorFileName = currentDateTime + "_errorImportTeacherInformation.xlsx";
            EasyExcel.write(relativePath + "/" + errorFileName,
                    TeacherInformationErrorRecord.class).sheet("ErrorRecords").doWrite(errorRecords);
        }

        // 检查同名同性的教师
        checkForDuplicateTeachers();
    }

    private void checkForDuplicateTeachers() {
        Map<String, List<TeacherInformationExcelImportVO>> nameGenderMap = new HashMap<>();
        for (TeacherInformationExcelImportVO teacher : allTeachers) {
            String key = teacher.getName() + "_" + teacher.getGender();
            nameGenderMap.computeIfAbsent(key, k -> new ArrayList<>()).add(teacher);
        }

        for (Map.Entry<String, List<TeacherInformationExcelImportVO>> entry : nameGenderMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                log.info("找到同名同性的教师: " + entry.getKey().split("_")[0]);
                for (TeacherInformationExcelImportVO duplicateTeacher : entry.getValue()) {
                    log.info(duplicateTeacher.toString());
                }
            }
        }
    }
}

