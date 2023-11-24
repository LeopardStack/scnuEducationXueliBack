package com.scnujxjy.backendpoint.controller.basic;


import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.model.bo.UserRolePermissionBO;
import com.scnujxjy.backendpoint.model.ro.basic.PlatformUserRO;
import com.scnujxjy.backendpoint.model.vo.basic.OnlineCount;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.model.vo.basic.UserLoginVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

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
    protected RedisTemplate<String, Object> redisTemplate;


    /**
     * 用户登录接口
     *
     * @param platformUserRO 用户登录信息
     * @return false-登陆失败，true-登陆成功
     * <p>token在Header中</p>
     */
    @PostMapping("/login")
    public SaResult userLogin(@RequestBody PlatformUserRO platformUserRO, HttpServletRequest request) {


        // 参数校验，登录名、密码不可或缺
        if (Objects.isNull(platformUserRO) || StrUtil.isBlank(platformUserRO.getUsername()) || StrUtil.isBlank(platformUserRO.getPassword())) {
            return SaResult.error("账户、密码不允许为空，登录失败");
        }
        // 登录
        PlatformUserPO isLogin = platformUserService.userLogin(platformUserRO);
        // 返回
        if (isLogin != null) {
            // Satoken 注册登录
            StpUtil.login(platformUserRO.getUsername());
            Object tokenInfo = StpUtil.getTokenInfo();
            List<String> permissionList = StpUtil.getPermissionList();
            if (StpUtil.getRoleList().size() != 1) {
                if (StpUtil.getRoleList().size() == 0) {
                    return SaResult.error(USER_LOGIN_FAIL.getMessage()).setCode(USER_LOGIN_FAIL.getCode());
                } else if (StpUtil.getRoleList().size() > 1) {
                    return SaResult.error(USER_LOGIN_FAIL1.getMessage()).setCode(USER_LOGIN_FAIL1.getCode());
                }
            }

            String roleName = StpUtil.getRoleList().get(0);
            String tmp = "管理员";
            if (roleName.contains(tmp)) {
                UserLoginVO userLoginVO = new UserLoginVO(tokenInfo, permissionList, tmp, roleName, (String) StpUtil.getLoginId(),
                        isLogin.getName(), isLogin.getWechatOpenId());


                // 更新角色在线人数和总在线人数
                updateOnlineCounts(StpUtil.getRoleList().get(0), true);
                return SaResult.data("成功登录 " + platformUserRO.getUsername())
                        .set("userInfo", userLoginVO);
            }

            UserLoginVO userLoginVO = new UserLoginVO(tokenInfo, permissionList, roleName, roleName, (String) StpUtil.getLoginId(),
                    isLogin.getName(), isLogin.getWechatOpenId());
            // 更新角色在线人数和总在线人数
            updateOnlineCounts(StpUtil.getRoleList().get(0), true);
            return SaResult.data("成功登录 " + platformUserRO.getUsername()).set("userInfo", userLoginVO);
        } else {
            return SaResult.error(USER_LOGIN_ERROR.getMessage()).setCode(USER_LOGIN_ERROR.getCode());
        }
    }

    /**
     * 根据userId批量更新用户信息
     * <p>目前只支持更新补充权限id集合</p>
     *
     * @param platformUserROS
     * @return
     */
    @PostMapping("/batch-update-user")
    public SaResult batchUpdateUser(@RequestBody List<PlatformUserRO> platformUserROS) {
        if (CollUtil.isEmpty(platformUserROS)) {
            throw dataMissError();
        }
        List<PlatformUserVO> platformUserVOS = platformUserService.updateUser(platformUserROS);
        if (CollUtil.isEmpty(platformUserVOS)) {
            return SaResult.error("更新失败");
        }
        return SaResult.data(platformUserVOS);
    }

    @GetMapping("/logout")
    public SaResult logOut() {
        // 获取当前用户角色
        if (!StpUtil.getRoleList().isEmpty()) {
            String roleName = StpUtil.getRoleList().get(0);

            // 更新角色在线人数和总在线人数
            updateOnlineCounts(StpUtil.getRoleList().get(0), false);
        }

        StpUtil.logout();
        return SaResult.ok();
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
        if (isLogin != null) {
            // Satoken 注册登录
            StpUtil.login(isLogin.getUsername());
            Object tokenInfo = StpUtil.getTokenInfo();
            List<String> permissionList = StpUtil.getPermissionList();
            if (StpUtil.getRoleList().size() != 1) {
                if (StpUtil.getRoleList().size() == 0) {
                    return SaResult.error(USER_LOGIN_FAIL.getMessage()).setCode(USER_LOGIN_FAIL.getCode());
                } else if (StpUtil.getRoleList().size() > 1) {
                    return SaResult.error(USER_LOGIN_FAIL1.getMessage()).setCode(USER_LOGIN_FAIL1.getCode());
                }
            }
            String roleName = StpUtil.getRoleList().get(0);
            String tmp = "管理员";
            if (roleName.contains(tmp)) {
                UserLoginVO userLoginVO = new UserLoginVO(tokenInfo, permissionList, tmp, roleName, (String) StpUtil.getLoginId(),
                        isLogin.getName(), isLogin.getWechatOpenId());
                // 更新角色在线人数和总在线人数
                updateOnlineCounts(StpUtil.getRoleList().get(0), true);
                return SaResult.data("成功登录 " + isLogin.getUsername()).set("userInfo", userLoginVO);
            }

            UserLoginVO userLoginVO = new UserLoginVO(tokenInfo, permissionList, roleName, roleName, (String) StpUtil.getLoginId(),
                    isLogin.getName(), isLogin.getWechatOpenId());
            // 更新角色在线人数和总在线人数
            updateOnlineCounts(StpUtil.getRoleList().get(0), true);
            return SaResult.data("成功登录 " + isLogin.getUsername()).set("userInfo", userLoginVO);
        } else {
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
        PlatformUserVO platformUserVO = platformUserService.detailByUsername((String) loginId);
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

    /**
     * 获取平台实时数据
     * 比如每个班的直播情况 上课数据 系统在线人数等等
     *
     * @return
     */
    @GetMapping("/getPlatformBasicInfo")
    public SaResult getPlatFormInfo() {
        try {
            // 从 Redis 中获取在线人数统计对象
            OnlineCount onlineCount = (OnlineCount) redisTemplate.opsForValue().get("onlineCount");
            if (onlineCount == null) {
                onlineCount = new OnlineCount(); // 如果 Redis 中没有数据，则创建一个新的 OnlineCount 对象
            }

            // 从 OnlineCount 对象中获取角色的在线人数和总在线人数
            Map<String, Integer> roleLoginCount = onlineCount.getRoleCounts();
            int totalOnlineCount = onlineCount.getTotalOnlineCount();

            // 将结果放入返回对象
            HashMap<String, Object> result = new HashMap<>();
            result.put("roleLoginCount", roleLoginCount);
            result.put("totalOnlineCount", totalOnlineCount);

            return SaResult.ok().setData(result);
        } catch (Exception e) {
            log.error("Error getting platform basic info: ", e);
            return SaResult.error("无法获取平台基本信息");
        }
    }


    private void updateOnlineCounts(String roleName, boolean increment) {
        // 获取当前的在线人数统计
        OnlineCount onlineCount = (OnlineCount) redisTemplate.opsForValue().get("onlineCount");
        if (onlineCount == null) {
            onlineCount = new OnlineCount();
        }

        // 更新统计
        onlineCount.updateCount(roleName, increment);

        // 将更新后的统计信息存回 Redis
        redisTemplate.opsForValue().set("onlineCount", onlineCount, 100, TimeUnit.HOURS);
    }
}

