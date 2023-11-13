package com.scnujxjy.backendpoint.model.bo.teaching_process;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class CourseScheduleStudentExcelBO {
    @ExcelProperty("学号")
    private String studentNumber;

    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("身份证号")
    private String idNumber;

    @ExcelProperty("专业名称")
    private String majorName;

    @ExcelProperty("班级名称")
    private String courseName;
    @ExcelProperty("性别")
    private String gender;
    @ExcelProperty("年级")
    private String grade;
    @ExcelProperty("学院")
    private String college;

    /**
     * 批次id
     */
    @ExcelIgnore
    @TableField(exist = false)
    private Long batchIndex;

    @ExcelIgnore
    @TableField(exist = false)
    private Set<String> includeColumnFiledNames;

    @Override
    public int hashCode() {
        return (name + idNumber).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof CourseScheduleStudentExcelBO)) {
            return false;
        }
        CourseScheduleStudentExcelBO courseScheduleStudentExcelBO = (CourseScheduleStudentExcelBO) obj;
        return courseScheduleStudentExcelBO.name.equals(this.name)
                && courseScheduleStudentExcelBO.idNumber.equals(this.idNumber);
    }
}
