package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.constant.enums.DownloadFileNameEnum;
import com.scnujxjy.backendpoint.constant.enums.LiveStatusEnum;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.constant.enums.MinioBucketEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesLearningPO;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamAssistantsPO;
import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamInfoPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.DownloadMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.GlobalConfigMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesLearningMapper;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamAssistantsMapper;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.DownloadMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.bo.teaching_process.ScheduleCoursesInformationBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.exam.BatchSetTeachersInfoRO;
import com.scnujxjy.backendpoint.model.ro.exam.ExamFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.exam.ExamInfoVO;
import com.scnujxjy.backendpoint.model.vo.exam.ExamStudentsInfoVO;
import com.scnujxjy.backendpoint.model.vo.exam.ExamTeachersInfoVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import com.scnujxjy.backendpoint.service.InterBase.OldDataSynchronize;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.ApplicationContextProvider;
import com.scnujxjy.backendpoint.util.tool.LogExecutionTime;
import com.scnujxjy.backendpoint.util.tool.ScnuTimeInterval;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 为二级学院管理员定义筛选器
 */
@Component
@Slf4j
public class CollegeAdminFilter extends AbstractFilter {

    @Override
    public List<StudentStatusPO> filterStudentInfo(List<StudentStatusPO> data) {
        // 为二级学院管理员实现学籍数据筛选逻辑
        return data;
    }

    private CollegeInformationPO getCollegeName() {
        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationMapper.selectById(platformUserPO.getUserId());
        if (Objects.isNull(collegeAdminInformationPO)) {
            return null;
        }
        CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectById(collegeAdminInformationPO.getCollegeId());
        if (Objects.isNull(collegeInformationPO)) {
            return null;
        }

        return collegeInformationPO;
    }

    @Override
    public CourseScheduleFilterDataVO filterCourseSchedule(PageRO<CourseScheduleRO> courseScheduleFilter) {
        CourseScheduleFilterDataVO courseScheduleFilterDataVO = new CourseScheduleFilterDataVO();
        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationMapper.selectById(platformUserPO.getUserId());
        if (Objects.isNull(collegeAdminInformationPO)) {
            return null;
        }
        CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectById(collegeAdminInformationPO.getCollegeId());
        if (Objects.isNull(collegeInformationPO)) {
            return null;
        }

        // 使用 courseScheduleMapper 获取数据

        List<TeacherCourseScheduleVO> courseSchedulePOS = courseScheduleMapper.getCourseSchedulesByConditions(collegeInformationPO.getCollegeName(),
                courseScheduleFilter);
        long total = courseScheduleMapper.countCourseSchedulesByConditions(collegeInformationPO.getCollegeName(),
                courseScheduleFilter);
        courseScheduleFilterDataVO.setCourseSchedulePOS(courseSchedulePOS);
        courseScheduleFilterDataVO.setTotal(total);
        return courseScheduleFilterDataVO;
    }


    /**
     * 筛选学籍数据
     *
     * @param studentStatusFilter 获取学籍数据的筛选数据
     * @return
     */
    @Override
    @LogExecutionTime
    public FilterDataVO filterStudentStatus(PageRO<StudentStatusFilterRO> studentStatusFilter) {
        FilterDataVO<StudentStatusAllVO> studentStatusVOFilterDataVO = new FilterDataVO<>();

        // 根据登录用户所属学院来添加额外的条件
        CollegeInformationPO college = getCollegeName();
        studentStatusFilter.getEntity().setCollege(college.getCollegeName());

        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }


        log.info("学籍数据查询参数 " + studentStatusFilter.getEntity());
        // 使用 courseInformationMapper 获取数据
        List<StudentStatusAllVO> studentStatusVOS = studentStatusMapper.selectByFilterAndPageByManager0(studentStatusFilter.getEntity(),
                studentStatusFilter.getPageSize(),
                studentStatusFilter.getPageSize() * (studentStatusFilter.getPageNumber() - 1));
        long total = studentStatusMapper.getCountByFilterAndPageManager0(studentStatusFilter.getEntity());
        studentStatusVOFilterDataVO.setData(studentStatusVOS);
        studentStatusVOFilterDataVO.setTotal(total);

