package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.*;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import com.scnujxjy.backendpoint.util.tool.LogExecutionTime;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    private CollegeInformationPO getCollegeName(){
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
        long total =  courseScheduleMapper.countCourseSchedulesByConditions(collegeInformationPO.getCollegeName(),
                courseScheduleFilter);
        courseScheduleFilterDataVO.setCourseSchedulePOS(courseSchedulePOS);
        courseScheduleFilterDataVO.setTotal(total);
        return courseScheduleFilterDataVO;
    }


    /**
     * 筛选学籍数据
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
                studentStatusFilter.getPageSize() * (studentStatusFilter.getPageNumber() -1));
        long total =  studentStatusMapper.getCountByFilterAndPageManager0(studentStatusFilter.getEntity());
        studentStatusVOFilterDataVO.setData(studentStatusVOS);
        studentStatusVOFilterDataVO.setTotal(total);

        return studentStatusVOFilterDataVO;
    }

    /**
     * 获取学籍数据筛选参数
     * @return
     */
    @Override
    @LogExecutionTime
    public StudentStatusSelectArgs filterStudentStatusSelectArgs() {
        StudentStatusSelectArgs studentStatusSelectArgs = new StudentStatusSelectArgs() ;
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
        List<PaymentInfoVO> paymentInfoVOList =  paymentInfoMapper.getStudentPayInfoByFilter(
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
     * @return
     */
    @Override
    @LogExecutionTime
    public PaymentInformationSelectArgs filterPaymentInformationSelectArgs() {
        PaymentInformationSelectArgs paymentInformationSelectArgs = new PaymentInformationSelectArgs();
        PaymentInfoFilterRO filter = new PaymentInfoFilterRO();

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
     * 为继续教育学院学历教育部获取成绩信息
     * @param scoreInformationFilterROPageRO 成绩筛选参数
     * @return
     */
    @Override
    public FilterDataVO filterGradeInfo(PageRO<ScoreInformationFilterRO> scoreInformationFilterROPageRO) {
        // 增加学院筛选参数，为不同的二级学院管理员查询成绩做好权限划分
        CollegeInformationPO college = getCollegeName();
        scoreInformationFilterROPageRO.getEntity().setCollege(college.getCollegeName());

        FilterDataVO<ScoreInformationVO> studentStatusVOFilterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId( ) + " 查询成绩的参数是 " + scoreInformationFilterROPageRO);
        List<ScoreInformationVO> paymentInfoVOList =  scoreInformationMapper.getStudentGradeInfoByFilter(
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
                courseInformationFilter.getPageSize() * (courseInformationFilter.getPageNumber() -1));
        long total =  courseInformationMapper.getCountByFilterAndPage(courseInformationFilter.getEntity());
        courseInformationFilterDataVO.setData(courseInformationVOS);
        courseInformationFilterDataVO.setTotal(total);

        return courseInformationFilterDataVO;
    }

    /**
     * 获取二级学院教学计划筛选参数
     * @return
     */
    @Override
    public CourseInformationSelectArgs filterCourseInformationSelectArgs() {
        CourseInformationSelectArgs courseInformationSelectArgs = new CourseInformationSelectArgs();

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

        List<String> grades = courseInformationMapper.selectDistinctGrades(collegeInformationPO.getCollegeName());
        List<String> majorNames = courseInformationMapper.selectDistinctMajorNames(collegeInformationPO.getCollegeName());
        List<String> levels = courseInformationMapper.selectDistinctLevels(collegeInformationPO.getCollegeName());
        List<String> courseNames = courseInformationMapper.selectDistinctCourseNames(collegeInformationPO.getCollegeName());
        List<String> studyForms = courseInformationMapper.selectDistinctStudyForms(collegeInformationPO.getCollegeName());
        List<String> classNames = courseInformationMapper.selectDistinctClassNames(collegeInformationPO.getCollegeName());
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
     * 获取排课表的课程信息
     * @param courseScheduleFilterROPageRO
     * @return
     */
    @Override
    public FilterDataVO filterScheduleCoursesInformation(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        // 设置学院信息
        CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
        courseScheduleFilterROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());

        List<ScheduleCourseInformationVO> scheduleCourseInformationVOS = courseScheduleMapper.
                selectCoursesInformation(courseScheduleFilterROPageRO.getEntity(),
                        courseScheduleFilterROPageRO.getPageSize(),
                        (courseScheduleFilterROPageRO.getPageNumber() - 1) * courseScheduleFilterROPageRO.getPageSize()
                );

        // 从排课表中获取的课程 还要进行再次处理 还是直接从教学计划中来获取 再与其进行对比
        for(ScheduleCourseInformationVO scheduleCourseInformationVO: scheduleCourseInformationVOS){
            CourseCoverChangeRO courseCoverChangeRO = new CourseCoverChangeRO();
            courseCoverChangeRO.setGrade(scheduleCourseInformationVO.getGrade());
            courseCoverChangeRO.setMajorName(scheduleCourseInformationVO.getMajorName());
            courseCoverChangeRO.setLevel(scheduleCourseInformationVO.getLevel());
            courseCoverChangeRO.setStudyForm(scheduleCourseInformationVO.getStudyForm());
            courseCoverChangeRO.setClassName(scheduleCourseInformationVO.getAdminClass());
            courseCoverChangeRO.setCourseName(scheduleCourseInformationVO.getCourseName());
            CourseInformationVO courseInformationVO = courseInformationMapper.selectSingleCourse(courseCoverChangeRO);
            scheduleCourseInformationVO.setCourseCover(courseInformationVO.getCourseCover());
        }

        FilterDataVO<ScheduleCourseInformationVO> filterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId( ) + " 查询排课表课程信息的参数是 " + courseScheduleFilterROPageRO);

        long l = courseScheduleMapper.countCoursesInformation(courseScheduleFilterROPageRO.getEntity());
        filterDataVO.setTotal(l);
        filterDataVO.setData(scheduleCourseInformationVOS);

        return filterDataVO;
    }


    /**
     * 获取排课表详细信息
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

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 为了设置时区为东八区

            Date now = new Date();
            Date teachingEndDate = null;
            try {
                String teachingEndDateStr = schedulesVO.getTeachingEndDate();
                teachingEndDate = sdf.parse(teachingEndDateStr);
            }catch (ParseException e){
                log.error("直播日期解析失败" + e.toString());
            }

            // 如果结束时间在当前时间之前
            if (teachingEndDate != null && teachingEndDate.before(now)) {
                schedulesVO.setOnlinePlatform("已结束");
            }
            // 如果onlinePlatform为空或仅包含空格
            else if (onlinePlatform == null || onlinePlatform.trim().isEmpty()) {
                schedulesVO.setOnlinePlatform("已结束");
            }
            // 如果onlinePlatform不为空且与VideoStreamRecord的ID匹配
            else {
                VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(
                        new LambdaQueryWrapper<VideoStreamRecordPO>().eq(VideoStreamRecordPO::getId, onlinePlatform));

                if (videoStreamRecordPO != null) {
                    // 此处你只检查了videoStreamRecordPO是否为null，但没使用它的其他属性。
                    // 假设你只想检查它是否存在，并据此设置onlinePlatform
                    schedulesVO.setOnlinePlatform("直播中");
                }
            }
        }


        FilterDataVO<SchedulesVO> filterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId( ) + " 查询排课表课程信息的参数是 " + courseScheduleFilterROPageRO);

        long l = courseScheduleMapper.selectCoursesInformationCount(courseScheduleFilterROPageRO.getEntity());
        filterDataVO.setTotal(l);
        filterDataVO.setData(schedulesVOS);

        return filterDataVO;
    }

    /**
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取排课表课程数据筛选参数
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
     * @param classInformationFilterROPageRO 班级筛选参数
     * @return
     */
    @Override
    public FilterDataVO filterClassInfo(PageRO<ClassInformationFilterRO> classInformationFilterROPageRO) {
        // 设置学院信息
        CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
        classInformationFilterROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());

        FilterDataVO<ClassInformationVO> classInformationVOFilterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId( ) + " 查询班级的参数是 " + classInformationFilterROPageRO);
        List<ClassInformationVO> classInformationVOList =  classInformationMapper.getClassInfoByFilter(
                classInformationFilterROPageRO.getEntity(),
                classInformationFilterROPageRO.getPageSize(),
                (classInformationFilterROPageRO.getPageNumber() - 1) * classInformationFilterROPageRO.getPageSize()
        );
        long countStudentPayInfoByFilter = classInformationMapper.getCountClassInfoByFilter(classInformationFilterROPageRO.getEntity());
//        long countStudentPayInfoByFilter = 100L;
        classInformationVOFilterDataVO.setTotal(countStudentPayInfoByFilter);
        classInformationVOFilterDataVO.setData(classInformationVOList);

        return classInformationVOFilterDataVO;
    }

    /**
     * 采用线程池技术，提高 SQL 查询筛选参数效率
     * 获取班级数据筛选参数
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
}

