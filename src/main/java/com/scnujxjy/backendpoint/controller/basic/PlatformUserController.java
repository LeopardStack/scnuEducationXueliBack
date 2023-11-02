package com.scnujxjy.backendpoint.controller.basic;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformRolePO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.model.bo.UserRolePermissionBO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.model.vo.basic.UserLoginVO;
import com.scnujxjy.backendpoint.service.basic.PlatformRoleService;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;
import static com.scnujxjy.backendpoint.util.ResultCode.*;

/**
 * 用户登录控制
 *
 * @author leopard
 * @since 2023-08-02
 */
@Slf4j
@RestController
@RequestMapping("/platform-user")
public class PlatformUserController {

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private PlatformRoleService platformRoleService;

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
            return SaResult.error("账户、密码不允许为空，登录失败");
        }
        // 登录
        PlatformUserPO isLogin = platformUserService.userLogin(platformUserRO);
        // 返回
        if(isLogin != null) {
            // Satoken 注册登录
            StpUtil.login(platformUserRO.getUsername());
            Object tokenInfo = StpUtil.getTokenInfo();
            List<String> permissionList = StpUtil.getPermissionList();
            if(StpUtil.getRoleList().size() != 1){
                if(StpUtil.getRoleList().size() == 0){
                    return SaResult.error(USER_LOGIN_FAIL.getMessage()).setCode(USER_LOGIN_FAIL.getCode());
                }else if(StpUtil.getRoleList().size() > 1){
                    return SaResult.error(USER_LOGIN_FAIL1.getMessage()).setCode(USER_LOGIN_FAIL1.getCode());
                }
            }
            String roleName = StpUtil.getRoleList().get(0);
            String tmp = "管理员";
            if(roleName.contains(tmp)){
                UserLoginVO userLoginVO = new UserLoginVO(tokenInfo, permissionList, tmp, roleName, (String) StpUtil.getLoginId(),
                        isLogin.getName(), isLogin.getWechatOpenId());
                return SaResult.data("成功登录 " + platformUserRO.getUsername()).set("userInfo", userLoginVO);
            }

            UserLoginVO userLoginVO = new UserLoginVO(tokenInfo, permissionList, roleName, roleName, (String) StpUtil.getLoginId(),
                    isLogin.getName(), isLogin.getWechatOpenId());
            return SaResult.data("成功登录 " + platformUserRO.getUsername()).set("userInfo", userLoginVO);
        }else{
            return SaResult.error(USER_LOGIN_ERROR.getMessage()).setCode(USER_LOGIN_ERROR.getCode());
        }
    }

    /**
     * 用户采用微信登录
     *
     * @param platformUserRO 用户登录信息
     * @return false-登陆失败，true-登陆成功
     * <p>token在Header中</p>
     */
    @PostMapping("/wechat-login")
    public SaResult wechatUserLogin(@RequestBody PlatformUserRO platformUserRO) {
        // 参数校验，登录名、密码不可或缺
        if (Objects.isNull(platformUserRO) || (StrUtil.isBlank(platformUserRO.getUsername()) &&
                StrUtil.isBlank(platformUserRO.getPassword()) && StrUtil.isBlank(platformUserRO.getWechatOpenId()))) {
            return SaResult.error("账户密码 和 微信登录ID不能同时为空，登录失败");
        }
        // 登录
        PlatformUserPO isLogin = platformUserService.userLogin(platformUserRO);
        // 返回
        if(isLogin != null) {
            // Satoken 注册登录
            StpUtil.login(isLogin.getUsername());
            Object tokenInfo = StpUtil.getTokenInfo();
            List<String> permissionList = StpUtil.getPermissionList();
            if(StpUtil.getRoleList().size() != 1){
                if(StpUtil.getRoleList().size() == 0){
                    return SaResult.error(USER_LOGIN_FAIL.getMessage()).setCode(USER_LOGIN_FAIL.getCode());
                }else if(StpUtil.getRoleList().size() > 1){
                    return SaResult.error(USER_LOGIN_FAIL1.getMessage()).setCode(USER_LOGIN_FAIL1.getCode());
                }
            }
            String roleName = StpUtil.getRoleList().get(0);
            String tmp = "管理员";
            if(roleName.contains(tmp)){
                UserLoginVO userLoginVO = new UserLoginVO(tokenInfo, permissionList, tmp, roleName, (String) StpUtil.getLoginId(),
                        isLogin.getName(), isLogin.getWechatOpenId());
                return SaResult.data("成功登录 " + isLogin.getUsername()).set("userInfo", userLoginVO);
            }

            UserLoginVO userLoginVO = new UserLoginVO(tokenInfo, permissionList, roleName, roleName, (String) StpUtil.getLoginId(),
                    isLogin.getName(), isLogin.getWechatOpenId());
            return SaResult.data("成功登录 " + isLogin.getUsername()).set("userInfo", userLoginVO);
        }else{
            return SaResult.error(USER_LOGIN_ERROR.getMessage()).setCode(USER_LOGIN_ERROR.getCode());
        }
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
     * 根据用户id查询数据
     *
     * @return 用户信息
     */
    @GetMapping("/detailUser")
    public SaResult detail() {
        Object loginId = StpUtil.getLoginId();
        // 查询数据
        PlatformUserVO platformUserVO = platformUserService.detailByuserName((String) loginId);
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


    /**
     * 请求某个用户是否已经登录，并返回其 token 剩余有效时间
     *
     * @param username 用户名
     * @return SaResult 包含登录状态和token剩余有效时间
     */
    @GetMapping("/checkLogin")
    public SaResult checkUserLoginStatus(@RequestParam String username) {
        String tokenValue = StpUtil.getTokenValue();
        if (StrUtil.isBlank(tokenValue)) {
            return SaResult.data("User not logged in");
        }

        String currentUsername = (String) StpUtil.getLoginIdByToken(tokenValue);
        if (username.equals(currentUsername)) {
            // 获取token的剩余有效时间
            long tokenTimeout = StpUtil.getTokenTimeout();
            SaTokenInfo tokenInfo1 = StpUtil.getTokenInfo();

            return SaResult.data(tokenInfo1);
        } else {
            return SaResult.data("Username does not match the current session").setCode(551);
        }
    }

}

