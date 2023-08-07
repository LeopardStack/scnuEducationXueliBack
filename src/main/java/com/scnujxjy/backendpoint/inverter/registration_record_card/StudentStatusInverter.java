package com.scnujxjy.backendpoint.inverter.registration_record_card;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusRO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class StudentStatusInverter {

    @Mappings({})
    public abstract StudentStatusVO po2VO(StudentStatusPO studentStatusPO);

    @Mappings({})
    public abstract List<StudentStatusVO> po2VO(List<StudentStatusPO> studentStatusPOS);

    @Mappings({})
    public abstract StudentStatusPO ro2PO(StudentStatusRO studentStatusRO);
}
