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
public class TeachingPointMangerInfoVO {
    /**
     * 教学点管理人员类型 班主任  负责人
     */
    private String mangerType;

    /**
     * 姓名
     */
    private String name;

    /**
     * 平台用户账号
     */
    private String username;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 身份证号码
     */
    private String idNumber;
}
