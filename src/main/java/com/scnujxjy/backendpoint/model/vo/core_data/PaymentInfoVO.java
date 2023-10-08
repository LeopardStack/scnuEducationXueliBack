package com.scnujxjy.backendpoint.model.vo.core_data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class PaymentInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 准考证号
     */
    private String admissionNumber;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号码
     */
    private String idCardNumber;

    /**
     * 缴费日期
     */
    private Date paymentDate;

    /**
     * 缴费类别
     */
    private String paymentCategory;

    /**
     * 学年
     */
    private String academicYear;

    /**
     * 缴费类型
     */
    private String paymentType;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 是否已缴费
     */
    private String isPaid;

    /**
     * 缴费方式
     */
    private String paymentMethod;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 学院
     */
    private String college;

    /**
     * 层次
     */
    private String level;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 年级
     */
    private String grade;

}
