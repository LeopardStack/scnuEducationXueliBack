package com.scnujxjy.backendpoint.model.vo.college;

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
public class CollegeAdminInformationVO {


    /**
     * 用户代码
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 姓名
     */
    private String name;

    /**
     * 所属学院
     */
    private String collegeId;

    /**
     * 电话
     */
    private String phone;

    /**
     * 工号
     */
    private String workNumber;

}
