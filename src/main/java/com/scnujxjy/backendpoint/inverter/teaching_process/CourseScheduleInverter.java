package com.scnujxjy.backendpoint.inverter.teaching_process;

import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleWithLiveInfoVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseScheduleInverter {

    @Mappings({})
    CourseScheduleVO po2VO(CourseSchedulePO courseSchedulePO);

    @Mappings({})
    CourseScheduleWithLiveInfoVO po2LiveVO(CourseSchedulePO courseSchedulePO);

    @Mappings({})
    List<CourseScheduleVO> po2VO(List<CourseSchedulePO> courseSchedulePOS);

    @Mappings({})
    List<CourseScheduleWithLiveInfoVO> po2LiveVO(List<CourseSchedulePO> courseSchedulePOS);

    @Mappings({})
    CourseSchedulePO ro2PO(CourseScheduleRO courseScheduleRO);
}


