package com.scnujxjy.backendpoint.service.basic;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.basic.RolePermissionPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.RolePermissionMapper;
import com.scnujxjy.backendpoint.inverter.basic.RolePermissionInverter;
import com.scnujxjy.backendpoint.model.vo.basic.RolePermissionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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
public class RolePermissionService extends ServiceImpl<RolePermissionMapper, RolePermissionPO> implements IService<RolePermissionPO> {

    @Resource
    private RolePermissionInverter rolePermissionInverter;

    /**
     * 通过角色id查询权限id列表
     *
     * @param roleId 角色id
     * @return 权限id列表
     */
    public List<RolePermissionVO> detailByRoleId(Long roleId) {
        // 校验参数
        if (Objects.isNull(roleId)) {
            log.error("参数缺失");
            return null;
        }
        // 查找数据
        List<RolePermissionPO> rolePermissionPOS = baseMapper.selectList(Wrappers.<RolePermissionPO>lambdaQuery().eq(RolePermissionPO::getRoleId, roleId));

        // 角色权限信息校验
        if (CollUtil.isEmpty(rolePermissionPOS)) {
            log.error("该roleId：{} 角色没有权限", roleId);
            return null;
        }
        // 返回数据
        return rolePermissionInverter.po2VO(rolePermissionPOS);
    }


}
