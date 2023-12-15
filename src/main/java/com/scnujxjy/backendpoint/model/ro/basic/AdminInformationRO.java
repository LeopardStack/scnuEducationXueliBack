package com.scnujxjy.backendpoint.model.ro.basic;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminInformationRO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @ExcelIgnore
    private Long id;

    /**
     * 姓名
     */
    @ExcelProperty(index=0, value = "姓名")
    private String name;

    /**
     * 工号
     */
    @ExcelProperty(index=1, value = "工号")
    private String wordNumber;

    /**
     * 身份证号码
     */
    @ExcelProperty(index=2, value = "身份证号码")
    private String idNumber;

    /**
     * 电话
     */
    @ExcelProperty(index=3, value = "电话")
    private String privatePhone;

    /**
     * 所属部门
     */
    @ExcelProperty(index=4, value = "所属部门")
    private String department;
}
