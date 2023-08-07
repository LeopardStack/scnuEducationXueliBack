package com.scnujxjy.backendpoint.inverter.registration_record_card;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.DegreeInfoPO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.DegreeInfoRO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.DegreeInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class DegreeInfoInverter {

    @Mappings({})
    public abstract DegreeInfoVO po2VO(DegreeInfoPO degreeInfoPO);

    @Mappings({})
    public abstract List<DegreeInfoVO> po2VO(List<DegreeInfoPO> degreeInfoPOS);

    @Mappings({})
    public abstract DegreeInfoPO ro2PO(DegreeInfoRO degreeInfoRO);

}
