package com.scnujxjy.backendpoint.model.vo.core_data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentInfoOldSystemVO {
    @ExcelProperty(value = " ", index = 0) // 空列名
    private String ident;

    @ExcelProperty(value = "学号", index = 1)
    private String studentNumber;

    @ExcelProperty(value = "身份证号", index = 2)
    private String idNumber;

    @ExcelProperty(value = "姓名", index = 3)
    private String name;

    @ExcelProperty(value = "教学学院", index = 4)
    private String college;

    @ExcelProperty(value = "专业", index = 5)
    private String majorName;

    @ExcelProperty(value = "年级", index = 6)
    private String grade;

    @ExcelProperty(value = "层次", index = 7)
    private String level;

    @ExcelProperty(value = "班别", index = 8)
    private String className;

    @ExcelProperty(value = "金额", index = 9)
    private String amount;

    @ExcelProperty(value = "缴费日期", index = 10)
    private String payDate;

    @ExcelProperty(value = "缴费方式", index = 11)
    private String payType;
}
