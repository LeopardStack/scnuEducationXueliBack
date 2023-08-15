package com.scnujxjy.backendpoint.dao.entity.college;

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
 * 学院基础信息表
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
@TableName("college_information")
public class CollegeInformationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 学院代码
     */
    @TableId(value = "college_id", type = IdType.AUTO)
    private String collegeId;

    /**
     * 学院名称
     */
    private String collegeName;

    /**
     * 学院地址
     */
    private String collegeAddress;

    /**
     * 学院电话
     */
    private String collegePhone;


}
