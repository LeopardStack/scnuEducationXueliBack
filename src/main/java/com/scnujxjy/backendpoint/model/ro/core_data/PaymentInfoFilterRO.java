package com.scnujxjy.backendpoint.model.ro.core_data;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Builder
@AllArgsConstructor
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PaymentInfoFilterRO {
    /**
     * 缴费表主键
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
     * 缴费开始时间：用于筛选查询
     * 使用@JsonFormat注解指定日期时间的格式
     */
    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentBeginDate;

    /**
     * 缴费结束时间：用于筛选查询
     * 使用@JsonFormat注解指定日期时间的格式
     */
    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date paymentEndDate;

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
     * 学院
     */
    private String college;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 层次
     */
    private String level;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 教学点
     */
    private String teachingPoint;


    /**
     * 年级
     */
    private String grade;

    /**
     * 班级集合
     */
    private Set<String> classNameSet;

    /**
     * 备注，用来展示这笔钱来自 退学、休学、转学
     */
    private String remark;

}
