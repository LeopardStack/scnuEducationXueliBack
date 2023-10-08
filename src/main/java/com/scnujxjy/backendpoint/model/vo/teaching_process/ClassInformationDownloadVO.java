package com.scnujxjy.backendpoint.model.vo.teaching_process;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Accessors(chain = true)
public class ClassInformationDownloadVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 0, value = "序号")
    private long index;

    /**
     * 自增主键
     */
    private Long id;

    @ExcelProperty(index = 1, value = "年级")
    private String grade;

    @ExcelProperty(index = 2, value = "学院")
    private String college;

    @ExcelProperty(index = 3, value = "班级名称")
    private String className;

    @ExcelProperty(index = 4, value = "学习形式")
    private String studyForm;

    @ExcelProperty(index = 5, value = "层次")
    private String level;

    @ExcelProperty(index = 6, value = "学制")
    private String studyPeriod;

    @ExcelProperty(index = 7, value = "专业名称")
    private String majorName;

    @ExcelProperty(index = 8, value = "学籍状态")
    private String studentStatus;
}
