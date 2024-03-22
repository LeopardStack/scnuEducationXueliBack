package com.scnujxjy.backendpoint.coursesLearningTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.*;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.TeachingAssistantsCourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.*;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.TeachingAssistantsCourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.util.tool.ScnuTimeInterval;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
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
    private LiveResourceMapper liveResourceMapper;

    @Resource
    private CourseAssistantsMapper courseAssistantsMapper;

    @Resource
    private TeachingAssistantsCourseScheduleMapper teachingAssistantsCourseScheduleMapper;

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private SectionsMapper sectionsMapper;

    @Resource
    private CoursesClassMappingMapper coursesClassMappingMapper;

    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;

    /**
     * 清除掉新课程学习的数据
     */
    @Test
    public void test0(){
        coursesLearningMapper.truncateTable();
        sectionsMapper.truncateTable();
        coursesClassMappingMapper.truncateTable();
        liveResourceMapper.truncateTable();
        courseAssistantsMapper.truncateTable();
        if(coursesLearningMapper.selectCount(null) == 0 &&
        sectionsMapper.selectCount(null) == 0 &&
        coursesClassMappingMapper.selectCount(null) == 0 &&
                liveResourceMapper.selectCount(null) == 0 &&
                courseAssistantsMapper.selectCount(null) == 0){
            log.info("课程学习核心数据已全部删除");
        }

    }

    @Test
    public void test1(){
        Integer i = courseScheduleMapper.selectCount(null);
        Integer i1 = courseScheduleMapper.selectCount(new LambdaQueryWrapper<CourseSchedulePO>()
                .eq(CourseSchedulePO::getGrade, "2023"));

        log.info("\n排课表总记录数为 "  + i + " 2023级的排课表记录数为 " + i1);

        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(null);

        // 用一个 HashMap 来记录同一批次的 即同一个直播间的
        HashMap<Long, CoursesLearningPO> courseSchedulePOHashMap = new HashMap<>();

        HashMap<CourseSchedulePO, String> errorMsg = new HashMap<>();

        int index = 1;
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
                        coursesLearningPO = coursesLearningPO1;
                    }else{


                        int insert = coursesLearningMapper.insert(coursesLearningPO);
                        if(insert < 0){
                            log.error("插入失败 " + coursesLearningPO + " 插入结果 " + insert);
                            errorMsg.put(courseSchedulePO, "插入失败 " + coursesLearningPO + " 插入结果 " + insert);
                        }

                        courseSchedulePOHashMap.put(courseSchedulePO.getBatchIndex(), coursesLearningPO);
                    }

                    // 插入章节 在插入之前 先查看这堂课是否存在 即时间是否一致，如果一致 说明 需要添加新的班级进来
                    ScnuTimeInterval timeInterval = scnuXueliTools.getTimeInterval(courseSchedulePO.getTeachingDate(), courseSchedulePO.getTeachingTime());

                    SectionsPO sectionsPO = new SectionsPO()
                            .setCourseId(coursesLearningPO1.getId())
                            .setMainTeacherUsername(courseSchedulePO.getTeacherUsername())
                            .setContentType(LIVING.getContentType())
                            .setStartTime(timeInterval.getStart())
                            .setDeadline(timeInterval.getEnd())
                            .setValid("Y")
                            ;
                    SectionsPO sectionsPO1 = sectionsMapper.selectOne(new LambdaQueryWrapper<SectionsPO>()
                            .eq(SectionsPO::getStartTime, sectionsPO.getStartTime())
                            .eq(SectionsPO::getDeadline, sectionsPO.getDeadline())
                            .eq(SectionsPO::getCourseId, sectionsPO.getCourseId())
                    );
                    if(sectionsPO1 != null){
                        // 添加班级进班级映射表
                    }else{
                        // 如果为空 则加入这样的一个 Section 并且把直播资源/点播资源插入进去
                        String onlinePlatform = courseSchedulePO.getOnlinePlatform();

                        if (onlinePlatform == null || !onlinePlatform.matches("\\d+")) {
                            // onlinePlatform 是空或者非纯数字
                            // 在这里处理非数字和空值的逻辑
                            int insert = sectionsMapper.insert(sectionsPO);
                            if(insert < 0){
                                log.error("插入节点失败 " + sectionsPO + "\n"
                                        + coursesLearningPO + " 插入结果 " + insert);
                                errorMsg.put(courseSchedulePO, "插入节点失败 " + sectionsPO + "\n"
                                        + coursesLearningPO + " 插入结果 " + insert);
                            }
                        } else {
                            // onlinePlatform 是纯数字
                            // 在这里处理纯数字的逻辑
                            VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(new LambdaQueryWrapper<VideoStreamRecordPO>()
                                    .eq(VideoStreamRecordPO::getId, Long.parseLong(courseSchedulePO.getOnlinePlatform())));


                            int insert = sectionsMapper.insert(sectionsPO);
                            if(insert < 0){
                                log.error("插入节点失败 " + sectionsPO + "\n"
                                        + coursesLearningPO + " 插入结果 " + insert);
                                errorMsg.put(courseSchedulePO, "插入节点失败 " + sectionsPO + "\n"
                                        + coursesLearningPO + " 插入结果 " + insert);
                            }
                           // 用新的直播资源表存储直播信息
                            LiveResourcesPO liveResourcesPO = new LiveResourcesPO()
                                    .setCourseId(coursesLearningPO.getId())
                                    .setSectionId(sectionsPO.getId())
                                    .setChannelId(videoStreamRecordPO.getChannelId())
                                    ;
                            LiveResourcesPO liveResourcesPO1 = liveResourceMapper.selectOne(new LambdaQueryWrapper<LiveResourcesPO>()
                                    .eq(LiveResourcesPO::getCourseId, coursesLearningPO.getId())
                                    .eq(LiveResourcesPO::getChannelId, videoStreamRecordPO.getChannelId())
                            );
                            if(liveResourcesPO1 == null){
                                int insert1 = liveResourceMapper.insert(liveResourcesPO);
                                errorMsg.put(courseSchedulePO, "插入直播资源失败 " + sectionsPO + "\n"
                                        + coursesLearningPO + "\n" + liveResourcesPO + " 插入结果 " + insert1);

                            }else{
                                liveResourcesPO = liveResourcesPO1;
                            }

                            sectionsPO.setContentId(liveResourcesPO.getId());
                            int i2 = sectionsMapper.updateById(sectionsPO);
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
                            // 要判断是否有重复班级
                            Integer classMappingCount = coursesClassMappingMapper.selectCount(new LambdaQueryWrapper<CoursesClassMappingPO>()
                                    .eq(CoursesClassMappingPO::getCourseId, coursesClassMappingPO.getCourseId())
                                    .eq(CoursesClassMappingPO::getClassIdentifier, coursesClassMappingPO.getClassIdentifier())
                            );
                            if(classMappingCount > 0){
                                // 存在 则不需要反复插入了 针对同一门课里面的班级
                            }else{
                                int insert = coursesClassMappingMapper.insert(coursesClassMappingPO);
                                if(insert < 0){
                                    log.error("插入班级映射关系失败 " + classInformationPO1 + "\n" +
                                            "插入节点失败 " + sectionsPO + "\n"
                                            + coursesLearningPO + " 插入结果 " + insert);
                                    errorMsg.put(courseSchedulePO, "插入班级映射关系失败 " + classInformationPO1 + "\n" +
                                            "插入节点失败 " + sectionsPO + "\n"
                                            + coursesLearningPO + " 插入结果 " + insert);
                                }
                            }
                        }
                    }catch (Exception e){
                        log.error("获取班级信息错误 " + e);
                        errorMsg.put(courseSchedulePO, "获取班级信息错误 " + e);
                    }


                    // 更新课程助教信息
                    Long batchIndex = courseSchedulePO.getBatchIndex();
                    List<TeachingAssistantsCourseSchedulePO> teachingAssistantsCourseSchedulePOS = teachingAssistantsCourseScheduleMapper.
                            selectList(new LambdaQueryWrapper<TeachingAssistantsCourseSchedulePO>().eq(
                                    TeachingAssistantsCourseSchedulePO::getBatchId, batchIndex
                            ));
                    for(TeachingAssistantsCourseSchedulePO teachingAssistantsCourseSchedulePO: teachingAssistantsCourseSchedulePOS){
                        String username = teachingAssistantsCourseSchedulePO.getUsername();
                        CourseAssistantsPO courseAssistantsPO = new CourseAssistantsPO()
                                .setCourseId(coursesLearningPO.getId())
                                .setUsername(username)
                                ;

                        CourseAssistantsPO courseAssistantsPO1 = courseAssistantsMapper.selectOne(new LambdaQueryWrapper<CourseAssistantsPO>()
                                .eq(CourseAssistantsPO::getCourseId, coursesLearningPO.getId())
                                .eq(CourseAssistantsPO::getUsername, username)
                        );

                        // 一门课里已存在的助教 不重复插入
                        if(courseAssistantsPO1 == null){
                            int insert = courseAssistantsMapper.insert(courseAssistantsPO);
                            if(insert < 0){
                                log.error("插入助教信息失败 " + teachingAssistantsCourseSchedulePO + "\n" +
                                        "插入节点失败 " + sectionsPO + "\n"
                                        + coursesLearningPO + " 插入结果 " + insert);
                                errorMsg.put(courseSchedulePO, "插入助教信息失败 " + teachingAssistantsCourseSchedulePO + "\n" +
                                        "插入节点失败 " + sectionsPO + "\n"
                                        + coursesLearningPO + " 插入结果 " + insert);
                            }
                        }


                    }



                }catch (Exception e){
                    log.info("该班级找不到课程信息 " + classInformationPO + " \n课程为 " + courseName + e);
                    errorMsg.put(courseSchedulePO, "该班级找不到课程信息 " + classInformationPO + " \n课程为 " + courseName + e);
                }
            }catch (Exception e){
                log.info("该排课记录找不到班级信息 " + courseSchedulePO + e);
                errorMsg.put(courseSchedulePO, "该排课记录找不到班级信息 " + courseSchedulePO + e);
            }

            log.info(index + "   " + courseSchedulePO);
            index += 1;

        }

        log.info("\n排课表总记录数为 "  + i + " 2023级的排课表记录数为 " + i1);
        if(errorMsg.keySet().isEmpty()){
            log.info("排课表所有记录全部插入成功");
        }else{
            log.info("排课表记录更新到新的课程学习数据库出现了部分错误 \n" + errorMsg);
        }
    }

    /**
     * 对每个课程的章节按 startTime 排序
     */
    @Test
    public void testSetSectionsSequence() {
        List<CoursesLearningPO> courses = coursesLearningMapper.selectList(null);

        for (CoursesLearningPO course : courses) {
            // 获取课程对应的所有章节
            List<SectionsPO> sections = sectionsMapper.selectList(
                    new LambdaQueryWrapper<SectionsPO>()
                            .eq(SectionsPO::getCourseId, course.getId())
                            .orderByAsc(SectionsPO::getStartTime)
            );

            // 更新 sequence
            for (int i = 0; i < sections.size(); i++) {
                SectionsPO section = sections.get(i);
                section.setSequence(i + 1); // 设置序列号，从1开始
                if(StringUtils.isBlank(section.getSectionName())){
                    section.setSectionName("第 " + (i+1) + " 次课");
                }
                sectionsMapper.updateById(section);
            }
        }
    }


    /**
     * 模拟旧的排课表 来查询 每门课的排课情况
     */
    @Test
    public void test3(){

    }
}
