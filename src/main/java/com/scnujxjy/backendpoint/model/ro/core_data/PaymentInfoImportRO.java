package com.scnujxjy.backendpoint.model.ro.core_data;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInfoImportRO {
    /**
     * 身份证号
     */
    @ExcelProperty(index = 0)
    private String idNumber;

    /**
     * 姓名
     */
    @ExcelProperty(index = 1)
    private String name;

    /**
     * 手机号码
     */
    @ExcelProperty(index = 2)
    private String phone;

    /**
     * 年级
     */
    @ExcelProperty(index = 3)
    private String grade;

    /**
     * 班别
     */
    @ExcelProperty(index = 4)
    private String className;

    /**
     * 专业
     */
    @ExcelProperty(index = 5)
    private String majorName;

    /**
     * 层次
     */
    @ExcelProperty(index = 6)
    private String level;

    /**
     * 学院
     */
    @ExcelProperty(index = 7)
    private String college;

    /**
     * 金额
     */
    @ExcelProperty(index = 8)
    private String amount;

    /**
     * 缴费日期
     */
    @ExcelProperty(index = 9)
    private String payDate;

    /**
     * 缴费方式
     */
    @ExcelProperty(index = 10)
    private String payType;

}
