package com.scnujxjy.backendpoint.inverter.registration_record_card;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.GraduationInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusRO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.StudentStatusAllVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class StudentStatusInverter {

    @Mappings({})
    public abstract StudentStatusVO po2VO(StudentStatusPO studentStatusPO);

    @Mappings({
            @Mapping(target = "id", source = "studentStatusVO.id"),
            @Mapping(target = "grade", source = "studentStatusVO.grade"),
            @Mapping(target = "idNumber", source = "studentStatusVO.idNumber"),
            @Mapping(target = "name", source = "personalInfoPO.name"),
            @Mapping(target = "studentNumber", source = "studentStatusVO.studentNumber")
    })
    public abstract StudentStatusAllVO po2VO(StudentStatusVO studentStatusVO, PersonalInfoPO personalInfoPO, GraduationInfoPO graduationInfoPO);

    @Mappings({})
    public abstract StudentStatusRO payInformationFilterRO2RO(PaymentInfoFilterRO filterRO);

    @Mappings({})
    public abstract List<StudentStatusVO> po2VO(List<StudentStatusPO> studentStatusPOS);

    @Mappings({})
    public abstract StudentStatusPO ro2PO(StudentStatusRO studentStatusRO);
}
