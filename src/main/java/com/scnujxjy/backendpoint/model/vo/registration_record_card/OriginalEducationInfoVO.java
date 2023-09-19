package com.scnujxjy.backendpoint.model.vo.registration_record_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class OriginalEducationInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 自增主键
     */
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
