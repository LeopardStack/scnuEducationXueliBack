package com.scnujxjy.backendpoint.model.vo.core_data;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NewStudentPaymentInfoExcelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @ExcelIgnore
    private Long id;

    /**
     * 学号
     */
    @ExcelIgnore
    private String studentNumber;

    /**
     * 序号
     */
    @ExcelProperty(index=0, value = "序号")
    private Integer index;

    /**
     * 准考证号
     */
    @ExcelProperty(index=2, value = "准考证号")
    private String admissionNumber;

    /**
     * 姓名
     */
    @ExcelProperty(index=4, value = "姓名")
    private String name;

    /**
     * 身份证号码
     */
    @ExcelProperty(index=3, value = "身份证号码")
    private String idCardNumber;

    /**
     * 缴费日期
     */
    @ExcelProperty(index=5, value = "缴费日期")
    private Date paymentDate;

    /**
     * 缴费类别
     */
    @ExcelProperty(index=6, value = "缴费类别")
    private String paymentCategory;

    /**
     * 学年
     */
    @ExcelProperty(index=7, value = "学年")
    private String academicYear;

    /**
     * 缴费类型
     */
    @ExcelProperty(index=8, value = "缴费类型")
    private String paymentType;

    /**
     * 金额
     */
    @ExcelProperty(index=9, value = "金额")
    private BigDecimal amount;

    /**
     * 是否已缴费
     */
    private String isPaid;

    /**
     * 缴费方式
     */
    @ExcelProperty(index=10, value = "缴费方式")
    private String paymentMethod;

    /**
     * 班级名称
     */
    @ExcelIgnore
    private String className;

    /**
     * 学院
     */
    private String college;

    /**
     * 专业名称
     */
    @ExcelProperty(index=13, value = "专业名称")
    private String majorName;

    /**
     * 层次
     */
    @ExcelProperty(index=14, value = "层次")
    private String level;

    /**
     * 学习形式
     */
    @ExcelProperty(index=15, value = "学习形式")
    private String studyForm;

    /**
     * 年级
     */
    @ExcelProperty(index=1, value = "年级")
    private String grade;

    /**
     * 备注，用来展示这笔钱来自 退学、休学、转学
     */
    @ExcelIgnore
    private String remark;

    /**
     * 学院
     */
    @ExcelProperty(index=11, value = "学院")
    private String collegeName;

    /**
     * 教学点
     */
    @ExcelProperty(index=12, value = "教学点")
    private String teachingPointName;
}
