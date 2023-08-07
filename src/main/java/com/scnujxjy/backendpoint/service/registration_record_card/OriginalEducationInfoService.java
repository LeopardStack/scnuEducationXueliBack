package com.scnujxjy.backendpoint.service.registration_record_card;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.OriginalEducationInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.OriginalEducationInfoMapper;
import com.scnujxjy.backendpoint.inverter.registration_record_card.OriginalEducationInfoInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.OriginalEducationInfoRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.OriginalEducationInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 原学历信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class OriginalEducationInfoService extends ServiceImpl<OriginalEducationInfoMapper, OriginalEducationInfoPO> implements IService<OriginalEducationInfoPO> {

    @Resource
    private OriginalEducationInfoInverter originalEducationInfoInverter;

    /**
     * 根据id查询原学历信息
     *
     * @param id 原学历信息id
     * @return 原学历信息
     */
    public OriginalEducationInfoVO detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        OriginalEducationInfoPO originalEducationInfoPO = baseMapper.selectById(id);
        return originalEducationInfoInverter.po2VO(originalEducationInfoPO);
    }

    /**
     * 分页查询原学历信息
     *
     * @param originalEducationInfoROPageRO 查询原学历信息分页参数
     * @return 分页查询结果
     */
    public PageVO<OriginalEducationInfoVO> pageQueryOriginalEducationInfo(PageRO<OriginalEducationInfoRO> originalEducationInfoROPageRO) {
        // 参数校验
        if (Objects.isNull(originalEducationInfoROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        OriginalEducationInfoRO entity = originalEducationInfoROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new OriginalEducationInfoRO();
        }
        // 构造查询参数
        LambdaQueryWrapper<OriginalEducationInfoPO> wrapper = Wrappers.<OriginalEducationInfoPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), OriginalEducationInfoPO::getId, entity.getId())
                .eq(StrUtil.isNotBlank(entity.getGrade()), OriginalEducationInfoPO::getGrade, entity.getGrade())
                .eq(StrUtil.isNotBlank(entity.getIdNumber()), OriginalEducationInfoPO::getIdNumber, entity.getIdNumber())
                .eq(StrUtil.isNotBlank(entity.getGraduationSchool()), OriginalEducationInfoPO::getGraduationSchool, entity.getGraduationSchool())
                .eq(StrUtil.isNotBlank(entity.getOriginalEducation()), OriginalEducationInfoPO::getOriginalEducation, entity.getOriginalEducation())
                .eq(Objects.nonNull(entity.getGraduationDate()), OriginalEducationInfoPO::getGraduationDate, entity.getGraduationDate());

        // 列表查询 或 分页查询 并返回数据
        if (Objects.equals(true, originalEducationInfoROPageRO.getIsAll())) {
            List<OriginalEducationInfoPO> originalEducationInfoPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(originalEducationInfoInverter.po2VO(originalEducationInfoPOS));
        } else {
            Page<OriginalEducationInfoPO> originalEducationInfoPOPage = baseMapper.selectPage(originalEducationInfoROPageRO.getPage(), wrapper);
            return new PageVO<>(originalEducationInfoPOPage, originalEducationInfoInverter.po2VO(originalEducationInfoPOPage.getRecords()));
        }
    }

    /**
     * 更新原学历信息
     *
     * @param originalEducationInfoRO 原学历信息
     * @return 更新后的原学历信息
     */
    public OriginalEducationInfoVO editById(OriginalEducationInfoRO originalEducationInfoRO) {
        // 参数校验
        if (Objects.isNull(originalEducationInfoRO) || Objects.isNull(originalEducationInfoRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        // 更新数据
        OriginalEducationInfoPO originalEducationInfoPO = originalEducationInfoInverter.ro2PO(originalEducationInfoRO);
        int count = baseMapper.updateById(originalEducationInfoPO);
        if (count <= 0) {
            log.error("更新失败，数据：{}", originalEducationInfoPO);
            return null;
        }
        // 返回更新后的数据
        return detailById(originalEducationInfoRO.getId());
    }

    /**
     * 根据id删除原学历信息
     *
     * @param id 原学历信息id
     * @return 删除结果
     */
    public Integer deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 删除数据
        int count = baseMapper.deleteById(id);
        if (count <= 0) {
            log.error("删除失败，id：{}", id);
            return null;
        }
        return count;
    }

}
