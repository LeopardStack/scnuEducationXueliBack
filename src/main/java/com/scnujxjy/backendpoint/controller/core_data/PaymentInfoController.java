package com.scnujxjy.backendpoint.controller.core_data;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import com.scnujxjy.backendpoint.service.core_data.PaymentInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 缴费信息表
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/payment-info")
public class PaymentInfoController {

    @Resource
    private PaymentInfoService paymentInfoService;

    /**
     * 通过id查询详情
     *
     * @param id 主键id
     * @return 支付信息详情
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询数据
        PaymentInfoVO paymentInfoVO = paymentInfoService.detailById(id);
        if (Objects.isNull(paymentInfoVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(paymentInfoVO);
    }

    /**
     * 分页查询、批量查询支付信息详情列表
     *
     * @param paymentInfoROPageRO 支付信息分页参数
     * @return 支付信息详情分页列表
     */
    @PostMapping("/page")
    public SaResult pageQueryPaymentInfo(@RequestBody PageRO<PaymentInfoRO> paymentInfoROPageRO) {
        // 参数校验
        if (Objects.isNull(paymentInfoROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(paymentInfoROPageRO.getEntity())) {
            paymentInfoROPageRO.setEntity(new PaymentInfoRO());
        }
        // 数据查询
        PageVO<PaymentInfoVO> paymentInfoVOPageVO = paymentInfoService.pageQueryPaymentInfo(paymentInfoROPageRO);
        if (Objects.isNull(paymentInfoVOPageVO)) {
            throw dataNotFoundError();
        }
        return SaResult.data(paymentInfoVOPageVO);
    }

    /**
     * 根据id更新支付信息
     *
     * @param paymentInfoRO 支付信息
     * @return 更新后的支付信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody PaymentInfoRO paymentInfoRO) {
        // 参数校验
        if (Objects.isNull(paymentInfoRO) || Objects.isNull(paymentInfoRO.getId())) {
            throw dataMissError();
        }
        // 数据更新
        PaymentInfoVO paymentInfoVO = paymentInfoService.editById(paymentInfoRO);
        if (Objects.isNull(paymentInfoVO)) {
            throw dataUpdateError();
        }
        return SaResult.data(paymentInfoVO);
    }

    /**
     * 根据id删除支付信息
     *
     * @param id 主键id
     * @return 删除数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询
        Integer count = paymentInfoService.deleteById(id);
        if (Objects.isNull(count)) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }
}

