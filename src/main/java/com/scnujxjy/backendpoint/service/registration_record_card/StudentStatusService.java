package com.scnujxjy.backendpoint.service.registration_record_card;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.inverter.registration_record_card.StudentStatusInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 学籍信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@Service
@Slf4j
public class StudentStatusService extends ServiceImpl<StudentStatusMapper, StudentStatusPO> implements IService<StudentStatusPO> {
    @Resource
    private StudentStatusInverter studentStatusInverter;

    /**
     * 根据id查询学籍信息
     *
     * @param id 学籍信息id
     * @return 学籍信息
     */
    public StudentStatusVO detailById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 查询
        StudentStatusPO studentStatusPO = baseMapper.selectById(id);
        return studentStatusInverter.po2VO(studentStatusPO);
    }

    /**
     * 分页查询学籍信息
     *
     * @param studentStatusROPageRO 分页参数
     * @return 学籍信息列表
     */
    public PageVO<StudentStatusVO> pageQueryStudentStatus(PageRO<StudentStatusRO> studentStatusROPageRO) {
        // 校验参数
        if (Objects.isNull(studentStatusROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        StudentStatusRO entity = studentStatusROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new StudentStatusRO();
        }
        // 构建查询参数
        LambdaQueryWrapper<StudentStatusPO> wrapper = Wrappers.<StudentStatusPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), StudentStatusPO::getId, entity.getId())
                .eq(StrUtil.isNotBlank(entity.getStudentNumber()), StudentStatusPO::getStudentNumber, entity.getStudentNumber())
                .eq(StrUtil.isNotBlank(entity.getGrade()), StudentStatusPO::getGrade, entity.getGrade())
                .eq(StrUtil.isNotBlank(entity.getCollege()), StudentStatusPO::getCollege, entity.getCollege())
                .eq(StrUtil.isNotBlank(entity.getTeachingPoint()), StudentStatusPO::getTeachingPoint, entity.getTeachingPoint())
                .eq(StrUtil.isNotBlank(entity.getMajorName()), StudentStatusPO::getMajorName, entity.getMajorName())
                .eq(StrUtil.isNotBlank(entity.getStudyForm()), StudentStatusPO::getStudyForm, entity.getStudyForm())
                .eq(StrUtil.isNotBlank(entity.getLevel()), StudentStatusPO::getLevel, entity.getLevel())
                .eq(StrUtil.isNotBlank(entity.getStudyDuration()), StudentStatusPO::getStudyDuration, entity.getStudyDuration())
                .eq(StrUtil.isNotBlank(entity.getAdmissionNumber()), StudentStatusPO::getAdmissionNumber, entity.getAdmissionNumber())
                .eq(StrUtil.isNotBlank(entity.getAcademicStatus()), StudentStatusPO::getAcademicStatus, entity.getAcademicStatus())
                .eq(Objects.nonNull(entity.getEnrollmentDate()), StudentStatusPO::getEnrollmentDate, entity.getEnrollmentDate())
                .last(StrUtil.isNotBlank(studentStatusROPageRO.getOrderBy()), studentStatusROPageRO.lastOrderSql());

        // 列表查询 或 分页查询 并返回结果
        if (Objects.equals(true, studentStatusROPageRO.getIsAll())) {
            List<StudentStatusPO> studentStatusPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(studentStatusInverter.po2VO(studentStatusPOS));
        } else {
            Page<StudentStatusPO> studentStatusPOPage = baseMapper.selectPage(studentStatusROPageRO.getPage(), wrapper);
            return new PageVO<>(studentStatusPOPage, studentStatusInverter.po2VO(studentStatusPOPage.getRecords()));
        }
    }

    /**
     * 更新学籍信息
     *
     * @param studentStatusRO 学籍信息
     * @return 更新后的学籍信息
     */
    public StudentStatusVO editById(StudentStatusRO studentStatusRO) {
        // 校验参数
        if (Objects.isNull(studentStatusRO) || Objects.isNull(studentStatusRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        // 更新学籍信息
        StudentStatusPO studentStatusPO = studentStatusInverter.ro2PO(studentStatusRO);
        int count = baseMapper.updateById(studentStatusPO);
        if (count <= 0) {
            log.error("更新失败，数据：{}", studentStatusPO);
            return null;
        }

        return detailById(studentStatusRO.getId());
    }

    /**
     * 删除学籍信息
     *
     * @param id 学籍信息id
     * @return 删除学籍信息的数量
     */
    public Integer deleteById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 删除学籍信息
        int count = baseMapper.deleteById(id);
        if (count <= 0) {
            log.error("删除失败，id：{}", id);
            return null;
        }
        return count;
    }
}
