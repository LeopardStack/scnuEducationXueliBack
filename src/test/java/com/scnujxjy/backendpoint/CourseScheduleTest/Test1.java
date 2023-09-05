package com.scnujxjy.backendpoint.CourseScheduleTest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelImportVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class Test1 {

    @Autowired(required = false)
    private CourseScheduleMapper courseScheduleMapper;

    /**
     * 将排课表 excel 文件导入数据库，需要检查他们所指定的每一个行政班是否存在，数据是否规范，老师是否在师资库中，日期、时间是否没问题可以被转为一个具体的 Java 时间实例
     * 如果上述条件 则可以导入
     */
    @Test
    public void test1(){
        String fileName = "D:\\MyProject\\xueliJYPlatform2\\xueliBackEnd\\src\\main\\resources\\data\\排课表\\计算机学院排课表信息导入.xlsx";
        int headRowNumber = 1;  // 根据你的 Excel 调整这个值
        // 使用ExcelReaderBuilder注册自定义的日期转换器
        ExcelReaderBuilder readerBuilder = EasyExcel.read(fileName, CourseScheduleExcelImportVO.class, new CourseScheduleListener(courseScheduleMapper));
        readerBuilder.registerConverter(new CustomDateConverter());

        // 继续你的读取操作
        readerBuilder.sheet().headRowNumber(headRowNumber).doRead();
    }
}
