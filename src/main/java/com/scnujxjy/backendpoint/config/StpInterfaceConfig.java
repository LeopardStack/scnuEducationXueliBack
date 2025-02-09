package com.scnujxjy.backendpoint.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformRolePO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformRoleMapper;
import com.scnujxjy.backendpoint.model.bo.UserRolePermissionBO;
import com.scnujxjy.backendpoint.model.vo.basic.PermissionVO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 自定义权限加载接口实现类
 */
@Component
@Slf4j
public class StpInterfaceConfig implements StpInterface {

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private PlatformRoleMapper platformRoleMapper;

    /**
     * 根据用户id获取用户权限列表
     *
     * @param loginUserName 账号
     * @param loginType     账号类型
     * @return 用户权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginUserName, String loginType) {
        PlatformUserVO platformUserVO = platformUserService.detailByUsername((String) loginUserName);
        // 参数校验
        if (Objects.isNull(platformUserVO)) {
            log.error("参数缺失");
            return ListUtil.of();
        }
        // 获取用户角色权限信息
        Long userId = platformUserVO.getUserId();
        UserRolePermissionBO userRolePermissionBO = platformUserService.rolePermissionDetailByUserId(userId);

        // 用户角色权限信息校验
        if (Objects.isNull(userRolePermissionBO)) {
            log.error("获取用户权限列表失败，用户id：{}", loginUserName);
            return ListUtil.of();
        }
        // 用户权限信息校验
        List<PermissionVO> permissionVOS = userRolePermissionBO.getPermissionVOS();
        if (CollUtil.isEmpty(permissionVOS)) {
            log.error("获取用户权限列表为空，用户角色权限：{}", userRolePermissionBO);
            return ListUtil.of();
        }

//        StpUtil.getRoleList();
//        StpUtil.getPermissionList();
        // 返回数据
        return permissionVOS.stream()
                .filter(Objects::nonNull)
                .map(PermissionVO::getResource)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户id获取用户角色信息
     *
     * @param loginUserName 账号
     * @param loginType     账号类型
     * @return 用户角色列表
     */
    @Override
    public List<String> getRoleList(Object loginUserName, String loginType) {
        // 参数校验
        PlatformUserVO platformUserVO = platformUserService.detailByUsername((String) loginUserName);



        // 角色信息校验
        if (Objects.isNull(platformUserVO)) {
            log.error("获取角色信息失败，userId：{}", loginUserName);
            return ListUtil.of();
        }
        // 权限信息校验
        Long roleId = platformUserVO.getRoleId();
        if (Objects.isNull(roleId)) {
            log.error("用户角色信息为空，用户信息：{}", platformUserVO);
            return ListUtil.of();
        }
        // 获取其他角色信息
        List<Long> roleIdList = ListUtil.toList(platformUserVO.getRoleId());
        if (CollUtil.isNotEmpty(platformUserVO.getSupplementaryRoleIdSet())) {
            roleIdList.addAll(platformUserVO.getSupplementaryRoleIdSet());
        }
        // 查询数据
        List<PlatformRolePO> platformRolePOS = platformRoleMapper.selectList(Wrappers.<PlatformRolePO>lambdaQuery()
                .in(PlatformRolePO::getRoleId, roleIdList));
        if (CollUtil.isEmpty(platformRolePOS)) {
            log.error("查询角色信息为空，角色 id 集合为：{}", roleIdList);
            return ListUtil.of();
        }
        // 返回数据
        return platformRolePOS.stream()
                .map(PlatformRolePO::getRoleName)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
    }
}
