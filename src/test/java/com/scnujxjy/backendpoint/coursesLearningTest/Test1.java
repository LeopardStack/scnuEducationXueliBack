package com.scnujxjy.backendpoint.coursesLearningTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesClassMappingPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesLearningPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.SectionsPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesClassMappingMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesLearningMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.SectionsMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.util.tool.ScnuTimeInterval;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

import static com.scnujxjy.backendpoint.constant.enums.CourseContentType.LIVING;

@SpringBootTest
@Slf4j
public class Test1 {
    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Resource
    private CourseInformationMapper courseInformationMapper;

    @Resource
    private CoursesLearningMapper coursesLearningMapper;

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private SectionsMapper sectionsMapper;

    @Resource
    private CoursesClassMappingMapper coursesClassMappingMapper;

    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;

    @Test
    public void test1(){
        Integer i = courseScheduleMapper.selectCount(null);
        Integer i1 = courseScheduleMapper.selectCount(new LambdaQueryWrapper<CourseSchedulePO>()
                .eq(CourseSchedulePO::getGrade, "2023"));

        log.info("\n排课表总记录数为 "  + i + " 2023级的排课表记录数为 " + i1);

        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(null);

        // 用一个 HashMap 来记录同一批次的 即同一个直播间的
        HashMap<Long, CoursesLearningPO> courseSchedulePOHashMap = new HashMap<>();

        for(CourseSchedulePO courseSchedulePO: courseSchedulePOS){
            String grade = courseSchedulePO.getGrade();
            String adminClass = courseSchedulePO.getAdminClass();
            String courseName = courseSchedulePO.getCourseName();
            String studyForm = courseSchedulePO.getStudyForm();
            String level = courseSchedulePO.getLevel();
            String majorName = courseSchedulePO.getMajorName();

            try {
                ClassInformationPO classInformationPO = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                        .eq(ClassInformationPO::getGrade, grade)
                        .eq(ClassInformationPO::getClassName, adminClass)
                        .eq(ClassInformationPO::getMajorName, majorName)
                        .eq(ClassInformationPO::getLevel, level)
                        .eq(ClassInformationPO::getStudyForm, studyForm)
                );
                try {
                    courseInformationMapper.selectOne(new LambdaQueryWrapper<CourseInformationPO>()
                            .eq(CourseInformationPO::getGrade, grade)
                            .eq(CourseInformationPO::getAdminClass, classInformationPO.getClassIdentifier())
                            .eq(CourseInformationPO::getMajorName, majorName)
                            .eq(CourseInformationPO::getLevel, level)
                            .eq(CourseInformationPO::getStudyForm, studyForm)
                            .eq(CourseInformationPO::getCourseName, courseName)
                    );

                    // 如果没有报任何异常  说明排课表中的每一条记录都是对应教学计划的 而且根据课程名称能找到唯一的课程
                    CoursesLearningPO coursesLearningPO = new CoursesLearningPO()
                            .setGrade(grade)
                            .setCourseName(courseName)
                            .setCourseType(courseSchedulePO.getTeachingMethod())
                            .setCourseName(courseName)
                            .setValid("Y")
                            .setDefaultMainTeacherUsername(courseSchedulePO.getTeacherUsername())
                            ;
                    // 通过年级 、主讲老师 课程名称 来匹配一个桶 即一门课 （可能存在跨课程）
                    CoursesLearningPO coursesLearningPO1 = courseSchedulePOHashMap.get(courseSchedulePO.getBatchIndex());
                    if(coursesLearningPO1 != null){
                        // 什么也不需要做 找到了 这个 课程 直接把直播上课插入
                    }else{
                        int insert = coursesLearningMapper.insert(coursesLearningPO);
                        if(insert < 0){
                            log.error("插入失败 " + coursesLearningPO + " 插入结果 " + insert);
                        }
                    }

                    // 插入章节 在插入之前 先查看这堂课是否存在 即时间是否一致，如果一致 说明 需要添加新的班级进来
                    ScnuTimeInterval timeInterval = scnuXueliTools.getTimeInterval(courseSchedulePO.getTeachingDate(), courseSchedulePO.getTeachingTime());

                    SectionsPO sectionsPO = new SectionsPO()
                            .setCourseId(coursesLearningPO.getId())
                            .setMainTeacherUsername(courseSchedulePO.getTeacherUsername())
                            .setContentType(LIVING.getContentType())
                            .setStartTime(timeInterval.getStart())
                            .setStartTime(timeInterval.getEnd())
                            .setValid("Y")
                            ;
                    SectionsPO sectionsPO1 = sectionsMapper.selectOne(new LambdaQueryWrapper<SectionsPO>()
                            .eq(SectionsPO::getStartTime, sectionsPO.getStartTime())
                            .eq(SectionsPO::getDeadline, sectionsPO.getDeadline())
                    );
                    if(sectionsPO1 != null){
                        // 添加班级进班级映射表
                    }else{
                        // 如果为空 则加入这样的一个 Section 并且把直播资源/点播资源插入进去
                        String onlinePlatform = courseSchedulePO.getOnlinePlatform();

                        if (onlinePlatform == null || !onlinePlatform.matches("\\d+")) {
                            // onlinePlatform 是空或者非纯数字
                            // 在这里处理非数字和空值的逻辑
                        } else {
                            // onlinePlatform 是纯数字
                            // 在这里处理纯数字的逻辑
                            VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(new LambdaQueryWrapper<VideoStreamRecordPO>()
                                    .eq(VideoStreamRecordPO::getId, Long.parseLong(courseSchedulePO.getOnlinePlatform())));

                        }


                        int insert = sectionsMapper.insert(sectionsPO);
                        if(insert < 0){
                            log.error("插入节点失败 " + sectionsPO + "\n"
                                    + coursesLearningPO + " 插入结果 " + insert);
                        }
                    }

                    /**
                     * 获取旧的排课表这条排课记录 所对应的班级
                     */
                    try {
                        ClassInformationPO classInformationPO1 = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                                .eq(ClassInformationPO::getGrade, courseSchedulePO.getGrade())
                                .eq(ClassInformationPO::getMajorName, courseSchedulePO.getMajorName())
                                .eq(ClassInformationPO::getStudyForm, courseSchedulePO.getStudyForm())
                                .eq(ClassInformationPO::getLevel, courseSchedulePO.getLevel())
                                .eq(ClassInformationPO::getClassName, courseSchedulePO.getAdminClass())
                        );
                        if (classInformationPO1 != null) {
                            CoursesClassMappingPO coursesClassMappingPO = new CoursesClassMappingPO()
                                    .setCourseId(coursesLearningPO.getId())
                                    .setClassIdentifier(classInformationPO1.getClassIdentifier())
                                    ;
                            int insert = coursesClassMappingMapper.insert(coursesClassMappingPO);
                            if(insert < 0){
                                log.error("插入班级映射关系失败 " + classInformationPO1 + "\n" +
                                        "插入节点失败 " + sectionsPO + "\n"
                                        + coursesLearningPO + " 插入结果 " + insert);
                            }
                        }
                    }catch (Exception e){
                        log.error("获取班级信息错误 " + e);
                    }



                }catch (Exception e){
                    log.info("该班级找不到课程信息 " + classInformationPO + " \n课程为 " + courseName + e);
                }
            }catch (Exception e){
                log.info("该排课记录找不到班级信息 " + courseSchedulePO + e);
            }

        }
    }
}
