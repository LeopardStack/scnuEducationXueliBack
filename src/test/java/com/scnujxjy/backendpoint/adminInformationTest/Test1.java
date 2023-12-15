package com.scnujxjy.backendpoint.adminInformationTest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.scnujxjy.backendpoint.NewStudentImport.AdmissionInformationListener;
import com.scnujxjy.backendpoint.dao.mapper.basic.AdminInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformRoleMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.basic.AdminInformationRO;
import com.scnujxjy.backendpoint.util.excelListener.AdminInformationListener;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
public class Test1 {
    @Resource
    private AdminInfoMapper adminInfoMapper;

    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private PlatformRoleMapper platformRoleMapper;


    /**
     * 使用 easyExcel 读取教学计划
     */
    @Test
    public  void test1(){

        String fileName = "D:\\ScnuWork\\xueli\\xueliBackEnd\\src\\main\\resources\\data\\继续教育学院管理人员信息的导入\\继续教育学院学历教育部教务员导入信息模板0922.xlsx";
        int headRowNumber = 1;  // 根据你的 Excel 调整这个值
        // 使用ExcelReaderBuilder注册自定义的日期转换器
        AdminInformationListener admissionInformationMapper = new AdminInformationListener(adminInfoMapper,
                platformRoleMapper, platformUserMapper);

        ExcelReaderBuilder readerBuilder = EasyExcel.read(fileName, AdminInformationRO.class,admissionInformationMapper);

        // 继续你的读取操作
        readerBuilder.sheet().headRowNumber(headRowNumber).doRead();
    }
}
