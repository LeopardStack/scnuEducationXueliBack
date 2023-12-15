package com.scnujxjy.backendpoint.dao.entity.basic;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 管理员信息表
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("admin_info")
public class AdminInfoPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 公共电话
     */
    private String publicPhone;

    /**
     * 私人电话
     */
    private String privatePhone;

    /**
     * 部门
     */
    private String department;

    /**
     * 职务
     */
    private String position;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 所负责的业务
     */
    private String responsibleBusiness;

    /**
     * 工号
     */
    private String workNumber;

    /**
     * 身份证号码
     */
    private String idNumber;


}
