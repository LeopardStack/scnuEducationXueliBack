package com.scnujxjy.backendpoint.model.ro.teaching_point;

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
public class TeachingPointInformationRO {


    /**
     * 教学点代码
     */
    private String teachingPointId;

    /**
     * 教学点名称
     */
    private String teachingPointName;

    /**
     * 电话
     */
    private String phone;

    /**
     * 地址
     */
    private String address;

    /**
     * 资质信息ID
     */
    private String qualificationId;
    /**
     * 别名
     */
    private String alias;

}