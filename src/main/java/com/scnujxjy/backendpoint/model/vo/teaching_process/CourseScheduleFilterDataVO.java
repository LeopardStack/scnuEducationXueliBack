package com.scnujxjy.backendpoint.model.vo.teaching_process;

import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseScheduleFilterDataVO {
    /**
     * 排课表记录
     */
    List<TeacherCourseScheduleVO> courseSchedulePOS;
    /**
     * 排课表总数据
     */
    long total;
}
