package com.scnujxjy.backendpoint.service.registration_record_card;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Sets;
import com.scnujxjy.backendpoint.dao.entity.admission_information.AdmissionInformationPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.inverter.registration_record_card.ClassInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseInfoVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusChangeClassInfoVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.TransferMajorClassInformationSelector;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.util.filter.AbstractFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 班级信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-14
 */
@Service
@Slf4j
public class ClassInformationService extends ServiceImpl<ClassInformationMapper, ClassInformationPO> implements IService<ClassInformationPO> {
    @Resource
    private ClassInformationInverter classInformationInverter;

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private AdmissionInformationMapper admissionInformationMapper;

    /**
     * 根据id查询班级信息
     *
     * @param id 班级信息id
     * @return 班级详细信息
     */
    public ClassInformationVO detailById(Long id) {
        // 校验信息
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        ClassInformationPO classInformationPO = baseMapper.selectById(id);
        // 转换数据并返回
        return classInformationInverter.po2VO(classInformationPO);
    }

    /**
     * 分页查询班级信息
     *
     * @param classInformationROPageRO 班级信息分页查询参数
     * @return 班级信息分页查询结果
     */
    public PageVO<ClassInformationVO> pageQueryClassInformation(PageRO<ClassInformationRO> classInformationROPageRO) {
        // 校验信息
        if (Objects.isNull(classInformationROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        ClassInformationRO entity = classInformationROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new ClassInformationRO();
        }
        // 构造查询条件
        LambdaQueryWrapper<ClassInformationPO> wrapper = Wrappers.<ClassInformationPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), ClassInformationPO::getId, entity.getId())
                .eq(StrUtil.isNotBlank(entity.getClassIdentifier()), ClassInformationPO::getClassIdentifier, entity.getClassIdentifier())
                .eq(StrUtil.isNotBlank(entity.getGrade()), ClassInformationPO::getGrade, entity.getGrade())
                .eq(StrUtil.isNotBlank(entity.getClassStudentPrefix()), ClassInformationPO::getClassStudentPrefix, entity.getClassStudentPrefix())
                .like(StrUtil.isNotBlank(entity.getClassName()), ClassInformationPO::getClassName, entity.getClassName())
                .eq(StrUtil.isNotBlank(entity.getStudyForm()), ClassInformationPO::getStudyForm, entity.getStudyForm())
                .eq(StrUtil.isNotBlank(entity.getLevel()), ClassInformationPO::getLevel, entity.getLevel())
                .eq(StrUtil.isNotBlank(entity.getStudyPeriod()), ClassInformationPO::getStudyPeriod, entity.getStudyPeriod())
                .eq(StrUtil.isNotBlank(entity.getCollege()), ClassInformationPO::getCollege, entity.getCollege())
                .eq(Objects.nonNull(entity.getFemaleCount()), ClassInformationPO::getFemaleCount, entity.getFemaleCount())
                .eq(Objects.nonNull(entity.getTotalCount()), ClassInformationPO::getTotalCount, entity.getTotalCount())
                .eq(Objects.nonNull(entity.getGraduateTotalCount()), ClassInformationPO::getGraduateTotalCount, entity.getGraduateTotalCount())
                .eq(Objects.nonNull(entity.getGraduateFemaleCount()), ClassInformationPO::getGraduateFemaleCount, entity.getGraduateFemaleCount())
                .like(StrUtil.isNotBlank(entity.getMajorName()), ClassInformationPO::getMajorName, entity.getMajorName())
                .between(Objects.nonNull(entity.getAdmissionStartDate()) && Objects.nonNull(entity.getAdmissionEndDate()),
                        ClassInformationPO::getAdmissionDate, entity.getAdmissionStartDate(), entity.getAdmissionEndDate())
                .between(Objects.nonNull(entity.getGraduationStartDate()) && Objects.nonNull(entity.getGraduationEndDate()),
                        ClassInformationPO::getGraduationDate, entity.getGraduationStartDate(), entity.getGraduationStartDate())
                .eq(StrUtil.isNotBlank(entity.getStudentStatus()), ClassInformationPO::getStudentStatus, entity.getStudentStatus())
                .eq(StrUtil.isNotBlank(entity.getMajorCode()), ClassInformationPO::getMajorCode, entity.getMajorCode())
                .eq(Objects.nonNull(entity.getTuition()), ClassInformationPO::getTuition, entity.getTuition())
                .eq(Objects.nonNull(entity.getIsTeacherStudent()), ClassInformationPO::getIsTeacherStudent, entity.getIsTeacherStudent())
                .last(StrUtil.isNotBlank(classInformationROPageRO.getOrderBy()), classInformationROPageRO.lastOrderSql());

