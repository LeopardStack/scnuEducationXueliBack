package com.scnujxjy.backendpoint.inverter.teaching_process;

import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class CourseInformationInverter {

    @Mappings({})
    public abstract CourseInformationVO po2VO(CourseInformationPO courseInformationPO);

    @Mappings({})
    public abstract List<CourseInformationVO> po2VO(List<CourseInformationPO> courseInformationPOS);

    @Mappings({})
    public abstract CourseInformationPO ro2PO(CourseInformationRO courseInformationRO);
}
