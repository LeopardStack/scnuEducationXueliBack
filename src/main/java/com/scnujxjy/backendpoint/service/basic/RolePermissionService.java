package com.scnujxjy.backendpoint.service.basic;

import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.basic.RolePermission;
import com.scnujxjy.backendpoint.dao.mapper.basic.RolePermissionMapper;
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
public class RolePermissionService extends ServiceImpl<RolePermissionMapper, RolePermission> implements IService<RolePermission> {

}
