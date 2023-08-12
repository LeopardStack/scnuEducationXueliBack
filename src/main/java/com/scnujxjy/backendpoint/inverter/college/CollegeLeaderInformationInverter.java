package com.scnujxjy.backendpoint.inverter.college;

import com.scnujxjy.backendpoint.dao.entity.college.CollegeLeaderInformationPO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeLeaderInformationRO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeLeaderInformationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CollegeLeaderInformationInverter {

    @Mappings({})
    public abstract CollegeLeaderInformationVO po2VO(CollegeLeaderInformationPO collegeLeaderInformationPO);

    @Mappings({})
    public abstract List<CollegeLeaderInformationVO> po2VO(List<CollegeLeaderInformationPO> collegeLeaderInformationPOS);

    @Mappings({})
    public abstract CollegeLeaderInformationPO ro2PO(CollegeLeaderInformationRO collegeLeaderInformationRO);

}
