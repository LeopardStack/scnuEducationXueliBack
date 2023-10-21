package com.scnujxjy.backendpoint.model.ro.registration_record_card;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class StudentStatusFilterRO {
    /**
     * 学籍表 id
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

    /**
     * 身份证号码
     */
    private String idNumber;

    /**
     * 班级标号
     */
    private String classIdentifier;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 毕业日期
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date graduationDate;

    /**
     * 班级名称
     */
    private String class_name;

    /**
     * 学生姓名
     */
    private String name;


}
