package com.scnujxjy.backendpoint.inverter;

import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseScheduleInverter {

    @Mappings({})
    CourseScheduleVO po2VO(CourseSchedulePO courseSchedulePO);

    @Mappings({})
    List<CourseScheduleVO> po2VO(List<CourseSchedulePO> courseSchedulePOS);

    @Mappings({})
    CourseSchedulePO ro2PO(CourseScheduleRO courseScheduleRO);
}


