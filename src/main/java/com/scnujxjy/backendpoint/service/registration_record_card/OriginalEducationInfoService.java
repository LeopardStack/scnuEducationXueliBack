package com.scnujxjy.backendpoint.service.registration_record_card;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.OriginalEducationInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.OriginalEducationInfoMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 原学历信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
public class OriginalEducationInfoService extends ServiceImpl<OriginalEducationInfoMapper, OriginalEducationInfoPO> implements IService<OriginalEducationInfoPO> {

}
