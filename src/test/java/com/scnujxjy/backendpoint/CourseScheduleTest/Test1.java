package com.scnujxjy.backendpoint.CourseScheduleTest;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleExcelImportVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@Slf4j
public class Test1 {

    @Autowired(required = false)
    private CourseScheduleMapper courseScheduleMapper;

    @Autowired(required = false)
    private ClassInformationMapper classInformationMapper;

    @Autowired(required = false)
    private TeacherInformationMapper teacherInformationMapper;

    /**
     * 将排课表 excel 文件导入数据库，需要检查他们所指定的每一个行政班是否存在，数据是否规范，老师是否在师资库中，日期、时间是否没问题可以被转为一个具体的 Java 时间实例
     * 如果上述条件 则可以导入
     */
    @Test
    public void test1(){
        String fileName = "D:\\MyProject\\xueliJYPlatform2\\xueliBackEnd\\src\\main\\resources\\data\\排课表\\教学科学学院排课表信息导入0907-修改2 .xlsx";
        String collegeName = "教育科学学院";
        int headRowNumber = 1;  // 根据你的 Excel 调整这个值
        // 使用ExcelReaderBuilder注册自定义的日期转换器
        ExcelReaderBuilder readerBuilder = EasyExcel.read(fileName, CourseScheduleExcelImportVO.class,
                new CourseScheduleListener(courseScheduleMapper, classInformationMapper, teacherInformationMapper, collegeName));
        readerBuilder.registerConverter(new CustomDateConverter());

        // 继续你的读取操作
        readerBuilder.sheet().headRowNumber(headRowNumber).doRead();
    }

    /**
     * 将导入的排课表信息中的主讲教师的平台用户账号填充进去
     */
    @Test
    public void fillTeacherPlatformAccount() {
        // 1. 获取所有排课表信息
        List<CourseSchedulePO> courseSchedules = courseScheduleMapper.selectList(null);

        for (CourseSchedulePO courseSchedule : courseSchedules) {
            // 2. 检查是否有教师平台用户账号信息
            if (courseSchedule.getTeacherUsername() == null || courseSchedule.getTeacherUsername().isEmpty()) {
                // 3. 通过教师名称查询教师信息表
                List<TeacherInformationPO> teacherInformationPOS = teacherInformationMapper.selectByName(courseSchedule.getMainTeacherName());

//                List<TeacherInformationPO> teacherInformationPOS1 = teacherInformationMapper.selectList(null);
                // 4. 如果查询结果唯一
                if (teacherInformationPOS.size() == 1) {
                    TeacherInformationPO teacherInfo = teacherInformationPOS.get(0);
                    // 5. 获取平台用户信息字段teacher_username
                    String teacherUsername = teacherInfo.getTeacherUsername();
                    if(teacherUsername == null){
                        log.error("更新教师: " + courseSchedule.getMainTeacherName() + " 的平台用户账号失败，该教师平台用户账号为空 " + teacherInfo.toString());
                    }else{
                        // 6. 更新到排课表中
                        int affectedRows = courseScheduleMapper.updateTeacherPlatformAccount(
                                courseSchedule.getMainTeacherName(),
                                null,
                                null,
                                teacherUsername
                        );

                        if (affectedRows > 0) {
                            log.info("成功更新教师: " + courseSchedule.getMainTeacherName() + " 的平台用户账号为: "
                                    + teacherUsername);
                        } else {
                            log.error("更新教师: " + courseSchedule.getMainTeacherName() + " 的平台用户账号失败");
                        }
                    }

                } else if (teacherInformationPOS.size() > 1) {
                    // 如果有多个匹配的教师，进一步匹配工号或身份证
                    TeacherInformationPO matchedTeacher = null;
                    String idNumber = null;
                    String workNumber = null;
                    if (courseSchedule.getMainTeacherId() != null) {
                        List<TeacherInformationPO> matchedByWorkNumber = teacherInformationMapper.selectByWorkNumber(courseSchedule.getMainTeacherId());
                        if (matchedByWorkNumber.size() == 1) {
                            matchedTeacher = matchedByWorkNumber.get(0);
                            workNumber = courseSchedule.getMainTeacherId();
                        }
                    } else if (courseSchedule.getMainTeacherIdentity() != null) {
                        List<TeacherInformationPO> matchedByIdCard = teacherInformationMapper.selectByIdCardNumber(courseSchedule.getMainTeacherIdentity());
                        if (matchedByIdCard.size() == 1) {
                            matchedTeacher = matchedByIdCard.get(0);
                            idNumber = courseSchedule.getMainTeacherIdentity();
                        }
                    }

                    if (matchedTeacher != null) {
                        // 更新到排课表中
                        if(matchedTeacher.getTeacherUsername() == null){
                            log.error("更新教师: " + courseSchedule.getMainTeacherName() + " 的平台用户账号失败，该教师平台用户账号为空 " + matchedTeacher.toString());
                        }else{
                            int affectedRows = courseScheduleMapper.updateTeacherPlatformAccount(
                                    courseSchedule.getMainTeacherName(),
                                    courseSchedule.getMainTeacherId(),
                                    courseSchedule.getMainTeacherIdentity(),
                                    matchedTeacher.getTeacherUsername()
                            );

                            if (affectedRows > 0) {
                                log.info("成功更新教师: " + courseSchedule.getMainTeacherName() + " 的平台用户账号为: "
                                        + matchedTeacher.getTeacherUsername());
                            } else {
                                log.error("更新教师: " + courseSchedule.getMainTeacherName() + " 的平台用户账号失败");
                            }
                        }

                    } else {
                        // 如果没有匹配的教师，你可能需要记录下这些情况以便手动处理。
                        log.error("没有找到匹配的教师: " + courseSchedule.getMainTeacherName());
                    }
                } else {
                    // 如果没有匹配的教师，你可能需要记录下这些情况以便手动处理。
                    log.error("没有找到匹配的教师: " + courseSchedule.getMainTeacherName());
                }
            }
        }
    }
}
