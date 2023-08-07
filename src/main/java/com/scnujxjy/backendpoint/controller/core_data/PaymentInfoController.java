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
 * <p>
 * 缴费信息表 前端控制器
 * </p>
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
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
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
        if (Objects.isNull(paymentInfoROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(paymentInfoROPageRO.getEntity())) {
            paymentInfoROPageRO.setEntity(new PaymentInfoRO());
        }
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
        if (Objects.isNull(paymentInfoRO) || Objects.isNull(paymentInfoRO.getId())) {
            throw dataMissError();
        }
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
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        Integer count = paymentInfoService.deleteById(id);
        if (Objects.isNull(count)) {
            throw dataDeleteError();
        }
        return SaResult.data(count);
    }
}

