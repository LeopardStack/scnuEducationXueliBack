package com.scnujxjy.backendpoint.basicTest;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseMetaDataRO {
    /**
     * 课程代码
     */
    @ExcelProperty(index=0, value = "课程代码")
    private String courseCode;

    /**
     * 课程代码
     */
    @ExcelProperty(index=1, value = "课程名称")
    private String courseName;
}
