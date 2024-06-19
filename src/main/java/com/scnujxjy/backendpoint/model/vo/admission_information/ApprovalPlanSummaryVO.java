package com.scnujxjy.backendpoint.model.vo.admission_information;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ApprovalPlanSummaryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 1, value = "序号")
    private Integer index;

    @ExcelProperty(index = 2, value = "主管院系")
    private String college;

    @ExcelProperty(index = 3, value = "学历层次")
    private String level;

    @ExcelProperty(index = 4, value = "专业名称")
    private String majorName;

    @ExcelProperty(index = 5, value = "授课地点")
    private String schoolLocation;

    @ExcelProperty(index = 6, value = "招生科类")
    private String enrollmentSubject;

    @ExcelProperty(index = 7, value = "学习形式")
    private String studyForm;

    @ExcelProperty(index = 8, value = "学习年限")
    private String educationLength;

    @ExcelProperty(index = 9, value = "学费")
    private String tuition;

    @ExcelProperty(index = 10, value = "备注")
    private String remarks;

    @ExcelProperty(index = 11, value = "审核状态")
    private String status;

    @ExcelProperty(value = "年")
    private String year;
}
