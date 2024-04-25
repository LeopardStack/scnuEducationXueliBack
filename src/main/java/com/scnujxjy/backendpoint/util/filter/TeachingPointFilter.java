package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.constant.enums.LiveStatusEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamAssistantsPO;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.exam.ExamFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.exam.ExamInfoVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import com.scnujxjy.backendpoint.util.tool.LogExecutionTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@Slf4j
public class TeachingPointFilter extends AbstractFilter {

    /**
     * 根据loginId获取教学点班级信息
     *
     * @return
     */
    private Set<String> getTeachingPointClassNameSet() {
        // 通过username查询userId，再查询对应教学点
        String loginId = StpUtil.getLoginIdAsString();
        if (StrUtil.isBlank(loginId)) {
            throw new BusinessException("获取用户id失败");
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery()
                .eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            throw new BusinessException("获取用户信息失败");
        }
        Long userId = platformUserPO.getUserId();
        // 获取教学点教务员的 teaching_id 进而他所管理的教学点的简称
        Set<String> classNameSet = new HashSet<>();
        List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationMapper.selectList(Wrappers.<TeachingPointAdminInformationPO>lambdaQuery()
                .eq(TeachingPointAdminInformationPO::getUserId, userId));
        for (TeachingPointAdminInformationPO teachingPointAdminInformationPO : teachingPointAdminInformationPOS) {
            String teachingPointId = teachingPointAdminInformationPO.getTeachingPointId();
            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.selectOne(Wrappers.<TeachingPointInformationPO>lambdaQuery()
                    .eq(TeachingPointInformationPO::getTeachingPointId, teachingPointId));
            String alias = teachingPointInformationPO.getAlias();
            classNameSet.add(alias);
        }
        if (CollUtil.isEmpty(classNameSet)) {
            throw new BusinessException("查询班级集合为空，查询失败");
        }
        return classNameSet;
    }

    /**
     * 分页条件查询指定教学点学生成绩；
     *
     * @param scoreInformationFilterROPageRO 分页条件查询参数，筛选项：学号、课程名称
     * @return 分页条件查询后的学生成绩
     */
    public FilterDataVO<ScoreInformationVO> filterGradeInfo(PageRO<ScoreInformationFilterRO> scoreInformationFilterROPageRO) {
        Set<String> classNameSet = getTeachingPointClassNameSet();
        scoreInformationFilterROPageRO.getEntity().setClassNameSet(classNameSet);
        Long count = scoreInformationMapper.getTeachingPointStudentGradeInfoByFilterCount(scoreInformationFilterROPageRO.getEntity());
        List<ScoreInformationVO> scoreInformationVOS = scoreInformationMapper.getTeachingPointStudentGradeInfoByFilter(scoreInformationFilterROPageRO.getEntity(),
                scoreInformationFilterROPageRO.getPageSize(),
                scoreInformationFilterROPageRO.getPageSize() * (scoreInformationFilterROPageRO.getPageNumber() - 1));
        return new FilterDataVO<>(scoreInformationVOS, count);
    }

    /**
     * 根据教学点查询学生的学籍信息;
     * 学籍信息：StudentStatus + PersonalInformation
     *
     * @param studentStatusFilter 条件分页查询参数
     * @return 条件分页查询学生学籍信息结果
     */
    @Override
    public FilterDataVO<StudentStatusAllVO> filterStudentStatus(PageRO<StudentStatusFilterRO> studentStatusFilter) {
        FilterDataVO<StudentStatusAllVO> studentStatusVOFilterDataVO = new FilterDataVO<>();
        // 通过username查询userId，再查询对应教学点
        String loginId = StpUtil.getLoginIdAsString();
        if (StrUtil.isBlank(loginId)) {
            throw new BusinessException("获取用户id失败");
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery()
                .eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            throw new BusinessException("获取用户信息失败");
        }
        Long userId = platformUserPO.getUserId();
        // 获取教学点教务员的 teaching_id 进而他所管理的教学点的简称
        Set<String> classNames = new HashSet<>();
        List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationMapper.selectList(new LambdaQueryWrapper<TeachingPointAdminInformationPO>()
                .eq(TeachingPointAdminInformationPO::getUserId, userId));
        for (TeachingPointAdminInformationPO teachingPointAdminInformationPO : teachingPointAdminInformationPOS) {
            String teachingPointId = teachingPointAdminInformationPO.getTeachingPointId();
            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                    .eq(TeachingPointInformationPO::getTeachingPointId, teachingPointId));
            String alias = teachingPointInformationPO.getAlias();
            classNames.add(alias);
        }


        studentStatusFilter.getEntity().setClassNames(classNames);

