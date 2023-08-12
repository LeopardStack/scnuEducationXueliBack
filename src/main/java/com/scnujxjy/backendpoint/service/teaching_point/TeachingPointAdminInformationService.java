package com.scnujxjy.backendpoint.service.teaching_point;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointAdminInformationMapper;
import com.scnujxjy.backendpoint.inverter.teaching_point.TeachingPointAdminInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_point.TeachingPointAdminInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_point.TeachingPointAdminInformationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 教学点教务员信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class TeachingPointAdminInformationService extends ServiceImpl<TeachingPointAdminInformationMapper, TeachingPointAdminInformationPO> implements IService<TeachingPointAdminInformationPO> {

    @Resource
    private TeachingPointAdminInformationInverter teachingPointAdminInformationInverter;

    /**
     * 根据userId查询教学点教务员信息
     *
     * @param userId
     * @return
     */
    public TeachingPointAdminInformationVO detailById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        TeachingPointAdminInformationPO teachingPointAdminInformationPO = baseMapper.selectById(userId);
        return teachingPointAdminInformationInverter.po2VO(teachingPointAdminInformationPO);
    }

    /**
     * 分页查询教学点教务员信息
     *
     * @param teachingPointAdminInformationROPageRO 教学点教务员分页查询参数
     * @return 教学点教务员分页信息
     */
    public PageVO<TeachingPointAdminInformationVO> pageQueryTeachingPointAdminInformation(PageRO<TeachingPointAdminInformationRO> teachingPointAdminInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(teachingPointAdminInformationROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        TeachingPointAdminInformationRO entity = teachingPointAdminInformationROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new TeachingPointAdminInformationRO();
        }
        // 构建查询参数
        LambdaQueryWrapper<TeachingPointAdminInformationPO> wrapper = Wrappers.<TeachingPointAdminInformationPO>lambdaQuery()
                .eq(StrUtil.isNotBlank(entity.getUserId()), TeachingPointAdminInformationPO::getUserId, entity.getUserId())
                .eq(StrUtil.isNotBlank(entity.getTeachingPointId()), TeachingPointAdminInformationPO::getTeachingPointId, entity.getTeachingPointId())
                .eq(StrUtil.isNotBlank(entity.getPhone()), TeachingPointAdminInformationPO::getPhone, entity.getPhone())
                .eq(StrUtil.isNotBlank(entity.getIdCardNumber()), TeachingPointAdminInformationPO::getIdCardNumber, entity.getIdCardNumber())
                .like(StrUtil.isNotBlank(entity.getName()), TeachingPointAdminInformationPO::getName, entity.getName());
        // 列表查询 或 分页查询 并返回结果
        if (Objects.equals(true, teachingPointAdminInformationROPageRO.getIsAll())) {
            List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(teachingPointAdminInformationInverter.po2VO(teachingPointAdminInformationPOS));
        } else {
            Page<TeachingPointAdminInformationPO> teachingPointAdminInformationPOPage = baseMapper.selectPage(teachingPointAdminInformationROPageRO.getPage(), wrapper);
            return new PageVO<>(teachingPointAdminInformationPOPage, teachingPointAdminInformationInverter.po2VO(teachingPointAdminInformationPOPage.getRecords()));
        }
    }

    /**
     * 根据userId更新教学点教务员信息
     *
     * @param teachingPointAdminInformationRO 更新的教学点教务员信息
     * @return 更新后的教学点教务员信息
     */
    public TeachingPointAdminInformationVO editById(TeachingPointAdminInformationRO teachingPointAdminInformationRO) {
        // 参数校验
        if (Objects.isNull(teachingPointAdminInformationRO) || StrUtil.isBlank(teachingPointAdminInformationRO.getUserId())) {
            log.error("参数缺失");
            return null;
        }
        // 转换类型
        TeachingPointAdminInformationPO teachingPointAdminInformationPO = teachingPointAdminInformationInverter.ro2PO(teachingPointAdminInformationRO);
        // 更新数据
        int count = baseMapper.updateById(teachingPointAdminInformationPO);
        // 更新校验
        if (count <= 0) {
            log.error("更新失败，数据：{}", teachingPointAdminInformationPO);
            return null;
        }
        // 返回数据
        return detailById(teachingPointAdminInformationRO.getUserId());
    }

    /**
     * 根据userId删除教学点教务员信息
     *
     * @param userId 用户id
     * @return 删除的数量
     */
    public Integer deleteById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            log.error("参数缺失");
            return null;
        }
        // 删除数据
        int count = baseMapper.deleteById(userId);
        // 删除参数校验
        if (count <= 0) {
            log.error("删除失败，userId：{}", userId);
            return null;
        }
        // 返回数据
        return count;
    }

}
