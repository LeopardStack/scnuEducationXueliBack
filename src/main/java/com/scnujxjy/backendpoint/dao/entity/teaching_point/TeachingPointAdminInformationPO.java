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
import java.util.Date;

/**
 * <p>
 * 教学点教务员信息表
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
@TableName("teaching_point_admin_information")
public class TeachingPointAdminInformationPO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 用户代码
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    /**
     * 用户ID
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
