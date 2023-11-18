package com.scnujxjy.backendpoint.examTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamAssistantsPO;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.TeachingAssistantsCourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamAssistantsMapper;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.TeachingAssistantsCourseScheduleMapper;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 将教学计划中的数据导入到考试信息中
 * 并且当主讲教师没有被设置时  就默认选择排课表中的老师
 */
@SpringBootTest
@Slf4j
public class ImportCourseInformationToExamInfo {
    @Resource
    private CourseInformationMapper courseInformationMapper;
    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private TeacherInformationMapper teacherInformationMapper;

    @Resource
    private CourseExamInfoMapper examInfoMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Resource
    private TeachingAssistantsCourseScheduleMapper teachingAssistantsCourseScheduleMapper;

    @Resource
    private CourseExamAssistantsMapper courseExamAssistantsMapper;

    @Test
    public void test1(){

        for(int grade = 2023; grade >= 2019; grade --){
            List<CourseInformationPO> courseInformationPOS = courseInformationMapper.selectList(new
                    LambdaQueryWrapper<CourseInformationPO>().eq(CourseInformationPO::getGrade, "" + grade));
            for(CourseInformationPO courseInformationPO: courseInformationPOS){
                CourseExamInfoPO courseExamInfoPO = new CourseExamInfoPO();
                courseExamInfoPO.setClassIdentifier(courseInformationPO.getAdminClass());
                courseExamInfoPO.setCourse(courseInformationPO.getCourseName());

                courseExamInfoPO.setClassName(classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                        .eq(ClassInformationPO::getClassIdentifier, courseInformationPO.getAdminClass())).getClassName());
                courseExamInfoPO.setExamStatus("未开始");
                courseExamInfoPO.setExamMethod("线下");

                List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(new LambdaQueryWrapper<CourseSchedulePO>()
                        .eq(CourseSchedulePO::getGrade, courseInformationPO.getGrade())
                        .eq(CourseSchedulePO::getMajorName, courseInformationPO.getMajorName())
                        .eq(CourseSchedulePO::getCourseName, courseInformationPO.getCourseName())
                        .eq(CourseSchedulePO::getStudyForm, courseInformationPO.getStudyForm())
                        .eq(CourseSchedulePO::getLevel, courseInformationPO.getLevel())
                        .eq(CourseSchedulePO::getAdminClass, classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                                .eq(ClassInformationPO::getClassIdentifier, courseInformationPO.getAdminClass())).getClassName())
                );

                // 使用 Set 来存储唯一的老师和助教信息
                Set<String> teachers = new HashSet<>();
                Set<String> teachingAssistants = new HashSet<>();

                Long batchIndex = null;
                Set<TeachingAssistantsCourseSchedulePO> teachingAssistantsCourseSchedulePOSet = new HashSet<>();
                // 遍历 courseSchedulePOS 列表
                for (CourseSchedulePO schedule : courseSchedulePOS) {
                    // 获取并存储主讲老师信息
                    String mainTeacher = schedule.getMainTeacherName() + " " + schedule.getTeacherUsername();
                    teachers.add(mainTeacher);

                    // 获取并存储助教信息
                    String tutor = schedule.getTutorName() + " " + schedule.getTeachingAssistantUsername();
                    teachingAssistants.add(tutor);

                    batchIndex = schedule.getBatchIndex();
                    List<TeachingAssistantsCourseSchedulePO> teachingAssistantsCourseSchedulePOS = teachingAssistantsCourseScheduleMapper.selectList(new LambdaQueryWrapper<TeachingAssistantsCourseSchedulePO>()
                            .eq(TeachingAssistantsCourseSchedulePO::getBatchId, batchIndex));
                    teachingAssistantsCourseSchedulePOSet.addAll(teachingAssistantsCourseSchedulePOS);
                }

                String mainTeacherName = null;
                String mainTeacherUserName = null;

                String tutorTeacherName = null;
                String tutorTeacherUserName = null;
                if(teachers.size() > 1){

                    throw new IllegalArgumentException("出现了同一条教学计划有多个主讲" + teachers + "\n" + courseInformationPO);
//                    log.error("出现了同一条教学计划有多个主讲" + teachers + "\n" + courseInformationPO);
                }
                if(teachers.size() == 1){
                    // 获取 Set 中唯一的元素
                    String mainTeacherInfo = teachers.iterator().next();

                    // 将字符串分割成姓名和用户名
                    String[] parts = mainTeacherInfo.split(" ");
                    mainTeacherName = parts[0];
                    mainTeacherUserName = parts[1];
                }

                courseExamInfoPO.setMainTeacher(mainTeacherName);
                courseExamInfoPO.setTeacherUsername(mainTeacherUserName);

//                int insert = examInfoMapper.insert(courseExamInfoPO);
//                if(insert <= 0){
//                    log.error("插入考试记录失败 插入结果  " + insert + " \n" + courseExamInfoPO);
//                }

                // 更新阅卷助教

                for(TeachingAssistantsCourseSchedulePO teachingAssistantsCourseSchedulePO: teachingAssistantsCourseSchedulePOSet){
                    String username = teachingAssistantsCourseSchedulePO.getUsername();
                    TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                            .eq(TeacherInformationPO::getTeacherUsername, username));
                    teachingAssistants.add(teacherInformationPO.getName() + " " + username);
                }
                for(String tutor: teachingAssistants){
                    // 将助教更新到阅卷助教表
                    String[] parts = tutor.split(" ");
                    tutorTeacherName = parts[0];
                    tutorTeacherUserName = parts[1];
                    CourseExamAssistantsPO courseExamAssistantsPO = new CourseExamAssistantsPO();
                    courseExamAssistantsPO.setAssistantName(tutorTeacherName);
                    courseExamAssistantsPO.setTeacherUsername(tutorTeacherUserName);
                    courseExamAssistantsPO.setCourseId(courseExamInfoPO.getId());

//                    int insert1 = courseExamAssistantsMapper.insert(courseExamAssistantsPO);
//                    if(insert1 <= 0){
//                        log.error("插入阅卷助教失败 插入结果  " + insert1 + " \n" + courseExamAssistantsPO);
//                    }
                }

            }
        }




    }
}
