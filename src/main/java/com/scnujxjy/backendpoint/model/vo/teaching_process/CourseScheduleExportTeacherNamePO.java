package com.scnujxjy.backendpoint.model.vo.teaching_process;

import com.alibaba.excel.annotation.ExcelProperty;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseScheduleExportTeacherNamePO extends CourseSchedulePO {
    @ExcelProperty(value = "不存在于系统师资库中的教师", index = 22)
    private String teacherExistenceStatus;

    public String getTeacherExistenceStatus() {
        return teacherExistenceStatus;
    }

    public void setTeacherExistenceStatus(String teacherExistenceStatus) {
        this.teacherExistenceStatus = teacherExistenceStatus;
    }
}
