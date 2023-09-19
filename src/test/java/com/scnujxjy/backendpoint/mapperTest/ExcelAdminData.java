package com.scnujxjy.backendpoint.mapperTest;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelAdminData {
    @ExcelProperty("姓名")
    private String name;

    @ExcelProperty("所属学院")
    private String collegeName;  // 注意: 这里是学院的名字, 还需要转换成collegeId

    @ExcelProperty("电话")
    private String phone;

    @ExcelProperty("工号")
    private String workNumber;

    @ExcelProperty("身份证号码")
    private String idNumber;
}

