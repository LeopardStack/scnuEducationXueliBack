package com.scnujxjy.backendpoint.service.registration_record_card;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.GraduationInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.GraduationInfoMapper;
import com.scnujxjy.backendpoint.inverter.registration_record_card.GraduationInfoInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.GraduationInfoRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.GraduationInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 毕业信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@Service
@Slf4j
public class GraduationInfoService extends ServiceImpl<GraduationInfoMapper, GraduationInfoPO> implements IService<GraduationInfoPO> {
    @Resource
    private GraduationInfoInverter graduationInfoInverter;

    /**
     * 根据id查询毕业信息
     *
     * @param id 毕业信息id
     * @return 毕业信息
     */
    public GraduationInfoVO detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        GraduationInfoPO graduationInfoPO = baseMapper.selectById(id);
        return graduationInfoInverter.po2VO(graduationInfoPO);
    }

    /**
     * 分页查询
     *
     * @param graduationInfoROPageRO 分页参数
     * @return 分页结果
     */
    public PageVO<GraduationInfoVO> pageQueryGraduationInfo(PageRO<GraduationInfoRO> graduationInfoROPageRO) {
        // 参数校验
        if (Objects.isNull(graduationInfoROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        GraduationInfoRO entity = graduationInfoROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new GraduationInfoRO();
        }
        // 构造查询参数
        LambdaQueryWrapper<GraduationInfoPO> wrapper = Wrappers.<GraduationInfoPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), GraduationInfoPO::getId, entity.getId())
                .eq(StrUtil.isNotBlank(entity.getGrade()), GraduationInfoPO::getGrade, entity.getGrade())
                .eq(StrUtil.isNotBlank(entity.getIdNumber()), GraduationInfoPO::getIdNumber, entity.getIdNumber())
                .eq(StrUtil.isNotBlank(entity.getStudentNumber()), GraduationInfoPO::getStudentNumber, entity.getStudentNumber())
                .eq(Objects.nonNull(entity.getThesisId()), GraduationInfoPO::getThesisId, entity.getThesisId())
                .eq(StrUtil.isNotBlank(entity.getGraduationPhoto()), GraduationInfoPO::getGraduationPhoto, entity.getGraduationPhoto())
                .eq(StrUtil.isNotBlank(entity.getGraduationNumber()), GraduationInfoPO::getGraduationNumber, entity.getGraduationNumber())
                .eq(StrUtil.isNotBlank(entity.getDocumentNumber()), GraduationInfoPO::getDocumentNumber, entity.getDocumentNumber())
                .eq(Objects.nonNull(entity.getGraduationDate()), GraduationInfoPO::getGraduationDate, entity.getGraduationDate())
                .last(StrUtil.isNotBlank(graduationInfoROPageRO.getOrderBy()), graduationInfoROPageRO.lastOrderSql());

        // 列表查询 或 分页查询 并返回结果
        if (Objects.equals(true, graduationInfoROPageRO.getIsAll())) {
            List<GraduationInfoPO> graduationInfoPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(graduationInfoInverter.po2VO(graduationInfoPOS));
        } else {
            Page<GraduationInfoPO> graduationInfoPOPage = baseMapper.selectPage(graduationInfoROPageRO.getPage(), wrapper);
            return new PageVO<>(graduationInfoPOPage, graduationInfoInverter.po2VO(graduationInfoPOPage.getRecords()));
        }
    }

    /**
     * 根据id更新数据
     *
     * @param graduationInfoRO 更新的数据
     * @return 更新后的数据
     */
    public GraduationInfoVO editById(GraduationInfoRO graduationInfoRO) {
        // 参数校验
        if (Objects.isNull(graduationInfoRO)) {
            log.error("参数缺失");
            return null;
        }
        // 更新数据
        GraduationInfoPO graduationInfoPO = graduationInfoInverter.ro2PO(graduationInfoRO);
        int count = baseMapper.updateById(graduationInfoPO);
        if (count <= 0) {
            log.error("更新失败，数据：{}", graduationInfoRO);
            return null;
        }
        return detailById(graduationInfoRO.getId());
    }

    /**
     * 根据id删除数据
     *
     * @param id 主键id
     * @return 删除的数据条数
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
