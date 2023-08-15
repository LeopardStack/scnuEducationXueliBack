package com.scnujxjy.backendpoint.dao.entity.registration_record_card;

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
 * 原学历信息表
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
@TableName("original_education_info")
@TableName("original_education_info")
public class OriginalEducationInfoPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 年级
     */
    private String grade;

    /**
     * 证件号码
     */
    private String idNumber;

    /**
     * 原毕业学校
     */
    private String graduationSchool;

    /**
     * 原文化程度
     */
    private String originalEducation;

    /**
     * 原毕业日期
     */
    private Date graduationDate;


}
