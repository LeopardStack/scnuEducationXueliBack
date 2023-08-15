package com.scnujxjy.backendpoint.inverter.registration_record_card;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationRO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ClassInformationInverter {

    @Mappings({})
    public abstract ClassInformationVO po2VO(ClassInformationPO classInformationPO);

    @Mappings({})
    public abstract List<ClassInformationVO> po2VO(List<ClassInformationPO> classInformationPOS);

    @Mappings({})
    public abstract ClassInformationPO ro2PO(ClassInformationRO classInformationRO);
}
