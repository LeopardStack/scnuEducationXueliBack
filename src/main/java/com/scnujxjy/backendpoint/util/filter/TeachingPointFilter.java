package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.GraduationInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
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
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
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
        String loginId = (String) StpUtil.getLoginId();
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
        if (Objects.isNull(scoreInformationFilterROPageRO)) {
            return null;
        }
        ScoreInformationFilterRO scoreInformationFilterRO = scoreInformationFilterROPageRO.getEntity();
        if (Objects.isNull(scoreInformationFilterRO)) {
            scoreInformationFilterRO = new ScoreInformationFilterRO();
        }
        StudentStatusFilterRO studentStatusFilterRO = studentStatusInverter.scoreInformationFilterRO2RO(scoreInformationFilterRO);
        // 条件筛选出本教学点的学生
        List<StudentStatusVO> studentStatusVOS = selectTeachingPointStudent(studentStatusFilterRO);
        if (CollUtil.isEmpty(studentStatusVOS)) {
            return null;
        }
        // 获取其中的学生学号集合
        Map<String, StudentStatusVO> studentNumberToStudentInformationMap = studentStatusVOS.stream()
                .collect(Collectors.toMap(StudentStatusVO::getStudentNumber, Function.identity(), (key1, key2) -> key2));
        Set<String> studentIdSet = studentNumberToStudentInformationMap.keySet();
        if (CollUtil.isEmpty(studentIdSet)) {
            return null;
        }
        // 查询学生的成绩
        List<ScoreInformationPO> scoreInformationPOS = scoreInformationMapper.selectList(Wrappers.<ScoreInformationPO>lambdaQuery()
                .in(ScoreInformationPO::getStudentId, studentIdSet)
                .eq(StrUtil.isNotBlank(scoreInformationFilterRO.getCourseName()), ScoreInformationPO::getCourseName, scoreInformationFilterRO.getCourseName()));
        if (CollUtil.isEmpty(scoreInformationPOS)) {
            return null;
        }
        List<ScoreInformationVO> scoreInformationVOS = scoreInformationPOS.stream()
                .map(ele -> {
                    ScoreInformationVO scoreInformationVO = scoreInformationInverter.po2VO(ele);
                    StudentStatusVO studentStatusVO = studentNumberToStudentInformationMap.getOrDefault(scoreInformationVO.getStudentId(), StudentStatusVO.builder().build());
                    scoreInformationVO.setStudyForm(studentStatusVO.getStudyForm())
                            .setLevel(studentStatusVO.getLevel())
                            .setClassName(studentStatusVO.getClassName());
                    // 填充个人名称
                    if (StrUtil.isNotBlank(studentStatusVO.getGrade()) && StrUtil.isNotBlank(studentStatusVO.getIdNumber())) {
                        PersonalInfoPO personalInfoPO = personalInfoMapper.selectOne(Wrappers.<PersonalInfoPO>lambdaQuery()
                                .eq(PersonalInfoPO::getGrade, ele.getGrade())
                                .eq(PersonalInfoPO::getIdNumber, studentStatusVO.getIdNumber()));
                        if (Objects.nonNull(personalInfoPO) && StrUtil.isNotBlank(personalInfoPO.getName())) {
                            scoreInformationVO.setName(personalInfoPO.getName());
                        }
                    }
                    return scoreInformationVO;
                })
                .collect(Collectors.toList());
        FilterDataVO<ScoreInformationVO> scoreInformationVOFilterDataVO = new FilterDataVO<>();
        scoreInformationVOFilterDataVO.setTotal(scoreInformationPOS.size());
        scoreInformationVOFilterDataVO.setData(scoreInformationVOS);
        return scoreInformationVOFilterDataVO;
    }

    /**
     * 根据教学点查询学生的学籍信息;
     * 学籍信息：StudentStatus + PersonalInformation
     *
     * @param studentStatusFilterROPageRO 条件分页查询参数
     * @return 条件分页查询学生学籍信息结果
     */
    @Override
    public FilterDataVO<StudentStatusAllVO> filterStudentStatus(PageRO<StudentStatusFilterRO> studentStatusFilterROPageRO) {
        if (Objects.isNull(studentStatusFilterROPageRO)) {
            return null;
        }
        StudentStatusFilterRO studentStatusFilterRO = studentStatusFilterROPageRO.getEntity();
        if (Objects.isNull(studentStatusFilterRO)) {
            studentStatusFilterRO = new StudentStatusFilterRO();
        }
        // 条件查询：该教学点下的学生
        List<StudentStatusVO> studentStatusVOS = selectTeachingPointStudent(studentStatusFilterRO);
        if (CollUtil.isEmpty(studentStatusVOS)) {
            return null;
        }
        // 查询PersonalInformation
        List<StudentStatusAllVO> studentStatusAllVOS = studentStatusVOS.stream()
                .map(ele -> {
                    // 填充PersonalInformation
                    PersonalInfoPO personalInfoPO = new PersonalInfoPO();
                    GraduationInfoPO graduationInfoPO = new GraduationInfoPO();
                    if (StrUtil.isNotBlank(ele.getIdNumber()) && StrUtil.isNotBlank(ele.getGrade())) {
                        personalInfoPO = personalInfoMapper.selectOne(Wrappers.<PersonalInfoPO>lambdaQuery()
                                .eq(PersonalInfoPO::getIdNumber, ele.getIdNumber())
                                .eq(PersonalInfoPO::getGrade, ele.getGrade()));
                        graduationInfoPO = graduationInfoMapper.selectOne(Wrappers.<GraduationInfoPO>lambdaQuery()
                                .eq(GraduationInfoPO::getIdNumber, ele.getIdNumber())
                                .eq(GraduationInfoPO::getGrade, ele.getGrade()));
                    }
                    return studentStatusInverter.po2VO(ele, personalInfoPO, graduationInfoPO);
                }).collect(Collectors.toList());
        FilterDataVO<StudentStatusAllVO> studentStatusAllVOFilterDataVO = new FilterDataVO<>();
        studentStatusAllVOFilterDataVO.setData(studentStatusAllVOS);
        studentStatusAllVOFilterDataVO.setTotal(studentStatusAllVOS.size());
        return studentStatusAllVOFilterDataVO;
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
        if (Objects.isNull(paymentInfoFilterROPageRO)) {
            return null;
        }
        PaymentInfoFilterRO paymentInfoFilterRO = paymentInfoFilterROPageRO.getEntity();
        if (Objects.isNull(paymentInfoFilterRO)) {
            paymentInfoFilterRO = new PaymentInfoFilterRO();
        }
        // 转换参数，使用查询学生中的筛选：年级、学院、专业名称、层次、学习形式 、行政班别、学号
        StudentStatusFilterRO studentStatusFilterRO = studentStatusInverter.paymentInformationFilterRO2RO(paymentInfoFilterRO);
        if (Objects.isNull(studentStatusFilterRO)) {
            return null;
        }
        List<StudentStatusVO> studentStatusVOS = selectTeachingPointStudent(studentStatusFilterRO);
        if (CollUtil.isEmpty(studentStatusVOS)) {
            return null;
        }
        // 根据学生分页，提高查询效率
        List<StudentStatusVO> pageStudentStatusVOS = ListUtil.page(Math.toIntExact(paymentInfoFilterROPageRO.getPageNumber()), Math.toIntExact(paymentInfoFilterROPageRO.getPageSize()), studentStatusVOS);
        List<PaymentInfoVO> paymentInfoVOS = new ArrayList<>();
        pageStudentStatusVOS.stream()
                .filter(ele -> StrUtil.isNotBlank(ele.getStudentNumber()))
                .map(ele -> {
                    List<PaymentInfoPO> paymentInfoPOS = paymentInfoMapper.selectList(Wrappers.<PaymentInfoPO>lambdaQuery().eq(PaymentInfoPO::getStudentNumber, ele.getStudentNumber()));
                    return paymentInfoPOS.stream()
                            .filter(Objects::nonNull)
                            .map(paymentInfoPO -> paymentInfoInverter.po2VO(ele, paymentInfoPO))
                            .collect(Collectors.toList());
                })
                .forEach(paymentInfoVOS::addAll);
        if (CollUtil.isEmpty(paymentInfoVOS)) {
            return null;
        }
        // 查找数量
        Set<String> studentNumberSet = studentStatusVOS.stream()
                .map(StudentStatusVO::getStudentNumber)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
        if (CollUtil.isEmpty(studentNumberSet)) {
            return null;
        }
        Integer count = paymentInfoMapper.selectCount(Wrappers.<PaymentInfoPO>lambdaQuery().in(PaymentInfoPO::getStudentNumber, studentNumberSet));
        FilterDataVO<PaymentInfoVO> filterDataVO = new FilterDataVO<>();
        filterDataVO.setData(paymentInfoVOS);
        filterDataVO.setTotal(count);
        return filterDataVO;
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
        if (Objects.isNull(courseInformationROPageRO)) {
            return null;
        }
        CourseInformationRO courseInformationRO = courseInformationROPageRO.getEntity();
        if (Objects.isNull(courseInformationRO)) {
            courseInformationRO = new CourseInformationRO();
        }
        // 查询指定教学点的班级信息
        StudentStatusFilterRO studentStatusFilterRO = studentStatusInverter.courseInformationRO2RO(courseInformationRO);
        List<ClassInformationPO> classInformationPOS = selectTeachingPointClassInformation(studentStatusFilterRO);
        if (CollUtil.isEmpty(classInformationPOS)) {
            return null;
        }
        // 获取其中的ClassIdentifier集合
        Map<String, ClassInformationPO> classIdentifier2ClassInformationMap = classInformationPOS.stream()
                .collect(Collectors.toMap(ClassInformationPO::getClassIdentifier, Function.identity(), (key1, key2) -> key2));
        Set<String> classIdentifierSet = classIdentifier2ClassInformationMap.keySet();
        if (CollUtil.isEmpty(classIdentifierSet)) {
            return null;
        }
        // 根据ClassIdentifier在 CourseInformation 中查询课程信息
        List<CourseInformationPO> courseInformationPOS = courseInformationMapper.selectList(Wrappers.<CourseInformationPO>lambdaQuery()
                .in(CourseInformationPO::getAdminClass, classIdentifierSet)
                .eq(StrUtil.isNotBlank(courseInformationRO.getCourseName()), CourseInformationPO::getCourseName, courseInformationRO.getCourseName()));
        if (CollUtil.isEmpty(courseInformationPOS)) {
            return null;
        }
        List<CourseInformationPO> pageCourseInformationPOS = ListUtil.page(Math.toIntExact(courseInformationROPageRO.getPageNumber()), Math.toIntExact(courseInformationROPageRO.getPageSize()), courseInformationPOS);
        if (CollUtil.isEmpty(pageCourseInformationPOS)) {
            return null;
        }
        List<CourseInformationVO> courseInformationVOS = pageCourseInformationPOS.stream()
                .map(ele -> {
                    ClassInformationPO classInformationPO = classIdentifier2ClassInformationMap.getOrDefault(ele.getAdminClass(), ClassInformationPO.builder().className("未知班级").build());
                    return courseInformationInverter.classInformation2VO(classInformationPO, ele);
                }).collect(Collectors.toList());
        FilterDataVO<CourseInformationVO> courseInformationVOFilterDataVO = new FilterDataVO<>();
        courseInformationVOFilterDataVO.setData(courseInformationVOS);
        courseInformationVOFilterDataVO.setTotal(courseInformationPOS.size());
        return courseInformationVOFilterDataVO;
    }

    /**
     * 条件查询分页查询指定教学点排课信息
     *
     * @param courseScheduleFilterROPageRO 排课表分页条件查询参数
     * @return 分页条件查询指定教学点排课信息结果
     */
    public FilterDataVO<SchedulesVO> filterSchedulesInformation(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        if (Objects.isNull(courseScheduleFilterROPageRO)) {
            return null;
        }
        CourseScheduleFilterRO courseScheduleFilterRO = Optional.ofNullable(courseScheduleFilterROPageRO.getEntity()).orElse(new CourseScheduleFilterRO());
        // 查询指定教学点的班级信息
        StudentStatusFilterRO studentStatusFilterRO = studentStatusInverter.courseScheduleFilterRO2RO(courseScheduleFilterRO);
        List<ClassInformationPO> classInformationPOS = selectTeachingPointClassInformation(studentStatusFilterRO);
        if (CollUtil.isEmpty(classInformationPOS)) {
            return null;
        }
        // 班级信息和排课表之间信息是通过年级、学习形式、层次、行政班级、专业名称来确定的，因此要先确定这些信息，去重
        List<ClassInformationPO> afterConsultClassInformationSet = new LinkedList<>();
        Set<String> gradeStudyFormLevelAdminClassMajorNameSet = new HashSet<>();
        for (ClassInformationPO classInformationPO : classInformationPOS) {
            String gradeStudyFormLevelAdminClassMajorName = classInformationPO.getGrade() + classInformationPO.getStudyForm() + classInformationPO.getLevel() + classInformationPO.getClassName() + classInformationPO.getMajorName();
            if (!gradeStudyFormLevelAdminClassMajorNameSet.contains(gradeStudyFormLevelAdminClassMajorName)) {
                gradeStudyFormLevelAdminClassMajorNameSet.add(gradeStudyFormLevelAdminClassMajorName);
                afterConsultClassInformationSet.add(classInformationPO);
            }
        }
        // 从去重后的班级信息集合中遍历查询排课信息
        List<CourseSchedulePO> classInformationResult = new LinkedList<>();
        afterConsultClassInformationSet.stream()
                .filter(ele -> StrUtil.isNotBlank(ele.getGrade()) && StrUtil.isNotBlank(ele.getStudyForm()) && StrUtil.isNotBlank(ele.getLevel()) && StrUtil.isNotBlank(ele.getClassName()) && StrUtil.isNotBlank(ele.getMajorName()))
                .map(ele -> {
                    LambdaQueryWrapper<CourseSchedulePO> wrapper = Wrappers.<CourseSchedulePO>lambdaQuery()
                            .eq(CourseSchedulePO::getGrade, ele.getGrade())
                            .eq(CourseSchedulePO::getStudyForm, ele.getStudyForm())
                            .eq(CourseSchedulePO::getLevel, ele.getLevel())
                            .eq(CourseSchedulePO::getAdminClass, ele.getClassName())
                            .eq(CourseSchedulePO::getMajorName, ele.getMajorName())
                            .eq(StrUtil.isNotBlank(courseScheduleFilterRO.getTeachingClassName()), CourseSchedulePO::getTeachingClass, courseScheduleFilterRO.getTeachingClassName())
                            .eq(StrUtil.isNotBlank(courseScheduleFilterRO.getCourseName()), CourseSchedulePO::getCourseName, courseScheduleFilterRO.getCourseName())
                            .eq(StrUtil.isNotBlank(courseScheduleFilterRO.getMainTeachingName()), CourseSchedulePO::getMainTeacherName, courseScheduleFilterRO.getMainTeachingName())
                            .eq(StrUtil.isNotBlank(courseScheduleFilterRO.getTeachingMethod()), CourseSchedulePO::getTeachingMethod, courseScheduleFilterRO.getTeachingMethod());
                    // 添加授课时间筛选
                    if (Objects.nonNull(courseScheduleFilterRO.getTeachingStartDate()) && Objects.nonNull(courseScheduleFilterRO.getTeachingEndDate())) {
                        String startDateStr = DateUtil.format(courseScheduleFilterRO.getTeachingStartDate(), "yyyy-MM-dd HH:mm");
                        String endDateStr = DateUtil.format(courseScheduleFilterRO.getTeachingEndDate(), "yyyy-MM-dd HH:mm");
                        String[] startDateArr = startDateStr.split(" ");
                        String[] endDateArr = endDateStr.split(" ");
                        if (startDateArr.length == endDateArr.length && endDateArr.length == 2) {
                            wrapper.between(CourseSchedulePO::getTeachingDate, startDateArr[0], endDateArr[0]);
                            wrapper.last("AND SUBSTRING_INDEX(teaching_time, '-', 1) > " + "'" + startDateArr[1] + "'"
                                    + " AND SUBSTRING_INDEX(teaching_time, '-', -1) < " + "'" + endDateArr[1] + "'");
                        }
                    }
                    return courseScheduleMapper.selectList(wrapper);
                })
                .filter(CollUtil::isNotEmpty)
                .forEach(classInformationResult::addAll);
        if (CollUtil.isEmpty(classInformationResult)) {
            return null;
        }
        List<SchedulesVO> schedulesVOS = classInformationResult.stream()
                .filter(Objects::nonNull)
                .map(ele -> {
                    VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectById(ele.getOnlinePlatform());
                    return courseScheduleInverter.po2SchedulesVO(ele, videoStreamRecordPO);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(schedulesVOS)) {
            return null;
        }
        FilterDataVO<SchedulesVO> schedulesVOFilterDataVO = new FilterDataVO<>();
        schedulesVOFilterDataVO.setData(schedulesVOS);
        schedulesVOFilterDataVO.setTotal(schedulesVOS.size());
        return schedulesVOFilterDataVO;
    }


}
