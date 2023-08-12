package com.scnujxjy.backendpoint.inverter.teaching_point;

import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointAdminInformationRO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointAdminInformationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TeachingPointAdminInformationInverter {

    @Mappings({})
    public abstract TeachingPointAdminInformationVO po2VO(TeachingPointAdminInformationPO teachingPointAdminInformationPO);

    @Mappings({})
    public abstract List<TeachingPointAdminInformationVO> po2VO(List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS);

    @Mappings({})
    public abstract TeachingPointAdminInformationPO ro2PO(TeachingPointAdminInformationRO teachingPointAdminInformationRO);
}