        return studentStatusVOFilterDataVO;
    }

    /**
     * 获取学籍数据筛选参数
     *
     * @return
     */
    @Override
    @LogExecutionTime
    public StudentStatusSelectArgs filterStudentStatusSelectArgs() {
        StudentStatusSelectArgs studentStatusSelectArgs = new StudentStatusSelectArgs();
        List<String> distinctGrades = studentStatusMapper.getDistinctGrades(new StudentStatusFilterRO());
        List<String> majorNames = studentStatusMapper.getDistinctMajorNames(new StudentStatusFilterRO());
        List<String> levels = studentStatusMapper.getDistinctLevels(new StudentStatusFilterRO());
        List<String> studyForms = studentStatusMapper.getDistinctStudyForms(new StudentStatusFilterRO());
        List<String> classNames = studentStatusMapper.getDistinctClassNames(new StudentStatusFilterRO());
        List<String> studyDurations = studentStatusMapper.getDistinctStudyDurations(new StudentStatusFilterRO());
        List<String> academicStatuss = studentStatusMapper.getDistinctAcademicStatuss(new StudentStatusFilterRO());

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
     * 为继续教育学院学历教育部获取缴费信息
     *
     * @param paymentInfoFilterROPageRO 缴费筛选参数
     * @return
     */
    @Override
    public FilterDataVO filterPayInfo(PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO) {
        // 添加二级学院的筛选参数
        CollegeInformationPO collegeName = getCollegeName();
        paymentInfoFilterROPageRO.getEntity().setCollege(collegeName.getCollegeName());

        FilterDataVO<PaymentInfoVO> studentStatusVOFilterDataVO = new FilterDataVO<>();
        log.info("用户缴费筛选参数" + paymentInfoFilterROPageRO);
        List<PaymentInfoVO> paymentInfoVOList = paymentInfoMapper.getStudentPayInfoByFilter(
                paymentInfoFilterROPageRO.getEntity(),
                paymentInfoFilterROPageRO.getPageSize(),
                (paymentInfoFilterROPageRO.getPageNumber() - 1) * paymentInfoFilterROPageRO.getPageSize()
        );
        long countStudentPayInfoByFilter = paymentInfoMapper.getCountStudentPayInfoByFilter(paymentInfoFilterROPageRO.getEntity());
//        long countStudentPayInfoByFilter = 100L;
        studentStatusVOFilterDataVO.setTotal(countStudentPayInfoByFilter);
        studentStatusVOFilterDataVO.setData(paymentInfoVOList);

        return studentStatusVOFilterDataVO;
    }


    /**
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取缴费数据筛选参数
     *
     * @return
     */
    @Override
    @LogExecutionTime
    public PaymentInformationSelectArgs filterPaymentInformationSelectArgs() {
        PaymentInformationSelectArgs paymentInformationSelectArgs = new PaymentInformationSelectArgs();
        PaymentInfoFilterRO filter = new PaymentInfoFilterRO();

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
     * 为继续教育学院学历教育部获取成绩信息
     *
     * @param scoreInformationFilterROPageRO 成绩筛选参数
     * @return
     */
    @Override
    public FilterDataVO filterGradeInfo(PageRO<ScoreInformationFilterRO> scoreInformationFilterROPageRO) {
        // 增加学院筛选参数，为不同的二级学院管理员查询成绩做好权限划分
        CollegeInformationPO college = getCollegeName();
        scoreInformationFilterROPageRO.getEntity().setCollege(college.getCollegeName());

        FilterDataVO<ScoreInformationVO> studentStatusVOFilterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId() + " 查询成绩的参数是 " + scoreInformationFilterROPageRO);
        List<ScoreInformationVO> paymentInfoVOList = scoreInformationMapper.getStudentGradeInfoByFilter(
                scoreInformationFilterROPageRO.getEntity(),
                scoreInformationFilterROPageRO.getPageSize(),
                (scoreInformationFilterROPageRO.getPageNumber() - 1) * scoreInformationFilterROPageRO.getPageSize()
        );
        long countStudentPayInfoByFilter = scoreInformationMapper.getCountStudentGradeInfoByFilter(scoreInformationFilterROPageRO.getEntity());
//        long countStudentPayInfoByFilter = 100L;
        studentStatusVOFilterDataVO.setTotal(countStudentPayInfoByFilter);
        studentStatusVOFilterDataVO.setData(paymentInfoVOList);

        return studentStatusVOFilterDataVO;
    }

    /**
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取学籍数据筛选参数
     *
     * @return
     */
    @Override
    @LogExecutionTime
    public ScoreInformationSelectArgs filterScoreInformationSelectArgs() {
        ScoreInformationSelectArgs scoreInformationSelectArgs = new ScoreInformationSelectArgs();
        ScoreInformationFilterRO filter = new ScoreInformationFilterRO();

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
     * 筛选教学计划
     *
     * @param courseInformationFilter 获取的教学计划筛选数据
     * @return
     */
    @Override
    public FilterDataVO filterCourseInformation(PageRO<CourseInformationRO> courseInformationFilter) {
        FilterDataVO<CourseInformationVO> courseInformationFilterDataVO = new FilterDataVO<>();

        CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
        courseInformationFilter.getEntity().setCollege(userBelongCollege.getCollegeName());

        log.info("查询参数 " + courseInformationFilter.getEntity());
        // 使用 courseInformationMapper 获取数据
        List<CourseInformationVO> courseInformationVOS = courseInformationMapper.selectByFilterAndPage(courseInformationFilter.getEntity(),
                courseInformationFilter.getPageSize(),
                courseInformationFilter.getPageSize() * (courseInformationFilter.getPageNumber() - 1));
        long total = courseInformationMapper.getCountByFilterAndPage(courseInformationFilter.getEntity());
        courseInformationFilterDataVO.setData(courseInformationVOS);
        courseInformationFilterDataVO.setTotal(total);

        return courseInformationFilterDataVO;
    }

    /**
     * 获取二级学院教学计划筛选参数
     *
     * @return
     */
    @Override
    public CourseInformationSelectArgs filterCourseInformationSelectArgs() {
        CourseInformationSelectArgs courseInformationSelectArgs = new CourseInformationSelectArgs();
        CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();

        CourseInformationRO courseInformationRO = new CourseInformationRO();
        courseInformationRO.setCollege(userBelongCollege.getCollegeName());

        List<String> grades = courseInformationMapper.selectDistinctGrades(courseInformationRO);
        List<String> majorNames = courseInformationMapper.selectDistinctMajorNames(courseInformationRO);
        List<String> levels = courseInformationMapper.selectDistinctLevels(courseInformationRO);
        List<String> courseNames = courseInformationMapper.selectDistinctCourseNames(courseInformationRO);
        List<String> studyForms = courseInformationMapper.selectDistinctStudyForms(courseInformationRO);
        List<String> classNames = courseInformationMapper.selectDistinctClassNames(courseInformationRO);
        courseInformationSelectArgs.setGrades(grades);
        courseInformationSelectArgs.setMajorNames(majorNames);
        courseInformationSelectArgs.setLevels(levels);
        courseInformationSelectArgs.setCourseNames(courseNames);
        courseInformationSelectArgs.setStudyForms(studyForms);
        courseInformationSelectArgs.setClassNames(classNames);
        return courseInformationSelectArgs;
    }


    /**
     * 批量导出教学计划
     *
     * @param courseInformationROPageRO
     * @return
     */
    @Override
    public byte[] downloadTeachingPlans(PageRO<CourseInformationRO> courseInformationROPageRO) {
        CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
        courseInformationROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());

        List<CourseInformationVO> courseInformationVOS = courseInformationMapper.selectByFilterAndPage(courseInformationROPageRO.getEntity(),
                null, null);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            // 将数据写入到 ByteArrayOutputStream
            EasyExcel.write(outputStream, CourseInformationVO.class).sheet("Sheet1").doWrite(courseInformationVOS);
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outputStream.toByteArray();
    }

    // 注意：这个子类没有重写filterDegreeInfo方法，所以它将使用AbstractFilter中的默认实现

    /**
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取排课表课程数据筛选参数
     *
     * @return
     */
    @Override
    public ScheduleCourseInformationSelectArgs getCoursesArgs(CourseScheduleFilterRO filter) {
        // 设置学院信息
        CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
        filter.setCollege(userBelongCollege.getCollegeName());
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
     * 获取二级学院考试信息
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    @Override
    public FilterDataVO filterCoursesInformationExams(PageRO<ExamFilterRO> courseScheduleFilterROPageRO) {
        // 设置学院信息
        CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
        courseScheduleFilterROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());

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
            examInfoVO.setExamType(courseExamInfoPO.getExamType());
            examInfoVO.setMainTeacherName(courseExamInfoPO.getMainTeacher());
            examInfoVO.setMainTeacherUsername(courseExamInfoPO.getTeacherUsername());

            List<CourseExamAssistantsPO> courseExamAssistantsPOS = courseExamAssistantsMapper.selectList(new LambdaQueryWrapper<CourseExamAssistantsPO>()
                    .eq(CourseExamAssistantsPO::getExamId, courseExamInfoPO.getId()));
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
        log.info(StpUtil.getLoginId() + " 查询排课表课程信息的参数是 " + courseScheduleFilterROPageRO);

        long l = courseInformationMapper.getCountByFilterAndPageForExam(courseScheduleFilterROPageRO.getEntity());
        filterDataVO.setTotal(l);
        filterDataVO.setData(courseInformationScheduleVOS);

        return filterDataVO;
    }


    /**
     * 获取排课表详细信息
     *
     * @return
     */
    public FilterDataVO filterSchedulesInformation(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        // 设置学院信息
        CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
        courseScheduleFilterROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());

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
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取排课表课程数据筛选参数
     *
     * @return
     */
    @Override
    @LogExecutionTime
    public ScheduleCourseInformationSelectArgs filterScheduleCourseInformationSelectArgs() {

        ScheduleCourseInformationSelectArgs scheduleCourseInformationSelectArgs = new ScheduleCourseInformationSelectArgs();
        CourseScheduleFilterRO filter = new CourseScheduleFilterRO();

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
        // 设置学院信息
        CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
        classInformationFilterROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());

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
        // 设置学院信息
        CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();


        ClassInformationSelectArgs classInformationSelectArgs = new ClassInformationSelectArgs();

        ClassInformationFilterRO filter = new ClassInformationFilterRO();
        filter.setCollege(userBelongCollege.getCollegeName());

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
     * 获取排课表课程管理信息
     *
     * @return
     */
    public FilterDataVO getScheduleCourses(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        // 二级学院得单独加一个条件
        courseScheduleFilterROPageRO.getEntity().setCollege(Objects.requireNonNull(getCollegeName()).getCollegeName());

        log.info(StpUtil.getLoginId() + " 查询排课表课程信息的参数是 " + courseScheduleFilterROPageRO);
        // 展示给前端的排课课程管理信息
        List<ScheduleCoursesInformationVO> scheduleCoursesInformationVOS = new ArrayList<>();

        // 获取指定条件的排课表表课程信息 但是还需要做二次处理 比如 去掉同一批次的重复信息 把时间和班级 还有直播间信息 单独摘出来
        String redisKey = "getScheduleCoursesInformation:" + courseScheduleFilterROPageRO.getEntity().toString();
        ValueOperations<String, Object> valueOps1 = redisTemplate.opsForValue();
        List<ScheduleCoursesInformationBO> schedulesVOS;

        // Check if data is present in Redis cache
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            schedulesVOS = (List<ScheduleCoursesInformationBO>) valueOps1.get(redisKey);
        } else {
            // If not present in cache, retrieve data from the database
            schedulesVOS = courseScheduleMapper.getScheduleCoursesInformation(courseScheduleFilterROPageRO.getEntity());
            // Store the data in cache with a timeout of 30 minutes
            valueOps1.set(redisKey, schedulesVOS, 30, TimeUnit.MINUTES);
        }

        List<ScheduleCoursesInformationVO> scheduleCoursesInformationVOList = new ArrayList<>();

        List<String> errorCourses = new ArrayList<>();

        // 去重 把同一批次的拿到 再去根据时间排序
        for (ScheduleCoursesInformationBO schedulesVO : schedulesVOS) {
            // 使用流来处理 这个 ScheduleCoursesInformationVO 对象，它如果发现这个 List 不存在，则新建
            ScheduleCoursesInformationVO scheduleCoursesInformationVO = scheduleCoursesInformationVOList.stream()
                    .filter(vo -> vo.getBatchIndex().equals(schedulesVO.getBatchIndex()))
                    .findFirst()
                    .orElseGet(() -> {
                        ScheduleCoursesInformationVO newVO = new ScheduleCoursesInformationVO(schedulesVO.getBatchIndex());
                        scheduleCoursesInformationVOList.add(newVO);
                        newVO.setClassName(new ArrayList<>());
                        return newVO;
                    });

            // 接下来根据每个批次里的排课日期和排课时间 拿到具体现在最近的 并且拿到它的直播状态 和 channelI

            scheduleCoursesInformationVO.setMainTeacherName(schedulesVO.getMainTeacherName());
            scheduleCoursesInformationVO.setTeacherUsername(schedulesVO.getTeacherUsername());
            scheduleCoursesInformationVO.setCourseName(schedulesVO.getCourseName());

            if (scheduleCoursesInformationVO.getTeachingDate() == null) {
                scheduleCoursesInformationVO.setTeachingDate(schedulesVO.getTeachingDate());
                scheduleCoursesInformationVO.setTeachingTime(schedulesVO.getTeachingTime());
            } else {
                ScnuTimeInterval timeInterval = scnuXueliTools.getTimeInterval(schedulesVO.getTeachingDate(), schedulesVO.getTeachingTime());
                Date newStart = timeInterval.getStart();
                Date now = new Date();
                Date currentTeachingDate = scnuXueliTools.getTimeInterval(scheduleCoursesInformationVO.getTeachingDate(),
                        scheduleCoursesInformationVO.getTeachingTime()).getStart();


                // 比较时间差
                long diffNew = newStart.getTime() - now.getTime();
                long diffCurrent = currentTeachingDate.getTime() - now.getTime();

                // 如果新的开始时间比现在时间晚，并且与现在的时间差比当前记录的时间差小
                if (diffCurrent > 0 && diffNew < 0) {
                    // 当前记录的排课的上课日期和上课时间 比此时此刻的大 而新的排课的上课日期和上课时间比现在小 那么就啥也不做
                } else if (diffCurrent > 0) {
                    if (Math.abs(diffNew) < Math.abs(diffCurrent)) {
                        // 选最近的
                        scheduleCoursesInformationVO.setTeachingDate(schedulesVO.getTeachingDate());
                        scheduleCoursesInformationVO.setTeachingTime(schedulesVO.getTeachingTime());
                        scheduleCoursesInformationVO.setOnlinePlatform(schedulesVO.getOnlinePlatform());
                    }

                } else {
                    // 目前拿到的上课时间 比当下的时间 大
                    if (diffNew > 0) {
                        scheduleCoursesInformationVO.setTeachingDate(schedulesVO.getTeachingDate());
                        scheduleCoursesInformationVO.setTeachingTime(schedulesVO.getTeachingTime());
                        scheduleCoursesInformationVO.setOnlinePlatform(schedulesVO.getOnlinePlatform());
                    } else if (diffCurrent >= 0 && diffNew < 0) {

                    } else {
                        if (Math.abs(diffNew) < Math.abs(diffCurrent)) {
                            // 选最近的
                            scheduleCoursesInformationVO.setTeachingDate(schedulesVO.getTeachingDate());
                            scheduleCoursesInformationVO.setTeachingTime(schedulesVO.getTeachingTime());
                            scheduleCoursesInformationVO.setOnlinePlatform(schedulesVO.getOnlinePlatform());
                        }
                    }
                }

            }
        }


        // 将拿到的每个批次 进行时间的升序排列 与现在相比比现在大的排在前面 比现在小的排在后面
        // 分为两组后 组内 按照 teachingDate 和 teachingTime 升序排列

        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(200);

        // 使用CompletableFuture来异步处理每个scheduleCoursesInformationVO
        List<CompletableFuture<Boolean>> futures = scheduleCoursesInformationVOList.stream()
                .map(scheduleCoursesInformationVO -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return processScheduleCoursesInformationVO(
                                scheduleCoursesInformationVO,
                                errorCourses,
                                courseScheduleFilterROPageRO.getEntity()
                        );
                    } catch (Exception e) {
                        // 记录详细的错误信息
                        e.printStackTrace(); // 或使用日志记录
                        return false;
                    }
                }, executorService))
                .collect(Collectors.toList());


        // 等待所有的future完成，并获取结果
        List<Boolean> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        // 根据结果移除不满足条件的对象
        for (int i = scheduleCoursesInformationVOList.size() - 1; i >= 0; i--) {
            if (!results.get(i)) {
                scheduleCoursesInformationVOList.remove(i);
            }
        }

        // 关闭线程池
        executorService.shutdown();

        // 移除所有直播状态不符合条件或被设置为null的 ScheduleCoursesInformationVO
        scheduleCoursesInformationVOList.removeIf(Objects::isNull);


        Long pageNumber = courseScheduleFilterROPageRO.getPageNumber();
        Long pageSize = courseScheduleFilterROPageRO.getPageSize();

        // 计算开始索引
        long startIndex = (pageNumber - 1) * pageSize;

        // 计算结束索引
        long endIndex = startIndex + pageSize;

        endIndex = endIndex > scheduleCoursesInformationVOList.size() ? scheduleCoursesInformationVOList.size() : endIndex;

        List<ScheduleCoursesInformationVO> pageData = scheduleCoursesInformationVOList.subList((int) startIndex, (int) endIndex);

        FilterDataVO<ScheduleCoursesInformationVO> filterDataVO = new FilterDataVO<>();

        long total = scheduleCoursesInformationVOList.size();
        filterDataVO.setTotal(total);
        filterDataVO.setData(pageData);

        log.info("所有的排课表信息出现错误的记录 \n" + errorCourses);

        return filterDataVO;
    }


    private boolean processScheduleCoursesInformationVO(
            ScheduleCoursesInformationVO scheduleCoursesInformationVO,
            List<String> errorCourses, CourseScheduleFilterRO courseScheduleFilterRO) {

        // 获取所有的行政班
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(
                new LambdaQueryWrapper<CourseSchedulePO>()
                        .eq(CourseSchedulePO::getBatchIndex, scheduleCoursesInformationVO.getBatchIndex())
        );

        List<String> colleges = new ArrayList<>();
        List<String> majorNames = new ArrayList<>();
        for (CourseSchedulePO courseSchedulePO : courseSchedulePOS) {
            // 从数据库获取班级信息
            ClassInformationPO classInformationPO = classInformationMapper.selectOne(
                    new LambdaQueryWrapper<ClassInformationPO>()
                            .eq(ClassInformationPO::getGrade, courseSchedulePO.getGrade())
                            .eq(ClassInformationPO::getMajorName, courseSchedulePO.getMajorName())
                            .eq(ClassInformationPO::getLevel, courseSchedulePO.getLevel())
                            .eq(ClassInformationPO::getStudyForm, courseSchedulePO.getStudyForm())
                            .eq(ClassInformationPO::getClassName, courseSchedulePO.getAdminClass())
            );

            if (classInformationPO == null) {
                // 记录错误信息
                String error = "班级信息获取失败，存在排课表记录获取不到班级信息 " +
                        courseSchedulePO.getGrade() + " " + courseSchedulePO.getMajorName() + " " +
                        courseSchedulePO.getStudyForm() + " " + courseSchedulePO.getLevel() + " " +
                        courseSchedulePO.getMainTeacherName() + " " + courseSchedulePO.getCourseName() + " " +
                        courseSchedulePO.getTeachingDate() + " " + courseSchedulePO.getTeachingTime();
                // 这里应该是线程安全的列表操作
                synchronized (errorCourses) {
                    errorCourses.add(error);
                }
            } else {
                colleges.add(classInformationPO.getCollege());
                majorNames.add(classInformationPO.getMajorName());
            }
        }

        // 去重学院信息 去重专业名称信息
        colleges = colleges.stream().distinct().collect(Collectors.toList());
        majorNames = majorNames.stream().distinct().collect(Collectors.toList());
        // 获取班级列表
        List<String> adminClassList = courseSchedulePOS.stream()
                .map(CourseSchedulePO::getAdminClass)
                .distinct()
                .collect(Collectors.toList());

        // 设置学院和班级信息
        scheduleCoursesInformationVO.setClassName(adminClassList);
        scheduleCoursesInformationVO.setColleges(colleges);
        scheduleCoursesInformationVO.setMajorNames(majorNames);

        // 处理在线平台信息
        String onlinePlatform = scheduleCoursesInformationVO.getOnlinePlatform();
        if (onlinePlatform != null) {
            // ... 处理在线平台信息的代码逻辑
            // 这里从数据库获取视频流信息
            VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(
                    new LambdaQueryWrapper<VideoStreamRecordPO>().eq(VideoStreamRecordPO::getId, onlinePlatform)
            );

            if (videoStreamRecordPO != null) {
                // 设置直播状态和频道ID
                scheduleCoursesInformationVO.setLivingStatus(videoStreamRecordPO.getWatchStatus());
                scheduleCoursesInformationVO.setChannelId(videoStreamRecordPO.getChannelId());
            }
            if (onlinePlatform.equals("已结束")) {
                scheduleCoursesInformationVO.setLivingStatus(LiveStatusEnum.END.status);
            }
        } else {
            scheduleCoursesInformationVO.setLivingStatus(LiveStatusEnum.UN_START0.status);
        }

        // 如果直播状态不与指定的直播条件一致  直接过滤掉
        // 检查直播状态
        if (scheduleCoursesInformationVO.getLivingStatus() != null && courseScheduleFilterRO.getLivingStatus() != null) {
            try {
                if (!scheduleCoursesInformationVO.getLivingStatus().equals(courseScheduleFilterRO.getLivingStatus())) {
                    return false; // 表示不保留这个对象
                }
            } catch (Exception e) {
                log.error(e.toString());
                return false;
            }
        }

        return true; // 表示保留这个对象
    }


    /**
     * 获取继续教育学院管理员的排课表课程管理的筛选参数
     *
     * @param courseScheduleFilterROPageRO 前端限制参数
     * @return
     */
    public ScheduleCourseManagetArgs getSelectScheduleCourseManageArgs(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        ScheduleCourseManagetArgs selectArgs = new ScheduleCourseManagetArgs();
        CourseScheduleFilterRO filter = courseScheduleFilterROPageRO.getEntity();

        // 二级学院得单独加一个条件
        filter.setCollege(Objects.requireNonNull(getCollegeName()).getCollegeName());

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
     * 考试信息批量导出
     *
     * @param entity
     * @param username
     */
    public void exportExamTeachersInfo(BatchSetTeachersInfoRO entity, String username, PlatformMessagePO platformMessagePO) {
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        CourseExamInfoMapper courseExamInfoMapper1 = ctx.getBean(CourseExamInfoMapper.class);
        CourseInformationMapper courseInformationMapper1 = ctx.getBean(CourseInformationMapper.class);
        ClassInformationMapper classInformationMapper1 = ctx.getBean(ClassInformationMapper.class);
        //        CourseScheduleMapper courseScheduleMapper1 = ctx.getBean(CourseScheduleMapper.class);
        CoursesLearningMapper coursesLearningMapper1 = ctx.getBean(CoursesLearningMapper.class);

        CourseExamAssistantsMapper courseExamAssistantsMapper1 = ctx.getBean(CourseExamAssistantsMapper.class);
        TeacherInformationMapper teacherInformationMapper1 = ctx.getBean(TeacherInformationMapper.class);
        PlatformUserService platformUserService1 = ctx.getBean(PlatformUserService.class);
        MinioService minioService1 = ctx.getBean(MinioService.class);
        GlobalConfigMapper globalConfigMapper1 = ctx.getBean(GlobalConfigMapper.class);
        PlatformMessageMapper platformMessageMapper = ctx.getBean(PlatformMessageMapper.class);
        DownloadMessageMapper downloadMessageMapper = ctx.getBean(DownloadMessageMapper.class);
        CollegeAdminInformationMapper collegeAdminInformationMapper1 = ctx.getBean(CollegeAdminInformationMapper.class);
        PlatformUserMapper platformUserMapper1 = ctx.getBean(PlatformUserMapper.class);

        // 获取数据
        List<ExamTeachersInfoVO> examTeachersInfoVOS = new ArrayList<>();

        @Data
        class DateInfo {
            private String year;
            private String month;
            private String day;

            // 构造器、getters 和 setters
        }

        // 获取当前日期
        LocalDate now = LocalDate.now();

        // 使用 LocalDate 获取年、月、日
        String year = String.valueOf(now.getYear());
        int monthValue = now.getMonthValue();
        String month = String.format("%02d", now.getMonthValue()); // 保证月份是两位数字
        String day = String.format("%02d", now.getDayOfMonth()); // 保证天数是两位数字

        // 设置填表时间到 DateInfo 对象
        DateInfo dateInfo = new DateInfo();
        dateInfo.setYear(year);
        dateInfo.setMonth(month);
        dateInfo.setDay(day);

        String season;
        if (monthValue >= 2 && monthValue <= 7) {
            season = "春季";
        } else {
            season = "秋季";
        }
        // 获取管理员的名字

        PlatformUserPO platformUserPO1 = platformUserMapper1.selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUsername, username));
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationMapper1.selectOne(new LambdaQueryWrapper<CollegeAdminInformationPO>()
                .eq(CollegeAdminInformationPO::getUserId, platformUserPO1.getUserId()));
        // 或者使用 Map
        Map<String, Object> dateInfoMap = new HashMap<>();
        dateInfoMap.put("year", year);
        dateInfoMap.put("month", month);
        dateInfoMap.put("day", day);
        dateInfoMap.put("collegeAdminName", collegeAdminInformationPO.getName());
        dateInfoMap.put("collegeAdminPhone", collegeAdminInformationPO.getPhone());
        dateInfoMap.put("season", season);

        List<CourseExamInfoPO> courseExamInfoPOS = courseExamInfoMapper1.batchSelectData(entity);
        int count = 1;
        for (CourseExamInfoPO courseExamInfoPO : courseExamInfoPOS) {
            ExamTeachersInfoVO examTeachersInfoVO = new ExamTeachersInfoVO();
            examTeachersInfoVO.setIndex(count);


            CourseInformationPO courseInformationPO = courseInformationMapper1.selectOne(new LambdaQueryWrapper<CourseInformationPO>()
                    .eq(CourseInformationPO::getAdminClass, courseExamInfoPO.getClassIdentifier())
                    .eq(CourseInformationPO::getCourseName, courseExamInfoPO.getCourse())
            );


            ClassInformationPO classInformationPO = classInformationMapper1.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                    .eq(ClassInformationPO::getClassIdentifier, courseExamInfoPO.getClassIdentifier()));

            examTeachersInfoVO.setCollege(classInformationPO.getCollege());
            examTeachersInfoVO.setMajorName(classInformationPO.getMajorName());
            examTeachersInfoVO.setStudyForm(classInformationPO.getStudyForm());
            examTeachersInfoVO.setLevel(classInformationPO.getLevel());
            examTeachersInfoVO.setGrade(classInformationPO.getGrade());
            examTeachersInfoVO.setCourseName(courseInformationPO.getCourseName());
            examTeachersInfoVO.setExamType(courseExamInfoPO.getExamType());
            examTeachersInfoVO.setExamType(courseExamInfoPO.getExamType());
            examTeachersInfoVO.setClassName(courseExamInfoPO.getClassName());
            examTeachersInfoVO.setExamType(courseExamInfoPO.getExamType());

            // 20240425 课程学习模块做了调整 目前需要从 courseLearning 里面拿到信息
