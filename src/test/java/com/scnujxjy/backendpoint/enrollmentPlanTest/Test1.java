package com.scnujxjy.backendpoint.enrollmentPlanTest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.basicTest.CourseMetaDataListener;
import com.scnujxjy.backendpoint.basicTest.CourseMetaDataRO;
import com.scnujxjy.backendpoint.dao.entity.admission_information.EnrollmentPlanPO;
import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.service.admission_information.EnrollmentPlanService;
import com.scnujxjy.backendpoint.service.basic.GlobalConfigService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private EnrollmentPlanService enrollmentPlanService;

    @Resource
    private GlobalConfigService globalConfigService;

    @Resource
    private TeachingPointInformationMapper teachingPointInformationMapper;

    @Test
    public void test1(){
        GlobalConfigPO globalConfigPO = globalConfigService.
                getGlobalConfigInfo("招生计划申报");
        log.info("招生计划申报配置 \n" + globalConfigPO);

        EnrollmentPlanPO enrollmentPlanPO = new EnrollmentPlanPO()
                .setYear(2024)
                .setMajorName("学前教育")
                .setStudyForm("函授")
                .setEducationLength("3")
                .setTrainingLevel("高起专")
                .setEnrollmentNumber(100)
                .setTargetStudents("dafsadf")
                .setEnrollmentRegion("广州达德")
                .setSchoolLocation("广州市")
                .setContactNumber("9521233434")
                .setCollege("计算机学院")
                .setTeachingLocation("广州达德教学点")
                .setEnrollmentSubject("文史类")
                .setTuition(BigDecimal.valueOf(3000.0))
                ;
        enrollmentPlanService.getBaseMapper().insert(enrollmentPlanPO);
    }

    /**
     * 使用 easyExcel 读取最新的教学点信息
     */
    @Test
    public  void test2(){

        String fileName = "D:\\ScnuWork\\xueli\\xueliBackEnd\\src\\main\\resources\\data\\教学点资格数据\\华南师范大学2024年高等学历继续教育校外教学点一览表(0424)(1).xlsx";
        int headRowNumber = 2;  // 根据你的 Excel 调整这个值
        // 使用ExcelReaderBuilder注册自定义的日期转换器
        TeachingPointInformationListener teachingPointInformationListener = new TeachingPointInformationListener(teachingPointInformationMapper);

        ExcelReaderBuilder readerBuilder = EasyExcel.read(fileName, TeachingPointDataRO.class,teachingPointInformationListener);

        // 继续你的读取操作
        readerBuilder.sheet().headRowNumber(headRowNumber).doRead();
    }
}
