package com.scnujxjy.backendpoint.model.ro.courses_learning;


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
public class StudentsCoursesInfoSearchRO {
    @ApiModelProperty(value = "课程状态", example = "在修")
    private String courseStatus;

    @ApiModelProperty(value = "课程主键ID", example = "146")
    private Long courseId;
}
