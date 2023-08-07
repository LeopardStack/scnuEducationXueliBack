package com.scnujxjy.backendpoint.inverter.registration_record_card;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.GraduationInfoPO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.GraduationInfoRO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.GraduationInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class GraduationInfoInverter {

    @Mappings({})
    public abstract GraduationInfoVO po2VO(GraduationInfoPO graduationInfoPO);

    @Mappings({})
    public abstract List<GraduationInfoVO> po2VO(List<GraduationInfoPO> graduationInfoPOS);

    @Mappings({})
    public abstract GraduationInfoPO ro2PO(GraduationInfoRO graduationInfoRO);
}
