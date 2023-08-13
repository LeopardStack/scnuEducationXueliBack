package com.scnujxjy.backendpoint.inverter.teaching_point;

import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointLeaderInformationPO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointLeaderInformationRO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointLeaderInformationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TeachingPointLeaderInformationInverter {

    @Mappings({})
    public abstract TeachingPointLeaderInformationVO po2VO(TeachingPointLeaderInformationPO teachingPointLeaderInformationPO);

    @Mappings({})
    public abstract List<TeachingPointLeaderInformationVO> po2VO(List<TeachingPointLeaderInformationPO> teachingPointLeaderInformationPOS);

    @Mappings({})
    public abstract TeachingPointLeaderInformationPO ro2PO(TeachingPointLeaderInformationRO teachingPointInformationRO);
}