//            List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper1.selectList(new LambdaQueryWrapper<CourseSchedulePO>()
//                    .eq(CourseSchedulePO::getGrade, courseInformationPO.getGrade())
//                    .eq(CourseSchedulePO::getMajorName, courseInformationPO.getMajorName())
//                    .eq(CourseSchedulePO::getAdminClass, classInformationPO.getClassName())
//                    .eq(CourseSchedulePO::getStudyForm, courseInformationPO.getStudyForm())
//                    .eq(CourseSchedulePO::getLevel, courseInformationPO.getLevel())
//                    .eq(CourseSchedulePO::getCourseName, courseInformationPO.getCourseName())
//            );
//            if (!courseSchedulePOS.isEmpty()) {
//                // 使用stream来提取教学班别字段，并且去重
//                List<String> uniqueTeachingClasses = courseSchedulePOS.stream()
//                        .map(CourseSchedulePO::getTeachingClass) // 提取教学班别
//                        .distinct() // 去重
//                        .collect(Collectors.toList()); // 收集到List中
//
//
//                // 将唯一教学班别的列表转换成一个由空格分隔的字符串
//                String teachingClassesString = String.join(" ", uniqueTeachingClasses);
//
//                // 将这个字符串设置到examTeachersInfoVO对象的teachingClass属性
//                examTeachersInfoVO.setTeachingClass(teachingClassesString);
//                examTeachersInfoVO.setXueliPlatform("是");
//            } else {
//                examTeachersInfoVO.setXueliPlatform("否");
//            }

            // 20240425 课程学习模块做了调整 目前需要从 courseLearning 里面拿到信息
            CoursesLearningPO coursesLearningPO = coursesLearningMapper1.selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                    .eq(CoursesLearningPO::getId, courseExamInfoPO.getCourseId()));
            if(courseExamInfoPO != null){
                examTeachersInfoVO.setTeachingClass("教学班 " + coursesLearningPO.getId());
                examTeachersInfoVO.setXueliPlatform("是");
            }else{
                examTeachersInfoVO.setXueliPlatform("否");
            }


            String teacherUsername = courseExamInfoPO.getTeacherUsername();
            if (teacherUsername != null) {
                TeacherInformationPO teacherInformationPO = teacherInformationMapper1.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                        .eq(TeacherInformationPO::getTeacherUsername, teacherUsername));
                examTeachersInfoVO.setMainTeacherName(teacherInformationPO.getName());
                examTeachersInfoVO.setMainTeacherPhone(teacherInformationPO.getPhone());
            } else {
                examTeachersInfoVO.setMainTeacherName("");
                examTeachersInfoVO.setMainTeacherPhone("");
            }

            List<CourseExamAssistantsPO> courseExamAssistantsPOS = courseExamAssistantsMapper1.selectList(new LambdaQueryWrapper<CourseExamAssistantsPO>()
                    .eq(CourseExamAssistantsPO::getExamId, courseExamInfoPO.getId()));
            if (courseExamAssistantsPOS.isEmpty()) {
                examTeachersInfoVO.setTutorName("");
                examTeachersInfoVO.setTutorPhone("");

                examTeachersInfoVOS.add(examTeachersInfoVO);
                count += 1;
            } else {
                StringBuilder tutorNames = new StringBuilder();
                StringBuilder tutorPhones = new StringBuilder();
                for (CourseExamAssistantsPO courseExamAssistantsPO : courseExamAssistantsPOS) {
                    ExamTeachersInfoVO examTeachersInfoVO1 = new ExamTeachersInfoVO();
                    BeanUtils.copyProperties(examTeachersInfoVO, examTeachersInfoVO1);

                    TeacherInformationPO teacherInformationPO = teacherInformationMapper1.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                            .eq(TeacherInformationPO::getTeacherUsername, courseExamAssistantsPO.getTeacherUsername()));
                    if (teacherInformationPO != null) {
                        tutorNames.append(teacherInformationPO.getName()).append(" \n");
                        tutorPhones.append(teacherInformationPO.getPhone()).append(" \n");
                    } else {
                        log.error("存在考试信息中的助教为空 " + courseExamAssistantsPO);
                    }

//                    examTeachersInfoVO.setTutorName(String.valueOf(tutorNames));
                    examTeachersInfoVO1.setTutorName(teacherInformationPO.getName());
                    examTeachersInfoVO1.setTutorPhone(teacherInformationPO.getPhone());
//                    examTeachersInfoVO.setTutorPhone(String.valueOf(tutorPhones));

                    examTeachersInfoVOS.add(examTeachersInfoVO1);
                    count += 1;
                    examTeachersInfoVO.setIndex(count);

                }
            }
        }


        log.info("导出了 " + examTeachersInfoVOS.size() + " 条考试信息数据");

        // 为每个StudentStatusAllVO对象设置序号
        for (int i = 0; i < examTeachersInfoVOS.size(); i++) {
            examTeachersInfoVOS.get(i).setIndex(i + 1);
        }

        InputStream fileInputStreamFromMinio = minioService1.getFileInputStreamFromMinio(globalConfigMapper1.selectOne(new LambdaQueryWrapper<GlobalConfigPO>()
                .eq(GlobalConfigPO::getConfigKey, "考试信息导出模板")).getConfigValue());

        // 使用 ByteArrayOutputStream 将数据写入到流中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        EasyExcel.write(outputStream, ExamTeachersInfoVO.class)
