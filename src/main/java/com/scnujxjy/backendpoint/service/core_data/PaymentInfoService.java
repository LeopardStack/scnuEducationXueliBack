package com.scnujxjy.backendpoint.service.core_data;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.inverter.core_data.PaymentInfoInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 缴费信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class PaymentInfoService extends ServiceImpl<PaymentInfoMapper, PaymentInfoPO> implements IService<PaymentInfoPO> {
    @Resource
    private PaymentInfoInverter paymentInfoInverter;

    /**
     * 通过id查询详情
     *
     * @param id 主键id
     * @return 支付信息详情
     */
    public PaymentInfoVO detailById(Long id) {
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        PaymentInfoPO paymentInfoPO = baseMapper.selectById(id);
        return paymentInfoInverter.po2VO(paymentInfoPO);
    }

    /**
     * 分页查询、批量查询支付信息详情列表
     *
     * @param paymentInfoROPageRO 支付信息分页参数
     * @return 支付信息详情分页列表
     */
    public PageVO<PaymentInfoVO> pageQueryPaymentInfo(PageRO<PaymentInfoRO> paymentInfoROPageRO) {
        // 参数校验
        if (Objects.isNull(paymentInfoROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        PaymentInfoRO entity = paymentInfoROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new PaymentInfoRO();
        }
        // 构造查询条件
        LambdaQueryWrapper<PaymentInfoPO> wrapper = Wrappers.<PaymentInfoPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), PaymentInfoPO::getId, entity.getId())
                .eq(StrUtil.isNotBlank(entity.getStudentNumber()), PaymentInfoPO::getStudentNumber, entity.getStudentNumber())
                .eq(StrUtil.isNotBlank(entity.getAdmissionNumber()), PaymentInfoPO::getAdmissionNumber, entity.getAdmissionNumber())
                .like(StrUtil.isNotBlank(entity.getName()), PaymentInfoPO::getName, entity.getName())
                .eq(StrUtil.isNotBlank(entity.getIdCardNumber()), PaymentInfoPO::getIdCardNumber, entity.getIdCardNumber())
                .between(Objects.nonNull(entity.getPaymentBeginDate()) && Objects.nonNull(entity.getPaymentEndDate()),
                        PaymentInfoPO::getPaymentDate, entity.getPaymentBeginDate(), entity.getPaymentEndDate())
                .eq(StrUtil.isNotBlank(entity.getPaymentCategory()), PaymentInfoPO::getPaymentCategory, entity.getPaymentCategory())
                .eq(StrUtil.isNotBlank(entity.getAcademicYear()), PaymentInfoPO::getAcademicYear, entity.getAcademicYear())
                .eq(StrUtil.isNotBlank(entity.getPaymentType()), PaymentInfoPO::getPaymentType, entity.getPaymentType())
                .eq(Objects.nonNull(entity.getAmount()), PaymentInfoPO::getAmount, entity.getAmount())
                .eq(StrUtil.isNotBlank(entity.getIsPaid()), PaymentInfoPO::getIsPaid, entity.getIsPaid())
                .eq(StrUtil.isNotBlank(entity.getPaymentMethod()), PaymentInfoPO::getPaymentMethod, entity.getPaymentMethod());
        // 根据参数选择是否查询全部还是分页查询，并返回
        if (Objects.equals(true, paymentInfoROPageRO.getIsAll())) {
            List<PaymentInfoPO> paymentInfoPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(paymentInfoInverter.po2VO(paymentInfoPOS));
        } else {
            Page<PaymentInfoPO> paymentInfoPOPage = baseMapper.selectPage(paymentInfoROPageRO.getPage(), wrapper);
            return new PageVO<>(paymentInfoPOPage, paymentInfoInverter.po2VO(paymentInfoPOPage.getRecords()));
        }
    }

    /**
     * 根据id删除支付信息
     *
     * @param id 主键id
     * @return 删除数量
     */
    public Integer deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 删除
        int count = baseMapper.deleteById(id);
        if (count <= 0) {
            log.error("删除失败，id：{}", id);
        }
        return count;
    }

    /**
     * 根据id更新支付信息
     *
     * @param paymentInfoRO 支付信息
     * @return 更新后的支付信息
     */
    public PaymentInfoVO editById(PaymentInfoRO paymentInfoRO) {
        // 参数校验
        if (Objects.isNull(paymentInfoRO) || Objects.isNull(paymentInfoRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        // 更新信息
        PaymentInfoPO paymentInfoPO = paymentInfoInverter.ro2PO(paymentInfoRO);
        int count = baseMapper.updateById(paymentInfoPO);
        if (count <= 0) {
            log.error("更新失败，数据为：{}", paymentInfoPO);
            return null;
        }
        return detailById(paymentInfoRO.getId());
    }


}
