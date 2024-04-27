package com.scnujxjy.backendpoint.model.vo.registration_record_card;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 转专业中拟转专业、现学费标准、学习形式、学制、拟转年级班别的实体类
 */
@Data
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@NoArgsConstructor
public class TransferMajorClassInformationSelector {

    @ApiModelProperty("拟转专业code")
    private String majorCode;

    @ApiModelProperty("拟转专业name")
    private String majorName;

    @ApiModelProperty("学习形式")
    private String studyForm;

    @ApiModelProperty("学制")
    private String studyPeriod;

    @ApiModelProperty("拟转班别信息")
    private List<ClassInformationVO> classInformationVOS;
}
