package com.scnujxjy.backendpoint.inverter.registration_record_card;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.PersonalInfoRO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.PersonalInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PersonalInfoInverter {

    @Mappings({})
    public abstract PersonalInfoVO po2VO(PersonalInfoPO personalInfoPO);

    @Mappings({})
    public abstract List<PersonalInfoVO> po2VO(List<PersonalInfoPO> personalInfoPOS);

    @Mappings({})
    public abstract PersonalInfoPO ro2PO(PersonalInfoRO personalInfoRO);
}
