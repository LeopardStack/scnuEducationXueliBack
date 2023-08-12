package com.scnujxjy.backendpoint.dao.entity.teaching_point;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 教学点教务员信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TeachingPointAdminInformationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户代码
     */
    @TableId(value = "user_id", type = IdType.AUTO)
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


}
