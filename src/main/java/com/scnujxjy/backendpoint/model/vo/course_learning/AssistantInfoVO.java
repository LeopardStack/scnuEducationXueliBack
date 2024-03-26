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
@Accessors(chain = true)
@Builder
public class AssistantInfoVO {
    @ApiModelProperty(value = "助教用户名", example = "TXXXX")
    private String username;
    @ApiModelProperty(value = "助教姓名", example = "李四")
    private String name;
}
