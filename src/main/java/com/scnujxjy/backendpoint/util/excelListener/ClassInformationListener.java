package com.scnujxjy.backendpoint.util.excelListener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.admission_information.MajorInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.MajorInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationConfirmRO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
public class ClassInformationListener extends AnalysisEventListener<ClassInformationConfirmRO> {
    private AdmissionInformationMapper admissionInformationMapper;

    private MajorInformationMapper majorInformationMapper;

    private CollegeInformationMapper collegeInformationMapper;

    private TeachingPointInformationMapper teachingPointInformationMapper;

    public ClassInformationListener(AdmissionInformationMapper admissionInformationMapper,
                                    MajorInformationMapper majorInformationMapper,
                                    CollegeInformationMapper collegeInformationMapper,
                                    TeachingPointInformationMapper teachingPointInformationMapper){
        this.admissionInformationMapper = admissionInformationMapper;
        this.majorInformationMapper = majorInformationMapper;
        this.collegeInformationMapper = collegeInformationMapper;
        this.teachingPointInformationMapper = teachingPointInformationMapper;
    }

    @Override
    public void invoke(ClassInformationConfirmRO classInformationConfirmRO, AnalysisContext analysisContext) {
//        log.info("接收到一条开班信息 " + classInformationConfirmRO);

        try {
            CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                    .eq(CollegeInformationPO::getCollegeName, classInformationConfirmRO.getCollege()));
            if (collegeInformationPO == null) {
                throw new IllegalArgumentException("找不到该学院信息 ");
            }

            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                    .eq(TeachingPointInformationPO::getAlias, classInformationConfirmRO.getTeachingPointNameAlias()));
            if (teachingPointInformationPO == null) {
                if(classInformationConfirmRO.getTeachingPointName().equals(classInformationConfirmRO.getCollege())){
                    // 这是校内教学点 也就是校内班
                    teachingPointInformationPO = teachingPointInformationMapper.selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                            .eq(TeachingPointInformationPO::getTeachingPointName, classInformationConfirmRO.getTeachingPointName()));
                    if(teachingPointInformationPO == null){
                        throw new IllegalArgumentException("找不到该校内教学点信息 ");
                    }
                }else{
                    throw new IllegalArgumentException("找不到该教学点信息 ");
                }
            }

            MajorInformationPO majorInformationPO = majorInformationMapper.selectOne(new LambdaQueryWrapper<MajorInformationPO>()
                    .eq(MajorInformationPO::getGrade, "2024")
                    .eq(MajorInformationPO::getCollegeId, collegeInformationPO.getCollegeId())
                    .eq(MajorInformationPO::getTeachingPointId, teachingPointInformationPO.getTeachingPointId())
                    .eq(MajorInformationPO::getMajorName, classInformationConfirmRO.getMajorName())
                    .eq(MajorInformationPO::getLevel, classInformationConfirmRO.getLevel())
                    .eq(MajorInformationPO::getStudyForm, classInformationConfirmRO.getStudyForm())
            );

//            log.info("找到了该专业信息 " + majorInformationPO);
        }catch (Exception e){
            log.error("查找专业信息失败 " + e + "\n" + classInformationConfirmRO);
        }


    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
