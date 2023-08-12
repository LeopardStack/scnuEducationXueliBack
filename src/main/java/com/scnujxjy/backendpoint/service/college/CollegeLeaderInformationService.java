package com.scnujxjy.backendpoint.service.college;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeLeaderInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeLeaderInformationMapper;
import com.scnujxjy.backendpoint.inverter.college.CollegeLeaderInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeLeaderInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeLeaderInformationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 负责人信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class CollegeLeaderInformationService extends ServiceImpl<CollegeLeaderInformationMapper, CollegeLeaderInformationPO> implements IService<CollegeLeaderInformationPO> {

    @Resource
    private CollegeLeaderInformationInverter collegeLeaderInformationInverter;

    /**
     * 根据userId查询负责人信息
     *
     * @param userId 用户id
     * @return 负责人信息
     */
    public CollegeLeaderInformationVO detailById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        CollegeLeaderInformationPO collegeLeaderInformationPO = baseMapper.selectById(userId);
        // 转换数据并返回结果
        return collegeLeaderInformationInverter.po2VO(collegeLeaderInformationPO);
    }

    /**
     * 分页查询负责人信息
     *
     * @param collegeLeaderInformationROPageRO 负责人信息分页查询参数
     * @return 负责人信息分页查询结果
     */
    public PageVO<CollegeLeaderInformationVO> pageQueryCollegeLeaderInformation(PageRO<CollegeLeaderInformationRO> collegeLeaderInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(collegeLeaderInformationROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        CollegeLeaderInformationRO entity = collegeLeaderInformationROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new CollegeLeaderInformationRO();
        }
        // 构造查询参数
        LambdaQueryWrapper<CollegeLeaderInformationPO> wrapper = Wrappers.<CollegeLeaderInformationPO>lambdaQuery()
                .eq(StrUtil.isNotBlank(entity.getUserId()), CollegeLeaderInformationPO::getUserId, entity.getUserId())
                .like(StrUtil.isNotBlank(entity.getName()), CollegeLeaderInformationPO::getName, entity.getName())
                .eq(StrUtil.isNotBlank(entity.getCollegeId()), CollegeLeaderInformationPO::getCollegeId, entity.getCollegeId())
                .eq(StrUtil.isNotBlank(entity.getPhone()), CollegeLeaderInformationPO::getPhone, entity.getPhone());
        // 列表查询 或 分页查询 并返回数据
        if (Objects.equals(true, collegeLeaderInformationROPageRO.getIsAll())) {
            List<CollegeLeaderInformationPO> collegeLeaderInformationPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(collegeLeaderInformationInverter.po2VO(collegeLeaderInformationPOS));
        } else {
            Page<CollegeLeaderInformationPO> collegeLeaderInformationPOPage = baseMapper.selectPage(collegeLeaderInformationROPageRO.getPage(), wrapper);
            return new PageVO<>(collegeLeaderInformationPOPage, collegeLeaderInformationInverter.po2VO(collegeLeaderInformationPOPage.getRecords()));
        }
    }

    /**
     * 根据userId更新负责人信息
     *
     * @param collegeLeaderInformationRO 更新的负责人信息
     * @return 更新后的负责人信息
     */
    public CollegeLeaderInformationVO editById(CollegeLeaderInformationRO collegeLeaderInformationRO) {
        // 参数校验
        if (Objects.isNull(collegeLeaderInformationRO) || StrUtil.isBlank(collegeLeaderInformationRO.getUserId())) {
            log.error("参数缺失");
            return null;
        }
        // 参数转换
        CollegeLeaderInformationPO collegeLeaderInformationPO = collegeLeaderInformationInverter.ro2PO(collegeLeaderInformationRO);
        // 更新数据
        int count = baseMapper.updateById(collegeLeaderInformationPO);
        // 更新后校验
        if (count <= 0) {
            log.error("更新失败，数据：{}", collegeLeaderInformationPO);
            return null;
        }
        // 返回数据
        return detailById(collegeLeaderInformationRO.getUserId());
    }

    /**
     * 根据userId删除负责人信息
     *
     * @param userId 用户id
     * @return 删除数量
     */
    public Integer deleteById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            log.error("参数校验");
            return null;
        }
        // 删除数据
        int count = baseMapper.deleteById(userId);
        // 删除后校验
        if (count <= 0) {
            log.error("删除失败，userId：{}", userId);
            return null;
        }
        // 返回数据
        return count;
    }
}
