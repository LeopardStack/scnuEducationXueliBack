package com.scnujxjy.backendpoint.model.vo.course_learning;

import com.scnujxjy.backendpoint.model.vo.core_data.TeacherSelectVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class CourseCreateParamsVO {
    /**
     * 主讲教师信息
     */
    private List<TeacherSelectVO> mainTeacherList;

    /**
     * 辅导教师
     */
    private List<TeacherSelectVO> tutorList;

    /**
     * 课程类型
     */
    private List<String> courseTypes;
}
