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
public class CourseClassMappingRO {
    @ApiModelProperty(value = "课程ID", example = "1")
    private Long courseId;
}
