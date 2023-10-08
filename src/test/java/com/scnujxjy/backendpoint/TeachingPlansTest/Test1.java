package com.scnujxjy.backendpoint.TeachingPlansTest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.CourseScheduleTest.CourseScheduleListener;
import com.scnujxjy.backendpoint.CourseScheduleTest.CustomDateConverter;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelImportVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private CourseInformationMapper courseInformationMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;


    public static String extractContentFromFileName(String fileName) {
        // 正则表达式，匹配括号里的内容
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(fileName);

        if (matcher.find()) {
            return matcher.group(1);  // 返回括号里的内容
        }
        return null;  // 如果没有匹配到任何内容，返回null
    }

    /**
     * 使用 easyExcel 读取教学计划
     */
    @Test
    public  void test1(){
        List<CourseInformationPO> courseInformationPOS = courseInformationMapper.
                selectByGradeAndCollege("2023", "计算机学院");
        log.info(courseInformationPOS.toString());

        String fileName = "D:\\MyProject\\xueliJYPlatform2\\xueliBackEnd\\src\\main\\resources" +
                "\\data\\2023级教学计划补充导入\\2023级教学计划导入(计算机学院).xlsx";
        String collegeName = extractContentFromFileName(fileName);
        int headRowNumber = 1;  // 根据你的 Excel 调整这个值
        // 使用ExcelReaderBuilder注册自定义的日期转换器
        CourseInformationListener courseInformationListener = new CourseInformationListener(courseInformationMapper,
                classInformationMapper, collegeName);

        ExcelReaderBuilder readerBuilder = EasyExcel.read(fileName, CourseInformationVO.class,courseInformationListener);

        // 继续你的读取操作
        readerBuilder.sheet().headRowNumber(headRowNumber).doRead();
    }
}
