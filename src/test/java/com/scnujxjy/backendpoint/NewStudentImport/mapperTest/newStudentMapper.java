package com.scnujxjy.backendpoint.NewStudentImport.mapperTest;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.mapper.NewStudent.NewStudentMapper;
import com.scnujxjy.backendpoint.model.ro.NewStudent.NewStudentInfoRo;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.vo.admission_information.NewStudentAdmissionInformationVo;
import com.scnujxjy.backendpoint.model.vo.newStudentVo.NewStudentVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Slf4j
public class newStudentMapper {
    @Autowired
    private NewStudentMapper newStudentMapper;
    @Test
    public void Select(){
        NewStudentInfoRo newStudentInfoRo = new NewStudentInfoRo();
//        newStudentInfoRo.setGrade("2021");
//        newStudentInfoRo.setStudentNumber("120202140");
        PageRO<NewStudentInfoRo> pageRO = new PageRO<>();
        pageRO.setEntity(newStudentInfoRo);
        pageRO.getEntity().setStudentNumber("120202140");
//        pageRO.getEntity().setGrade("2022");
//        pageRO.setPageSize(10L);
//        pageRO.setPageNumber(1L);

      List<NewStudentAdmissionInformationVo> list =  newStudentMapper.selectNewStudentInfo(pageRO);
//      log.debug("结果[{}]",list);
        System.out.println(list);
    }
}
