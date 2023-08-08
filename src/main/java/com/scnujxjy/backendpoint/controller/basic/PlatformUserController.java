package com.scnujxjy.backendpoint.controller.basic;


import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.model.bo.UserRolePermissionBO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author leopard
 * @since 2023-08-02
 */
@RestController
@RequestMapping("/platform-user")
public class PlatformUserController {

    @Resource
    private PlatformUserService platformUserService;

    /**
     * 用户登录接口
     *
     * @param platformUserRO 用户登录信息
     * @return false-登陆失败，true-登陆成功
     * <p>token在Header中</p>
     */
    @PostMapping("/login")
    public SaResult userLogin(@RequestBody PlatformUserRO platformUserRO) {
        // 参数校验，登录名、密码不可或缺
        if (Objects.isNull(platformUserRO) || StrUtil.isBlank(platformUserRO.getUsername()) || StrUtil.isBlank(platformUserRO.getPassword())) {
            return SaResult.data(false);
        }
        // 登录
        Boolean isLogin = platformUserService.userLogin(platformUserRO);
        // 返回
        return SaResult.data(isLogin);
    }

    /**
     * 根据用户id查询数据
     *
     * @param userId 用户id
     * @return 用户信息
     */
    @GetMapping("/detail")
    public SaResult detailByUserId(Long userId) {
        // 参数校验
        if (Objects.isNull(userId)) {
            throw dataMissError();
        }
        // 查询数据
        PlatformUserVO platformUserVO = platformUserService.detailById(userId);
        // 校验返回数据
        if (Objects.isNull(platformUserVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(platformUserVO);
    }

    /**
     * 根据用户id查询角色权限资源信息
     *
     * @param userId 用户id
     * @return 角色权限信息
     */
    @GetMapping("/role-permission/detail")
    public SaResult rolePermissionDetailByUserId(Long userId) {
        // 参数校验
        if (Objects.isNull(userId)) {
            throw dataMissError();
        }
        // 查询数据
        UserRolePermissionBO userRolePermissionBO = platformUserService.rolePermissionDetailByUserId(userId);
        // 返回参数校验
        if (Objects.isNull(userRolePermissionBO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(userRolePermissionBO);
    }

}

