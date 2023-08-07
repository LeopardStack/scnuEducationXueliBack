package com.scnujxjy.backendpoint.service.registration_record_card;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 个人基本信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-04
 */
@Service
public class PersonalInfoService extends ServiceImpl<PersonalInfoMapper, PersonalInfoPO> implements IService<PersonalInfoPO> {

}
