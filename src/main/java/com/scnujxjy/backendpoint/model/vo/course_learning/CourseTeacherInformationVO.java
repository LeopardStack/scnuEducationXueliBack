package com.scnujxjy.backendpoint.model.vo.course_learning;

import io.swagger.annotations.ApiModelProperty;
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
public class CourseTeacherInformationVO {
    @ApiModelProperty(value = "教师姓名", example = "张三")
    private String teacherName;

    @ApiModelProperty(value = "教师电话", example = "154XXX")
    private String phone;

    @ApiModelProperty(value = "校内还是校外老师", example = "校内")
    private String teacherType1;

    @ApiModelProperty(value = "主讲教师 还是 辅导教师", example = "主讲教师")
    private String teacherType2;
}
