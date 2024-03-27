package com.scnujxjy.backendpoint.model.bo.course_learning;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class TeacherInfo {
    // 教师用户名
    private String teacherUsername;
    // 教师姓名
    private String teacherName;
}