        log.info("学籍数据查询参数 " + studentStatusFilter.getEntity());
        // 使用 courseInformationMapper 获取数据
        List<StudentStatusAllVO> studentStatusVOS = studentStatusMapper.selectByFilterAndPageByTeachingPoint(studentStatusFilter.getEntity(),
                studentStatusFilter.getPageSize(),
                studentStatusFilter.getPageSize() * (studentStatusFilter.getPageNumber() - 1));
        long total = studentStatusMapper.selectByFilterAndPageByTeachingPointCount(studentStatusFilter.getEntity());
        studentStatusVOFilterDataVO.setData(studentStatusVOS);
        studentStatusVOFilterDataVO.setTotal(total);

        return studentStatusVOFilterDataVO;
    }

    /**
     * 根据教学点查询学生缴费信息；
     * 支持条件查询和分页查询；
     * 年级、学院、专业名称、层次、学习形式 、行政班别、学号
     *
     * @param paymentInfoFilterROPageRO 条件查询分页查询参数
     * @return 分页查询条件查询结果
     */
    @Override
    public FilterDataVO<PaymentInfoVO> filterPayInfo(PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO) {
        Set<String> classNameSet = getTeachingPointClassNameSet();
        paymentInfoFilterROPageRO.getEntity().setClassNameSet(classNameSet);
        List<PaymentInfoVO> paymentInfoVOS = paymentInfoMapper.getTeachingPointStudentPayInfoByFilter(paymentInfoFilterROPageRO.getEntity(),
                paymentInfoFilterROPageRO.getPageSize(),
                paymentInfoFilterROPageRO.getPageSize() * (paymentInfoFilterROPageRO.getPageNumber() - 1));
        Long size = paymentInfoMapper.getTeachingPointStudentPayInfoByFilterCount(paymentInfoFilterROPageRO.getEntity());
        return new FilterDataVO<>(paymentInfoVOS, size);
    }

    /**
     * 根据教学点查询教学计划信息；
     * 支持条件查询和分页查询；
     * 筛选条件：年级、学院、专业名称、层次、学习形式、行政班别、课程名称
     *
     * @param courseInformationROPageRO 条件查询分页查询参数
     * @return 分页查询条件查询结果
     */
    @Override
    public FilterDataVO<CourseInformationVO> filterCourseInformation(PageRO<CourseInformationRO> courseInformationROPageRO) {
        Set<String> classNameSet = getTeachingPointClassNameSet();
        courseInformationROPageRO.getEntity().setClassNameSet(classNameSet);
        List<CourseInformationVO> courseInformationVOS = courseInformationMapper.selectTeachingPointByFilterAndPage(courseInformationROPageRO.getEntity(),
                courseInformationROPageRO.getPageSize(),
                courseInformationROPageRO.getPageSize() * (courseInformationROPageRO.getPageNumber() - 1));
        Long count = courseInformationMapper.selectTeachingPointByFilterAndPageCount(courseInformationROPageRO.getEntity());
        return new FilterDataVO<>(courseInformationVOS, count);
    }

    /**
     * 条件查询分页查询指定教学点排课信息
     *
     * @param courseScheduleFilterROPageRO 排课表分页条件查询参数
     * @return 分页条件查询指定教学点排课信息结果
     */
    public FilterDataVO filterSchedulesInformation(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        Set<String> classNameSet = getTeachingPointClassNameSet();
        List<String> classNameList = new ArrayList<>(classNameSet);
        courseScheduleFilterROPageRO.getEntity().setClassSet(classNameList);

        List<SchedulesVO> schedulesVOS = courseScheduleMapper.selectSchedulesInformation(
                courseScheduleFilterROPageRO.getEntity(),
                courseScheduleFilterROPageRO.getPageSize(),
                (courseScheduleFilterROPageRO.getPageNumber() - 1) * courseScheduleFilterROPageRO.getPageSize());

        for (SchedulesVO schedulesVO : schedulesVOS) {
            String onlinePlatform = schedulesVO.getOnlinePlatform();

            if (onlinePlatform != null) {
                VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(
                        new LambdaQueryWrapper<VideoStreamRecordPO>().eq(VideoStreamRecordPO::getId, onlinePlatform));

                if (videoStreamRecordPO != null) {
                    // 此处你只检查了videoStreamRecordPO是否为null，但没使用它的其他属性。
                    // 假设你只想检查它是否存在，并据此设置onlinePlatform
                    // 设置直播状态
                    schedulesVO.setLivingStatus(videoStreamRecordPO.getWatchStatus());
                    schedulesVO.setChannelId(videoStreamRecordPO.getChannelId());
                }
            } else {
                schedulesVO.setLivingStatus("未开播");
            }
        }


        FilterDataVO<SchedulesVO> filterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId() + " 查询排课表课程信息的参数是 " + courseScheduleFilterROPageRO);

        long l = courseScheduleMapper.selectCoursesInformationCount(courseScheduleFilterROPageRO.getEntity());
        filterDataVO.setTotal(l);
        filterDataVO.setData(schedulesVOS);

        return filterDataVO;
    }

    /**
     * 获取教学点学生选择参数
     *
     * @return
     */
    @Override
    public StudentStatusSelectArgs filterStudentStatusSelectArgs() {
        StudentStatusSelectArgs studentStatusSelectArgs = new StudentStatusSelectArgs();
        StudentStatusFilterRO studentStatusFilterRO = new StudentStatusFilterRO();
        studentStatusFilterRO.setClassNames(getTeachingPointClassNameSet());
        List<String> distinctGrades = studentStatusMapper.getDistinctGradesByTeachingPoint(studentStatusFilterRO);
        List<String> majorNames = studentStatusMapper.getDistinctMajorNamesByTeachingPoint(studentStatusFilterRO);
        List<String> levels = studentStatusMapper.getDistinctLevelsByTeachingPoint(studentStatusFilterRO);
        List<String> studyForms = studentStatusMapper.getDistinctStudyFormsByTeachingPoint(studentStatusFilterRO);
        List<String> classNames = studentStatusMapper.getDistinctClassNamesByTeachingPoint(studentStatusFilterRO);
        List<String> studyDurations = studentStatusMapper.getDistinctStudyDurationsByTeachingPoint(studentStatusFilterRO);
        List<String> academicStatuss = studentStatusMapper.getDistinctAcademicStatussByTeachingPoint(studentStatusFilterRO);

        studentStatusSelectArgs.setGrades(distinctGrades);
        studentStatusSelectArgs.setMajorNames(majorNames);
        studentStatusSelectArgs.setLevels(levels);
        studentStatusSelectArgs.setClassNames(classNames);
        studentStatusSelectArgs.setStudyForms(studyForms);
        studentStatusSelectArgs.setStudyDurations(studyDurations);
        studentStatusSelectArgs.setAcademicStatus(academicStatuss);

        return studentStatusSelectArgs;
    }

    /**
     * 获取学生缴费信息筛选选项
     *
     * @return
     */
    @Override
    public PaymentInformationSelectArgs filterPaymentInformationSelectArgs() {
        PaymentInformationSelectArgs paymentInformationSelectArgs = new PaymentInformationSelectArgs();
        PaymentInfoFilterRO filter = new PaymentInfoFilterRO();
        filter.setClassNameSet(getTeachingPointClassNameSet());
        ExecutorService executor = Executors.newFixedThreadPool(9); // 9 代表你有9个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> paymentInfoMapper.getDistinctGrades(filter));
        Future<List<String>> distinctLevelsFuture = executor.submit(() -> paymentInfoMapper.getDistinctLevels(filter));
        Future<List<String>> distinctStudyFormsFuture = executor.submit(() -> paymentInfoMapper.getDistinctStudyForms(filter));
        Future<List<String>> distinctClassNamesFuture = executor.submit(() -> paymentInfoMapper.getDistinctClassNames(filter));
        Future<List<String>> distinctTeachingPointsFuture = executor.submit(() -> paymentInfoMapper.getDistinctTeachingPoints(filter));
        Future<List<String>> distinctCollegeNamesFuture = executor.submit(() -> paymentInfoMapper.getDistinctCollegeNames(filter));
        Future<List<String>> distinctAcademicYearsFuture = executor.submit(() -> paymentInfoMapper.getDistinctAcademicYears(filter));
        Future<List<String>> distinctRemarksFuture = executor.submit(() -> paymentInfoMapper.getDistinctRemarks(filter));
        Future<List<String>> distinctMajorNamesFuture = executor.submit(() -> paymentInfoMapper.getDistinctMajorNames(filter));

        try {
            paymentInformationSelectArgs.setGrades(distinctGradesFuture.get());
            paymentInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            paymentInformationSelectArgs.setStudyForms(distinctStudyFormsFuture.get());
            paymentInformationSelectArgs.setClassNames(distinctClassNamesFuture.get());
            paymentInformationSelectArgs.setTeachingPoints(distinctTeachingPointsFuture.get());
            paymentInformationSelectArgs.setCollegeNames(distinctCollegeNamesFuture.get());
            paymentInformationSelectArgs.setAcademicYears(distinctAcademicYearsFuture.get());
            paymentInformationSelectArgs.setRemarks(distinctRemarksFuture.get());
            paymentInformationSelectArgs.setMajorNames(distinctMajorNamesFuture.get());

        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return paymentInformationSelectArgs;
    }

    /**
     * 获取学生成绩缴费筛选
     *
     * @return
     */
    @Override
    public ScoreInformationSelectArgs filterScoreInformationSelectArgs() {
        ScoreInformationSelectArgs scoreInformationSelectArgs = new ScoreInformationSelectArgs();
        ScoreInformationFilterRO filter = new ScoreInformationFilterRO();
        filter.setClassNameSet(getTeachingPointClassNameSet());
        ExecutorService executor = Executors.newFixedThreadPool(8); // 8 代表你有8个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> scoreInformationMapper.getDistinctGrades(filter));
        Future<List<String>> majorNamesFuture = executor.submit(() -> scoreInformationMapper.getDistinctMajorNames(filter));
        Future<List<String>> levelsFuture = executor.submit(() -> scoreInformationMapper.getDistinctLevels(filter));
        Future<List<String>> studyFormsFuture = executor.submit(() -> scoreInformationMapper.getDistinctStudyForms(filter));
        Future<List<String>> classNamesFuture = executor.submit(() -> scoreInformationMapper.getDistinctClassNames(filter));
        Future<List<String>> courseNamesFuture = executor.submit(() -> scoreInformationMapper.getDistinctCourseNames(filter));
        Future<List<String>> statusesFuture = executor.submit(() -> scoreInformationMapper.getDistinctStatus(filter));

        try {
            scoreInformationSelectArgs.setGrades(distinctGradesFuture.get());
            scoreInformationSelectArgs.setMajorNames(majorNamesFuture.get());
            scoreInformationSelectArgs.setLevels(levelsFuture.get());
            scoreInformationSelectArgs.setStudyForms(studyFormsFuture.get());
            scoreInformationSelectArgs.setClassNames(classNamesFuture.get());
            scoreInformationSelectArgs.setCourseNames(courseNamesFuture.get());
            scoreInformationSelectArgs.setStatuses(statusesFuture.get());
        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return scoreInformationSelectArgs;
    }

    /**
     * 获取教学计划筛选项
     *
     * @return
     */
    @Override
    public CourseInformationSelectArgs filterCourseInformationSelectArgs() {
        CourseInformationSelectArgsManagerZero courseInformationSelectArgs = new CourseInformationSelectArgsManagerZero();
        Set<String> classNameSet = getTeachingPointClassNameSet();
        CourseInformationRO courseInformationRO = new CourseInformationRO();
        courseInformationRO.setClassNameSet(classNameSet);

        List<String> grades = courseInformationMapper.selectDistinctGrades(courseInformationRO);
        List<String> majorNames = courseInformationMapper.selectDistinctMajorNames(courseInformationRO);
        List<String> levels = courseInformationMapper.selectDistinctLevels(courseInformationRO);
        List<String> courseNames = courseInformationMapper.selectDistinctCourseNames(courseInformationRO);
        List<String> studyForms = courseInformationMapper.selectDistinctStudyForms(courseInformationRO);
        List<String> classNames = courseInformationMapper.selectDistinctClassNames(courseInformationRO);
        List<String> collegeNames = courseInformationMapper.selectDistinctCollegeNames(courseInformationRO);

        courseInformationSelectArgs.setGrades(grades);
        courseInformationSelectArgs.setMajorNames(majorNames);
        courseInformationSelectArgs.setLevels(levels);
        courseInformationSelectArgs.setCourseNames(courseNames);
        courseInformationSelectArgs.setStudyForms(studyForms);
        courseInformationSelectArgs.setClassNames(classNames);
        courseInformationSelectArgs.setCollegeNames(collegeNames);
        return courseInformationSelectArgs;
    }

    /**
     * 获取排课表明细筛选项
     *
     * @return
     */
    @Override
    public ScheduleCourseInformationSelectArgs filterScheduleCourseInformationSelectArgs() {

        ScheduleCourseInformationSelectArgs scheduleCourseInformationSelectArgs = new ScheduleCourseInformationSelectArgs();
        CourseScheduleFilterRO filter = new CourseScheduleFilterRO();
        Set<String> classNameSet = getTeachingPointClassNameSet();
        List<String> classNameList = new ArrayList<>(classNameSet);

        filter.setClassNames(classNameList);
        ExecutorService executor = Executors.newFixedThreadPool(8); // 8 代表你有8个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> courseScheduleMapper.getDistinctGrades(filter));
        Future<List<String>> distinctCollegeNamesFuture = executor.submit(() -> courseScheduleMapper.getDistinctCollegeNames(filter));
        Future<List<String>> distinctStudyFormsFuture = executor.submit(() -> courseScheduleMapper.getDistinctStudyForms(filter));
        Future<List<String>> distinctClassNamesFuture = executor.submit(() -> courseScheduleMapper.getDistinctClassNames(filter));
        Future<List<String>> distinctLevelsFuture = executor.submit(() -> courseScheduleMapper.getDistinctLevels(filter));
        Future<List<String>> distinctMajorNamesFuture = executor.submit(() -> courseScheduleMapper.getDistinctMajorNames(filter));
        Future<List<String>> distinctTeachingClassesFuture = executor.submit(() -> courseScheduleMapper.getDistinctTeachingClasses(filter));
        Future<List<String>> distinctCourseNamesFuture = executor.submit(() -> courseScheduleMapper.getDistinctCourseNames(filter));

        try {
            scheduleCourseInformationSelectArgs.setGrades(distinctGradesFuture.get());

            scheduleCourseInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            scheduleCourseInformationSelectArgs.setStudyForms(distinctStudyFormsFuture.get());
            scheduleCourseInformationSelectArgs.setAdminClassNames(distinctClassNamesFuture.get());
            scheduleCourseInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            scheduleCourseInformationSelectArgs.setCollegeNames(distinctCollegeNamesFuture.get());
            scheduleCourseInformationSelectArgs.setMajorNames(distinctMajorNamesFuture.get());
            scheduleCourseInformationSelectArgs.setTeachingClasses(distinctTeachingClassesFuture.get());
            scheduleCourseInformationSelectArgs.setCourseNames(distinctCourseNamesFuture.get());

        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return scheduleCourseInformationSelectArgs;
    }


    /**
     * 为继续教育学院学历教育部获取班级信息
     *
     * @param classInformationFilterROPageRO 班级筛选参数
     * @return
     */
    @Override
    public FilterDataVO filterClassInfo(PageRO<ClassInformationFilterRO> classInformationFilterROPageRO) {
        // 设置管理的班级信息
        Set<String> classNameSet = getTeachingPointClassNameSet();
        List<String> classNameList = new ArrayList<>(classNameSet);
        classInformationFilterROPageRO.getEntity().setClassNames(classNameList);

        FilterDataVO<ClassInformationVO> classInformationVOFilterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId() + " 查询班级的参数是 " + classInformationFilterROPageRO);
        List<ClassInformationVO> classInformationVOList = classInformationMapper.getClassInfoByFilter(
                classInformationFilterROPageRO.getEntity(),
                classInformationFilterROPageRO.getPageSize(),
                (classInformationFilterROPageRO.getPageNumber() - 1) * classInformationFilterROPageRO.getPageSize()
        );
        long countStudentPayInfoByFilter = classInformationMapper.getCountClassInfoByFilter(classInformationFilterROPageRO.getEntity());
        classInformationVOFilterDataVO.setTotal(countStudentPayInfoByFilter);
        // 设置一下班级人数
        for(ClassInformationVO classInformationVO: classInformationVOList){
            Integer studentCount = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>()
                    .eq(StudentStatusPO::getClassIdentifier, classInformationVO.getClassIdentifier()));
            classInformationVO.setClassStudentCounts(studentCount);
        }
        classInformationVOFilterDataVO.setData(classInformationVOList);

        return classInformationVOFilterDataVO;
    }

    /**
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取班级数据筛选参数
     *
     * @return
     */
    @Override
    @LogExecutionTime
    public ClassInformationSelectArgs filterClassInformationSelectArgs() {
        // 设置班级信息

        ClassInformationSelectArgs classInformationSelectArgs = new ClassInformationSelectArgs();
        Set<String> classNameSet = getTeachingPointClassNameSet();
        List<String> classNameList = new ArrayList<>(classNameSet);
        ClassInformationFilterRO filter = new ClassInformationFilterRO();
        filter.setClassNames(classNameList);

        ExecutorService executor = Executors.newFixedThreadPool(8); // 8 代表你有8个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> classInformationMapper.getDistinctGrades(filter));
        Future<List<String>> distinctLevelsFuture = executor.submit(() -> classInformationMapper.getDistinctLevels(filter));
        Future<List<String>> distinctStudyFormsFuture = executor.submit(() -> classInformationMapper.getDistinctStudyForms(filter));
        Future<List<String>> distinctClassNamesFuture = executor.submit(() -> classInformationMapper.getDistinctClassNames(filter));
        Future<List<String>> distinctTeachingPointsFuture = executor.submit(() -> classInformationMapper.getDistinctTeachingPoints(filter));
        Future<List<String>> distinctCollegeNamesFuture = executor.submit(() -> classInformationMapper.getDistinctCollegeNames(filter));
        Future<List<String>> distinctMajorNamesFuture = executor.submit(() -> classInformationMapper.getDistinctMajorNames(filter));
        Future<List<String>> distinctStudyPeriodsFuture = executor.submit(() -> classInformationMapper.getDistinctStudyPeriods(filter));

        try {
            classInformationSelectArgs.setGrades(distinctGradesFuture.get());

            classInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            classInformationSelectArgs.setStudyForms(distinctStudyFormsFuture.get());
            classInformationSelectArgs.setClassNames(distinctClassNamesFuture.get());
            classInformationSelectArgs.setTeachingPoints(distinctTeachingPointsFuture.get());
            classInformationSelectArgs.setCollegeNames(distinctCollegeNamesFuture.get());
            classInformationSelectArgs.setMajorNames(distinctMajorNamesFuture.get());
            classInformationSelectArgs.setStudyDurations(distinctStudyPeriodsFuture.get());

        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return classInformationSelectArgs;
    }


    /**
     * 获取排课表课程管理的筛选参数
     *
     * @param courseScheduleFilterROPageRO 前端限制参数
     * @return
     */
    public ScheduleCourseManagetArgs getSelectScheduleCourseManageArgs(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        ScheduleCourseManagetArgs selectArgs = new ScheduleCourseManagetArgs();
        CourseScheduleFilterRO filter = courseScheduleFilterROPageRO.getEntity();

        // 二级学院得单独加一个条件
        filter.setClassSet(new ArrayList<>(scnuXueliTools.getTeachingPointClassNameSet()));

        ExecutorService executor = Executors.newFixedThreadPool(5); // 5 代表你有5个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> courseScheduleMapper.getDistinctGrades(filter));
        Future<List<String>> collegesFuture = executor.submit(() -> courseScheduleMapper.getDistinctCollegeNames(filter));
        Future<List<String>> majorNamesFuture = executor.submit(() -> courseScheduleMapper.getDistinctMajorNames(filter));
        Future<List<String>> classNamesFuture = executor.submit(() -> courseScheduleMapper.getDistinctClassNames(filter));
        Future<List<String>> courseNamesFuture = executor.submit(() -> courseScheduleMapper.getDistinctCourseNames(filter));

        try {
            selectArgs.setGrades(distinctGradesFuture.get());
            selectArgs.setCollegeNames(collegesFuture.get());
            selectArgs.setMajorNames(majorNamesFuture.get());
            selectArgs.setClassNames(classNamesFuture.get());
            selectArgs.setCourseNames(courseNamesFuture.get());

            List<String> statusList = new ArrayList<>();

            // 遍历直播状态的所有值
            for (LiveStatusEnum statusEnum : LiveStatusEnum.values()) {
                // 将枚举项的 status 字段值添加到列表中
                statusList.add(statusEnum.status);
            }

            selectArgs.setLivingStatuses(statusList);
        } catch (Exception e) {

            log.error("获取排课表课程管理筛选参数失败 " + e.toString());
        } finally {
            executor.shutdown();
        }

        return selectArgs;
    }


    /**
     * 获取二级学院考试信息
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    @Override
    public FilterDataVO filterCoursesInformationExams(PageRO<ExamFilterRO> courseScheduleFilterROPageRO) {
        // 设置学院信息
        Set<String> teachingPointClassNameSet = scnuXueliTools.getTeachingPointClassNameSet();
        courseScheduleFilterROPageRO.getEntity().setClassSet(new ArrayList<>(teachingPointClassNameSet));

        List<CourseInformationVO> courseInformationVOS = courseInformationMapper.selectByFilterAndPageForExam(courseScheduleFilterROPageRO.getEntity(),
                courseScheduleFilterROPageRO.getPageSize(),
                (courseScheduleFilterROPageRO.getPageNumber() - 1) * courseScheduleFilterROPageRO.getPageSize());

        List<ExamInfoVO> courseInformationScheduleVOS = new ArrayList<>();
        // 首先获取教学计划中与排课表相对应的课程
        for (CourseInformationVO courseInformationVO : courseInformationVOS) {
            ExamInfoVO examInfoVO = new ExamInfoVO();
            BeanUtils.copyProperties(courseInformationVO, examInfoVO);
            examInfoVO.setMainTeachers(new ArrayList<>());
            examInfoVO.setTutors(new ArrayList<>());

            // 比较 教学计划中的 admin_class 即班级标识 然后获取其考试信息及助教信息
            // 比较年级、专业、学习形式、层次、班级名称、课程名称
            CourseScheduleFilterRO courseScheduleFilterRO = new CourseScheduleFilterRO();
            courseScheduleFilterRO.setGrade(courseInformationVO.getGrade());
            courseScheduleFilterRO.setMajorName(courseInformationVO.getMajorName());
            courseScheduleFilterRO.setLevel(courseInformationVO.getLevel());
            courseScheduleFilterRO.setStudyForm(courseInformationVO.getStudyForm());
            courseScheduleFilterRO.setAdminClassName(courseInformationVO.getClassName());
            courseScheduleFilterRO.setCourseName(courseInformationVO.getCourseName());
            List<ScheduleCourseInformationVO> scheduleCourseInformationVOS = courseScheduleMapper.selectCoursesInformationWithoutPage(courseScheduleFilterRO);

            // 通过考试信息表和阅卷助教表来获取主讲和助教老师
            CourseExamInfoPO courseExamInfoPO = courseExamInfoMapper.selectOne(new LambdaQueryWrapper<CourseExamInfoPO>()
                    .eq(CourseExamInfoPO::getClassIdentifier, courseInformationVO.getAdminClass())
                    .eq(CourseExamInfoPO::getCourse, courseInformationVO.getCourseName())
            );
            if (courseExamInfoPO == null) {
                throw new IllegalArgumentException("获取不到指定教学计划的考试信息 " + courseInformationVO);
            }
            examInfoVO.setExamMethod(courseExamInfoPO.getExamMethod());
            examInfoVO.setExamStatus(courseExamInfoPO.getExamStatus());
            examInfoVO.setMainTeacherName(courseExamInfoPO.getMainTeacher());
            examInfoVO.setMainTeacherUsername(courseExamInfoPO.getTeacherUsername());

            List<CourseExamAssistantsPO> courseExamAssistantsPOS = courseExamAssistantsMapper.selectList(new LambdaQueryWrapper<CourseExamAssistantsPO>()
                    .eq(CourseExamAssistantsPO::getCourseId, courseExamInfoPO.getId()));
            for (CourseExamAssistantsPO courseExamAssistantsPO : courseExamAssistantsPOS) {
                String teacherUsername = courseExamAssistantsPO.getTeacherUsername();
                TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                        .eq(TeacherInformationPO::getTeacherUsername, teacherUsername));
                examInfoVO.getTutors().add(teacherInformationPO);
            }

            // 获取班级人数
            Integer classSize = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>()
                    .eq(StudentStatusPO::getClassIdentifier, courseExamInfoPO.getClassIdentifier()));
            examInfoVO.setClassSize(classSize);

            if (scheduleCourseInformationVOS.isEmpty()) {
                examInfoVO.setTeachingMethod("线下");
            } else {
                examInfoVO.setTeachingMethod(scheduleCourseInformationVOS.get(0).getTeachingMethod());
            }

            courseInformationScheduleVOS.add(examInfoVO);

        }

        FilterDataVO<ExamInfoVO> filterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId() + " 查询考试信息的参数是 " + courseScheduleFilterROPageRO);

        long l = courseInformationMapper.getCountByFilterAndPageForExam(courseScheduleFilterROPageRO.getEntity());
        filterDataVO.setTotal(l);
        filterDataVO.setData(courseInformationScheduleVOS);

        return filterDataVO;
    }


    /**
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取考试数据筛选参数
     *
     * @return
     */
    @Override
    public ScheduleCourseInformationSelectArgs getCoursesArgs(CourseScheduleFilterRO filter) {
        // 设置学院信息
        Set<String> teachingPointClassNameSet = scnuXueliTools.getTeachingPointClassNameSet();
        filter.setClassSet(new ArrayList<>(teachingPointClassNameSet));
        ScheduleCourseInformationSelectArgs scheduleCourseInformationSelectArgs = new ScheduleCourseInformationSelectArgs();

        ExecutorService executor = Executors.newFixedThreadPool(10); // 8 代表你有8个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> courseInformationMapper.getDistinctGrades(filter));
        Future<List<String>> distinctCollegeNamesFuture = executor.submit(() -> courseInformationMapper.getDistinctCollegeNames(filter));
        Future<List<String>> distinctStudyFormsFuture = executor.submit(() -> courseInformationMapper.getDistinctStudyForms(filter));
        Future<List<String>> distinctClassNamesFuture = executor.submit(() -> courseInformationMapper.getDistinctClassNames(filter));
        Future<List<String>> distinctLevelsFuture = executor.submit(() -> courseInformationMapper.getDistinctLevels(filter));
        Future<List<String>> distinctMajorNamesFuture = executor.submit(() -> courseInformationMapper.getDistinctMajorNames(filter));
        Future<List<String>> distinctSemastersFuture = executor.submit(() -> courseInformationMapper.getDistinctSemasters(filter));
        Future<List<String>> distinctCourseNamesFuture = executor.submit(() -> courseInformationMapper.getDistinctCourseNames(filter));
        Future<List<String>> distinctExamStatusesFuture = executor.submit(() -> courseInformationMapper.getDistinctExamStatuses(filter));
        Future<List<String>> distinctExamMethodsFuture = executor.submit(() -> courseInformationMapper.getDistinctExamMethods(filter));
        Future<List<String>> distinctCourseTypesFuture = executor.submit(() -> courseInformationMapper.getDistinctCourseTypes(filter));

        try {
            scheduleCourseInformationSelectArgs.setGrades(distinctGradesFuture.get());

            scheduleCourseInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            scheduleCourseInformationSelectArgs.setStudyForms(distinctStudyFormsFuture.get());
            scheduleCourseInformationSelectArgs.setAdminClassNames(distinctClassNamesFuture.get());
            scheduleCourseInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            scheduleCourseInformationSelectArgs.setCollegeNames(distinctCollegeNamesFuture.get());
            scheduleCourseInformationSelectArgs.setMajorNames(distinctMajorNamesFuture.get());
            scheduleCourseInformationSelectArgs.setSemesters(distinctSemastersFuture.get());
            scheduleCourseInformationSelectArgs.setCourseNames(distinctCourseNamesFuture.get());
            scheduleCourseInformationSelectArgs.setExamStatuses(distinctExamStatusesFuture.get());
            scheduleCourseInformationSelectArgs.setExamMethods(distinctExamMethodsFuture.get());
            scheduleCourseInformationSelectArgs.setCourseTypes(distinctCourseTypesFuture.get());

        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return scheduleCourseInformationSelectArgs;
    }


    /**
     * 获取新生缴费数据筛选参数
     * @param filter
     * @return
     */
    public PaymentInformationSelectArgs getNewStudentPaymentInfoArgs(PaymentInfoFilterRO filter) {
        PaymentInformationSelectArgs paymentInformationSelectArgs = new PaymentInformationSelectArgs();
        TeachingPointInformationPO userBelongTeachingPoint = scnuXueliTools.getUserBelongTeachingPoint();
        filter.setTeachingPoint(userBelongTeachingPoint.getTeachingPointName());
        ExecutorService executor = Executors.newFixedThreadPool(6); // 6 代表你有6个查询

        Future<List<String>> distinctGradesFuture = executor.submit(() -> paymentInfoMapper.getDistinctNewStudentGrades(filter));
        Future<List<String>> distinctLevelsFuture = executor.submit(() -> paymentInfoMapper.getDistinctNewStudentLevels(filter));
        Future<List<String>> distinctStudyFormsFuture = executor.submit(() -> paymentInfoMapper.getDistinctNewStudentStudyForms(filter));
        Future<List<String>> distinctTeachingPointsFuture = executor.submit(() -> paymentInfoMapper.getDistinctNewStudentTeachingPoints(filter));
        Future<List<String>> distinctCollegeNamesFuture = executor.submit(() -> paymentInfoMapper.getDistinctNewStudentCollegeNames(filter));
        Future<List<String>> distinctMajorNamesFuture = executor.submit(() -> paymentInfoMapper.getDistinctNewStudentMajorNames(filter));

        try {
            paymentInformationSelectArgs.setGrades(distinctGradesFuture.get());
            paymentInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            paymentInformationSelectArgs.setStudyForms(distinctStudyFormsFuture.get());
            paymentInformationSelectArgs.setTeachingPoints(distinctTeachingPointsFuture.get());
            paymentInformationSelectArgs.setCollegeNames(distinctCollegeNamesFuture.get());
            paymentInformationSelectArgs.setMajorNames(distinctMajorNamesFuture.get());

        } catch (Exception e) {
            // Handle exceptions like InterruptedException or ExecutionException
            e.printStackTrace();
        } finally {
            executor.shutdown(); // Always remember to shutdown the executor after usage
        }

        return paymentInformationSelectArgs;
    }


    /**
     * 获取新生的缴费信息
     * @param paymentInfoFilterROPageRO
     * @return
     */
    public FilterDataVO filterNewStudentPayInfo(PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO) {
        FilterDataVO<PaymentInfoVO> studentStatusVOFilterDataVO = new FilterDataVO<>();
        paymentInfoFilterROPageRO.getEntity().setTeachingPoint(scnuXueliTools.getUserBelongTeachingPoint().getTeachingPointName());
        log.info("用户缴费筛选参数" + paymentInfoFilterROPageRO);
        List<PaymentInfoVO> paymentInfoVOList = paymentInfoMapper.getNewStudentPayInfoByFilter(
                paymentInfoFilterROPageRO.getEntity(),
                paymentInfoFilterROPageRO.getPageSize(),
                (paymentInfoFilterROPageRO.getPageNumber() - 1) * paymentInfoFilterROPageRO.getPageSize()
        );
        long countStudentPayInfoByFilter = paymentInfoMapper.getCountNewStudentPayInfoByFilter(paymentInfoFilterROPageRO.getEntity());
//        long countStudentPayInfoByFilter = 100L;
        studentStatusVOFilterDataVO.setTotal(countStudentPayInfoByFilter);
        studentStatusVOFilterDataVO.setData(paymentInfoVOList);

        return studentStatusVOFilterDataVO;
    }

}
