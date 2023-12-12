package com.scnujxjy.backendpoint.NewStudentImport;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.scnujxjy.backendpoint.inverter.newStudent.NewStudentExcelInverter;
import com.scnujxjy.backendpoint.inverter.newStudent.NewStudentInverter;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.PersonalInfoRO;
import com.scnujxjy.backendpoint.model.vo.newStudentVo.NewStudentExcel;
import com.scnujxjy.backendpoint.model.vo.newStudentVo.NewStudentVo;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
import com.scnujxjy.backendpoint.service.registration_record_card.PersonalInfoService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewStudentListener implements ReadListener<NewStudentExcel> {


    private NewStudentExcelInverter newStudentExcelInverter;

    private static final int BATCH_COUNT = 100;
    /**
     * 缓存的数据
     */
    private List<NewStudentVo> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);




    private NewStudentExcel newStudentExcel;



    /**
    * @Version：1.0.0
    * @Description：下面是数据库相关
    * @Author：3304393868@qq.com
    * @Date：2023/12/11-16:56
    */

    private AdmissionInformationService admissionInformationService;
    private PlatformMessageService platformMessageService;
    private PersonalInfoService personalInfoService;
    private NewStudentInverter newStudentInverter;

    public NewStudentListener(AdmissionInformationService admissionInformationService, PlatformMessageService platformMessageService, PersonalInfoService personalInfoService, NewStudentInverter newStudentInverter) {
        this.admissionInformationService = admissionInformationService;
        this.platformMessageService = platformMessageService;
        this.personalInfoService = personalInfoService;
        this.newStudentInverter = newStudentInverter;
    }

    /**
     * @Version：1.0.0
     * @Description：插入数据库逻辑
     * @Author：3304393868@qq.com
     * @Date：2023/12/8-15:16
     */
    public void wirteDataBase(List<NewStudentVo> newStudentVoList) {
        int i =0;
        for (NewStudentVo newstu : newStudentVoList) {
            AdmissionInformationRO admissionInformationRO = newStudentInverter.Vo2AdmissionInformationRo(newstu);
            if (admissionInformationService.insterAdmissionInformation(admissionInformationRO) > 0) {
                PersonalInfoRO personalInfoRO = newStudentInverter.Vo2PersonalInfoInRo(newstu);
                if (personalInfoService.InsterPersonalInfo(personalInfoRO) > 0) {

                }
            }
            i++;
        }
        log.info("信息全部导入完成,一共导入[{}]条",i);
    }







    private NewStudentVo ExcelTOVo(NewStudentExcel newStudentExcel) {
        NewStudentVo newStudentVo = new NewStudentVo();

        newStudentVo.setStudentNumber(newStudentExcel.getStudentNumber());
        newStudentVo.setName(newStudentExcel.getName());
        newStudentVo.setGender(newStudentExcel.getGender());
        newStudentVo.setTotalScore(newStudentExcel.getTotalScore());
        newStudentVo.setMajorCode(newStudentExcel.getMajorCode());
        newStudentVo.setMajorName(newStudentExcel.getMajorName());
        newStudentVo.setLevel(newStudentExcel.getLevel());
        newStudentVo.setStudyForm(newStudentExcel.getStudyForm());
        newStudentVo.setOriginalEducation(newStudentExcel.getOriginalEducation());
        newStudentVo.setGraduationSchool(newStudentExcel.getGraduationSchool());
        newStudentVo.setGraduationDate(newStudentExcel.getGraduationDate());
        newStudentVo.setPhoneNumber(newStudentExcel.getPhoneNumber());
        newStudentVo.setIdCardNumber(newStudentExcel.getIdCardNumber());
        newStudentVo.setBirthDate(newStudentExcel.getBirthDate());
        newStudentVo.setAddress(newStudentExcel.getAddress());
        newStudentVo.setPostalCode(newStudentExcel.getPostalCode());
        newStudentVo.setEthnicity(newStudentExcel.getEthnicity());
        newStudentVo.setPoliticalStatus(newStudentExcel.getPoliticalStatus());
        newStudentVo.setAdmissionNumber(newStudentExcel.getAdmissionNumber());
        newStudentVo.setCollege(newStudentExcel.getCollege());
        newStudentVo.setTeachingPoint(newStudentExcel.getTeachingPoint());
        newStudentVo.setReportLocation(newStudentExcel.getReportLocation());
        newStudentVo.setEntrancePhotoUrl(newStudentExcel.getEntrancePhotoUrl());
        newStudentVo.setGrade(newStudentExcel.getGrade());

        // You may need to set other properties if there are additional fields

        return newStudentVo;
    }

    @Override
    public void invoke(NewStudentExcel newStudentExcel, AnalysisContext analysisContext) {
//        log.info("解析到一条数据："+newStudentExcel.toString());
        NewStudentVo newStudentDateVo = this.ExcelTOVo(newStudentExcel);

//        newStudentVo = newStudentExcel;
        cachedDataList.add(newStudentDateVo);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (cachedDataList.size() >= BATCH_COUNT) {

            log.info(cachedDataList.toString());
            this.Save();
            // 存储完成清理 list
            cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
        }
    }


    private void Save() {
//        this.wirteDataBase(cachedDataList);
        this.wirteDataBase(cachedDataList);

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        log.info("数据解析完成");
        this.Save();
//        log.info(cachedDataList.toString());
    }
}
