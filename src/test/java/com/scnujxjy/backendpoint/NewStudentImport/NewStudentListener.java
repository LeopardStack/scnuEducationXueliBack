package com.scnujxjy.backendpoint.NewStudentImport;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
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

import java.util.List;
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewStudentListener implements ReadListener<NewStudentVo> {
//
//**
//        * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
//     */

//执行数据库操作
    private AdmissionInformationService admissionInformationService;

    private PlatformMessageService platformMessageService;

   private   PersonalInfoService personalInfoService;



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
