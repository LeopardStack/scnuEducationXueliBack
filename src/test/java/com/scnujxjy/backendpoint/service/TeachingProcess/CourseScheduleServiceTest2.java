package com.scnujxjy.backendpoint.service.TeachingProcess;

import com.alibaba.excel.EasyExcel;
import com.scnujxjy.backendpoint.TeacherInformationTest.TeacherInformationErrorRecord;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExportTeacherNamePO;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
public class CourseScheduleServiceTest2 {
    @Resource
    private CourseScheduleService courseScheduleService;

    @Resource
    private TeacherInformationService teacherInformationService;

    @Test
    public void checkTeacherNameInCourseSchedule() {
        List<CourseSchedulePO> courseSchedules = courseScheduleService.getBaseMapper().selectList(null);
        List<CourseScheduleExportTeacherNamePO> exportData = new ArrayList<>();

        for (CourseSchedulePO courseSchedule : courseSchedules) {
            CourseScheduleExportTeacherNamePO exportPO = new CourseScheduleExportTeacherNamePO();
            BeanUtils.copyProperties(courseSchedule, exportPO); // 使用Spring的BeanUtils来复制属性

            String mainTeacherName = courseSchedule.getMainTeacherName();
            List<TeacherInformationPO> teachers = teacherInformationService.getBaseMapper().selectByName(mainTeacherName);

            if (teachers.isEmpty()) {
                exportPO.setTeacherExistenceStatus("不存在于师资库中");
                exportData.add(exportPO); // 只有不存在于师资库中的教师才被添加到exportData中
            } else if (teachers.size() > 1) {
                exportPO.setTeacherExistenceStatus("在师资库中不唯一");
                exportData.add(exportPO); // 在师资库中不唯一的教师也被添加到exportData中
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String currentDateTime = LocalDateTime.now().format(formatter);
        String relativePath = "data_import_error_excel/courseScheduleTeacher";
        String errorFileName = currentDateTime + "_notInTeacherInformationTable.xlsx";
        EasyExcel.write(relativePath + "/" + errorFileName,
                CourseScheduleExportTeacherNamePO.class).sheet("ErrorRecords").doWrite(exportData);
    }

}
