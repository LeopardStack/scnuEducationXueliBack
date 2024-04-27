package com.scnujxjy.backendpoint.dao.entity.college;

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
 * 教务员信息表
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
@TableName("college_admin_information")
public class CollegeAdminInformationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户代码
     */
    @TableId(value = "user_id")
    private Long userId;


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
    /**
     * 身份证号码
     */
    private String idNumber;


}
