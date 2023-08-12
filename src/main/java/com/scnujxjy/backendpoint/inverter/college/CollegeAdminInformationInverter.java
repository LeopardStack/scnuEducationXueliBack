package com.scnujxjy.backendpoint.inverter.college;

import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeAdminInformationRO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeAdminInformationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CollegeAdminInformationInverter {

    @Mappings({})
    public abstract CollegeAdminInformationVO po2VO(CollegeAdminInformationPO collegeAdminInformationPO);

    @Mappings({})
    public abstract List<CollegeAdminInformationVO> po2VO(List<CollegeAdminInformationPO> collegeAdminInformationPOS);

    @Mappings({})
    public abstract CollegeAdminInformationPO ro2PO(CollegeAdminInformationRO collegeAdminInformationRO);
}
