package com.scnujxjy.backendpoint.basicTest;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.basic.CourseMetadataPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.CourseMetadataMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@NoArgsConstructor
public class CourseMetaDataListener  extends AnalysisEventListener<CourseMetaDataRO> {

    private CourseMetadataMapper courseMetadataMapper;

    public CourseMetaDataListener(CourseMetadataMapper courseMetadataMapper) {
        this.courseMetadataMapper = courseMetadataMapper;
    }

    @Override
    public void invoke(CourseMetaDataRO courseMetaDataRO, AnalysisContext analysisContext) {
        log.info("拿到一条数据" + courseMetaDataRO);


        // 去重首尾空格
        courseMetaDataRO.setCourseCode(courseMetaDataRO.getCourseCode().trim());
        courseMetaDataRO.setCourseName(courseMetaDataRO.getCourseName().trim());

        CourseMetadataPO courseMetadataPO = new CourseMetadataPO()
                .setCourseCode(courseMetaDataRO.getCourseCode())
                .setCourseName(courseMetaDataRO.getCourseName())
                ;

        // 去重
        CourseMetadataPO courseMetadataPO1 = courseMetadataMapper.selectOne(new LambdaQueryWrapper<CourseMetadataPO>()
                .eq(CourseMetadataPO::getCourseCode, courseMetaDataRO.getCourseCode())
                .eq(CourseMetadataPO::getCourseName, courseMetaDataRO.getCourseName())
        );
        if(courseMetadataPO1 != null){
            log.error("重复记录\n" + courseMetadataPO);
        }else{
            int insert = courseMetadataMapper.insert(courseMetadataPO);
            if(insert <= 0){
                log.error("插入失败，数据库插入失败 " + insert + "\n" + courseMetadataPO);
            }
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
