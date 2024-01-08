package com.scnujxjy.backendpoint.model.vo.oa;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Builder
public class MajorChangeRecordExcelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "自增ID", index = 0)
    private Long id;

    @ExcelProperty(value = "序号", index = 1)
    private Integer serialNumber;

    @ExcelProperty(value = "当下年份", index = 2)
    private String currentYear;

    @ExcelProperty(value = "准考证号码", index = 3)
    private String examRegistrationNumber;

    @ExcelProperty(value = "身份证号码", index = 4)
    private String idNumber;

    @ExcelProperty(value = "学号", index = 5)
    private String studentNumber;

    @ExcelProperty(value = "姓名", index = 6)
    private String studentName;

    @ExcelProperty(value = "旧的班级标识", index = 7)
    private String oldClassIdentifier;

    @ExcelProperty(value = "旧的年级", index = 8)
    private String oldGrade;

    @ExcelProperty(value = "旧的学习形式", index = 9)
    private String oldStudyForm;

    @ExcelProperty(value = "旧的专业名称", index = 10)
    private String oldMajorName;

    @ExcelProperty(value = "新的班级标识", index = 11)
    private String newClassIdentifier;

    @ExcelProperty(value = "新的年级", index = 12)
    private String newGrade;

    @ExcelProperty(value = "新的学习形式", index = 13)
    private String newStudyForm;

    @ExcelProperty(value = "新的专业名称", index = 14)
    private String newMajorName;

    @ExcelProperty(value = "审批历史记录表ID", index = 15)
    private Long approvalHistoryId;

    @ExcelProperty(value = "办理人", index = 16)
    private Long approvalUserId;

    @ExcelProperty(value = "申请原因", index = 17)
    private String reason;

    @ExcelProperty(value = "办理日期", index = 18)
    private String approvalDate;

    @ExcelProperty(value = "备注信息", index = 19)
    private String remark;

    @ExcelProperty(value = "层次", index = 20)
    private String level;

}