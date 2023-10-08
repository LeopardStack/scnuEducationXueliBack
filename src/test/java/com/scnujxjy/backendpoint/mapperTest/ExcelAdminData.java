package com.scnujxjy.backendpoint.mapperTest;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelAdminData {
    @ExcelProperty(index = 0, value = "姓名")
    private String name;

    @ExcelProperty(index = 1, value = "所属学院")
    private String collegeName;  // 注意: 这里是学院的名字, 还需要转换成collegeId

    @ExcelProperty(index = 2, value = "电话")
    private String phone;

    @ExcelProperty(index = 3, value = "工号")
    private String workNumber;

    @ExcelProperty(index = 4, value = "身份证号码")
    private String idNumber;
}

