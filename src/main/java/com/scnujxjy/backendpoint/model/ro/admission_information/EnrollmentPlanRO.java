package com.scnujxjy.backendpoint.model.ro.admission_information;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentPlanRO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否开启招生计划申报", example = "1")
    private Boolean applyOpen;

    @ApiModelProperty(value = "申报截止时间", example = "2023-05-17 14:30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date dueDate;

    @ApiModelProperty(value = "符合申报条件的教学点 ID 集合", example = "[1,2]")
    private List<String> teachingPointIdList;
}
