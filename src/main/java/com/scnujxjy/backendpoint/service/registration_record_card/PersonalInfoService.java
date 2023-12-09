package com.scnujxjy.backendpoint.service.registration_record_card;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.inverter.registration_record_card.PersonalInfoInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.PersonalInfoRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.PersonalInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 个人基本信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@Service
@Slf4j
public class PersonalInfoService extends ServiceImpl<PersonalInfoMapper, PersonalInfoPO> implements IService<PersonalInfoPO> {
    @Resource
    private PersonalInfoInverter personalInfoInverter;





    /**
    * @Version：1.0.0
    * @Description：插入个人信息
    * @Author：3304393868@qq.com
    * @Date：2023/12/7-23:27
    */
    public Integer InsterPersonalInfo(PersonalInfoRO personalInfoRO){
        PersonalInfoPO personalInfoPO = personalInfoInverter.ro2PO(personalInfoRO);
        int count = baseMapper.insert(personalInfoPO);
        return count;
    }



    /**
     * 根据id查询个人基本信息
     *
     * @param id 个人基本信息id
     * @return 个人基本信息
     */
    public PersonalInfoVO detailById(Long id) {
        // 校验参数
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        PersonalInfoPO personalInfoPO = baseMapper.selectById(id);
        return personalInfoInverter.po2VO(personalInfoPO);
    }

    /**
     * 分页查询个人基本信息
     *
     * @param personalInfoROPageRO 分页查询参数
     * @return 分页查询结果
     */
    public PageVO<PersonalInfoVO> pageQueryPersonalInfo(PageRO<PersonalInfoRO> personalInfoROPageRO) {
        // 校验参数
        if (Objects.isNull(personalInfoROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        PersonalInfoRO entity = personalInfoROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new PersonalInfoRO();
        }
        // 构造查询参数
        LambdaQueryWrapper<PersonalInfoPO> wrapper = Wrappers.<PersonalInfoPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), PersonalInfoPO::getId, entity.getId())
                .eq(StrUtil.isNotBlank(entity.getGender()), PersonalInfoPO::getGender, entity.getGender())
                .eq(Objects.nonNull(entity.getBirthDate()), PersonalInfoPO::getBirthDate, entity.getBirthDate())
                .eq(StrUtil.isNotBlank(entity.getPoliticalStatus()), PersonalInfoPO::getPoliticalStatus, entity.getPoliticalStatus())
                .eq(StrUtil.isNotBlank(entity.getEthnicity()), PersonalInfoPO::getEthnicity, entity.getEthnicity())
                .eq(StrUtil.isNotBlank(entity.getNativePlace()), PersonalInfoPO::getNativePlace, entity.getNativePlace())
                .eq(StrUtil.isNotBlank(entity.getIdType()), PersonalInfoPO::getIdType, entity.getIdType())
                .eq(StrUtil.isNotBlank(entity.getIdNumber()), PersonalInfoPO::getIdNumber, entity.getIdNumber())
                .eq(StrUtil.isNotBlank(entity.getPostalCode()), PersonalInfoPO::getPostalCode, entity.getPostalCode())
                .eq(StrUtil.isNotBlank(entity.getPhoneNumber()), PersonalInfoPO::getPhoneNumber, entity.getPhoneNumber())
                .eq(StrUtil.isNotBlank(entity.getEmail()), PersonalInfoPO::getEmail, entity.getEmail())
                .eq(StrUtil.isNotBlank(entity.getAddress()), PersonalInfoPO::getAddress, entity.getAddress())
                .eq(StrUtil.isNotBlank(entity.getIsDisabled()), PersonalInfoPO::getIsDisabled, entity.getIsDisabled())
                .eq(StrUtil.isNotBlank(entity.getGrade()), PersonalInfoPO::getGrade, entity.getGrade())
                .last(StrUtil.isNotBlank(personalInfoROPageRO.getOrderBy()), personalInfoROPageRO.lastOrderSql());


        // 查询数据
        if (Objects.equals(true, personalInfoROPageRO.getIsAll())) {
            List<PersonalInfoPO> personalInfoPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(personalInfoInverter.po2VO(personalInfoPOS));
        } else {
            Page<PersonalInfoPO> personalInfoPOPage = baseMapper.selectPage(personalInfoROPageRO.getPage(), wrapper);
            return new PageVO<>(personalInfoPOPage, personalInfoInverter.po2VO(personalInfoPOPage.getRecords()));
        }
    }

    /**
     * 根据id更新个人基本信息
     *
     * @param personalInfoRO 个人基本信息
     * @return 更新后的个人基本信息
     */
    public PersonalInfoVO editById(PersonalInfoRO personalInfoRO) {
        // 校验参数
        if (Objects.isNull(personalInfoRO) || Objects.isNull(personalInfoRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        // 更新数据
        PersonalInfoPO personalInfoPO = personalInfoInverter.ro2PO(personalInfoRO);
        int count = baseMapper.updateById(personalInfoPO);
        if (count <= 0) {
            log.error("更新失败，数据：{}", personalInfoPO);
            return null;
        }
        // 返回更新后的数据
        return detailById(personalInfoRO.getId());
    }

    /**
     * 根据id删除数据
     *
     * @param id 个人基本信息id
     * @return 删除的数据条数
     */
    public Integer deleteById(Long id) {
        // 校验参数
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
