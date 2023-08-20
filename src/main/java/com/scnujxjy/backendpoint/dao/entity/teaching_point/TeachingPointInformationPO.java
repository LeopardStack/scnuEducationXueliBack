package com.scnujxjy.backendpoint.dao.entity.teaching_point;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 教学点基础信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("teaching_point_information")
public class TeachingPointInformationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 教学点代码
     */
    @TableId(value = "teaching_point_id", type = IdType.AUTO)
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
