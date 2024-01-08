package com.scnujxjy.backendpoint.classInformationImport;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.scnujxjy.backendpoint.NewStudentImport.AdmissionInformationListener;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.MajorInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationConfirmRO;
import com.scnujxjy.backendpoint.util.excelListener.ClassInformationListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private MajorInformationMapper majorInformationMapper;

    @Resource
    private AdmissionInformationMapper admissionInformationMapper;

    @Resource
    private CollegeInformationMapper collegeInformationMapper;

    @Resource
    private TeachingPointInformationMapper teachingPointInformationMapper;


    @Test
    public void test1(){
        String fileName = "D:\\ScnuWork\\xueli\\xueliBackEnd\\src\\main\\resources\\data\\开班分班\\2023年成教开班情况统计表（2024级）.xlsx";
        int headRowNumber = 2;  // 根据你的 Excel 调整这个值
        // 使用ExcelReaderBuilder注册自定义的日期转换器
        ClassInformationListener classInformationListener = new ClassInformationListener(admissionInformationMapper,
                majorInformationMapper, collegeInformationMapper, teachingPointInformationMapper);

        ExcelReaderBuilder readerBuilder = EasyExcel.read(fileName, ClassInformationConfirmRO.class,classInformationListener);

        // 继续你的读取操作
        readerBuilder.sheet().headRowNumber(headRowNumber).doRead();
    }
}
