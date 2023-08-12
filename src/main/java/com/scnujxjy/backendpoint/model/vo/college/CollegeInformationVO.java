package com.scnujxjy.backendpoint.model.vo.college;

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
public class CollegeInformationVO {

    /**
     * 学院代码
     */
    private String collegeId;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 学院地址
     */
    private String collegeAddress;

    /**
     * 学院电话
     */
    private String collegePhone;


}