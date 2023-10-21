package com.scnujxjy.backendpoint.model.vo.core_data;

import com.alibaba.excel.annotation.ExcelProperty;
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
public class PaymentInfoAllVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 0, value = "序号")
    private long index;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 学号
     */
    @ExcelProperty(index = 1, value = "学号")
    private String studentNumber;

    /**
     * 准考证号
     */
    @ExcelProperty(index = 2, value = "准考证号")
    private String admissionNumber;

    /**
     * 姓名
     */
    @ExcelProperty(index = 3, value = "姓名")
    private String name;

    /**
     * 身份证号码
     */
    @ExcelProperty(index = 4, value = "身份证号码")
    private String idCardNumber;

    /**
     * 缴费日期
     */
    @ExcelProperty(index = 5, value = "缴费日期")
    private Date paymentDate;

    /**
     * 缴费类别
     */
    @ExcelProperty(index = 6, value = "缴费类别")
    private String paymentCategory;

    /**
     * 学年
     */
    @ExcelProperty(index = 7, value = "学年")
    private String academicYear;

    /**
     * 缴费类型
     */
    @ExcelProperty(index = 8, value = "缴费类型")
    private String paymentType;

    /**
     * 金额
     */
    @ExcelProperty(index = 9, value = "金额")
    private BigDecimal amount;

    /**
     * 是否已缴费
     */
    @ExcelProperty(index = 10, value = "是否已缴费")
    private String isPaid;

    /**
     * 缴费方式
     */
    @ExcelProperty(index = 11, value = "缴费方式")
    private String paymentMethod;

    /**
     * 班级名称
     */
    @ExcelProperty(index = 12, value = "班级名称")
    private String className;

    /**
     * 学院
     */
    @ExcelProperty(index = 13, value = "学院")
    private String college;

    /**
     * 层次
     */
    @ExcelProperty(index = 14, value = "层次")
    private String level;

    /**
     * 学习形式
     */
    @ExcelProperty(index = 15, value = "学习形式")
    private String studyForm;

    /**
     * 年级
     */
    @ExcelProperty(index = 16, value = "年级")
    private String grade;

    /**
     * 教学点
     */
    @ExcelProperty(index = 17, value = "教学点")
    private String teachingPoint;
}
