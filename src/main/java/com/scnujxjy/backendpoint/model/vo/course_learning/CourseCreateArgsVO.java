package com.scnujxjy.backendpoint.model.vo.course_learning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseCreateArgsVO {
    /**
     * 年级
     */
    private List<String> grades;

    /**
     * 课程名称
     */
    private List<String> courseNames;


}
