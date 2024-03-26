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
public class CourseRetakeRO {
    @ApiModelProperty(value = "课程主键ID", example = "1")
    private Long courseId;

    @ApiModelProperty(value = "学号", example = "24XXXXX")
    private String studentNumber;

}
