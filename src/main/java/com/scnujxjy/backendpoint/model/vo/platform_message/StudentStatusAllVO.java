package com.scnujxjy.backendpoint.model.vo.platform_message;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class StudentStatusAllVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自增主键
     */
    private Long id;

    private Integer index;

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
    @DateTimeFormat("yyyy-MM-dd")
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
     * 毕业日期
     */
    @DateTimeFormat("yyyy-MM-dd")
    private Date graduationDate;

    /**
     * 毕业照片
     */
    private String graduationPhoto;

    /**
     * 班级名称
     */
    private String className;

    /**
     * 学生姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 出生日期
     */
    @DateTimeFormat("yyyy-MM-dd")
    private Date birthDate;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 民族
     */
    private String ethnicity;
    /**
     * 联系电话
     */
    private String phoneNumber;
    /**
     * 地址
     */
    private String address;

    /**
     * 学费
     */
    private Double tuition;


    private String teachingPointName;

    /**
     * 已读未读
     */
    private boolean isRead;

    /**
     * 用户平台 ID
     */
    private Long userId;

}
