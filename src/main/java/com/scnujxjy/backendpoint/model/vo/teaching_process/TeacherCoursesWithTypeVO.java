package com.scnujxjy.backendpoint.model.vo.teaching_process;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class TeacherCoursesWithTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 教师类型
     * 主讲教师 辅导教师
     */
    private String teacherType;

    /**
     * 课程列表
     */
    private List<TeacherCoursesVO> teacherCoursesVOList;
}
