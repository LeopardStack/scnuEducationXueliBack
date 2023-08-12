package com.scnujxjy.backendpoint.inverter.college;

import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeInformationRO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeInformationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CollegeInformationInverter {

    @Mappings({})
    public abstract CollegeInformationVO po2VO(CollegeInformationPO collegeInformationPO);

    @Mappings({})
    public abstract List<CollegeInformationVO> po2VO(List<CollegeInformationPO> collegeInformationPOS);

    @Mappings({})
    public abstract CollegeInformationPO ro2PO(CollegeInformationRO collegeInformationRO);

}
