package com.scnujxjy.backendpoint.service.basic;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.basic.AdminInfoPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.AdminInfoMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 管理员信息表 服务实现类
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-12-15
 */
@Service
public class AdminInfoService extends ServiceImpl<AdminInfoMapper, AdminInfoPO> implements IService<AdminInfoPO> {

}
