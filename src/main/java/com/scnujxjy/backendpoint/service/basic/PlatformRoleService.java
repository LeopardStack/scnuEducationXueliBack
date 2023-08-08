package com.scnujxjy.backendpoint.service.basic;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformRolePO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformRoleMapper;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
public class PlatformRoleService extends ServiceImpl<PlatformRoleMapper, PlatformRolePO> implements IService<PlatformRolePO> {

}
