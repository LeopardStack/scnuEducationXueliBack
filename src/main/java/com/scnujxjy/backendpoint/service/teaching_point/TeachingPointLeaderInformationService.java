package com.scnujxjy.backendpoint.service.teaching_point;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointLeaderInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointLeaderInformationMapper;
import com.scnujxjy.backendpoint.inverter.teaching_point.TeachingPointLeaderInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointLeaderInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointLeaderInformationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 教学点负责人信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class TeachingPointLeaderInformationService extends ServiceImpl<TeachingPointLeaderInformationMapper, TeachingPointLeaderInformationPO> implements IService<TeachingPointLeaderInformationPO> {

    @Resource
    private TeachingPointLeaderInformationInverter teachingPointLeaderInformationInverter;

    /**
     * 根据userId查询教学点负责人信息
     *
     * @param userId 用户id
     * @return 教学点负责人信息
     */
    public TeachingPointLeaderInformationVO detailById(String userId) {
        // 数据校验
        if (StrUtil.isBlank(userId)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        TeachingPointLeaderInformationPO teachingPointLeaderInformationPO = baseMapper.selectById(userId);
        // 转换并返回数据
        return teachingPointLeaderInformationInverter.po2VO(teachingPointLeaderInformationPO);
    }

    /**
     * 分页查询教学点负责人信息
     *
     * @param teachingPointLeaderInformationROPageRO 分页参数
     * @return 分页查询后的数据
     */
    public PageVO<TeachingPointLeaderInformationVO> pageQueryTeachingPointLeaderInformation(PageRO<TeachingPointLeaderInformationRO> teachingPointLeaderInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(teachingPointLeaderInformationROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        TeachingPointLeaderInformationRO entity = teachingPointLeaderInformationROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new TeachingPointLeaderInformationRO();
        }
        // 构建查询参数
        LambdaQueryWrapper<TeachingPointLeaderInformationPO> wrapper = Wrappers.<TeachingPointLeaderInformationPO>lambdaQuery()
                .eq(StrUtil.isNotBlank(entity.getUserId()), TeachingPointLeaderInformationPO::getUserId, entity.getUserId())
                .eq(StrUtil.isNotBlank(entity.getTeachingPointId()), TeachingPointLeaderInformationPO::getTeachingPointId, entity.getTeachingPointId())
                .eq(StrUtil.isNotBlank(entity.getIdCardNumber()), TeachingPointLeaderInformationPO::getIdCardNumber, entity.getIdCardNumber())
                .like(StrUtil.isNotBlank(entity.getName()), TeachingPointLeaderInformationPO::getName, entity.getName())
                .eq(StrUtil.isNotBlank(entity.getPhone()), TeachingPointLeaderInformationPO::getPhone, entity.getPhone());
        // 列表查询 或 分页查询 并返回结果
        if (Objects.equals(true, teachingPointLeaderInformationROPageRO.getIsAll())) {
            List<TeachingPointLeaderInformationPO> teachingPointLeaderInformationPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(teachingPointLeaderInformationInverter.po2VO(teachingPointLeaderInformationPOS));
        } else {
            Page<TeachingPointLeaderInformationPO> teachingPointLeaderInformationPOPage = baseMapper.selectPage(teachingPointLeaderInformationROPageRO.getPage(), wrapper);
            return new PageVO<>(teachingPointLeaderInformationPOPage, teachingPointLeaderInformationInverter.po2VO(teachingPointLeaderInformationPOPage.getRecords()));
        }
    }

    /**
     * 根据userId更新教学点负责人信息
     *
     * @param teachingPointLeaderInformationRO 更新的教学点负责人信息
     * @return 更新后的教学点负责人信息
     */
    public TeachingPointLeaderInformationVO editById(TeachingPointLeaderInformationRO teachingPointLeaderInformationRO) {
        // 参数校验
        if (Objects.isNull(teachingPointLeaderInformationRO) || StrUtil.isBlank(teachingPointLeaderInformationRO.getUserId())) {
            log.error("参数缺失");
            return null;
        }
        // 数据转换
        TeachingPointLeaderInformationPO teachingPointLeaderInformationPO = teachingPointLeaderInformationInverter.ro2PO(teachingPointLeaderInformationRO);
        // 数据更新
        int count = baseMapper.updateById(teachingPointLeaderInformationPO);
        // 数据校验
        if (count <= 0) {
            log.error("更新失败，数据：{}", teachingPointLeaderInformationPO);
            return null;
        }
        // 返回数据
        return detailById(teachingPointLeaderInformationRO.getUserId());
    }

    /**
     * 根据userId删除教学点负责人信息
     *
     * @param userId 用户id
     * @return 删除数量
     */
    public Integer deleteById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            log.error("参数缺失");
            return null;
        }
        // 删除数据
        int count = baseMapper.deleteById(userId);
        // 数据校验
        if (count <= 0) {
            log.error("删除失败，userId：{}", userId);
            return null;
        }
        // 返回结果
        return count;
    }
}
