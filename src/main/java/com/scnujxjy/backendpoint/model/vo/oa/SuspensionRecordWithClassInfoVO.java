package com.scnujxjy.backendpoint.model.vo.oa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuspensionRecordWithClassInfoVO extends SuspensionRecordVO{

    /**
     * 班级名称
     */
    private String className;

    /**
     * 学院
     */
    private String college;


    /**
     * 学籍状态
     */
    private String studentStatus;

    /**
     * 录取专业代码
     */
    private String majorCode;

    /**
     * 学费
     */
    private BigDecimal tuition;

    /**
     * 班级名称
     */
    private String newClassName;

    /**
     * 学院
     */
    private String newCollege;


    /**
     * 学籍状态
     */
    private String newStudentStatus;

    /**
     * 录取专业代码
     */
    private String newMajorCode;

    /**
     * 学费
     */
    private BigDecimal newTuition;

}
