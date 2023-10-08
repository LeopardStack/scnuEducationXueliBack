package com.scnujxjy.backendpoint.model.vo.teaching_process;

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

    @ExcelProperty(index = 0, value = "序号")
    private Integer index;

    /**
     * 学号
     */
    @ExcelProperty(index = 1, value = "学号")
    private String studentNumber;

    /**
     * 年级
     */
    @ExcelProperty(index = 2, value = "年级")
    private String grade;

    /**
     * 学院
     */
    @ExcelProperty(index = 3, value = "学院")
    private String college;

    /**
     * 教学点
     */
    @ExcelProperty(index = 4, value = "教学点")
    private String teachingPoint;

    /**
     * 专业名称
     */
    @ExcelProperty(index = 5, value = "专业名称")
    private String majorName;

    /**
     * 学习形式
     */
    @ExcelProperty(index = 6, value = "学习形式")
    private String studyForm;

    /**
     * 层次
     */
    @ExcelProperty(index = 7, value = "层次")
    private String level;

    /**
     * 学制
     */
    @ExcelProperty(index = 8, value = "学制")
    private String studyDuration;

    /**
     * 考生号
     */
    @ExcelProperty(index = 9, value = "考生号")
    private String admissionNumber;

    /**
     * 学籍状态
     */
    @ExcelProperty(index = 10, value = "学籍状态")
    private String academicStatus;

    /**
     * 入学日期，一般为入学年份 + 03
     */
    @ExcelProperty(index = 11, value = "入学日期")
    @DateTimeFormat("yyyy-MM-dd")
    private Date enrollmentDate;

    /**
     * 身份证号码
     */
    @ExcelProperty(index = 12, value = "身份证号码")
    private String idNumber;

    /**
     * 班级标号
     */
    @ExcelProperty(index = 13, value = "班级标号")
    private String classIdentifier;
    /**
     * 毕业日期
     */
    @ExcelProperty(index = 14, value = "毕业日期")
    @DateTimeFormat("yyyy-MM-dd")
    private Date graduationDate;

    /**
     * 毕业照片
     */
    private String graduationPhoto;

    /**
     * 班级名称
     */
    @ExcelProperty(index = 15, value = "班级名称")
    private String className;

    /**
     * 学生姓名
     */
    @ExcelProperty(index = 16, value = "学生姓名")
    private String name;

    /**
     * 性别
     */
    @ExcelProperty(index = 17, value = "性别")
    private String gender;

    /**
     * 出生日期
     */
    @ExcelProperty(index = 18, value = "出生日期")
    @DateTimeFormat("yyyy-MM-dd")
    private Date birthDate;

    /**
     * 政治面貌
     */
    @ExcelProperty(index = 19, value = "政治面貌")
    private String politicalStatus;

    /**
     * 民族
     */
    @ExcelProperty(index = 20, value = "民族")
    private String ethnicity;
    /**
     * 联系电话
     */
    @ExcelProperty(index = 21, value = "联系电话")
    private String phoneNumber;
    /**
     * 地址
     */
    @ExcelProperty(index = 22, value = "地址")
    private String address;
    /**
     * 入学照片
     */
    private String entrancePhoto;
}
