package com.scnujxjy.backendpoint.dao.entity.college;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 教务员信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("college_admin_information")
public class CollegeAdminInformationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户代码
     */
    @TableId(value = "user_id", type = IdType.AUTO)
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

    /**
     * 工号
     */
    private String workNumber;


}