        // 列表查询 或 分页查询 并返回数据
        if (Objects.equals(true, classInformationROPageRO.getIsAll())) {
            List<ClassInformationPO> classInformationPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(classInformationInverter.po2VO(classInformationPOS));
        } else {
            Page<ClassInformationPO> classInformationPOPage = baseMapper.selectPage(classInformationROPageRO.getPage(), wrapper);
            return new PageVO<>(classInformationPOPage, classInformationInverter.po2VO(classInformationPOPage.getRecords()));
        }
    }

    /**
     * 根据id更新班级信息
     *
     * @param classInformationRO 班级信息
     * @return
     */
    public ClassInformationVO editById(ClassInformationRO classInformationRO) {
        // 数据校验
        if (Objects.isNull(classInformationRO) || Objects.isNull(classInformationRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        // 转换数据
        ClassInformationPO classInformationPO = classInformationInverter.ro2PO(classInformationRO);
        // 更新数据
        int count = baseMapper.updateById(classInformationPO);
        // 检查更新结果
        if (count <= 0) {
            log.error("更新失败，数据：{}", classInformationPO);
            return null;
        }
        // 返回更新后的数据
        return detailById(classInformationRO.getId());
    }

    /**
     * 根据id删除班级信息
     *
     * @param id 班级信息id
     * @return 删除数量
     */
    public Integer deleteById(Long id) {
        // 校验信息
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 删除数据
        int count = baseMapper.deleteById(id);
        // 检查删除结果
        if (count <= 0) {
            log.error("删除失败，id：{}", id);
            return null;
        }
        // 返回删除结果
        return count;
    }

    /**
     * 根据角色筛选器 获取班级信息
     *
     * @param classInformationFilterROPageRO 缴费班级信息
     * @param filter                         角色筛选器
     * @return
     */
    public FilterDataVO allPageQueryPayInfoFilter(PageRO<ClassInformationFilterRO> classInformationFilterROPageRO,
                                                  AbstractFilter filter) {
        return filter.filterClassInfo(classInformationFilterROPageRO);
    }

    /**
     * 获取缴费数据的筛选参数
     *
     * @param loginId 登录用户名
     * @param filter  筛选参数（如果是其他用户则需要额外的限制参数，二级学院、教师、教学点）
     * @return
     */
    public ClassInformationSelectArgs getClassInformationArgs(String loginId, AbstractFilter filter) {
        return filter.filterClassInformationSelectArgs();
    }

    /**
     * 为不同角色导出班级数据
     *
     * @param pageRO
     * @param filter
     * @param userId
     * @param platformMessagePO
     */
    public void generateBatchClassInformationData(PageRO<ClassInformationFilterRO> pageRO, AbstractFilter filter, String userId, PlatformMessagePO platformMessagePO) {
        // 校验参数
        if (Objects.isNull(pageRO)) {
            log.error("导出班级信息数据参数缺失");

        }


        filter.exportClassInformationData(pageRO, userId, platformMessagePO);
    }

    public List<ClassInformationVO> selectClassInformationByBatchIndex(Long batchIndex) {
        if (Objects.isNull(batchIndex)) {
            return null;
        }
        List<CourseSchedulePO> courseScheduleVOS = courseScheduleMapper.selectList(Wrappers.<CourseSchedulePO>lambdaQuery().eq(CourseSchedulePO::getBatchIndex, batchIndex));
        // 查询去重班级信息
        if (CollUtil.isEmpty(courseScheduleVOS)) {
            return null;
        }
        Set<String> set = new HashSet<>();
        List<ClassInformationVO> classInformationVOS = new ArrayList<>();
        courseScheduleVOS
                .forEach(ele -> {
                    String key = ele.getGrade() + ele.getStudyForm() + ele.getLevel() + ele.getAdminClass() + ele.getMajorName();
                    if (!set.contains(key)) {
                        ClassInformationPO classInformationPO = baseMapper.selectOne(Wrappers.<ClassInformationPO>lambdaQuery()
                                .eq(ClassInformationPO::getGrade, ele.getGrade())
                                .eq(ClassInformationPO::getStudyForm, ele.getStudyForm())
                                .eq(ClassInformationPO::getLevel, ele.getLevel())
                                .eq(ClassInformationPO::getClassName, ele.getAdminClass())
                                .eq(ClassInformationPO::getMajorName, ele.getMajorName()));
                        set.add(key);
                        classInformationVOS.add(classInformationInverter.po2VO(classInformationPO));
                    }
                });
        return classInformationVOS;
    }

    public List<StudentStatusChangeClassInfoVO> pageQueryStudentStatusChangeClassInformation(ClassInformationRO classInformationRO) {
        return getBaseMapper().getStudentStatusChangeClassInfoByFilter(classInformationRO);
    }


    public List<TransferMajorClassInformationSelector> transferMajorSelector() {
        List<TransferMajorClassInformationSelector> selectors = new ArrayList<>();
        String username = StpUtil.getLoginIdAsString();
        // 获取学籍信息
        List<StudentStatusVO> studentStatusVOS = studentStatusMapper.selectStudentByidNumber(username);
        if (CollUtil.isEmpty(studentStatusVOS)) {
            log.warn("获取学生学籍信息为空");
            return Lists.newArrayList();
        }
        // 根据年级排序
        studentStatusVOS = studentStatusVOS.stream()
                .sorted(Comparator.comparing(StudentStatusVO::getGrade))
                .collect(Collectors.toList());
        StudentStatusVO lastStudentStatusVO = studentStatusVOS.get(studentStatusVOS.size() - 1);
        // 获取到最新的年级
        String grade = lastStudentStatusVO.getGrade();
        // 查询相同年级的班级
        List<ClassInformationPO> classInformationPOS = baseMapper.selectList(Wrappers.<ClassInformationPO>lambdaQuery().eq(ClassInformationPO::getGrade, grade));
        if (CollUtil.isEmpty(classInformationPOS)) {
            log.warn("该年级无班级信息 {}", grade);
            return Lists.newArrayList();
        }
        // 获取该学生的分数
        AdmissionInformationPO admissionInformationPO = admissionInformationMapper.selectOne(Wrappers.<AdmissionInformationPO>lambdaQuery()
                .eq(AdmissionInformationPO::getIdCardNumber, username)
                .eq(AdmissionInformationPO::getGrade, grade));
        if (Objects.isNull(admissionInformationPO)) {
            log.warn("该学生的录取信息为空");
            return selectors;
        }
        // 查询同年级班级所有专业下的录取分数
        Map<String, List<ClassInformationPO>> majorCode2ClassInformationListMap = classInformationPOS.stream()
                .collect(Collectors.groupingBy(ClassInformationPO::getMajorCode));
        List<AdmissionInformationPO> admissionInformationPOS = admissionInformationMapper.selectList(Wrappers.<AdmissionInformationPO>lambdaQuery()
                .in(AdmissionInformationPO::getMajorCode, majorCode2ClassInformationListMap.keySet())
                .eq(AdmissionInformationPO::getGrade, grade));
        if (CollUtil.isEmpty(admissionInformationPOS)) {
            log.error("专业代码集合 {} 年级 {} 查询到录取信息为空", majorCode2ClassInformationListMap.keySet(), grade);
            return Lists.newArrayList();
        }
        // 筛选出小于等于学生分数的专业
        Set<String> avaliableMajorCodeSet = Sets.newHashSet();
        Map<String, List<AdmissionInformationPO>> majorCode2AdmissionInformationListMap = admissionInformationPOS.stream()
                .collect(Collectors.groupingBy(AdmissionInformationPO::getMajorCode));
        majorCode2AdmissionInformationListMap.forEach((majorCode, pos) -> {
            // 根据录取分数排序
            pos.sort(Comparator.comparing(AdmissionInformationPO::getTotalScore));
            Integer minTotalScore = pos.get(0).getTotalScore();
            if (admissionInformationPO.getTotalScore() >= minTotalScore) {
                // 该专业学生能选
                avaliableMajorCodeSet.add(majorCode);
            }
        });
        // 查询可选专业的信息
        if (CollUtil.isEmpty(avaliableMajorCodeSet)) {
            log.warn("该学生不存在可以转的专业");
            return Lists.newArrayList();
        }
        avaliableMajorCodeSet.forEach(majorCode -> {
            List<ClassInformationPO> classList = majorCode2ClassInformationListMap.get(majorCode);
            if (CollUtil.isEmpty(classList)) {
                log.warn("此专业代码 {} 不存在班级信息", majorCode);
                return;
            }
            selectors.add(TransferMajorClassInformationSelector.builder()
                    .majorCode(majorCode)
                    .majorName(classList.get(0).getMajorName())
                    .studyForm(classList.get(0).getStudyForm())
                    .studyPeriod(classList.get(0).getStudyPeriod())
                    .classInformationVOS(classInformationInverter.po2VO(classList))
                    .build());
        });
        return selectors;
    }
}
