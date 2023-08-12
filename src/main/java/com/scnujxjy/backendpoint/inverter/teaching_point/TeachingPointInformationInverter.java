package com.scnujxjy.backendpoint.inverter.teaching_point;

import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointInformationRO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointInformationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TeachingPointInformationInverter {

    @Mappings({})
    public abstract TeachingPointInformationVO po2VO(TeachingPointInformationPO teachingPointInformationPO);

    @Mappings({})
    public abstract List<TeachingPointInformationVO> po2VO(List<TeachingPointInformationPO> teachingPointInformationPOS);

    @Mappings({})
    public abstract TeachingPointInformationPO ro2PO(TeachingPointInformationRO teachingPointInformationRO);

}
