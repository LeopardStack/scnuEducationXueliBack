//package com.scnujxjy.backendpoint.util;
//
//
//import cn.dev33.satoken.stp.StpInterface;
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.scnujxjy.backendpoint.entity.basic.Permission;
//import com.scnujxjy.backendpoint.entity.basic.PlatformRole;
//import com.scnujxjy.backendpoint.entity.basic.PlatformUser;
//import com.scnujxjy.backendpoint.entity.basic.RolePermission;
//import com.scnujxjy.backendpoint.service.basic.PermissionService;
//import com.scnujxjy.backendpoint.service.basic.PlatformRoleService;
//import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
//import com.scnujxjy.backendpoint.service.basic.RolePermissionService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 自定义权限验证接口扩展
// */
//@Component    // 保证此类被SpringBoot扫描，完成Sa-Token的自定义权限验证扩展
//public class StpInterfaceImpl implements StpInterface {
//    private static final Logger logger = LoggerFactory.getLogger(StpInterfaceImpl.class);
//    @Autowired()
//    private PermissionService permissionService;
//
//    @Autowired(required = false)
//    private PlatformRoleService platformRoleService;
//
//    @Autowired(required = false)
//    private PlatformUserService platformUserService;
//    @Autowired(required = false)
//    private RolePermissionService rolePermissionService;
//
//    private List<RolePermission> getRolePermissions(long loginId){
//        QueryWrapper<PlatformUser> queryWrapper0 = new QueryWrapper<>();
//        // 你需要替换 "IDNumber" 为实际的角色名字列名
//        queryWrapper0.eq("UserID", loginId);
//        PlatformUser users = platformUserService.getOne(queryWrapper0);
//
//        long roleID = users.getRoleID();
//        QueryWrapper<RolePermission> queryWrapper1 = new QueryWrapper<>();
//        // 你需要替换 "roleID" 为实际的角色名字列名
//        queryWrapper1.eq("RoleID", roleID);
//        return rolePermissionService.list(queryWrapper1);
//    }
//
//
//    /**
//     * 返回一个账号所拥有的权限码集合
//     */
//    @Override
//    public List<String> getPermissionList(Object loginId, String loginType) {
//        List<String> list = new ArrayList<String>();
//        try {
//            long userID = Long.parseLong((String)loginId);
//            List<RolePermission> rolePermissions = getRolePermissions(userID);
//            for(RolePermission rolePermission: rolePermissions){
//                QueryWrapper<Permission> queryWrapper3 = new QueryWrapper<>();
//                // 你需要替换 "PermissionID" 为实际的角色名字列名
//                queryWrapper3.eq("PermissionID", rolePermission.getPermissionID());
//                Permission permission = permissionService.getOne(queryWrapper3);
//                list.add(permission.getResource());
//            }
//            logger.info(list.toString());
//        }catch (Exception e){
//            logger.error(e.toString());
//        }
//        return list;
//    }
//
//    /**
//     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
//     */
//    @Override
//    public List<String> getRoleList(Object loginId, String loginType) {
//        // 本list仅做模拟，实际项目中要根据具体业务逻辑来查询角色
//        List<String> list = new ArrayList<String>();
//        try {
//            long userID = Long.parseLong((String)loginId);
//
//            QueryWrapper<PlatformUser> queryWrapper0 = new QueryWrapper<>();
//            // 你需要替换 "IDNumber" 为实际的角色名字列名
//            queryWrapper0.eq("UserID", userID);
//            PlatformUser users = platformUserService.getOne(queryWrapper0);
//
//            long roleID = users.getRoleID();
//            QueryWrapper<RolePermission> queryWrapper1 = new QueryWrapper<>();
//            // 你需要替换 "roleID" 为实际的角色名字列名
//            queryWrapper1.eq("RoleID", roleID);
//            List<RolePermission> rolePermissions = rolePermissionService.list(queryWrapper1);
//
//            QueryWrapper<PlatformRole> queryWrapper2 = new QueryWrapper<>();
//            // 你需要替换 "roleID" 为实际的角色名字列名
//            queryWrapper2.eq("RoleID", roleID);
//            PlatformRole platformRole = platformRoleService.getOne(queryWrapper2);
//
//            list.add(platformRole.getRoleName());
//        }catch (Exception e){
//            logger.error(e.toString());
//        }
//
//        return list;
//    }
//
//}
