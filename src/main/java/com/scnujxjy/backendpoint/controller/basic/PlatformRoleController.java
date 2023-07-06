package com.scnujxjy.backendpoint.controller.basic;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dto.RolePermissions;
import com.scnujxjy.backendpoint.dto.RoleUpdateRequest;
import com.scnujxjy.backendpoint.entity.basic.Permission;
import com.scnujxjy.backendpoint.entity.basic.PlatformRole;
import com.scnujxjy.backendpoint.entity.basic.PlatformUser;
import com.scnujxjy.backendpoint.entity.basic.RolePermission;
import com.scnujxjy.backendpoint.service.basic.*;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.StpInterfaceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * platform-role
 *   角色信息
 *
 * @author leopard
 * @since 2023-07-02
 */
@RestController
@RequestMapping("/platform-role")
public class PlatformRoleController {
    private static final Logger logger = LoggerFactory.getLogger(PlatformRoleController.class);

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
     * getRoleAllInfo
     * 获取所有角色的信息
     * @return SaResult
     */
    @SaCheckPermission("permission.role")
    @RequestMapping("getRoleAllInfo")
    public SaResult getRoleAllInfo(){
        try{
            List<PlatformRole> platformRoles = platformRoleService.list();
            List<RolePermissions> rolePermissionsList = new ArrayList<>();
            for(PlatformRole platformRole: platformRoles){
                RolePermissions rolePermissions = new RolePermissions();
                rolePermissions.setRoleID(platformRole.getRoleID());
                rolePermissions.setRoleName(platformRole.getRoleName());
                rolePermissions.setRoleDescription(platformRole.getRoleDescription());

                ArrayList<String> permissions = new ArrayList<>();
                QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("RoleID", platformRole.getRoleID());
                List<RolePermission> rolePermissions1 = rolePermissionService.list(queryWrapper);
                for(RolePermission rolePermission: rolePermissions1){
                    long permissionID = rolePermission.getPermissionID();

                    Permission permission = permissionService.getById(permissionID);
                    permissions.add(permission.getResource());

                }

                rolePermissions.setPermissions(permissions);
                rolePermissionsList.add(rolePermissions);
            }

            return SaResult.ok().set("roles", rolePermissionsList);
        }catch (Exception e){
            logger.error(e.toString());
        }
        return SaResult.error(ResultCode.ROLE_ALL_INFO_GET_FAIL.getMessage()).setCode(ResultCode.ROLE_ALL_INFO_GET_FAIL.getCode());
    }

    /**
     * 创建角色
     * @param roleName 角色名称
     * @param roleDescription 角色描述
     * @return SaResult
     */
    @SaCheckLogin
    @SaCheckRole("超级管理员")
    @RequestMapping("createRole")
    public SaResult createRole(@RequestParam("roleName") String roleName,
                               @RequestParam("roleDescription") String roleDescription) {
        PlatformRole platformRole = new PlatformRole();
        platformRole.setRoleName(roleName);
        platformRole.setRoleDescription(roleDescription);

        QueryWrapper<PlatformRole> wrapper = new QueryWrapper<>();
        wrapper.eq("roleName", roleName)
                .eq("roleDescription", roleDescription);
        // Check if the role already exists
        if (platformRoleService.count(wrapper) > 0) {
            logger.error(ResultCode.ADD_NEW_ROLE_FAIL_2.getMessage() + platformRole.toString());
            return SaResult.error(ResultCode.ADD_NEW_ROLE_FAIL_2.getMessage()).setCode(ResultCode.ADD_NEW_ROLE_FAIL_2.getCode());
        }
        try {
            platformRoleService.save(platformRole);
            // 返回添加成功的权限对象
            return SaResult.ok("添加新角色成功！").set("role", platformRole);
        }catch (Exception e){
            logger.error("添加新的角色失败 " + platformRole.toString());
        }
        return SaResult.error(ResultCode.ADD_NEW_ROLE_FAIL.getMessage()).setCode(ResultCode.ADD_NEW_ROLE_FAIL.getCode());

    }


    /**
     * 修改角色权限信息
     * @param request 前端请求
     * @return SaResult
     */
    @SaCheckLogin
    @SaCheckRole("超级管理员")
    @RequestMapping(value = "updateRolePermission", method = RequestMethod.POST, consumes = "application/json")
    public SaResult updateRolePermission(@RequestBody RoleUpdateRequest request) {
        String roleID = request.getRoleID();
        String roleName = request.getRoleName();
        String roleDescription = request.getRoleDescription();
        List<String> permissions = request.getPermissions();

        logger.info("角色姓名 " + roleName);
        logger.info("角色描述 " + roleDescription);
        logger.info("角色权限列表 " + permissions);

        try {
            PlatformRole platformRole = platformRoleService.getById(Long.parseLong(roleID));
            if (platformRole == null) {
                return SaResult.error("未找到对应角色");
            }

            logger.info("查询到的系统里的角色信息 " + platformRole.toString());

            // 更新角色信息
            platformRole.setRoleName(roleName);
            platformRole.setRoleDescription(roleDescription);
            platformRoleService.updateById(platformRole);

            // 先清空当前角色的所有权限
            QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("RoleID", platformRole.getRoleID());
            rolePermissionService.remove(queryWrapper);

            // 然后添加新的权限
            for(String resource: permissions){
                QueryWrapper<Permission> permissionQueryWrapper = new QueryWrapper<>();
                permissionQueryWrapper.eq("Resource", resource);
                Permission permission = permissionService.getOne(permissionQueryWrapper);
                if(permission != null){
                    RolePermission rolePermission = new RolePermission();
                    rolePermission.setRoleID(platformRole.getRoleID());
                    rolePermission.setPermissionID(permission.getPermissionID());
                    rolePermissionService.save(rolePermission);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
            return SaResult.error("更新角色权限失败");
        }
        return SaResult.ok("成功更新角色 " + roleName + " 的权限");
    }


}

