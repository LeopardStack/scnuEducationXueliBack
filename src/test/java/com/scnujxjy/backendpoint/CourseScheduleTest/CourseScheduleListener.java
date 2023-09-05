package com.scnujxjy.backendpoint.CourseScheduleTest;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationExcelImportVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelImportVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;

@Slf4j
public class CourseScheduleListener extends AnalysisEventListener<CourseScheduleExcelImportVO> {
    private CourseScheduleMapper courseScheduleMapper;

    private int dataCount = 0; // 添加一个计数变量

    public CourseScheduleListener(CourseScheduleMapper courseScheduleMapper) {
        this.courseScheduleMapper = courseScheduleMapper;
    }

    private CourseSchedulePO convertVOtoPO(CourseScheduleExcelImportVO vo) {
        CourseSchedulePO po = CourseSchedulePO.builder()
                .id(vo.getId())
                .grade(vo.getGrade())
                .majorName(vo.getMajorName())
                .level(vo.getLevel())
                .studyForm(vo.getStudyForm())
                .adminClass(vo.getAdminClass())
                .teachingClass(vo.getTeachingClass())
                .studentCount(vo.getStudentCount())
                .courseName(vo.getCourseName())
                .classHours(vo.getClassHours())
                .examType(vo.getExamType())
                .mainTeacherName(vo.getMainTeacherName())
                .mainTeacherId(vo.getMainTeacherId())
                .mainTeacherIdentity(vo.getMainTeacherIdentity())
                .tutorName(vo.getTutorName())
                .tutorId(vo.getTutorId())
                .tutorIdentity(vo.getTutorIdentity())
                .teachingMethod(vo.getTeachingMethod())
                .classLocation(vo.getClassLocation())
                .onlinePlatform(vo.getOnlinePlatform())
                .teachingDate(vo.getTeachingDate())
                .teachingTime(vo.getTeachingTime())
                .build();
        return po;
    }

    @Override
    public void invoke(CourseScheduleExcelImportVO data, AnalysisContext context) {
        // 将读取到的数据插入到数据库中
        try {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(data.getTeachingDate());

            String[] timeParts = data.getTeachingTime().split("[:-]");
            // 这会把 "8:30-11:30" 分为 "8", "30", "11", "30"

            // 开始时间
            int startHour = Integer.parseInt(timeParts[0]);
            int startMinute = Integer.parseInt(timeParts[1]);
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            Date startDateTime = calendar.getTime();

            // 结束时间
            int endHour = Integer.parseInt(timeParts[2]);
            int endMinute = Integer.parseInt(timeParts[3]);
            calendar.set(Calendar.HOUR_OF_DAY, endHour);
            calendar.set(Calendar.MINUTE, endMinute);
            Date endDateTime = calendar.getTime();

            // 你现在可以使用exactDateTime变量，这是一个具体的时间点。

            // 插入之前校验一下班级、年级、专业名称、层次、课程名称是否对得上教学计划、主讲教师是否在师资库、日期时间是否正确

            courseScheduleMapper.insert(convertVOtoPO(data));
            log.info("读入一行数据 " + data.toString() + "\n 上课时间为 " + startDateTime + " 下课时间为 " + endDateTime);
            dataCount++; // 每次成功插入数据时，增加计数
        }catch (Exception e){
            log.error("插入数据失败 " + data.toString() + "\n" + e.toString());
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 可进行一些后置处理
        log.info("总共读入了 " + dataCount + " 条数据"); // 输出数据数量
    }

}
