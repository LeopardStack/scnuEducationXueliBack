package com.scnujxjy.backendpoint.model.vo.core_data;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 缴费信息的筛选参数
 */
@Data
public class PaymentInformationSelectArgs implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 年级
     */
    private List<String> grades;

    /**
     * 层次
     */
    private List<String> levels;

    /**
     * 学习形式
     */
    private List<String> studyForms;

    /**
     * 班级名称
     */
    private List<String> classNames;

    /**
     * 教学点
     */
    private List<String> teachingPoints;

    /**
     * 学院
     */
    private List<String> collegeNames;

    /**
     * 学年
     */
    private List<String> academicYears;

    /**
     * 备注，用来展示这笔钱来自 退学、休学、转学
     */
    private List<String> remarks;
}
