package com.scnujxjy.backendpoint.model.ro.basic;

import com.alibaba.excel.annotation.ExcelProperty;
import com.scnujxjy.backendpoint.dao.entity.basic.AdminInfoPO;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminInfoExcelOutputVO extends AdminInformationRO {
    @ExcelProperty(index=5, value = "导入失败原因")
    private String errorMessage; // 用于存储导入失败的原因
}
