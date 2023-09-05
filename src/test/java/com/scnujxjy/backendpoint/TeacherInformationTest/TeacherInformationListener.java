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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class TeacherInformationListener extends AnalysisEventListener<TeacherInformationExcelImportVO> {

    private TeacherInformationMapper teacherInformationMapper;

    private int dataCount = 0; // 添加一个计数变量

    // 记录导入失败的数据
    private List<TeacherInformationErrorRecord> errorRecords = new ArrayList<>();


    public TeacherInformationListener(TeacherInformationMapper teacherInformationMapper) {
        this.teacherInformationMapper = teacherInformationMapper;
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
            try {
                return format.parse(birthDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;  // 如果格式不匹配，返回null或者你可以选择抛出异常
    }

    public static TeacherInformationPO from(TeacherInformationExcelImportVO vo) {
        TeacherInformationListener listener = new TeacherInformationListener(null); // 这里为了使用convertBirthDate方法，我们实例化了一个Listener，但不需要mapper
        Date birthDate = null;
        if(vo.getBirthDate() != null){
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


    @Override
    public void invoke(TeacherInformationExcelImportVO data, AnalysisContext context) {
        try {
            TeacherInformationPO teacherInformationPO = from(data);
            String errorMsg = checkDuplicate(data);
            if (errorMsg != null) {
                log.error("插入数据失败 " + data.toString() + "\n" + errorMsg);
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
                        .errorDescription(errorMsg)
                        .build());

            } else {
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
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("总共读入了 " + dataCount + " 条数据");

        if (!errorRecords.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/teacherInformation";
            String errorFileName = currentDateTime + "_errorImportTeacherInformation.xlsx";
            EasyExcel.write(relativePath + "/" + errorFileName,
                    TeacherInformationErrorRecord.class).sheet("ErrorRecords").doWrite(errorRecords);
        }
    }
}

