package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
@Slf4j
public class TeachingPointFilter extends AbstractFilter {


    private Set<String> getTeachingPointClassNameSet() {
        String loginId = StpUtil.getLoginIdAsString().replace("M", "");
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        // 获取教学点教务员的 teaching_id 进而他所管理的教学点的简称
        Set<String> classNameSet = new HashSet<>();
        List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationMapper.selectList(new LambdaQueryWrapper<TeachingPointAdminInformationPO>()
                .eq(TeachingPointAdminInformationPO::getIdCardNumber, loginId));
        for (TeachingPointAdminInformationPO teachingPointAdminInformationPO : teachingPointAdminInformationPOS) {
            String teachingPointId = teachingPointAdminInformationPO.getTeachingPointId();
            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                    .eq(TeachingPointInformationPO::getTeachingPointId, teachingPointId));
            String alias = teachingPointInformationPO.getAlias();
            classNameSet.add(alias);
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


        String loginId = StpUtil.getLoginIdAsString().replace("M", "");
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        // 获取教学点教务员的 teaching_id 进而他所管理的教学点的简称
        Set<String> classNames = new HashSet<>();
        List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationMapper.selectList(new LambdaQueryWrapper<TeachingPointAdminInformationPO>()
                .eq(TeachingPointAdminInformationPO::getIdCardNumber, loginId));
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
        courseScheduleFilterROPageRO.getEntity().setClassNames((classNameList));
        List<SchedulesVO> schedulesVOS = courseScheduleMapper.selectTeachingPointSchedulesInformation(courseScheduleFilterROPageRO.getEntity(),
                courseScheduleFilterROPageRO.getPageSize(),
                courseScheduleFilterROPageRO.getPageSize() * (courseScheduleFilterROPageRO.getPageNumber() - 1));
        Long count = courseScheduleMapper.selectTeachingPointSchedulesInformationCount(courseScheduleFilterROPageRO.getEntity());
        return new FilterDataVO<>(schedulesVOS, count);
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
        List<String> distinctGrades = studentStatusMapper.getDistinctGrades(studentStatusFilterRO);
        List<String> majorNames = studentStatusMapper.getDistinctMajorNames(studentStatusFilterRO);
        List<String> levels = studentStatusMapper.getDistinctLevels(studentStatusFilterRO);
        List<String> studyForms = studentStatusMapper.getDistinctStudyForms(studentStatusFilterRO);
        List<String> classNames = studentStatusMapper.getDistinctClassNames(studentStatusFilterRO);
        List<String> studyDurations = studentStatusMapper.getDistinctStudyDurations(studentStatusFilterRO);
        List<String> academicStatuss = studentStatusMapper.getDistinctAcademicStatuss(studentStatusFilterRO);

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
        ExecutorService executor = Executors.newFixedThreadPool(7); // 8 代表你有8个查询
        Future<List<String>> distinctGradesFuture = executor.submit(() -> paymentInfoMapper.getDistinctGrades(filter));
        Future<List<String>> distinctLevelsFuture = executor.submit(() -> paymentInfoMapper.getDistinctLevels(filter));
        Future<List<String>> distinctStudyFormsFuture = executor.submit(() -> paymentInfoMapper.getDistinctStudyForms(filter));
        Future<List<String>> distinctClassNamesFuture = executor.submit(() -> paymentInfoMapper.getDistinctClassNames(filter));
        Future<List<String>> distinctTeachingPointsFuture = executor.submit(() -> paymentInfoMapper.getDistinctTeachingPoints(filter));
        Future<List<String>> distinctAcademicYearsFuture = executor.submit(() -> paymentInfoMapper.getDistinctAcademicYears(filter));

        try {
            paymentInformationSelectArgs.setGrades(distinctGradesFuture.get());
            paymentInformationSelectArgs.setLevels(distinctLevelsFuture.get());
            paymentInformationSelectArgs.setStudyForms(distinctStudyFormsFuture.get());
            paymentInformationSelectArgs.setClassNames(distinctClassNamesFuture.get());
            paymentInformationSelectArgs.setTeachingPoints(distinctTeachingPointsFuture.get());
            paymentInformationSelectArgs.setAcademicYears(distinctAcademicYearsFuture.get());

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
        List<String> grades = courseInformationMapper.selectDistinctGrades(null, classNameSet);
        List<String> majorNames = courseInformationMapper.selectDistinctMajorNames(null, classNameSet);
        List<String> levels = courseInformationMapper.selectDistinctLevels(null, classNameSet);
        List<String> courseNames = courseInformationMapper.selectDistinctCourseNames(null, classNameSet);
        List<String> studyForms = courseInformationMapper.selectDistinctStudyForms(null, classNameSet);
        List<String> classNames = courseInformationMapper.selectDistinctClassNames(null, classNameSet);
        List<String> collegeNames = courseInformationMapper.selectDistinctCollegeNames(classNameSet);

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

}
