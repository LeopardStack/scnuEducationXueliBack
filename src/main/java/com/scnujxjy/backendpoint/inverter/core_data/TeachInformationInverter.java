package com.scnujxjy.backendpoint.inverter.core_data;

import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.model.ro.core_data.TeacherInformationRO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class TeachInformationInverter {

    @Mappings({})
    public abstract TeacherInformationVO po2VO(TeacherInformationPO teacherInformationPO);

    @Mappings({})
    public abstract TeacherInformationPO ro2PO(TeacherInformationRO teacherInformationRO);

    @Mappings({})
    public abstract List<TeacherInformationVO> po2VO(List<TeacherInformationPO> teacherInformationPOS);
}