//                .withTemplate(fileInputStreamFromMinio)
//                .sheet("考试信息")
//                // 设置从第3行开始写入数据（行号从0开始计数）
//                .relativeHeadRowIndex(3)
//                .doWrite(examTeachersInfoVOS);
        // 配置 Excel 写入操作
        ExcelWriter excelWriter = null;
        try {
            // 设置响应头（如果在Web环境中）
            // ExcelDataUtil.setResponseHeader(response, errorFileName);

            excelWriter = EasyExcel.write(outputStream, ExamTeachersInfoVO.class)
                    .withTemplate(fileInputStreamFromMinio)
                    .build();

            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(examTeachersInfoVOS, fillConfig, writeSheet);
            // 填充填表时间
            excelWriter.fill(dateInfoMap, writeSheet);  // 如果使用 Map
            excelWriter.finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
        // 获取文件大小
        int fileSize = outputStream.size();
        // 获取桶名和子目录
        String bucketName = MinioBucketEnum.DATA_DOWNLOAD_EXAM_THEACHER.getBucketName();
        String subDirectory = MinioBucketEnum.DATA_DOWNLOAD_EXAM_THEACHER.getSubDirectory();

        // 使用当前日期和时间作为文件名的一部分
        Date generateData = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        String currentDateTime = sdf.format(generateData);

        // 构建文件名
        String fileName = subDirectory + "/" + username + "_" + currentDateTime + "_examTeachersData.xlsx";

        // 上传到 Minio
        // 将流转换为 ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        boolean b = minioService1.uploadStreamToMinio(inputStream, fileName, bucketName);

        // 如果上传成功了 则修改数据库中的用户下载消息
        if (b) {
            DownloadMessagePO downloadMessagePO = new DownloadMessagePO();
            downloadMessagePO.setCreatedAt(generateData);
            downloadMessagePO.setFileName(DownloadFileNameEnum.EXAM_TEACHERS_EXPORT_FILE.getFilename());
            downloadMessagePO.setFileMinioUrl(bucketName + "/" + fileName);
            downloadMessagePO.setFileSize((long) fileSize);
            int insert = downloadMessageMapper.insert(downloadMessagePO);
            log.info("下载考试信息数据、下载文件消息插入 " + insert);

            // 获取自增ID
            Long generatedId = downloadMessagePO.getId();

            platformMessagePO.setRelatedMessageId(generatedId);
            int update = platformMessageMapper.updateById(platformMessagePO);
            log.info("机考信息附件1下载消息所需附件生成完毕 更新结果 " + update);
        }
    }

    /**
     * 导出机考名单
     * @param entity
     * @param username
     */
    public void exportExamStudentsInfo(BatchSetTeachersInfoRO entity, String username, PlatformMessagePO platformMessagePO) {
        log.info("机考名单筛选参数" + entity);
        ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
        CourseExamInfoMapper courseExamInfoMapper1 = ctx.getBean(CourseExamInfoMapper.class);
        CourseInformationMapper courseInformationMapper1 = ctx.getBean(CourseInformationMapper.class);
        StudentStatusMapper studentStatusMapper1 = ctx.getBean(StudentStatusMapper.class);
        ClassInformationMapper classInformationMapper1 = ctx.getBean(ClassInformationMapper.class);
        PersonalInfoMapper personalInfoMapper1 = ctx.getBean(PersonalInfoMapper.class);
        CoursesLearningMapper coursesLearningMapper1 = ctx.getBean(CoursesLearningMapper.class);
        CollegeAdminInformationMapper collegeAdminInformationMapper1 = ctx.getBean(CollegeAdminInformationMapper.class);
        TeacherInformationMapper teacherInformationMapper1 = ctx.getBean(TeacherInformationMapper.class);
        PlatformUserService platformUserService1 = ctx.getBean(PlatformUserService.class);
        MinioService minioService1 = ctx.getBean(MinioService.class);
        GlobalConfigMapper globalConfigMapper1 = ctx.getBean(GlobalConfigMapper.class);
        PlatformMessageMapper platformMessageMapper = ctx.getBean(PlatformMessageMapper.class);
        DownloadMessageMapper downloadMessageMapper = ctx.getBean(DownloadMessageMapper.class);
        PlatformUserMapper platformUserMapper1 = ctx.getBean(PlatformUserMapper.class);

        // 获取数据
        List<ExamStudentsInfoVO> examStudentsInfoVOS = new ArrayList<>();
        @Data
        class DateInfo {
            private String year;
            private String month;
            private String day;

            // 构造器、getters 和 setters
        }

        // 获取当前日期
        LocalDate now = LocalDate.now();

        // 使用 LocalDate 获取年、月、日
        String year = String.valueOf(now.getYear());
        int monthValue = now.getMonthValue();
        String month = String.format("%02d", now.getMonthValue()); // 保证月份是两位数字
        String day = String.format("%02d", now.getDayOfMonth()); // 保证天数是两位数字

        // 设置填表时间到 DateInfo 对象
        DateInfo dateInfo = new DateInfo();
        dateInfo.setYear(year);
        dateInfo.setMonth(month);
        dateInfo.setDay(day);

        String season;
        if (monthValue >= 2 && monthValue <= 7) {
            season = "春季";
        } else {
            season = "秋季";
        }
        // 获取管理员的名字

        PlatformUserPO platformUserPO1 = platformUserMapper1.selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUsername, username));
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationMapper1.selectOne(new LambdaQueryWrapper<CollegeAdminInformationPO>()
                .eq(CollegeAdminInformationPO::getUserId, platformUserPO1.getUserId()));
        // 或者使用 Map
        Map<String, Object> dateInfoMap = new HashMap<>();
        dateInfoMap.put("year", year);
        dateInfoMap.put("month", month);
        dateInfoMap.put("day", day);
        dateInfoMap.put("collegeAdminName", collegeAdminInformationPO.getName());
        dateInfoMap.put("collegeAdminPhone", collegeAdminInformationPO.getPhone());
        dateInfoMap.put("season", season);

        List<CourseExamInfoPO> courseExamInfoPOS = courseExamInfoMapper1.batchSelectData(entity);
        int count = 1;
        for (CourseExamInfoPO courseExamInfoPO : courseExamInfoPOS) {
            ExamStudentsInfoVO examStudentsInfoVO = new ExamStudentsInfoVO();
            examStudentsInfoVO.setIndex(count);


            CourseInformationPO courseInformationPO = courseInformationMapper1.selectOne(new LambdaQueryWrapper<CourseInformationPO>()
                    .eq(CourseInformationPO::getAdminClass, courseExamInfoPO.getClassIdentifier())
                    .eq(CourseInformationPO::getCourseName, courseExamInfoPO.getCourse())
            );


            ClassInformationPO classInformationPO = classInformationMapper1.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                    .eq(ClassInformationPO::getClassIdentifier, courseExamInfoPO.getClassIdentifier()));

            examStudentsInfoVO.setCollege(classInformationPO.getCollege());
            examStudentsInfoVO.setMajorName(classInformationPO.getMajorName());
            examStudentsInfoVO.setStudyForm(classInformationPO.getStudyForm());
            examStudentsInfoVO.setLevel(classInformationPO.getLevel());
            examStudentsInfoVO.setGrade(classInformationPO.getGrade());
            examStudentsInfoVO.setCourseName(courseInformationPO.getCourseName());
            examStudentsInfoVO.setClassName(courseExamInfoPO.getClassName());

            CoursesLearningPO coursesLearningPO = coursesLearningMapper1.selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                    .eq(CoursesLearningPO::getId, courseExamInfoPO.getCourseId()));
            if(courseExamInfoPO != null){
                examStudentsInfoVO.setTeachingClass("教学班 " + coursesLearningPO.getId());
            }

            String teacherUsername = courseExamInfoPO.getTeacherUsername();
            if (teacherUsername != null) {
                TeacherInformationPO teacherInformationPO = teacherInformationMapper1.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                        .eq(TeacherInformationPO::getTeacherUsername, teacherUsername));
                examStudentsInfoVO.setMainTeacherName(teacherInformationPO.getName());
                examStudentsInfoVO.setMainTeacherPhone(teacherInformationPO.getPhone());
            } else {
                examStudentsInfoVO.setMainTeacherName("");
                examStudentsInfoVO.setMainTeacherPhone("");
            }

            List<StudentStatusPO> studentStatusPOS = studentStatusMapper1.selectList(new LambdaQueryWrapper<StudentStatusPO>()
                    .eq(StudentStatusPO::getClassIdentifier, courseExamInfoPO.getClassIdentifier()));
            for(StudentStatusPO studentStatusPO: studentStatusPOS){
                examStudentsInfoVO.setStudentNumber(studentStatusPO.getStudentNumber());
                PersonalInfoPO personalInfoPO = personalInfoMapper1.selectOne(new LambdaQueryWrapper<PersonalInfoPO>()
                        .eq(PersonalInfoPO::getGrade, studentStatusPO.getGrade())
                        .eq(PersonalInfoPO::getIdNumber, studentStatusPO.getIdNumber())
                );
                examStudentsInfoVO.setStudentName(personalInfoPO.getName());
                ExamStudentsInfoVO examStudentsInfoVO1 = new ExamStudentsInfoVO();
                BeanUtils.copyProperties(examStudentsInfoVO, examStudentsInfoVO1);
                examStudentsInfoVOS.add(examStudentsInfoVO1);
                count += 1;
            }


        }


        log.info("导出了 " + examStudentsInfoVOS.size() + " 条考生信息数据");

        // 为每个StudentStatusAllVO对象设置序号
        for (int i = 0; i < examStudentsInfoVOS.size(); i++) {
            examStudentsInfoVOS.get(i).setIndex(i + 1);
        }

        InputStream fileInputStreamFromMinio = minioService1.getFileInputStreamFromMinio(globalConfigMapper1.selectOne(new LambdaQueryWrapper<GlobalConfigPO>()
                .eq(GlobalConfigPO::getConfigKey, "机考考生信息模板")).getConfigValue());

        // 使用 ByteArrayOutputStream 将数据写入到流中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 配置 Excel 写入操作
        ExcelWriter excelWriter = null;
        try {
            // 设置响应头（如果在Web环境中）
            // ExcelDataUtil.setResponseHeader(response, errorFileName);

            excelWriter = EasyExcel.write(outputStream, ExamStudentsInfoVO.class)
                    .withTemplate(fileInputStreamFromMinio)
                    .build();

            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(examStudentsInfoVOS, fillConfig, writeSheet);
            // 填充填表时间
            excelWriter.fill(dateInfoMap, writeSheet);  // 如果使用 Map
            excelWriter.finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
        // 获取文件大小
        int fileSize = outputStream.size();
        // 获取桶名和子目录
        String bucketName = MinioBucketEnum.DATA_DOWNLOAD_EXAM_STUDENT.getBucketName();
        String subDirectory = MinioBucketEnum.DATA_DOWNLOAD_EXAM_STUDENT.getSubDirectory();

        // 使用当前日期和时间作为文件名的一部分
        Date generateData = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        String currentDateTime = sdf.format(generateData);

        // 构建文件名
        String fileName = subDirectory + "/" + username + "_" + currentDateTime + "_examStudentsData.xlsx";

        // 上传到 Minio
        // 将流转换为 ByteArrayInputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        boolean b = minioService1.uploadStreamToMinio(inputStream, fileName, bucketName);

        // 如果上传成功了 则修改数据库中的用户下载消息
        if (b) {
            DownloadMessagePO downloadMessagePO = new DownloadMessagePO();
            downloadMessagePO.setCreatedAt(generateData);
            downloadMessagePO.setFileName(DownloadFileNameEnum.EXAM_STUDENTS_EXPORT_FILE.getFilename());
            downloadMessagePO.setFileMinioUrl(bucketName + "/" + fileName);
            downloadMessagePO.setFileSize((long) fileSize);
            int insert = downloadMessageMapper.insert(downloadMessagePO);
            log.info("下载考试信息数据、下载文件消息插入 " + insert);

            // 获取自增ID
            Long generatedId = downloadMessagePO.getId();

            platformMessagePO.setRelatedMessageId(generatedId);

            int update = platformMessageMapper.updateById(platformMessagePO);
            log.info("机考信息附件2下载消息所需附件生成完毕 更新结果 " + update);
        }
    }


    /**
     * 获取新生缴费数据筛选参数
     * @param filter
     * @return
     */
    public PaymentInformationSelectArgs getNewStudentPaymentInfoArgs(PaymentInfoFilterRO filter) {
        PaymentInformationSelectArgs paymentInformationSelectArgs = new PaymentInformationSelectArgs();
        CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
        filter.setCollege(userBelongCollege.getCollegeName());
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
        paymentInfoFilterROPageRO.getEntity().setCollege(scnuXueliTools.getUserBelongCollege().getCollegeName());
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

