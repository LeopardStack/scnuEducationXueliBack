package com.scnujxjy.backendpoint.examTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.scnujxjy.backendpoint.constant.enums.CourseContentType;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseAssistantsPO;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamAssistantsPO;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
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
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseClassMappingRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseLearningVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CoursesInfoByClassMappingVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import com.scnujxjy.backendpoint.service.courses_learning.CourseAssignmentsService;
import com.scnujxjy.backendpoint.service.courses_learning.CourseAssistantsService;
import com.scnujxjy.backendpoint.service.courses_learning.CoursesClassMappingService;
import com.scnujxjy.backendpoint.service.courses_learning.CoursesLearningService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        int newCount = 0;
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

//                    throw new IllegalArgumentException("出现了同一条教学计划有多个主讲" + teachers + "\n" + courseInformationPO);
                    log.error("出现了同一条教学计划有多个主讲" + teachers + "\n" + courseInformationPO);
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
                // 检查一下 这条教学计划在不在考试信息里面 如果在 则更新它
                List<CourseExamInfoPO> courseExamInfoPO1 = examInfoMapper.selectList(new LambdaQueryWrapper<CourseExamInfoPO>()
                        .eq(CourseExamInfoPO::getCourse, courseExamInfoPO.getCourse())
                        .eq(CourseExamInfoPO::getCourse, courseExamInfoPO.getCourse())
                        .eq(CourseExamInfoPO::getClassIdentifier, courseExamInfoPO.getClassIdentifier())
                );

                if(courseExamInfoPO1 != null && !courseExamInfoPO1.isEmpty()){
                    // 不需要更新教学计划相关信息，因为课程名称和班级标识是固定的
                    if(courseExamInfoPO1.size() > 1){
                        // 先检测能否用学期锁死
                        List<CourseExamInfoPO> courseExamInfoPO2 = examInfoMapper.selectList(new LambdaQueryWrapper<CourseExamInfoPO>()
                                .eq(CourseExamInfoPO::getCourse, courseExamInfoPO.getCourse())
                                .eq(CourseExamInfoPO::getCourse, courseExamInfoPO.getCourse())
                                .eq(CourseExamInfoPO::getTeachingSemester, courseExamInfoPO.getTeachingSemester())
                                .eq(CourseExamInfoPO::getClassIdentifier, courseExamInfoPO.getClassIdentifier())
                        );
                        if(courseExamInfoPO2.isEmpty()){
                            // 如果同一个班出现多门一模一样的课程名称，用学期来锁死 先把系统里存在的考试相关的教学计划删除掉 然后补充 学期字段
                            int delete = examInfoMapper.delete(new LambdaQueryWrapper<CourseExamInfoPO>()
                                    .eq(CourseExamInfoPO::getCourse, courseExamInfoPO.getCourse())
                                    .eq(CourseExamInfoPO::getCourse, courseExamInfoPO.getCourse())
                                    .eq(CourseExamInfoPO::getClassIdentifier, courseExamInfoPO.getClassIdentifier()));
                            log.info("删除掉没有学期的教学计划 " + delete);
                            newCount += 1;
                            // 设置学期信息
                            courseExamInfoPO.setTeachingSemester(courseInformationPO.getTeachingSemester());
                            int insert = examInfoMapper.insert(courseExamInfoPO);
                            if (insert <= 0) {
                                log.error("插入考试记录失败 插入结果  " + insert + " \n" + courseExamInfoPO);
                            }


                            // 更新阅卷助教

                            for (TeachingAssistantsCourseSchedulePO teachingAssistantsCourseSchedulePO : teachingAssistantsCourseSchedulePOSet) {
                                String username = teachingAssistantsCourseSchedulePO.getUsername();
                                TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                                        .eq(TeacherInformationPO::getTeacherUsername, username));
                                teachingAssistants.add(teacherInformationPO.getName() + " " + username);
                            }
                            for (String tutor : teachingAssistants) {
                                // 将助教更新到阅卷助教表
                                String[] parts = tutor.split(" ");
                                tutorTeacherName = parts[0];
                                tutorTeacherUserName = parts[1];
                                CourseExamAssistantsPO courseExamAssistantsPO = new CourseExamAssistantsPO();
                                courseExamAssistantsPO.setAssistantName(tutorTeacherName);
                                courseExamAssistantsPO.setTeacherUsername(tutorTeacherUserName);
                                courseExamAssistantsPO.setCourseId(courseExamInfoPO.getId());

                                int insert1 = courseExamAssistantsMapper.insert(courseExamAssistantsPO);
                                if(insert1 <= 0){
                                    log.error("插入阅卷助教失败 插入结果  " + insert1 + " \n" + courseExamAssistantsPO);
                                }
                            }
                        }else if(courseExamInfoPO2.size() > 1){
                            log.error("插入考试记录失败 该教学计划存在多份，并且使用班级标识、授课学期、课程名称也锁不死一条记录\n  " + courseExamInfoPO1);
                        }


                    }
                }
                else {
                    newCount += 1;
                    int insert = examInfoMapper.insert(courseExamInfoPO);
                    if (insert <= 0) {
                        log.error("插入考试记录失败 插入结果  " + insert + " \n" + courseExamInfoPO);
                    }


                    // 更新阅卷助教

                    for (TeachingAssistantsCourseSchedulePO teachingAssistantsCourseSchedulePO : teachingAssistantsCourseSchedulePOSet) {
                        String username = teachingAssistantsCourseSchedulePO.getUsername();
                        TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                                .eq(TeacherInformationPO::getTeacherUsername, username));
                        teachingAssistants.add(teacherInformationPO.getName() + " " + username);
                    }
                    for (String tutor : teachingAssistants) {
                        // 将助教更新到阅卷助教表
                        String[] parts = tutor.split(" ");
                        tutorTeacherName = parts[0];
                        tutorTeacherUserName = parts[1];
                        CourseExamAssistantsPO courseExamAssistantsPO = new CourseExamAssistantsPO();
                        courseExamAssistantsPO.setAssistantName(tutorTeacherName);
                        courseExamAssistantsPO.setTeacherUsername(tutorTeacherUserName);
                        courseExamAssistantsPO.setCourseId(courseExamInfoPO.getId());

                    int insert1 = courseExamAssistantsMapper.insert(courseExamAssistantsPO);
                    if(insert1 <= 0){
                        log.error("插入阅卷助教失败 插入结果  " + insert1 + " \n" + courseExamAssistantsPO);
                    }
                    }
                }

            }
        }

        log.info("新增 " + newCount + " 条教学计划");


    }

    /**
     * 将考试信息加入授课学期字段 进行课程的锁死
     *
     */
    @Test
    public void test2(){
        List<CourseExamInfoPO> courseExamInfoPOS = examInfoMapper.selectList(null);
        for(CourseExamInfoPO courseExamInfoPO: courseExamInfoPOS){
            List<CourseInformationPO> courseInformationPOS = courseInformationMapper.selectList(new LambdaQueryWrapper<CourseInformationPO>()
                    .eq(CourseInformationPO::getAdminClass, courseExamInfoPO.getClassIdentifier())
                    .eq(CourseInformationPO::getCourseName, courseExamInfoPO.getCourse())
            );
            if(courseInformationPOS.size() > 1){
                List<CourseInformationPO> courseInformationPOS1 = courseInformationMapper.selectList(new LambdaQueryWrapper<CourseInformationPO>()
                        .eq(CourseInformationPO::getAdminClass, courseExamInfoPO.getClassIdentifier())
                        .eq(CourseInformationPO::getCourseName, courseExamInfoPO.getCourse())
                        .eq(CourseInformationPO::getTeachingSemester, courseExamInfoPO.getTeachingSemester())
                );
                if(courseInformationPOS1.isEmpty()){
                    log.error("该考试信息无法找到教学计划 " + courseExamInfoPO);
                }else if(courseInformationPOS1.size() > 1){
                    log.error("该考试信息通过学期、班级标识、课程还是找到了多条记录 " + courseExamInfoPO);
                }
            }else if(courseInformationPOS.size() == 1){
                CourseInformationPO courseInformationPO = courseInformationPOS.get(0);
                UpdateWrapper<CourseExamInfoPO> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("teaching_semester", courseInformationPO.getTeachingSemester())
                        .eq("id", courseExamInfoPO.getId());

                int i = examInfoMapper.update(null, updateWrapper);
            }else{
                log.error("该考试信息无法找到教学计划 " + courseExamInfoPO);
            }
        }
    }


    // 课程学习模块经过了升级  目前需要从 courses_learning courses_class_mapping
    // course_assistants 获取课程学习数据、班级映射数据、主讲老师信息、助教信息

    @Resource
    private CoursesLearningService coursesLearningService;

    @Resource
    private CoursesClassMappingService coursesClassMappingService;

    @Resource
    private CourseAssistantsService courseAssistantsService;


    /**
     * 看一下多少的考试信息被修改了教学计划
     */
    @Test
    public void checkExamInfoToTeachingPlansInfo(){
        List<CourseExamInfoPO> courseExamInfoPOS = examInfoMapper.selectList(null);
        for(CourseExamInfoPO courseExamInfoPO : courseExamInfoPOS){
            String classIdentifier = courseExamInfoPO.getClassIdentifier();
            List<CourseInformationPO> courseInformationPOS = courseInformationMapper.selectList(new LambdaQueryWrapper<CourseInformationPO>()
                    .eq(CourseInformationPO::getAdminClass, classIdentifier)
                    .eq(CourseInformationPO::getCourseName, courseExamInfoPO.getClassName())
            );
            if(courseExamInfoPO.getExamMethod().equals("机考")){
                // 目前只考虑去年设置过机考的
                if(courseInformationPOS.isEmpty()){
                    log.error("该门课的考试信息 找不到当初的教学计划信息 " + courseExamInfoPO);
                }
                if(courseInformationPOS.size() > 1){
                    log.error("该门考试信息中的班级和课程名称在教学计划中可以找到 1 条以上的教学计划 " + courseExamInfoPO);
                }
            }

        }
    }

    /**
     * 清空机考信息 还有考试助教信息
     */
    @Test
    public void clearExamInfo(){
        examInfoMapper.truncateTableInfo();
        courseExamAssistantsMapper.truncateTableInfo();
    }

    /**
     * 导入机考信息
     */
    @Test
    public void importExamInfoFromCourseLearning(){
        for(int grade = 2024; grade >= 2020; grade --) {
            List<CourseInformationVO> courseInformationVOList = courseInformationMapper.
                    selectCourseInformationWithClassInfo(new CourseInformationRO().setGrade(""+ grade));

            for(CourseInformationVO courseInformationVO : courseInformationVOList){
                CourseExamInfoPO courseExamInfoPO = new CourseExamInfoPO()
                        .setClassName(courseInformationVO.getClassName())
                        .setClassIdentifier(courseInformationVO.getAdminClass())
                        .setCourse(courseInformationVO.getCourseName())
                        .setExamMethod("线下")    // 默认值
                        .setExamType("闭卷")  // 默认值
                        .setIsValid("Y")   // 默认有效
                        .setExamStatus("未开始")   // 默认有效
                        .setTeachingSemester(courseInformationVO.getTeachingSemester())
                         ;
                // 查看课程学习表 看是否有主讲信息、助教信息 有的话 直接写入
                CoursesInfoByClassMappingVO coursesInfoByClassMappingVO = null;
                List<CoursesInfoByClassMappingVO> coursesInfoByClassMappingVOS = coursesClassMappingService.getBaseMapper()
                        .selectCoursesInfo(new CourseClassMappingRO()
                        .setCourseName(courseInformationVO.getCourseName())
                        .setClassIdentifier(courseInformationVO.getAdminClass())
                );
                if(!coursesInfoByClassMappingVOS.isEmpty()){
                    if(coursesInfoByClassMappingVOS.size() > 1){
                        log.error("该门教学计划存在多个课程学习记录 "+ courseInformationVO);
                    }
                    coursesInfoByClassMappingVO = coursesInfoByClassMappingVOS.get(0);
                }

                // 设置主讲教师信息
                if (coursesInfoByClassMappingVO != null) {

                    if(!coursesInfoByClassMappingVO.getCourseType().equals(CourseContentType.LIVING.getContentType())){
                        continue;
                    }
                    courseExamInfoPO.setCourseId(coursesInfoByClassMappingVO.getCourseId());
                    courseExamInfoPO.setExamMethod("机考");

                    String defaultMainTeacherUsername = coursesInfoByClassMappingVO.getDefaultMainTeacherUsername();
                    TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                            .eq(TeacherInformationPO::getTeacherUsername, defaultMainTeacherUsername));
                    if(teacherInformationPO != null){
                        courseExamInfoPO.setTeacherUsername(teacherInformationPO.getTeacherUsername());
                        courseExamInfoPO.setMainTeacher(teacherInformationPO.getName());
                    }else{
                        if(coursesInfoByClassMappingVO.getCourseType().equals(CourseContentType.LIVING.getContentType())){
                            log.error("该直播课没有设置主讲老师 " + coursesInfoByClassMappingVO + "\n 教学计划为 "
                                    + courseInformationVO);
                        }
                    }

                    int insert = examInfoMapper.insert(courseExamInfoPO);
                    if(insert <= 0){
                        log.error("数据库插入考试信息失败 " + courseExamInfoPO);
                    }

                    // 设置考试助教信息
                    List<CourseAssistantsPO> courseAssistantsPOS = courseAssistantsService.getBaseMapper().selectList(new LambdaQueryWrapper<CourseAssistantsPO>()
                            .eq(CourseAssistantsPO::getCourseId, coursesInfoByClassMappingVO.getCourseId()));

                    for(CourseAssistantsPO courseAssistantsPO : courseAssistantsPOS){
                        TeacherInformationPO teacherInformationPO1 = null;
                        try{
                            teacherInformationPO1 = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                                    .eq(TeacherInformationPO::getTeacherUsername, courseAssistantsPO.getUsername()));
                        }catch (Exception e){
                            log.error("获取考试助教信息失败 " + e);
                        }
                        if(teacherInformationPO1 != null){
                            CourseExamAssistantsPO courseExamAssistantsPO = new CourseExamAssistantsPO()
                                    .setCourseId(coursesInfoByClassMappingVO.getCourseId())
                                    .setExamId(courseExamInfoPO.getId())
                                    .setTeacherUsername(courseAssistantsPO.getUsername())
                                    .setAssistantName(teacherInformationPO1.getName())
                                    ;

                            CourseExamAssistantsPO courseExamAssistantsPO1 = courseExamAssistantsMapper.selectOne(new LambdaQueryWrapper<CourseExamAssistantsPO>()
                                    .eq(CourseExamAssistantsPO::getCourseId, coursesInfoByClassMappingVO.getCourseId())
                                    .eq(CourseExamAssistantsPO::getExamId, courseExamInfoPO.getId())
                                    .eq(CourseExamAssistantsPO::getTeacherUsername,
                                            courseExamAssistantsPO.getTeacherUsername())
                            );
                            if (courseExamAssistantsPO1 != null) {
                                // 无需对同一门课的助教重复插入
                            }else{
                                int insert1 = courseExamAssistantsMapper.insert(courseExamAssistantsPO);
                                if(insert1 <= 0){
                                    log.error("插入考试助教信息失败");
                                }
                            }
                        }
                    }

                }



            }


        }
    }


    @Test
    public void importExamInfoFromCourseLearningBetter() {
        ExecutorService executorService = Executors.newFixedThreadPool(100);

        for (int grade = 2024; grade >= 2020; grade--) {
            List<CourseInformationVO> courseInformationVOList = courseInformationMapper
                    .selectCourseInformationWithClassInfo(new CourseInformationRO().setGrade("" + grade));
            int size = courseInformationVOList.size();
            int partSize = size < 100 ? 1 : size / 100;

            CountDownLatch latch = new CountDownLatch(100);
            for (int i = 0; i < 100; i++) {
                final int finalI = i;
                executorService.submit(() -> {
                    try {
                        int start = finalI * partSize;
                        int end = (finalI + 1) * partSize;
                        if (finalI == 99) { // last thread takes care of the remainder
                            end = size;
                        }

                        for (int j = start; j < end; j++) {
                            CourseInformationVO courseInformationVO = courseInformationVOList.get(j);
                            processCourseInformation(courseInformationVO);
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            try {
                latch.await(); // Wait for all threads to finish
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted", e);
            }
        }

        executorService.shutdown();
    }

    private void processCourseInformation(CourseInformationVO courseInformationVO) {
        CourseExamInfoPO courseExamInfoPO = new CourseExamInfoPO()
                .setClassName(courseInformationVO.getClassName())
                .setClassIdentifier(courseInformationVO.getAdminClass())
                .setCourse(courseInformationVO.getCourseName())
                .setExamMethod("线下")    // 默认值
                .setExamType("闭卷")  // 默认值
                .setIsValid("Y")   // 默认有效
                .setTeachingSemester(courseInformationVO.getTeachingSemester());

        // 查看课程学习表 看是否有主讲信息、助教信息
        List<CoursesInfoByClassMappingVO> coursesInfoByClassMappingVOS = coursesClassMappingService.getBaseMapper()
                .selectCoursesInfo(new CourseClassMappingRO()
                        .setCourseName(courseInformationVO.getCourseName())
                        .setClassIdentifier(courseInformationVO.getAdminClass()));

        if (!coursesInfoByClassMappingVOS.isEmpty()) {
            CoursesInfoByClassMappingVO coursesInfoByClassMappingVO = coursesInfoByClassMappingVOS.get(0);
            if (coursesInfoByClassMappingVOS.size() > 1) {
                log.error("该门教学计划存在多个课程学习记录 " + courseInformationVO);
            }

            // 设置主讲教师信息
            if (coursesInfoByClassMappingVO != null) {
                String defaultMainTeacherUsername = coursesInfoByClassMappingVO.getDefaultMainTeacherUsername();
                TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                        .eq(TeacherInformationPO::getTeacherUsername, defaultMainTeacherUsername));
                if (teacherInformationPO != null) {
                    courseExamInfoPO.setTeacherUsername(teacherInformationPO.getTeacherUsername());
                    courseExamInfoPO.setMainTeacher(teacherInformationPO.getName());
                } else {
                    if (coursesInfoByClassMappingVO.getCourseType().equals(CourseContentType.LIVING.getContentType())) {
                        log.error("该直播课没有设置主讲老师 " + coursesInfoByClassMappingVO + "\n 教学计划为 "
                                + courseInformationVO);
                    }
                }
            }
        }

        int insert = examInfoMapper.insert(courseExamInfoPO);
        if (insert <= 0) {
            log.error("数据库插入考试信息失败 " + courseExamInfoPO);
        }
    }
}
