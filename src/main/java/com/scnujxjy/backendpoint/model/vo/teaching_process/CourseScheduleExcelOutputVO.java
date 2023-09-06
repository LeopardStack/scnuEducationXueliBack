package com.scnujxjy.backendpoint.model.vo.teaching_process;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseScheduleExcelOutputVO extends CourseScheduleExcelImportVO {
    @ExcelProperty(index = 22, value = "导入失败的原因")
    private String errorMessage; // 用于存储导入失败的原因
}
