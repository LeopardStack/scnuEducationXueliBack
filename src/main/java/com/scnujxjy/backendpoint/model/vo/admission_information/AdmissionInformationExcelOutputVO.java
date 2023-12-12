package com.scnujxjy.backendpoint.model.vo.admission_information;

import com.alibaba.excel.annotation.ExcelProperty;
import com.scnujxjy.backendpoint.model.ro.admission_information.AdmissionInformationRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdmissionInformationExcelOutputVO extends AdmissionInformationRO {
    @ExcelProperty(index=24, value = "导入失败原因")
    private String errorMessage; // 用于存储导入失败的原因
}
