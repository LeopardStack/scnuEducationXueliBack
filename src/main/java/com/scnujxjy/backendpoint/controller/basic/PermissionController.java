package com.scnujxjy.backendpoint.controller.basic;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dto.UserRolePermissions;
import com.scnujxjy.backendpoint.entity.basic.Permission;
import com.scnujxjy.backendpoint.entity.basic.PlatformRole;
import com.scnujxjy.backendpoint.entity.basic.PlatformUser;
import com.scnujxjy.backendpoint.entity.basic.RolePermission;
import com.scnujxjy.backendpoint.service.basic.*;
import com.scnujxjy.backendpoint.util.ResultCode;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * permission
 *   权限信息
 *
 * @author leopard
 * @since 2023-07-02
 */
@RestController
@RequestMapping("/permission")
public class PermissionController {
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
     * getPermissionsInfo
     * 访问用户个人权限、角色信息
     * @return {@link SaResult}
     */
    @SaCheckLogin
    @SaCheckPermission("permission.info.view")
    @RequestMapping("getPermissionsInfo")
    public SaResult getPermissionsInfo() {
        List<Permission> permissionList = permissionService.list();
        return SaResult.ok().set("permissions", permissionList);
    }

    /**
     * getPermissionsInfoForRole
     * 访问所有的权限控制资源信息
     * @return {@link SaResult}
     */
    @SaCheckLogin
    @SaCheckPermission("permission.info.view")
    @RequestMapping("getPermissionsInfoForRole")
    public SaResult getPermissionsInfoForRole() {
        ArrayList<String> resources = new ArrayList<>();

        List<Permission> permissionList = permissionService.list();

        for(Permission permission: permissionList){
            String resource = permission.getResource();
            resources.add(resource);
        }

        return SaResult.ok().set("resources", resources);
    }

    /**
     * addNewPermission
     * 添加新的权限信息
     * @param permissionName 权限名称
     * @param resource 权限所控制的资源
     * @param permissionDescription 权限描述信息
     * @return SaResult ADD_NEW_PERMISSION_FAIL2
     */
    @SaCheckLogin
    @SaCheckRole("超级管理员")
    @RequestMapping("addNewPermission")
    public SaResult addNewPermission(@RequestParam("permissionName") String permissionName,
                                     @RequestParam("resource") String resource,
                                     @RequestParam("permissionDescription") String permissionDescription){
        Permission permission = new Permission();
        permission.setPermissionName(permissionName);
        permission.setResource(resource);
        permission.setDescription(permissionDescription);

        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        wrapper.eq("permissionName", permissionName)
                .eq("resource", resource)
                .eq("description", permissionDescription);

        // Check if the permission already exists
        if (permissionService.count(wrapper) > 0) {
            logger.error(ResultCode.ADD_NEW_PERMISSION_FAIL2.getMessage() + permission.toString());
            return SaResult.error(ResultCode.ADD_NEW_PERMISSION_FAIL2.getMessage()).setCode(ResultCode.ADD_NEW_PERMISSION_FAIL2.getCode());
        }

        try {
            permissionService.save(permission);
            // 返回添加成功的权限对象
            return SaResult.ok("添加新权限成功！").set("permission", permission);
        }catch (Exception e){
            logger.error("添加新的权限失败 " + permission.toString());
        }
        return SaResult.error(ResultCode.ADD_NEW_PERMISSION_FAIL.getMessage()).setCode(ResultCode.ADD_NEW_PERMISSION_FAIL.getCode());
    }

