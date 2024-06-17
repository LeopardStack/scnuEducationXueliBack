package com.scnujxjy.backendpoint.basicTest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.scnujxjy.backendpoint.NewStudentImport.AdmissionInformationListener;
import com.scnujxjy.backendpoint.dao.mapper.basic.CourseMetadataMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class CourseMetaDataTest {

    @Resource
    private CourseMetadataMapper courseMetadataMapper;

    /**
     * 使用 easyExcel 读取课程元信息 包含课程代码和课程名称
     */
    @Test
    public  void test1(){

        String fileName = "D:\\ScnuWork\\xueliBackEnd\\src\\main\\resources\\data\\学历教育平台课程信息（2024级）(1).xlsx";
        int headRowNumber = 1;  // 根据你的 Excel 调整这个值
        // 使用ExcelReaderBuilder注册自定义的日期转换器
        CourseMetaDataListener courseMetaDataListener = new CourseMetaDataListener(courseMetadataMapper);

        ExcelReaderBuilder readerBuilder = EasyExcel.read(fileName, CourseMetaDataRO.class,courseMetaDataListener);

        // 继续你的读取操作
        readerBuilder.sheet().headRowNumber(headRowNumber).doRead();
    }

}
