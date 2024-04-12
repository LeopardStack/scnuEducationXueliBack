package com.scnujxjy.backendpoint.model.vo.course_learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class TeacherInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;
    // 教师用户名
    private String teacherUsername;
    // 教师姓名
    private String name;
}
