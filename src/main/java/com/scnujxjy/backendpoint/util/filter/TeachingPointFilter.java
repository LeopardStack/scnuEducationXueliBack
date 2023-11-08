package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.GraduationInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.ScoreInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.GraduationInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.inverter.registration_record_card.StudentStatusInverter;
import com.scnujxjy.backendpoint.inverter.teaching_process.ScoreInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScoreInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.StudentStatusAllVO;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointInformationService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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


    /**
     * 条件筛选指定教学点学生信息；
     * 从Token中获取到UserId，根据UserId定位教学点TeachingPointId；
     * 根据TeachingPointId定位教学点信息Alias；
     * 使用Alias在班级信息ClassName中模糊匹配班级信息ClassIdentifier；
     * 通过ClassIdentifier筛选出所有的学生信息；
     *
     * @param studentStatusRO 条件查询参数，筛选项：年级、学院、专业名称、层次、学习形式 、行政班别
     * @return 查询指定教学点、筛选后的学生信息
     */
    private List<StudentStatusVO> selectTeachingPointStudent(StudentStatusRO studentStatusRO) {
        if (Objects.isNull(studentStatusRO)) {
            studentStatusRO = new StudentStatusRO();
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
        // 根据TeachingPointId定位教学点信息Alias，筛选：行政班别
        List<ClassInformationPO> classInformationPOS = classInformationMapper.selectList(Wrappers.<ClassInformationPO>lambdaQuery()
                .like(ClassInformationPO::getClassName, teachingPointInformationVO.getAlias())
                .eq(StrUtil.isNotBlank(studentStatusRO.getClassName()), ClassInformationPO::getClassName, studentStatusRO.getClassName()));
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
        // 通过ClassIdentifier筛选出所有的学生信息，筛选年级、学院名称、专业名称、层次、学习形式
        LambdaQueryWrapper<StudentStatusPO> wrapper = Wrappers.<StudentStatusPO>lambdaQuery()
                .in(StudentStatusPO::getClassIdentifier, classInformationIdentifierSet)
                .eq(StrUtil.isNotBlank(studentStatusRO.getGrade()), StudentStatusPO::getGrade, studentStatusRO.getGrade())
                .eq(StrUtil.isNotBlank(studentStatusRO.getCollege()), StudentStatusPO::getCollege, studentStatusRO.getCollege())
                .eq(StrUtil.isNotBlank(studentStatusRO.getMajorName()), StudentStatusPO::getMajorName, studentStatusRO.getMajorName())
                .eq(StrUtil.isNotBlank(studentStatusRO.getLevel()), StudentStatusPO::getLevel, studentStatusRO.getLevel())
                .eq(StrUtil.isNotBlank(studentStatusRO.getStudyForm()), StudentStatusPO::getStudyForm, studentStatusRO.getStudyForm());
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
     * @param studentStatusROPageRO 分页条件查询参数，筛选项：年级、学院、专业名称、层次、学习形式 、行政班别
     * @return 分页条件查询后的学生成绩
     */
    public PageVO<ScoreInformationVO> selectTeachingPointStudentScoreInformation(PageRO<StudentStatusRO> studentStatusROPageRO) {
        if (Objects.isNull(studentStatusROPageRO)) {
            return null;
        }
        StudentStatusRO studentStatusRO = studentStatusROPageRO.getEntity();
        if (Objects.isNull(studentStatusRO)) {
            studentStatusRO = new StudentStatusRO();
        }
        // 条件筛选出本教学点的学生
        List<StudentStatusVO> studentStatusVOS = selectTeachingPointStudent(studentStatusRO);
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
        List<ScoreInformationPO> scoreInformationPOS = scoreInformationMapper.selectList(Wrappers.<ScoreInformationPO>lambdaQuery().in(ScoreInformationPO::getStudentId, studentIdSet));
        if (CollUtil.isEmpty(scoreInformationPOS)) {
            return null;
        }
        List<ScoreInformationPO> pageScoreInformationPOS = ListUtil.page(Math.toIntExact(studentStatusROPageRO.getPageNumber()), Math.toIntExact(studentStatusROPageRO.getPageSize()), scoreInformationPOS);
        List<ScoreInformationVO> scoreInformationVOS = pageScoreInformationPOS.stream()
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
        return new PageVO<>(studentStatusROPageRO, (long) scoreInformationPOS.size(), scoreInformationVOS);
    }

    /**
     * 根据教学点查询学生的学籍信息;
     * 学籍信息：StudentStatus + PersonalInformation
     *
     * @param studentStatusROPageRO 条件分页查询参数
     * @return 条件分页查询学生学籍信息结果
     */
    public PageVO<StudentStatusAllVO> selectTeachingPointStudentAllStatus(PageRO<StudentStatusRO> studentStatusROPageRO) {
        if (Objects.isNull(studentStatusROPageRO)) {
            return null;
        }
        StudentStatusRO studentStatusRO = studentStatusROPageRO.getEntity();
        if (Objects.isNull(studentStatusRO)) {
            studentStatusRO = new StudentStatusRO();
        }
        // 条件查询：该教学点下的学生
        List<StudentStatusVO> studentStatusVOS = selectTeachingPointStudent(studentStatusRO);
        if (CollUtil.isEmpty(studentStatusVOS)) {
            return null;
        }
        // 分页，先分页再查询PersonalInformation加快效率
        List<StudentStatusVO> pageStudentStatusVOS = ListUtil.page(Math.toIntExact(studentStatusROPageRO.getPageNumber()), Math.toIntExact(studentStatusROPageRO.getPageSize()), studentStatusVOS);
        List<StudentStatusAllVO> studentStatusAllVOS = pageStudentStatusVOS.stream()
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
        return new PageVO<>(studentStatusROPageRO, (long) studentStatusVOS.size(), studentStatusAllVOS);
    }


}
