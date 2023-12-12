package com.scnujxjy.backendpoint.NewStudentImport;

import com.scnujxjy.backendpoint.inverter.newStudent.NewStudentInverter;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.PersonalInfoRO;
import com.scnujxjy.backendpoint.model.vo.newStudentVo.NewStudentVo;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
import com.scnujxjy.backendpoint.service.registration_record_card.PersonalInfoService;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@NoArgsConstructor
public  class NewStudentMapperService {

    //执行数据库操作
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
     * @Description：插入数据库逻辑
     * @Author：3304393868@qq.com
     * @Date：2023/12/8-15:16
     */
    public void wirteDataBase(List<NewStudentVo> newStudentVoList) {
        for (NewStudentVo newstu : newStudentVoList) {
            AdmissionInformationRO admissionInformationRO = newStudentInverter.Vo2AdmissionInformationRo(newstu);
            if (admissionInformationService.insterAdmissionInformation(admissionInformationRO) > 0) {
                PersonalInfoRO personalInfoRO = newStudentInverter.Vo2PersonalInfoInRo(newstu);
                if (personalInfoService.InsterPersonalInfo(personalInfoRO) > 0) {
                }
            }
        }
    }

    @Test
    public void mainTest() {
        NewStudentVo newStudentVo = new NewStudentVo();
        newStudentVo.setStudentNumber("0012312");
        newStudentVo.setGender("2023");
        newStudentVo.setGrade("男");
        newStudentVo.setIdCardNumber(String.valueOf(12314));
        List<NewStudentVo> newStudentVoList = new ArrayList<>();
        newStudentVoList.add(newStudentVo);
        wirteDataBase(newStudentVoList);
    }
}
