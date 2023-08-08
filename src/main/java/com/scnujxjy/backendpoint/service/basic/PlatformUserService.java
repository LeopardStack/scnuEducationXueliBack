package com.scnujxjy.backendpoint.service.basic;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.SM3;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.inverter.basic.PlatformUserInverter;
import com.scnujxjy.backendpoint.model.bo.UserRolePermissionBO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.basic.PermissionVO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
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
public class PlatformUserService extends ServiceImpl<PlatformUserMapper, PlatformUserPO> implements IService<PlatformUserPO> {
    @Resource
    private PlatformUserInverter platformUserInverter;

    @Resource
    private PlatformRoleService platformRoleService;

    @Resource
    private SM3 sm3;

    /**
     * 根据userId获取用户信息
     *
     * @param userId 用户id
     * @return 用户信息
     */
    public PlatformUserVO detailById(Long userId) {

        if (Objects.isNull(userId)) {
            log.error("参数缺失");
            return null;
        }

        PlatformUserPO platformUserPO = baseMapper.selectById(userId);

        return platformUserInverter.po2VO(platformUserPO);
    }

    /**
     * 根据username和password登录
     *
     * @param platformUserRO 登录信息
     * @return true-成功，false-失败
     */
    public Boolean userLogin(PlatformUserRO platformUserRO) {
        // 参数校验
        if (Objects.isNull(platformUserRO) || StrUtil.isBlank(platformUserRO.getUsername()) || StrUtil.isBlank(platformUserRO.getPassword())) {
            log.error("参数缺失");
            return false;
        }
        // 密码加密
        platformUserRO.setPassword(sm3.digestHex(platformUserRO.getPassword()));
        PlatformUserPO platformUserPO = baseMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, platformUserRO.getUsername()).eq(PlatformUserPO::getPassword, platformUserRO.getPassword()));
        // 如果无法查询到，则说明密码错误
        if (Objects.isNull(platformUserPO)) {
            return false;
        }
        StpUtil.login(platformUserPO.getUserId());
        return true;
    }

    /**
     * 根据userId返回所有的权限以及资源信息
     *
     * @param userId 用户id
     * @return 权限资源信息
     */
    public UserRolePermissionBO rolePermissionDetailByUserId(Long userId) {

        if (Objects.isNull(userId)) {
            log.error("参数缺失");
            return null;
        }
        // 获取用户详细信息
        PlatformUserVO platformUserVO = detailById(userId);

        if (Objects.isNull(platformUserVO)) {
            log.error("该userId：{} 对应用户不存在", userId);
            return null;
        }

        // 获取权限详情列表
        Long roleId = platformUserVO.getRoleId();
        List<PermissionVO> permissionVOS = platformRoleService.permissionVOSByRoleId(roleId);

        // 参数校验
        if (CollUtil.isEmpty(permissionVOS)) {
            log.error("获取权限详情列表为空，roleId：{}", roleId);
            return null;
        }

        // 获取资源列表
        List<String> recourses = permissionVOS.stream()
                .filter(Objects::nonNull)
                .map(PermissionVO::getResource)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        // 返回数据
        return UserRolePermissionBO.builder()
                .userId(userId)
                .roleId(roleId)
                .resources(recourses)
                .permissionVOS(permissionVOS)
                .build();
    }


}
