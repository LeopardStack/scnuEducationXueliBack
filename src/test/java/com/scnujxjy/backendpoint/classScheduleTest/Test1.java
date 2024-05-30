package com.scnujxjy.backendpoint.classScheduleTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.admission_information.MajorInformationPO;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.admission_information.ClassScheduleService;
import com.scnujxjy.backendpoint.service.admission_information.MajorInformationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 开班计划审核初始数据生成
 * 即根据某一年级的录取数据 生成开班审核数据
 */
@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private AdmissionInformationService admissionInformationService;

    @Resource
    private ClassScheduleService classScheduleService;

    @Resource
    private MajorInformationService majorInformationService;

    @Test
    public void test1(){
        String grade = "2024";
        List<AdmissionInformationPO> admissionInformationPOS = admissionInformationService.getBaseMapper().selectList(new LambdaQueryWrapper<AdmissionInformationPO>()
                .eq(AdmissionInformationPO::getGrade, grade));
        Set<String> majorCodeSet = admissionInformationPOS.stream().map(AdmissionInformationPO::getMajorCode).collect(Collectors.toSet());
        Set<String> gradeSet = admissionInformationPOS.stream().map(AdmissionInformationPO::getGrade).collect(Collectors.toSet());
        List<MajorInformationPO> majorInformationPOS = majorInformationService.getBaseMapper().selectList(Wrappers.<MajorInformationPO>lambdaQuery()
                .in(MajorInformationPO::getAdmissionMajorCode, majorCodeSet)
                .in(MajorInformationPO::getGrade, gradeSet));
        Map<String, List<MajorInformationPO>> majorCodeGrade2MajorInformationMap = majorInformationPOS.stream()
                .peek(ele -> ele.setAdmissionMajorCode(ele.getGrade() + ele.getAdmissionMajorCode()))
                .collect(Collectors.groupingBy(MajorInformationPO::getAdmissionMajorCode));
        boolean ident = true;
        for(AdmissionInformationPO admissionInformationPO : admissionInformationPOS){
            String majorCode = admissionInformationPO.getMajorCode();
//            MajorInformationPO majorInformationPO = majorInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<MajorInformationPO>()
//                    .eq(MajorInformationPO::getAdmissionMajorCode, majorCode)
//                    .eq(MajorInformationPO::getGrade, grade)
//            );
            if (!majorCodeGrade2MajorInformationMap.containsKey(grade + majorCode)) {
                ident = false;
                log.info(admissionInformationPO + "\n 该学生专业代码找不到专业信息");
            }
//            if(majorInformationPO == null){
//                ident = false;
//                log.info(admissionInformationPO + "\n 该学生专业代码找不到专业信息");
//            }
        }
        if(ident){
            log.info("没有一个学生的专业代码是找不到专业信息的");
        }

    }
}
