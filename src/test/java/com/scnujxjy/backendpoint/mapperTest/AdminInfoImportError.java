package com.scnujxjy.backendpoint.mapperTest;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class AdminInfoImportError extends ExcelAdminData{
    @ExcelProperty(index = 5, value = "导入失败原因")
    private String errorMsg;
}
