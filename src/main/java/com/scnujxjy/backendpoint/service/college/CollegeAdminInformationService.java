package com.scnujxjy.backendpoint.service.college;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.SM3;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.RoleEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.inverter.college.CollegeAdminInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.college.CollegeAdminInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.model.vo.college.CollegeAdminInformationVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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


}
