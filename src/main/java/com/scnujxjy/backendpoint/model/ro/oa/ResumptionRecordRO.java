package com.scnujxjy.backendpoint.model.ro.oa;

import com.alibaba.excel.annotation.ExcelIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResumptionRecordRO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    @ExcelIgnore
    private Long id;

    /**
     * 办理年份
     */
    private String currentYear;

    /**
     * 准考证号码
     */
    private String examRegistrationNumber;

    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 姓名
     */
    private String studentName;

    /**
     * 办理开始日期
     */
    private String approvalStartDate;

    /**
     * 办理结束日期
     */
    private String approvalEndDate;
}
