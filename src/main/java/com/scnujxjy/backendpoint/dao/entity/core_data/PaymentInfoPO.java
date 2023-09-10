package com.scnujxjy.backendpoint.dao.entity.core_data;

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
    private Double amount;

    /**
     * 是否已缴费
     */
    private String isPaid;

    /**
     * 缴费方式
     */
    private String paymentMethod;


}