    /**
     * updatePermission
     * 更新权限信息
     * @param permissionID 权限 ID
     * @param permissionName 权限名称
     * @param resource 权限所控制的资源
     * @param permissionDescription 权限描述信息
     * @return SaResult
     * 其中 返回值 PERMISSION_NOT_FOUND 40003 代表 更新权限失败，无该ID的权限
     */
    @SaCheckLogin
    @SaCheckRole("超级管理员")
    @RequestMapping("updatePermission")
    public SaResult updatePermission(@RequestParam("permissionID") Long permissionID,
                                     @RequestParam("permissionName") String permissionName,
                                     @RequestParam("resource") String resource,
                                     @RequestParam("permissionDescription") String permissionDescription) {
        // 获取指定 ID 的权限
        Permission permission = permissionService.getById(permissionID);
        if (permission == null) {
            logger.error(ResultCode.PERMISSION_NOT_FOUND.getMessage() + "ID: " + permissionID);
            return SaResult.error(ResultCode.PERMISSION_NOT_FOUND.getMessage()).setCode(ResultCode.PERMISSION_NOT_FOUND.getCode());
        }

        // 更新权限信息
        permission.setPermissionName(permissionName);
        permission.setResource(resource);
        permission.setDescription(permissionDescription);

        QueryWrapper<Permission> wrapper = new QueryWrapper<>();
        //除了当前更新的记录，其他记录不能重复
        wrapper.eq("permissionName", permissionName)
                .eq("resource", resource)
                .eq("description", permissionDescription);

        // 检查是否有重复的权限
        if (permissionService.count(wrapper) > 0) {
            logger.error(ResultCode.UPDATE_PERMISSION_FAIL2.getMessage() + permission.toString());
            return SaResult.error(ResultCode.UPDATE_PERMISSION_FAIL2.getMessage()).setCode(ResultCode.UPDATE_PERMISSION_FAIL2.getCode());
        }

        try {
            // 保存权限
            permissionService.saveOrUpdate(permission);
            // 返回更新后的权限对象
            return SaResult.ok("更新权限信息成功！").set("permission", permission);
        } catch (Exception e) {
            logger.error("更新权限信息失败 " + permission.toString());
            return SaResult.error(ResultCode.UPDATE_PERMISSION_FAIL.getMessage()).setCode(ResultCode.UPDATE_PERMISSION_FAIL.getCode());
        }
    }

    /**
     * deletePermission
     * 删除权限信息
     * @return {@link SaResult}
     */
    @SaCheckLogin
    @SaCheckRole("超级管理员")
    @RequestMapping("deletePermission")
    public SaResult deletePermission(@RequestParam("permissionID") Long permissionID) {
        // 获取指定 ID 的权限
        Permission permission = permissionService.getById(permissionID);
        if (permission == null) {
            logger.error(ResultCode.PERMISSION_NOT_FOUND.getMessage() + "ID: " + permissionID);
            return SaResult.error(ResultCode.PERMISSION_NOT_FOUND.getMessage()).setCode(ResultCode.PERMISSION_NOT_FOUND.getCode());
        }

        try {
            // 删除权限
            permissionService.removeById(permissionID);
            // 返回成功信息
            return SaResult.ok("权限删除成功！");
        } catch (Exception e) {
            logger.error("删除权限失败 " + permissionID);
            return SaResult.error(ResultCode.DELETE_PERMISSION_FAIL.getMessage()).setCode(ResultCode.DELETE_PERMISSION_FAIL.getCode());
        }
    }


    @SaCheckLogin
    @SaCheckPermission("permission.resources.view")
    @RequestMapping("getUserPermissionsInfo")
    public SaResult getUserPermissionsInfo() {
        ArrayList<UserRolePermissions> userRolePermissionsArrayList = new ArrayList<>();
        List<PlatformUser> platformUsers = platformUserService.list();
        for(PlatformUser platformUser: platformUsers){
            UserRolePermissions userRolePermissions = new UserRolePermissions();
            userRolePermissions.setUserID(platformUser.getUserID());
            userRolePermissions.setUserName(platformUser.getUsername());

            long roleID = platformUser.getRoleID();

            QueryWrapper<PlatformRole> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.eq("RoleID", roleID);
            PlatformRole role = platformRoleService.getOne(roleQueryWrapper);
            userRolePermissions.setRoleName(role.getRoleName());

            QueryWrapper<RolePermission> rolePermissionQueryWrapper = new QueryWrapper<>();
            rolePermissionQueryWrapper.eq("RoleID", roleID);
            List<RolePermission> rolePermissions = rolePermissionService.list(rolePermissionQueryWrapper);

            ArrayList<String> resources = new ArrayList<>();
            for(RolePermission rolePermission: rolePermissions){
                Long permissionID = rolePermission.getPermissionID();
                Permission permission = permissionService.getById(permissionID);
                resources.add(permission.getResource());
            }
            userRolePermissions.setResources(resources);
            userRolePermissionsArrayList.add(userRolePermissions);
        }

        return SaResult.ok("获取所有用户的权限信息").set("usersPermissionsInfo", userRolePermissionsArrayList);
    }

}

