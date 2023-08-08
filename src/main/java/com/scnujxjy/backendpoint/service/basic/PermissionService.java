package com.scnujxjy.backendpoint.service.basic;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.basic.PermissionPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PermissionMapper;
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
public class PermissionService extends ServiceImpl<PermissionMapper, PermissionPO> implements IService<PermissionPO> {

}
