package com.scnujxjy.backendpoint.dao.entity.core_data;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 教师信息表
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
@TableName("teacher_information")
public class TeacherInformationPO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户代码
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 姓名
     */
    @ExcelProperty(index = 1)
    @TableField(value = "name")
    private String name;

    /**
     * 性别
     */
    @TableField(value = "gender")
    @ExcelProperty(index = 2)
    private String gender;

    /**
     * 出生年月
     */
    @TableField(value = "birth_date")
    @ExcelProperty(index = 3)
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthDate;

    /**
     * 政治面貌
     */
    @TableField(value = "political_status")
    @ExcelProperty(index = 4)
    private String politicalStatus;

    /**
     * 学历
     */
    @TableField(value = "education")
    @ExcelProperty(index = 5)
    private String education;

    /**
     * 学位
     */
    @TableField(value = "degree")
    @ExcelProperty(index = 6)
    private String degree;

    /**
     * 专业技术职称
     */
    @ExcelProperty(index = 7)
    private String professionalTitle;

    /**
     * 职称级别
     */
    @ExcelProperty(index = 8)
    private String titleLevel;

    /**
     * 毕业学校
     */
    @ExcelProperty(index = 9)
    private String graduationSchool;

    /**
     * 现任职单位
     */
    @ExcelProperty(index = 10)
    private String currentPosition;

    /**
     * 所属学院
     */
    @ExcelProperty(index = 11)
    private String collegeId;

    /**
     * 所属教学点
     */
    @ExcelProperty(index = 12)
    private String teachingPoint;

    /**
     * 行政职务
     */
    @ExcelProperty(index = 13)
    private String administrativePosition;

    /**
     * 工号/学号
     */
    @ExcelProperty(index = 14)
    private String workNumber;

    /**
     * 身份证号码
     */
    @ExcelProperty(index = 15)
    private String idCardNumber;

    /**
     * 联系电话
     */
    @ExcelProperty(index = 16)
    private String phone;

    /**
     * 电子邮箱
     */
    @ExcelProperty(index = 17)
    private String email;

    /**
     * 开始聘用学期
     */
    @ExcelProperty(index = 18)
    private String startTerm;

    /**
     * 教师类型1
     */
    @ExcelProperty(index = 19)
    private String teacherType1;

    /**
     * 教师类型2
     */
    private String teacherType2;

    /**
     * 教师的平台用户名
     */
    private String teacherUsername;

}
