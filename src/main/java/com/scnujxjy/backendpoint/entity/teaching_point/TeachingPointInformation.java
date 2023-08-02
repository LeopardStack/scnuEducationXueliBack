package com.scnujxjy.backendpoint.entity.teaching_point;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 教学点基础信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TeachingPointInformation implements Serializable {

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


}
