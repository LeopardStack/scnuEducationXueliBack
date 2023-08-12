package com.scnujxjy.backendpoint.model.ro.college;

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
public class CollegeLeaderInformationRO {


    /**
     * 用户代码
     */
    private String userId;

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


}