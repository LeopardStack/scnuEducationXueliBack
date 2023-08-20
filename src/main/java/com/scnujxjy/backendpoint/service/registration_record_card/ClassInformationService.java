package com.scnujxjy.backendpoint.service.registration_record_card;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.inverter.registration_record_card.ClassInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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

}
