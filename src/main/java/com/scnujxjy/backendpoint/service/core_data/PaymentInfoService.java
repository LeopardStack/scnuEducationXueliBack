package com.scnujxjy.backendpoint.service.core_data;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.core_data.PaymentInfo;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 缴费信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
public class PaymentInfoService extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements IService<PaymentInfo> {

}
