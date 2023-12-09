package com.scnujxjy.backendpoint.NewStudentImport;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.inverter.newStudent.NewStudentInverter;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.platform_message.UserAnnouncementRo;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.PersonalInfoRO;
import com.scnujxjy.backendpoint.model.vo.newStudentVo.NewStudentVo;
import com.scnujxjy.backendpoint.service.admission_information.AdmissionInformationService;
import com.scnujxjy.backendpoint.service.platform_message.PlatformMessageService;
import com.scnujxjy.backendpoint.service.registration_record_card.PersonalInfoService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewStudentListener implements ReadListener<NewStudentVo> {


    //执行数据库操作
    private AdmissionInformationService admissionInformationService;

    private PlatformMessageService platformMessageService;

   private   PersonalInfoService personalInfoService;

   private NewStudentInverter newStudentInverter;

/**
* @Version：1.0.0
* @Description：插入数据库逻辑
* @Author：3304393868@qq.com
* @Date：2023/12/8-15:16
*/
   private void wirteDataBase(List<NewStudentVo> newStudentVoList){
       for (NewStudentVo newstu:newStudentVoList){
         AdmissionInformationRO admissionInformationRO= newStudentInverter.Vo2AdmissionInformationRo(newStudentVo);
           if (admissionInformationService.insterAdmissionInformation(admissionInformationRO)>0){
               PersonalInfoRO personalInfoRO = newStudentInverter.Vo2PersonalInfoInRo(newStudentVo);
               if (personalInfoService.InsterPersonalInfo(personalInfoRO)>0){
//                   开始插入用户导入消息表
                   UserAnnouncementRo userAnnouncementRo = new UserAnnouncementRo();
                   userAnnouncementRo.setTitle("账号导入成功");
                   userAnnouncementRo.setUserId(Long.parseLong(newstu.getIdCardNumber()));
                   userAnnouncementRo.setIsRead(false);
                   userAnnouncementRo.setMessageType(String.valueOf(MessageEnum.ANNOUNCEMENT_MSG));
                   userAnnouncementRo.setCreatedAt(new Date());
                platformMessageService.InsterAnnouncementMessage(userAnnouncementRo);
               }
           }
       }
   }

//**
//        * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
//     */

    private static final int BATCH_COUNT = 100;
    /**
     * 缓存的数据
     */
    private List<NewStudentVo> cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);

    private NewStudentVo newStudentVo;

    @Override
    public void invoke(NewStudentVo newStudentVo, AnalysisContext analysisContext) {
        this.newStudentVo =  newStudentVo;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
