package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.GraduationInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.inverter.core_data.PaymentInfoInverter;
import com.scnujxjy.backendpoint.inverter.registration_record_card.StudentStatusInverter;
import com.scnujxjy.backendpoint.inverter.teaching_process.CourseInformationInverter;
import com.scnujxjy.backendpoint.inverter.teaching_process.CourseScheduleInverter;
import com.scnujxjy.backendpoint.inverter.teaching_process.ScoreInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointInformationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TeachingPointFilter extends AbstractFilter {
    @Resource
    private TeachingPointAdminInformationMapper teachingPointAdminInformationMapper;

    @Resource
    private TeachingPointInformationService teachingPointInformationService;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private ScoreInformationMapper scoreInformationMapper;

    @Resource
    private ScoreInformationInverter scoreInformationInverter;

    @Resource
    private StudentStatusInverter studentStatusInverter;

    @Resource
    private PersonalInfoMapper personalInfoMapper;

    @Resource
    private GraduationInfoMapper graduationInfoMapper;

    @Resource
    private PaymentInfoMapper paymentInfoMapper;

    @Resource
    private PaymentInfoInverter paymentInfoInverter;

    @Resource
    private CourseInformationInverter courseInformationInverter;

    @Resource
    private CourseScheduleInverter courseScheduleInverter;


    /**
     * 根据教学点查询班级信息；
     * 条件查询、分页查询；
     * 筛选条件：行政班别（班级名称）、年级、学院名称、专业名称、层次、学习形式
     *
     * @param studentStatusFilterRO 传递的筛选条件参数
     * @return 班级信息结果
     */
    private List<ClassInformationPO> selectTeachingPointClassInformation(StudentStatusFilterRO studentStatusFilterRO) {
        if (Objects.isNull(studentStatusFilterRO)) {
            studentStatusFilterRO = new StudentStatusFilterRO();
        }
        // 从Token中获取到UserId，根据UserId定位教学点TeachingPointId；
        String loginId = StpUtil.getLoginIdAsString().replace("M", "");
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        TeachingPointAdminInformationPO teachingPointAdminInformationPO = teachingPointAdminInformationMapper.selectOne(Wrappers.<TeachingPointAdminInformationPO>lambdaQuery().eq(TeachingPointAdminInformationPO::getIdCardNumber, loginId));
        if (Objects.isNull(teachingPointAdminInformationPO) || StrUtil.isBlank(teachingPointAdminInformationPO.getTeachingPointId())) {
            return null;
        }
        TeachingPointInformationVO teachingPointInformationVO = teachingPointInformationService.detailById(teachingPointAdminInformationPO.getTeachingPointId());
        if (Objects.isNull(teachingPointInformationVO) || StrUtil.isBlank(teachingPointInformationVO.getAlias())) {
            return null;
        }
        // 根据TeachingPointId定位教学点信息Alias，筛选：行政班别、年级、学院名称、专业名称、层次、学习形式、
        List<ClassInformationPO> classInformationPOS = classInformationMapper.selectList(Wrappers.<ClassInformationPO>lambdaQuery()
                .like(ClassInformationPO::getClassName, teachingPointInformationVO.getAlias())
                .eq(StrUtil.isNotBlank(studentStatusFilterRO.getClassName()), ClassInformationPO::getClassName, studentStatusFilterRO.getClassName())
                .eq(StrUtil.isNotBlank(studentStatusFilterRO.getGrade()), ClassInformationPO::getGrade, studentStatusFilterRO.getGrade())
                .eq(StrUtil.isNotBlank(studentStatusFilterRO.getCollege()), ClassInformationPO::getCollege, studentStatusFilterRO.getCollege())
                .eq(StrUtil.isNotBlank(studentStatusFilterRO.getMajorName()), ClassInformationPO::getMajorName, studentStatusFilterRO.getMajorName())
                .eq(StrUtil.isNotBlank(studentStatusFilterRO.getLevel()), ClassInformationPO::getLevel, studentStatusFilterRO.getLevel())
                .eq(StrUtil.isNotBlank(studentStatusFilterRO.getStudyForm()), ClassInformationPO::getStudyForm, studentStatusFilterRO.getStudyForm()));
        if (CollUtil.isEmpty(classInformationPOS)) {
            return null;
        }
        return classInformationPOS;
    }

    /**
     * 条件筛选指定教学点学生信息；
     * 从Token中获取到UserId，根据UserId定位教学点TeachingPointId；
     * 根据TeachingPointId定位教学点信息Alias；
     * 使用Alias在班级信息ClassName中模糊匹配班级信息ClassIdentifier；
     * 通过ClassIdentifier筛选出所有的学生信息；
     *
     * @param studentStatusFilterRO 条件查询参数，筛选项：学号、考生号、证件号码、学制、学习状态
     * @return 查询指定教学点、筛选后的学生信息
     */
    private List<StudentStatusVO> selectTeachingPointStudent(StudentStatusFilterRO studentStatusFilterRO) {
        if (Objects.isNull(studentStatusFilterRO)) {
            studentStatusFilterRO = new StudentStatusFilterRO();
        }
        // 查询指定教学点的班级信息
        List<ClassInformationPO> classInformationPOS = selectTeachingPointClassInformation(studentStatusFilterRO);
        if (CollUtil.isEmpty(classInformationPOS)) {
            return null;
        }
        // 使用Alias在班级信息ClassName中模糊匹配班级信息ClassIdentifier；
        Map<String, ClassInformationPO> classIdentifier2ClassInformationMap = classInformationPOS.stream()
                .collect(Collectors.toMap(ClassInformationPO::getClassIdentifier, Function.identity(), (key1, key2) -> key2));
        Set<String> classInformationIdentifierSet = classIdentifier2ClassInformationMap.keySet();
        if (CollUtil.isEmpty(classInformationIdentifierSet)) {
            return null;
        }
        // 通过ClassIdentifier筛选出所有的学生信息，筛选学号
        LambdaQueryWrapper<StudentStatusPO> wrapper = Wrappers.<StudentStatusPO>lambdaQuery()
                .in(StudentStatusPO::getClassIdentifier, classInformationIdentifierSet)
                .eq(StrUtil.isNotBlank(studentStatusFilterRO.getStudentNumber()), StudentStatusPO::getStudentNumber, studentStatusFilterRO.getStudentNumber())
                .eq(StrUtil.isNotBlank(studentStatusFilterRO.getAdmissionNumber()), StudentStatusPO::getAdmissionNumber, studentStatusFilterRO.getAdmissionNumber())
                .eq(StrUtil.isNotBlank(studentStatusFilterRO.getIdNumber()), StudentStatusPO::getIdNumber, studentStatusFilterRO.getIdNumber())
                .eq(StrUtil.isNotBlank(studentStatusFilterRO.getAcademicStatus()), StudentStatusPO::getAcademicStatus, studentStatusFilterRO.getAcademicStatus())
                .eq(StrUtil.isNotBlank(studentStatusFilterRO.getStudyDuration()), StudentStatusPO::getStudyDuration, studentStatusFilterRO.getStudyDuration());
        List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectList(wrapper);
        if (CollUtil.isEmpty(studentStatusPOS)) {
            return null;
        }
        List<StudentStatusVO> studentStatusVOS = studentStatusPOS.stream()
                .map(ele -> {
                    StudentStatusVO studentStatusVO = studentStatusInverter.po2VO(ele);
                    // 填充班级名
                    studentStatusVO.setClassName(classIdentifier2ClassInformationMap.getOrDefault(studentStatusVO.getClassIdentifier(), ClassInformationPO.builder().className("未知班级").build()).getClassName());
                    return studentStatusVO;
                }).collect(Collectors.toList());
        return studentStatusVOS;
    }

    /**
     * 分页条件查询指定教学点学生成绩；
     *
     * @param scoreInformationFilterROPageRO 分页条件查询参数，筛选项：学号、课程名称
     * @return 分页条件查询后的学生成绩
     */
    public FilterDataVO<ScoreInformationVO> filterGradeInfo(PageRO<ScoreInformationFilterRO> scoreInformationFilterROPageRO) {
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
    public FilterDataVO<SchedulesVO> filterSchedulesInformation(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
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
        courseScheduleFilterROPageRO.getEntity().setClassNameSet(classNameSet);
        List<SchedulesVO> schedulesVOS = courseScheduleMapper.selectTeachingPointSchedulesInformation(courseScheduleFilterROPageRO.getEntity(),
                courseScheduleFilterROPageRO.getPageSize(),
                courseScheduleFilterROPageRO.getPageSize() * (courseScheduleFilterROPageRO.getPageNumber() - 1));
        Long count = courseScheduleMapper.selectTeachingPointSchedulesInformationCount(courseScheduleFilterROPageRO.getEntity());
        return new FilterDataVO<>(schedulesVOS, count);
    }


}
