package com.scnujxjy.backendpoint.service.basic;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformRole;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformRoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class PlatformRoleService extends ServiceImpl<PlatformRoleMapper, PlatformRole> implements IService<PlatformRole> {

}
