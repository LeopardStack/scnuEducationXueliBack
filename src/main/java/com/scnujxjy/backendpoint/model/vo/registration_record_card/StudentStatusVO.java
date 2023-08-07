package com.scnujxjy.backendpoint.model.vo.registration_record_card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class StudentStatusVO {
    /**
     * 自增主键
     */
    private Long id;

    /**
     * 学号
     */
    private String studentNumber;

    /**
     * 年级
     */
    private String grade;

    /**
     * 学院
     */
    private String college;

    /**
     * 教学点
     */
    private String teachingPoint;

    /**
     * 专业名称
     */
    private String majorName;

    /**
     * 学习形式
     */
    private String studyForm;

    /**
     * 层次
     */
    private String level;

    /**
     * 学制
     */
    private String studyDuration;

    /**
     * 考生号
     */
    private String admissionNumber;

    /**
     * 学籍状态
     */
    private String academicStatus;

    /**
     * 入学日期，一般为入学年份 + 03
     */
    private Date enrollmentDate;
}
