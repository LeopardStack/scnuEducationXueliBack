package com.scnujxjy.backendpoint.TeacherInformationTest;

import com.alibaba.excel.EasyExcel;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationExcelImportVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class Test1 {

    @Autowired(required = false)
    private TeacherInformationMapper teacherInformationMapper;


    @Test
    public void test1(){
        String fileName = "D:\\MyProject\\xueliJYPlatform2\\xueliBackEnd\\src\\main\\resources\\data\\授课教师信息\\计算机学院教师信息导入.xlsx";
        int headRowNumber = 1;  // 根据你的 Excel 调整这个值
        EasyExcel.read(fileName, TeacherInformationExcelImportVO.class, new TeacherInformationListener(teacherInformationMapper)).
                sheet().headRowNumber(headRowNumber).doRead();
    }
}
