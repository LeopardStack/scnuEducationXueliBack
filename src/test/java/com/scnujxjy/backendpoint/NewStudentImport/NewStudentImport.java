package com.scnujxjy.backendpoint.NewStudentImport;

import com.alibaba.excel.EasyExcel;
import com.scnujxjy.backendpoint.inverter.newStudent.NewStudentInverter;
import com.scnujxjy.backendpoint.model.vo.newStudentVo.NewStudentExcel;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
import com.scnujxjy.backendpoint.service.registration_record_card.PersonalInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;

@SpringBootTest
@Slf4j
public class NewStudentImport {

@Resource
    private AdmissionInformationService admissionInformationService;
@Resource
    private PlatformMessageService platformMessageService;
@Resource
    private PersonalInfoService personalInfoService;
@Resource
    private NewStudentInverter newStudentInverter;
    /**
    * @Version：1.0.0
    * @Description：導入學生信息
    * @Author：3304393868@qq.com
    * @Date：2023/12/8-10:21
    */
    @Test
    public void Import(){
        File excelFile = new File("C:/Users/33043/Desktop/副本华南师范大学学历教育新生录取模板.xlsx");
        //2、判断文件是否存在，不存在则创建一个Excel文件
        if (!excelFile.exists()) {
            log.error("文件不存在");
        }

        NewStudentListener newStudentListener  = new  NewStudentListener(admissionInformationService,platformMessageService,personalInfoService,newStudentInverter);
        EasyExcel.read(excelFile, NewStudentExcel.class,newStudentListener).sheet().doRead();
    }
}
