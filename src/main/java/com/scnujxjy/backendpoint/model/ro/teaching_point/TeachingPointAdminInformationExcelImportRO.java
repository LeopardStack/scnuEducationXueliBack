package com.scnujxjy.backendpoint.model.ro.teaching_point;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 教学点教务员信息批量导入模板
 */
@Data
public class TeachingPointAdminInformationExcelImportRO {
    /**
     * 教务员姓名
     */
    @ExcelProperty(index = 0, value = "姓名")
    private String name;

    @ExcelProperty(index = 1, value = "身份证号码")
    private String idCardNumber;

    @ExcelProperty(index = 2, value = "电话")
    private String phone;

    @ExcelProperty(index = 3, value = "所属教学点")
    private String teachingPointName;
}
