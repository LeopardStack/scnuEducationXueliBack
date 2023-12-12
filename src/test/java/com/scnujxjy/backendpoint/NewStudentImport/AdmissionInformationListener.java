package com.scnujxjy.backendpoint.NewStudentImport;

import cn.hutool.crypto.digest.SM3;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.CourseScheduleTest.CourseScheduleListener;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.vo.admission_information.AdmissionInformationExcelOutputVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationExcelOutputVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelImportVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelOutputVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdmissionInformationListener  extends AnalysisEventListener<AdmissionInformationRO> {
    private final SM3 sm3 = new SM3();

    private AdmissionInformationMapper admissionInformationMapper;

    private PersonalInfoMapper personalInfoMapper;

    private PlatformUserMapper platformUserMapper;

    private String collegeName;

    private List<AdmissionInformationExcelOutputVO> outputDataList = new ArrayList<>();

    public AdmissionInformationListener(AdmissionInformationMapper admissionInformationMapper,
                                        PersonalInfoMapper personalInfoMapper,
                                        PlatformUserMapper platformUserMapper,
                                     String collegeName){
        this.admissionInformationMapper = admissionInformationMapper;
        this.personalInfoMapper = personalInfoMapper;
        this.platformUserMapper = platformUserMapper;
        this.collegeName = collegeName;
    }

    private static final SimpleDateFormat[] DATE_FORMATS = {
            new SimpleDateFormat("yyyy/M/d"),
            new SimpleDateFormat("yyyy/M/dd"),
            new SimpleDateFormat("yyyy/MM/d"),
            new SimpleDateFormat("yyyy/MM/dd")
            // 可以根据需要添加其他日期格式
    };

    private Date parseDateStringToDate(String dateString) {
        for (SimpleDateFormat dateFormat : DATE_FORMATS) {
            try {
                // 尝试使用当前格式解析日期
                Date parsedDate = dateFormat.parse(dateString);
                if (parsedDate != null) {
                    return parsedDate;
                }
            } catch (ParseException ignored) {
                // 解析失败，尝试下一个格式
            }
        }
        return null; // 如果所有格式都无法解析，返回null或者处理默认值
    }

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

    @Override
    public void invoke(AdmissionInformationRO admissionInformationRO, AnalysisContext analysisContext) {

        AdmissionInformationExcelOutputVO outputData = new AdmissionInformationExcelOutputVO();
        admissionInformationRO.setGrade(admissionInformationRO.getGrade().replace("级", ""));

        // 处理照片统一为 考生号
        admissionInformationRO.setEntrancePhotoUrl("./xuelistudentpictures/2024/import/" + admissionInformationRO.getAdmissionNumber() + ".JPG");

        BeanUtils.copyProperties(admissionInformationRO, outputData);
//        log.info(admissionInformationRO.toString());
        try {
            AdmissionInformationPO admissionInformationPO = new AdmissionInformationPO();
            BeanUtils.copyProperties(admissionInformationRO, admissionInformationPO);
            Date date1 = parseDateStringToDate(admissionInformationRO.getGraduationDate());
            if(date1 != null) {
                admissionInformationPO.setGraduationDate(date1);
            }else{
                throw new IllegalArgumentException("该学生毕业日期解析失败 ");
            }
            Date date2 = parseDateStringToDate(admissionInformationRO.getBirthDate());
            if(date2 != null) {
                admissionInformationPO.setBirthDate(date2);
            }else{
                throw new IllegalArgumentException("该学生出生日期解析失败 ");
            }

            PersonalInfoPO personalInfoPO = new PersonalInfoPO();
            personalInfoPO.setGender(admissionInformationPO.getGender());
            personalInfoPO.setGrade(admissionInformationPO.getGrade());
            personalInfoPO.setName(admissionInformationPO.getName());
            personalInfoPO.setEthnicity(admissionInformationPO.getEthnicity());
            personalInfoPO.setBirthDate(admissionInformationPO.getBirthDate());
            personalInfoPO.setIdNumber(admissionInformationPO.getIdCardNumber());
            personalInfoPO.setIdType(identifyID(admissionInformationPO.getIdCardNumber()));
            personalInfoPO.setAddress(admissionInformationPO.getAddress());
            personalInfoPO.setPhoneNumber(admissionInformationPO.getPhoneNumber());
            personalInfoPO.setEntrancePhoto(admissionInformationPO.getEntrancePhotoUrl());
            personalInfoPO.setPostalCode(admissionInformationPO.getPostalCode());
            personalInfoPO.setPoliticalStatus(admissionInformationPO.getPoliticalStatus());

            PlatformUserPO platformUserPO = new PlatformUserPO();
            platformUserPO.setUsername(admissionInformationPO.getIdCardNumber());
            platformUserPO.setName(admissionInformationPO.getName());
            platformUserPO.setRoleId(1L);
            // 密码加密
            String userName = admissionInformationPO.getIdCardNumber();
            String encryptedPassword = sm3.digestHex(userName.substring(userName.length() - 6));
            platformUserPO.setPassword(encryptedPassword);

            // 判断是否存在 然后一并插入

            AdmissionInformationPO admissionInformationPO1 = admissionInformationMapper.selectOne(new LambdaQueryWrapper<AdmissionInformationPO>()
                    .eq(AdmissionInformationPO::getIdCardNumber, admissionInformationPO.getIdCardNumber())
                    .eq(AdmissionInformationPO::getGrade, admissionInformationPO.getGrade())
            );
            if(admissionInformationPO1 == null){
                int insert = admissionInformationMapper.insert(admissionInformationPO);
                if(insert <= 0){
                    throw new IllegalArgumentException("插入新生录取信息失败");
                }else{
                    log.info("插入新生  " + admissionInformationPO.getIdCardNumber() + " 姓名为 " + admissionInformationPO.getName());
                }
                PersonalInfoPO personalInfoPO1 = personalInfoMapper.selectOne(new LambdaQueryWrapper<PersonalInfoPO>()
                        .eq(PersonalInfoPO::getIdNumber, admissionInformationPO.getIdCardNumber())
                        .eq(PersonalInfoPO::getGrade, admissionInformationPO.getGrade())
                );
                if(personalInfoPO1 == null){
                    int insert1 = personalInfoMapper.insert(personalInfoPO);
                    if(insert1 <= 0){
                        throw new IllegalArgumentException("插入个人信息失败");
                    }else{
                        log.info("插入新生个人信息  " + admissionInformationPO.getIdCardNumber() + " 姓名为 " + admissionInformationPO.getName());
                    }
                }

                PlatformUserPO platformUserPO1 = platformUserMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                        .eq(PlatformUserPO::getUsername, admissionInformationPO.getIdCardNumber())
                );
                if(platformUserPO1 == null){
                    int insert1 = platformUserMapper.insert(platformUserPO);
                    if(insert1 <= 0){
                        log.info("该学生已经在平台存在账号");
                    }else{
                        log.info("插入新生账号信息  " + admissionInformationPO.getIdCardNumber() + " 姓名为 " + admissionInformationPO.getName());
                    }
                }

            }

        }catch (Exception e){
            outputData.setErrorMessage(e.getMessage()); // 设置错误信息
            outputDataList.add(outputData); // 将输出数据添加到列表中
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 使用EasyExcel写入数据到新的Excel文件中
        if(outputDataList.size() > 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Asia/Shanghai"));

            String currentDateTime = LocalDateTime.now().format(formatter);
            String relativePath = "data_import_error_excel/admissionData";
            String errorFileName = collegeName + "_" + currentDateTime + "_新生数据导入失败数据.xlsx";
            // 检查目录是否存在，如果不存在则创建它
            File directory = new File(relativePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 写入Excel文件
            EasyExcel.write(relativePath + "/" + errorFileName, AdmissionInformationExcelOutputVO.class)
                    .sheet("Sheet1").doWrite(outputDataList);

            log.info(collegeName + " 新生数据存在错误记录，已写入 " + errorFileName);
        }else{
            log.info(collegeName + " 新生数据没有任何错误");
        }
    }
}
