package com.scnujxjy.backendpoint.service.basic;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformRolePO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformRoleMapper;
import com.scnujxjy.backendpoint.model.vo.basic.PermissionVO;
import com.scnujxjy.backendpoint.model.vo.basic.RolePermissionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@Service
@Slf4j
public class PlatformRoleService extends ServiceImpl<PlatformRoleMapper, PlatformRolePO> implements IService<PlatformRolePO> {

    @Resource
    private RolePermissionService rolePermissionService;

    @Resource
    private PermissionService permissionService;

    /**
     * 根据角色id查询权限详情列表
     *
     * @param roleId 角色id
     * @return 权限详情列表
     */
    public List<PermissionVO> permissionVOSByRoleId(Long roleId) {
        // 入参校验
        if (Objects.isNull(roleId)) {
            log.error("参数缺失");
            return null;
        }
        // 查询角色对应权限
        List<RolePermissionVO> rolePermissionVOS = rolePermissionService.detailByRoleId(roleId);
        // 角色权限表校验
        if (CollUtil.isEmpty(rolePermissionVOS)) {
            log.error("该roleId：{} 角色没有权限", roleId);
            return null;
        }
        // 获取其中的权限id列表
        List<Long> permissionIds = rolePermissionVOS.stream()
                .filter(Objects::nonNull)
                .map(RolePermissionVO::getPermissionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        // 根据权限id列表查询权限列表
        List<PermissionVO> permissionVOS = permissionService.detailById(permissionIds);
        // 结果校验
        if (CollUtil.isEmpty(permissionVOS)) {
            log.error("该权限id列表：{} 查询结果为空", permissionVOS);
            return null;
        }
        // 返回结果
        return permissionVOS;
    }
}
