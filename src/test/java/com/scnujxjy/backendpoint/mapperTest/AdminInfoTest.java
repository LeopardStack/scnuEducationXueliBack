package com.scnujxjy.backendpoint.mapperTest;

import com.alibaba.excel.EasyExcel;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
@Slf4j
public class AdminInfoTest {

    @Resource
    private CollegeInformationMapper collegeInformationMapper;
    @Resource
    private CollegeAdminInformationMapper collegeAdminInformationMapper;

    @Resource
    private TeachingPointInformationMapper teachingPointInformationMapper;

    @Resource
    private TeachingPointAdminInformationMapper teachingPointAdminInformationMapper;

    @Test
    public void test1(){
        String excelPath = "D:\\MyProject\\xueliJYPlatform2\\xueliBackEnd\\src\\main\\resources\\data\\教务员信息导入\\二级学院教务员信息导入0918.xlsx";
        AdminDataListener listener = new AdminDataListener(collegeAdminInformationMapper, collegeInformationMapper);
        EasyExcel.read(excelPath, ExcelAdminData.class, listener).sheet().headRowNumber(1).doRead();
    }
}
