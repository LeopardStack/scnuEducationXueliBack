package com.scnujxjy.backendpoint.dao.entity.core_data;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 缴费信息表
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
@TableName("payment_info")
public class PaymentInfoPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 学号
     */
    @ExcelProperty(value = "学号", index = 1)
    private String studentNumber;

    /**
     * 准考证号
     */
    @ExcelProperty(value = "准考证号", index = 2)
    private String admissionNumber;

    /**
     * 姓名
     */
    @ExcelProperty(value = "姓名", index = 3)
    private String name;

    /**
     * 身份证号码
     */
    @ExcelProperty(value = "身份证号码", index = 4)
    private String idCardNumber;

    /**
     * 缴费日期
     */
    @ExcelProperty(value = "缴费日期", index = 5)
    private Date paymentDate;

    /**
     * 缴费类别
     */
    @ExcelProperty(value = "缴费类别", index = 6)
    private String paymentCategory;

    /**
     * 学年
     */
    @ExcelProperty(value = "学年", index = 7)
    private String academicYear;

    /**
     * 缴费类型
     */
    @ExcelProperty(value = "缴费类型", index = 8)
    private String paymentType;

    /**
     * 金额
     */
    @ExcelProperty(value = "金额", index = 9)
    private Double amount;

    /**
     * 是否已缴费
     */
    @ExcelProperty(value = "是否已缴费", index = 10)
    private String isPaid;

    /**
     * 缴费方式
     */
    @ExcelProperty(value = "缴费方式", index = 11)
    private String paymentMethod;

    /**
     * 年级
     */
    @ExcelProperty(value = "年级", index = 12)
    private String grade;

    /**
     * 班级标识
     */
    @ExcelIgnore
    private String classIdentifier;


}
