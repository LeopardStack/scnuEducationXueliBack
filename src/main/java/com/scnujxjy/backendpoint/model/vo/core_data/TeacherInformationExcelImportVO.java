package com.scnujxjy.backendpoint.model.vo.core_data;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherInformationExcelImportVO {
    private static final long serialVersionUID = 1L;

    /**
     * 用户代码
     */
    @TableId(value = "user_id", type = IdType.AUTO)
    private int userId;

    /**
     * 姓名
     */
    @ExcelProperty(index = 0)
    private String name;

    /**
     * 性别
     */
    @ExcelProperty(index = 1)
    private String gender;

    /**
     * 出生年月
     */
    @ExcelProperty(index = 2)
    private String birthDate;

    /**
     * 政治面貌
     */
    @ExcelProperty(index = 3)
    private String politicalStatus;

    /**
     * 学历
     */
    @ExcelProperty(index = 4)
    private String education;

    /**
     * 学位
     */
    @ExcelProperty(index = 5)
    private String degree;

    /**
     * 专业技术职称
     */
    @ExcelProperty(index = 6)
    private String professionalTitle;

    /**
     * 职称级别
     */
    @ExcelProperty(index = 7)
    private String titleLevel;

    /**
     * 毕业学校
     */
    @ExcelProperty(index = 8)
    private String graduationSchool;

    /**
     * 现任职单位
     */
    @ExcelProperty(index = 9)
    private String currentPosition;

    /**
     * 所属学院
     */
    @ExcelProperty(index = 10)
    private String collegeId;

    /**
     * 所属教学点
     */
    @ExcelProperty(index = 11)
    private String teachingPoint;

    /**
     * 行政职务
     */
    @ExcelProperty(index = 12)
    private String administrativePosition;

    /**
     * 工号/学号
     */
    @ExcelProperty(index = 13)
    private String workNumber;

    /**
     * 身份证号码
     */
    @ExcelProperty(index = 14)
    private String idCardNumber;

    /**
     * 联系电话
     */
    @ExcelProperty(index = 15)
    private String phone;

    /**
     * 电子邮箱
     */
    @ExcelProperty(index = 16)
    private String email;

    /**
     * 开始聘用学期
     */
    @ExcelProperty(index = 17)
    private String startTerm;

    /**
     * 教师类型1
     */
    @ExcelProperty(index = 18)
    private String teacherType1;
}
