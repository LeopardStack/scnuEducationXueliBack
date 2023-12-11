package com.scnujxjy.backendpoint.inverter.newStudent;

import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.PersonalInfoRO;
import com.scnujxjy.backendpoint.model.vo.newStudentVo.NewStudentExcel;
import com.scnujxjy.backendpoint.model.vo.newStudentVo.NewStudentVo;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public abstract class NewStudentInverter {
    @Mappings({})
    public abstract AdmissionInformationRO Vo2AdmissionInformationRo(NewStudentVo newStudentVo);
    @Mappings({})
    public abstract PersonalInfoRO Vo2PersonalInfoInRo(NewStudentVo newStudentVo);


}
