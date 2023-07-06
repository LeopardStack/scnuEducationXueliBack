package com.scnujxjy.backendpoint.controller.basic;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dto.LoginInput;
import com.scnujxjy.backendpoint.dto.UserReturn;
import com.scnujxjy.backendpoint.entity.basic.*;
import com.scnujxjy.backendpoint.service.basic.*;
import com.scnujxjy.backendpoint.util.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * platform-user
 *   平台用户
 *
 * @author leopard
 * @since 2023-07-02
 * @copyright 2023 barm Inc. All rights reserved
 */
@RestController
@RequestMapping("/platform-user")
public class PlatformUserController {
    private static final Logger logger = LoggerFactory.getLogger(PlatformUserController.class);

    @Autowired
    private PlatformUserService platformUserService;
    @Autowired
    private PlatformRoleService platformRoleService;

    @Autowired
    private RolePermissionService rolePermissionService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private PlatformManagerService platformManagerService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private LecturerService lecturerService;

    /**
     * info
     * 访问用户个人信息
     * @return {@link SaResult}
     */
    @SaCheckLogin
    @RequestMapping("info")
    public SaResult info() {
        logger.info("是否过期" + StpUtil.getTokenTimeout());
        Object loginId = StpUtil.getLoginId();
        long userID = Long.parseLong((String)loginId);
        QueryWrapper<PlatformUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("UserID", userID);
        PlatformUser user = platformUserService.getOne(queryWrapper);
        long roleID = user.getRoleID();

        QueryWrapper<PlatformRole> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("RoleID", roleID);
        PlatformRole role = platformRoleService.getOne(queryWrapper1);
        String roleName = role.getRoleName();
        if("项目管理员".equals(roleName) || "培训部教务员".equals(roleName) || "培训部负责人".equals(roleName)){
            QueryWrapper<PlatformManager> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("UserID", userID);
            PlatformManager manager = platformManagerService.getOne(queryWrapper2);
            return SaResult.ok("查询用户信息").set("userInfo", manager).set("roleName", "项目管理员");
        }
        else if("培训学员".equals(roleName)){
            QueryWrapper<Student> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("UserID", userID);
            Student student = studentService.getOne(queryWrapper2);
            return SaResult.ok("查询用户信息").set("userInfo", student).set("roleName", "培训学员");
        }
        else if("培训教师".equals(roleName)){
            QueryWrapper<Lecturer> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("UserID", userID);
            Lecturer lecturer = lecturerService.getOne(queryWrapper2);
            return SaResult.ok("查询用户信息").set("userInfo", lecturer).set("roleName", "培训教师");
        }
        else{
            return SaResult.error("没有用户信息");
        }
    }

    /**
     * add
     * 角色校验：必须具有指定角色才能进入该方法
     * @return {@link SaResult}
     */
    @SaCheckRole("超级管理员")
    @RequestMapping("add1")
    public SaResult add() {
        return SaResult.ok("用户增加");
    }

    /**
     * addUser
     * 角色校验：必须具有指定权限才能进入该方法
     * @return {@link SaResult}
     */
    @SaCheckPermission("user-add")
    @RequestMapping("add2")
    public SaResult addUser() {
        return SaResult.ok("用户增加2");
    }

    /**
     * addMsg
     * 角色校验：必须具有指定权限才能进入该方法
     * @return {@link SaResult}
     */
    @RequestMapping("add3")
    public SaResult addMsg() {
        return SaResult.ok("用户增加3");
    }

    /**
     * addMsg2
     * 角色校验：必须具有指定权限才能进入该方法
     * @return {@link SaResult}
     */
    @RequestMapping("add4")
    public SaResult addMsg2() {
        PlatformRole platformRole = new PlatformRole();
        platformRole.setRoleID(5L);
        platformRole.setRoleName("jack");
        return SaResult.ok("用户增加4").setData(platformRole);
    }

    @RequestMapping("doLogin")
    public SaResult doLogin(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("role") String role) {
        LoginInput loginInput = new LoginInput();
        loginInput.setUsername(username);
        loginInput.setRoleName(role);
        Exception e = null;
        try {

            QueryWrapper<PlatformUser> queryWrapper0 = new QueryWrapper<>();
            // 你需要替换 "IDNumber" 为实际的角色名字列名
            queryWrapper0.eq("Username", username);
            PlatformUser user = platformUserService.getOne(queryWrapper0);

            String sysUserName = user.getUsername();
            String roleName = platformUserService.getUserRoleName(user.getUserID());
            if(!role.equals(roleName)){
                if("超级管理员".equals(roleName)){

                } else if("培训部负责人".equals(roleName)){

                }
                else{
                    return SaResult.error("登录失败 用户角色错误");
                }
            }

            if (username != null && username.equals(sysUserName) && user.getPassword().equals(password)) {
                long userId = user.getUserID();
                StpUtil.login(userId);
                // 获取当前会话的 Token
                String token = StpUtil.getTokenValue();
                SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
                tokenInfo.setLoginId(userId);

                List<String> permissionList = StpUtil.getPermissionList();

                UserReturn userReturn = new UserReturn();
                userReturn.setSaTokenInfo(tokenInfo);
                userReturn.setPermissionList(permissionList);
                userReturn.setRoleName(roleName);


                SaResult.data(tokenInfo);

//            List<Messages> list = messagesService.list(null);
//            // 在需要使用 WebSocket 会话时获取
//            if(list.size() > 0) {
//                webSocketHandler.sendToAll(JSON.toJSONString(list.get(list.size()-1).getContent()));
//            }
                // 将 Token 添加到响应体中
                return SaResult.ok("登录成功").set("userInfo", userReturn);
            }
        }catch (Exception e1){
            e = e1;
            logger.error(e.toString());
        }
        return SaResult.error("登录失败").set("userInput", loginInput).setData(e);
    }

    /**
     * getUsrID
     * 角色校验：必须具有指定角色才能进入该方法
     * @return {@link SaResult}
     */
    @SaCheckLogin
    @RequestMapping("getUserID")
    public SaResult getUsrID() {
        Object loginId = StpUtil.getLoginId();
        Long userID = null;
        try{
            userID = Long.parseLong((String) loginId);
            QueryWrapper<PlatformUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("UserID", userID);
            PlatformUser user = platformUserService.getOne(queryWrapper);
            long roleID = user.getRoleID();
            QueryWrapper<RolePermission> permissionQueryWrapper = new QueryWrapper<>();
            permissionQueryWrapper.eq("RoleID", roleID);
            List<RolePermission> rolePermissions = rolePermissionService.list(permissionQueryWrapper);
        }catch (Exception e){
            logger.error(e.toString());
            return SaResult.error("获取ID失败").setCode(ResultCode.USER_ID_GET_FAIL.getCode());
        }
        return SaResult.ok("获取 ID 成功").set("userID", userID);
    }

    /**
     * logOut
     * 用户退出系统
     * @return {@link SaResult}
     */
    @RequestMapping("logOut")
    public SaResult logOut() {
        logger.info(StpUtil.getLoginId() + " 即将退出");
        StpUtil.logout();
        return SaResult.ok("成功退出");
    }

}

