package com.scnujxjy.backendpoint.mapperTest;

import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.UserAnnouncementRo;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.PersonalInfoRO;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionsImport;
import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
import com.scnujxjy.backendpoint.service.registration_record_card.PersonalInfoService;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Slf4j
public class Test1 {
    @Autowired(required = false)
    private AdmissionInformationMapper admissionInformationMapper;

    @Autowired
    private AdmissionInformationService admissionInformationService;


    @Autowired
    private PersonalInfoService personalInfoService;
    /**
     * 获取录取学生总人数
     */
    @Test
    public void test1(){
        Integer integer = admissionInformationMapper.selectCount(null);
        log.info("学生总数 " + integer);
    }
    @Test
    public void  InsterAdmissions(){
        AdmissionInformationRO admissionInformationRO =  new AdmissionInformationRO();
        admissionInformationRO.setAdmissionNumber("00712232123");
        admissionInformationRO.setAddress("山东省济南市槐荫区");
        admissionInformationRO.setName("张三");
//        admissionInformationRO.setBirthDate(new Date("2023-10-1"));
        admissionInformationRO.setCollege("计算机与科学技术");
        admissionInformationRO.setMajorCode("202312");
        admissionInformationRO.setGrade("2023");
        admissionInformationRO.setGender("男");
        Integer count  =  admissionInformationService.insterAdmissionInformation(admissionInformationRO);
    }
    @Test
    public void InsterPersonInfo(){
        PersonalInfoRO personalInfoRO = new PersonalInfoRO();
        personalInfoRO.setName("宋伟");
        personalInfoRO.setEmail("3304393868");
        personalInfoRO.setIdNumber("124566");
        personalInfoRO.setPhoneNumber("14566723");
        personalInfoRO.setGrade("2023");
        Integer count = personalInfoService.InsterPersonalInfo(personalInfoRO);
    }
    @Autowired
    PlatformMessageService platformMessageService;

    @Test
    public void InsertAnnoucementessage(){
        UserAnnouncementRo userAnnouncementRo = new UserAnnouncementRo();
        userAnnouncementRo.setMessageType(MessageEnum.ANNOUNCEMENT_MSG.getMessage_name());
        userAnnouncementRo.setContent("測試公告");
        userAnnouncementRo.setIsRead(false);
        userAnnouncementRo.setUserId(121424312);
        boolean a=   platformMessageService.InsterAnnouncementMessage(userAnnouncementRo);
        log.info("打印[{}]",a);
    }
}
