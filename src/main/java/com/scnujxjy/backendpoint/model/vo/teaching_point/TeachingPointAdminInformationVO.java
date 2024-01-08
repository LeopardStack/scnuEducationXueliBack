package com.scnujxjy.backendpoint.model.vo.teaching_point;

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
public class TeachingPointAdminInformationVO {

    /**
     * 用户代码
     */
    private String userId;

    /**
     * 所属教学点
     */
    private String teachingPointId;

    /**
     * 电话
     */
    private String phone;

    /**
     * 身份证号码
     */
    private String idCardNumber;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份
     */
    private String identity;

}