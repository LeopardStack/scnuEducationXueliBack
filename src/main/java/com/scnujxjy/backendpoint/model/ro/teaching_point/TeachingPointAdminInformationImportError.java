package com.scnujxjy.backendpoint.model.ro.teaching_point;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TeachingPointAdminInformationImportError extends TeachingPointAdminInformationExcelImportRO{
    @ExcelProperty(index = 5, value = "导入失败原因")
    private String errorMsg;
}
