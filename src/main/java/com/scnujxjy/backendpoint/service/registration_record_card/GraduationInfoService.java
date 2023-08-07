package com.scnujxjy.backendpoint.service.registration_record_card;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.GraduationInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.GraduationInfoMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 毕业信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@Service
public class GraduationInfoService extends ServiceImpl<GraduationInfoMapper, GraduationInfoPO> implements IService<GraduationInfoPO> {

}
