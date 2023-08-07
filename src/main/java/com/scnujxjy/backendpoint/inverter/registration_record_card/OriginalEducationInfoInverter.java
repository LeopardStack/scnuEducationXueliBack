package com.scnujxjy.backendpoint.inverter.registration_record_card;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.OriginalEducationInfoPO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.OriginalEducationInfoRO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.OriginalEducationInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class OriginalEducationInfoInverter {

    @Mappings({})
    public abstract OriginalEducationInfoVO po2VO(OriginalEducationInfoPO originalEducationInfoPO);

    @Mappings({})
    public abstract List<OriginalEducationInfoVO> po2VO(List<OriginalEducationInfoPO> originalEducationInfoPOS);

    @Mappings({})
    public abstract OriginalEducationInfoPO ro2PO(OriginalEducationInfoRO originalEducationInfoRO);
}
