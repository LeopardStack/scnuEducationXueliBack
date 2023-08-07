package com.scnujxjy.backendpoint.service.registration_record_card;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.DegreeInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.DegreeInfoMapper;
import com.scnujxjy.backendpoint.inverter.registration_record_card.DegreeInfoInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.DegreeInfoRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.DegreeInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 学位信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@Service
@Slf4j
public class DegreeInfoService extends ServiceImpl<DegreeInfoMapper, DegreeInfoPO> implements IService<DegreeInfoPO> {

    @Resource
    private DegreeInfoInverter degreeInfoInverter;

    public DegreeInfoVO detailById(Long id) {
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        DegreeInfoPO degreeInfoPO = baseMapper.selectById(id);
        return degreeInfoInverter.po2VO(degreeInfoPO);
    }

    public PageVO<DegreeInfoVO> pageQueryDegreeInfo(PageRO<DegreeInfoRO> degreeInfoROPageRO) {
        if (Objects.isNull(degreeInfoROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        DegreeInfoRO entity = degreeInfoROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new DegreeInfoRO();
        }
        LambdaQueryWrapper<DegreeInfoPO> wrapper = Wrappers.<DegreeInfoPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), DegreeInfoPO::getId, entity.getId())
                .eq(StrUtil.isNotBlank(entity.getAdmissionNumber()), DegreeInfoPO::getAdmissionNumber, entity.getAdmissionNumber())
                .like(StrUtil.isNotBlank(entity.getName()), DegreeInfoPO::getName, entity.getName())
                .like(StrUtil.isNotBlank(entity.getNamePinyin()), DegreeInfoPO::getNamePinyin, entity.getNamePinyin())
                .eq(StrUtil.isNotBlank(entity.getGender()), DegreeInfoPO::getGender, entity.getGender())
                .eq(StrUtil.isNotBlank(entity.getEthnicity()), DegreeInfoPO::getEthnicity, entity.getEthnicity())
                .eq(StrUtil.isNotBlank(entity.getPoliticalStatus()), DegreeInfoPO::getPoliticalStatus, entity.getPoliticalStatus())
                .eq(Objects.nonNull(entity.getBirthDate()), DegreeInfoPO::getBirthDate, entity.getBirthDate())
                .eq(StrUtil.isNotBlank(entity.getIdType()), DegreeInfoPO::getIdType, entity.getIdType())
                .eq(StrUtil.isNotBlank(entity.getIdNumber()), DegreeInfoPO::getIdNumber, entity.getIdNumber())
                .eq(StrUtil.isNotBlank(entity.getPrincipalName()), DegreeInfoPO::getPrincipalName, entity.getPrincipalName())
                .eq(StrUtil.isNotBlank(entity.getCertificateName()), DegreeInfoPO::getCertificateName, entity.getCertificateName())
                .eq(StrUtil.isNotBlank(entity.getMajorName()), DegreeInfoPO::getMajorName, entity.getMajorName())
                .eq(Objects.nonNull(entity.getGraduationDate()), DegreeInfoPO::getGraduationDate, entity.getGraduationDate())
                .eq(StrUtil.isNotBlank(entity.getStudyPeriod()), DegreeInfoPO::getStudyPeriod, entity.getStudyPeriod())
                .eq(StrUtil.isNotBlank(entity.getStudyForm()), DegreeInfoPO::getStudyForm, entity.getStudyForm())
                .eq(StrUtil.isNotBlank(entity.getDegreeCertificateNumber()), DegreeInfoPO::getDegreeCertificateNumber, entity.getDegreeCertificateNumber())
                .eq(Objects.nonNull(entity.getDegreeDate()), DegreeInfoPO::getDegreeDate, entity.getDegreeDate())
                .eq(StrUtil.isNotBlank(entity.getDegreeType()), DegreeInfoPO::getDegreeType, entity.getDegreeType())
                .eq(StrUtil.isNotBlank(entity.getDegreeProcessNumber()), DegreeInfoPO::getDegreeProcessNumber, entity.getDegreeProcessNumber())
                .eq(StrUtil.isNotBlank(entity.getDegreeForeignLanguagePassNumber()), DegreeInfoPO::getDegreeForeignLanguagePassNumber, entity.getDegreeForeignLanguagePassNumber())
                .eq(StrUtil.isNotBlank(entity.getCollege()), DegreeInfoPO::getCollege, entity.getCollege())
                .eq(StrUtil.isNotBlank(entity.getGraduationCertificateNumber()), DegreeInfoPO::getGraduationCertificateNumber, entity.getGraduationCertificateNumber())
                .eq(Objects.nonNull(entity.getAverageScore()), DegreeInfoPO::getAverageScore, entity.getAverageScore())
                .eq(StrUtil.isNotBlank(entity.getAwardingCollege()), DegreeInfoPO::getAwardingCollege, entity.getAwardingCollege())
                .eq(StrUtil.isNotBlank(entity.getDegreeForeignLanguageSubject()), DegreeInfoPO::getDegreeForeignLanguageSubject, entity.getDegreeForeignLanguageSubject())
                .eq(Objects.nonNull(entity.getDegreeForeignLanguagePassDate()), DegreeInfoPO::getDegreeForeignLanguagePassDate, entity.getDegreeForeignLanguagePassDate())
                .eq(StrUtil.isNotBlank(entity.getDegreePhotoUrl()), DegreeInfoPO::getDegreePhotoUrl, entity.getDegreePhotoUrl());
        if (Objects.equals(true, degreeInfoROPageRO.getIsAll())) {
            List<DegreeInfoPO> degreeInfoPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(degreeInfoInverter.po2VO(degreeInfoPOS));
        } else {
            Page<DegreeInfoPO> degreeInfoPOPage = baseMapper.selectPage(degreeInfoROPageRO.getPage(), wrapper);
            return new PageVO<>(degreeInfoPOPage, degreeInfoInverter.po2VO(degreeInfoPOPage.getRecords()));
        }
    }

    public DegreeInfoVO editById(DegreeInfoRO degreeInfoRO) {
        if (Objects.isNull(degreeInfoRO) || Objects.isNull(degreeInfoRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        DegreeInfoPO degreeInfoPO = degreeInfoInverter.ro2PO(degreeInfoRO);
        int count = baseMapper.updateById(degreeInfoPO);
        if (count <= 0) {
            log.error("更新失败，数据：{}", degreeInfoPO);
            return null;
        }
        return detailById(degreeInfoRO.getId());
    }

    public Integer deleteById(Long id) {
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        int count = baseMapper.deleteById(id);
        if (count <= 0) {
            log.error("删除失败，id：{}", id);
            return null;
        }
        return count;
    }

}
