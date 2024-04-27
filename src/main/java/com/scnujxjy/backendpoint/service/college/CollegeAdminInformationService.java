package com.scnujxjy.backendpoint.service.college;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.inverter.college.CollegeAdminInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeAdminInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeAdminInformationVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 教务员信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class CollegeAdminInformationService extends ServiceImpl<CollegeAdminInformationMapper, CollegeAdminInformationPO> implements IService<CollegeAdminInformationPO> {

    @Resource
    private CollegeAdminInformationInverter collegeAdminInformationInverter;
    @Autowired
    private PlatformUserService platformUserService;

    /**
     * 通过userId查询教务员信息
     *
     * @param userId 用户id
     * @return 教务员信息
     */
    public CollegeAdminInformationVO detailById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        CollegeAdminInformationPO collegeAdminInformationPO = baseMapper.selectById(userId);
        // 转换数据 并返回
        return collegeAdminInformationInverter.po2VO(collegeAdminInformationPO);
    }

    /**
     * 分页查询学院教务员信息
     *
     * @param collegeAdminInformationROPageRO 学院教务员分页查询参数
     * @return 学院教务员信息分页
     */
    public PageVO<CollegeAdminInformationVO> pageQueryCollegeAdminInformation(PageRO<CollegeAdminInformationRO> collegeAdminInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(collegeAdminInformationROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        CollegeAdminInformationRO entity = collegeAdminInformationROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new CollegeAdminInformationRO();
        }
        // 构建查询语句
        LambdaQueryWrapper<CollegeAdminInformationPO> wrapper = Wrappers.<CollegeAdminInformationPO>lambdaQuery()
                .eq(StrUtil.isNotBlank(entity.getUserId()), CollegeAdminInformationPO::getUserId, entity.getUserId())
                .like(StrUtil.isNotBlank(entity.getName()), CollegeAdminInformationPO::getName, entity.getName())
                .eq(StrUtil.isNotBlank(entity.getCollegeId()), CollegeAdminInformationPO::getCollegeId, entity.getCollegeId())
                .eq(StrUtil.isNotBlank(entity.getPhone()), CollegeAdminInformationPO::getPhone, entity.getPhone())
                .eq(StrUtil.isNotBlank(entity.getWorkNumber()), CollegeAdminInformationPO::getWorkNumber, entity.getWorkNumber())
                .last(StrUtil.isNotBlank(collegeAdminInformationROPageRO.getOrderBy()), collegeAdminInformationROPageRO.lastOrderSql());
        // 列表查询 或 分页查询 并返回结果
        if (Objects.equals(true, collegeAdminInformationROPageRO.getIsAll())) {
            List<CollegeAdminInformationPO> collegeAdminInformationPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(collegeAdminInformationInverter.po2VO(collegeAdminInformationPOS));
        } else {
            Page<CollegeAdminInformationPO> collegeAdminInformationPOPage = baseMapper.selectPage(collegeAdminInformationROPageRO.getPage(), wrapper);
            return new PageVO<>(collegeAdminInformationPOPage, collegeAdminInformationInverter.po2VO(collegeAdminInformationPOPage.getRecords()));
        }
    }

    /**
     * 根据userId更新学院教务员信息
     *
     * @param collegeAdminInformationRO
     * @return
     */
    public CollegeAdminInformationVO editById(CollegeAdminInformationRO collegeAdminInformationRO) {
        // 参数校验
        if (Objects.isNull(collegeAdminInformationRO) || StrUtil.isBlank(collegeAdminInformationRO.getUserId())) {
            log.error("参数缺失");
            return null;
        }
        // 转换类型
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationInverter.ro2PO(collegeAdminInformationRO);
        // 更新数据
        int count = baseMapper.updateById(collegeAdminInformationPO);
        // 更新校验
        if (count <= 0) {
            log.error("更新失败，数据：{}", collegeAdminInformationPO);
            return null;
        }
        // 返回数据
        return detailById(collegeAdminInformationRO.getUserId());
    }

    /**
     * 根据userId删除学院教务员信息
     *
     * @param userId
     * @return
     */
    public Integer deleteById(String userId) {
        // 参数校验
        if (StrUtil.isBlank(userId)) {
            log.error("参数缺失");
            return null;
        }
        // 删除数据
        int count = baseMapper.deleteById(userId);
        // 删除校验
        if (count <= 0) {
            log.error("删除失败，userId：{}", userId);
            return null;
        }
        return count;
    }

    /**
     * 根据学院查询学院管理员 username
     *
     * @param collegeId 学院 id
     * @return
     */
    public List<String> adminUsernameByCollegeId(String collegeId) {
        if (Objects.isNull(collegeId)) {
            throw new BusinessException("学院 id 为空");
        }
        List<CollegeAdminInformationPO> collegeAdminInformationPOS = baseMapper.selectList(Wrappers.<CollegeAdminInformationPO>lambdaQuery()
                .eq(CollegeAdminInformationPO::getCollegeId, collegeId)
                .select(CollegeAdminInformationPO::getUserId));
        if (CollUtil.isEmpty(collegeAdminInformationPOS)) {
            return ListUtil.of();
        }
        List<Long> userIdList = collegeAdminInformationPOS.stream()
                .map(CollegeAdminInformationPO::getUserId)
                .map(Long::valueOf)
                .collect(Collectors.toList());
        Map<Long, String> userId2UsernameMap = platformUserService.getUsernameByUserId(userIdList);
        if (MapUtil.isEmpty(userId2UsernameMap)) {
            return ListUtil.of();
        }
        return ListUtil.toList(userId2UsernameMap.values());
    }


}
