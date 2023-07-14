package com.scnujxjy.backendpoint.platformUserTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dto.UserPermissions;
import com.scnujxjy.backendpoint.dto.UserRolePermissions;
import com.scnujxjy.backendpoint.entity.basic.Permission;
import com.scnujxjy.backendpoint.entity.basic.PlatformRole;
import com.scnujxjy.backendpoint.entity.basic.PlatformUser;
import com.scnujxjy.backendpoint.entity.basic.RolePermission;
import com.scnujxjy.backendpoint.service.basic.PermissionService;
import com.scnujxjy.backendpoint.service.basic.PlatformRoleService;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.basic.RolePermissionService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Slf4j
public class TestUser1 {
    @Autowired
    private PlatformUserService platformUserService;
    @Autowired
    private PlatformRoleService platformRoleService;

    @Autowired(required = false)
    private RolePermissionService rolePermissionService;

    @Autowired()
    private PermissionService permissionService;

    @Test
    public void test1(){
        List<PlatformUser> list = platformUserService.list();
        log.info("平台所有用户 " + list);
    }

    @Test
    public void addUser(){
        List<PlatformRole> roleList = platformRoleService.list();
        log.info("平台所有角色 " + roleList);

        PlatformUser platformUser = new PlatformUser();
        platformUser.setUsername("leopard");
        platformUser.setRoleID(8L);
        platformUser.setPassword("123456");
        platformUser.setAvatarImagePath("/path/to/image5");
        try {
            boolean save = platformUserService.save(platformUser);
            log.info("是否成功插入 " + save);
        }catch (Exception e){
            log.info("插入失败 " + e.toString());
        }
    }

    @Test
    public void changeUserInfo(){
        List<PlatformRole> roleList = platformRoleService.list();
        log.info("平台所有角色 " + roleList);

        PlatformUser platformUser = new PlatformUser();
        platformUser.setUsername("leopard");
        platformUser.setRoleID(8L);
        platformUser.setPassword("123456");
        platformUser.setAvatarImagePath("/path/to/image5");
        try {
            boolean save = platformUserService.save(platformUser);
            log.info("是否成功插入 " + save);
        }catch (Exception e){
            log.info("插入失败 " + e.toString());
        }
    }

    @Test
    public void getUserPermissions(){
        List<String> list = new ArrayList<String>();
        try {
            long userID = 4L;

            QueryWrapper<PlatformUser> queryWrapper0 = new QueryWrapper<>();
            // 你需要替换 "IDNumber" 为实际的角色名字列名
            queryWrapper0.eq("UserID", userID);
            PlatformUser users = platformUserService.getOne(queryWrapper0);

            long roleID = users.getRoleID();
            QueryWrapper<RolePermission> queryWrapper1 = new QueryWrapper<>();
            // 你需要替换 "roleID" 为实际的角色名字列名
            queryWrapper1.eq("RoleID", roleID);
            List<RolePermission> rolePermissions = rolePermissionService.list(queryWrapper1);

            QueryWrapper<PlatformRole> queryWrapper2 = new QueryWrapper<>();
            // 你需要替换 "roleID" 为实际的角色名字列名
            queryWrapper2.eq("RoleID", roleID);
            PlatformRole platformRole = platformRoleService.getOne(queryWrapper2);
            log.info("用户角色是 " + platformRole.toString());

            for(RolePermission rolePermission: rolePermissions){
                QueryWrapper<Permission> queryWrapper3 = new QueryWrapper<>();
                // 你需要替换 "PermissionID" 为实际的角色名字列名
                queryWrapper3.eq("PermissionID", rolePermission.getPermissionID());
                Permission permission = permissionService.getOne(queryWrapper3);
                list.add(permission.getResource());
            }
            log.info(list.toString());
        }catch (Exception e){
            log.error(e.toString());
        }
    }

    /**
     * 插入权限
     */
    @Test
    public void insertPermissions() {
        List<Permission> permissions = Arrays.asList(
                new Permission("ViewBackHome", "访问后台首页", "home"),
                new Permission("ViewPermissionRole", "访问权限管理的角色列表", "permission.role"),
                new Permission("ViewPermissionMenu", "访问权限管理的菜单列表", "permission.menu"),
                new Permission("ViewPermissionResources", "访问权限管理的资源列表", "permission.resources"),
                new Permission("ViewProjectCreate", "访问项目创建", "project.create"),
                new Permission("ViewProjectManageBrief", "访问培训概要", "project.manage.brief"),
                new Permission("ViewProjectManageRecord", "访问培训记录", "project.manage.record"),
                new Permission("ViewProjectManageStudentManage", "访问培训学员管理", "project.manage.student"),
                new Permission("ViewProjectManageTeacherManage", "访问培训教师管理", "project.manage.teacher"),
                new Permission("ViewProjectManageLiveManage", "访问培训直播管理", "project.manage.live"),
                new Permission("ViewProjectManageVideoManage", "访问培训点播管理", "project.manage.video"),
                new Permission("ViewProjectManageFileExport", "访问培训项目资料导出", "project.manage.file"),
                new Permission("ViewProjectManageForum", "访问培训项目讨论区管理", "project.manage.forum.manage"),
                new Permission("ViewProjectManageForumIn", "查看培训项目讨论区", "project.manage.forum.view"),
                new Permission("ViewProjectManageDisplay", "查看培训项目成果展示区", "project.manage.display.view"),
                new Permission("ViewProjectManageMessageManage", "查看培训项目消息管理", "project.manage.message"),
                new Permission("ViewProjectTeachersManage", "查看培训项目师资库管理", "project.user.teacher"),
                new Permission("ViewProjectManagerManage", "查看培训项目管理人员管理", "project.user.manager"),
                new Permission("ViewProjectPlatformLog", "查看培训平台日志", "project.platform.log"),
                new Permission("ViewProjectPlatformStatistic", "查看培训平台统计分析", "project.platform.statistic"),
                new Permission("ViewProjectForum", "查看项目讨论区", "project.forum.view"),
                new Permission("EditProjectForum", "项目讨论区发帖", "project.forum.edit"),
                new Permission("CommentProjectForum", "项目讨论区评论", "project.forum.comment"),
                new Permission("ViewProjectResult", "查看项目成果展示区", "project.result.view"),
                new Permission("EditProjectResult", "项目成果展示区发布成果", "project.result.edit"),
                new Permission("ViewTeacherSpace", "查看教师教学空间", "project.teacherSpace.view"),
                new Permission("EditTeacherSpace", "操作教师教学空间", "project.teacherSpace.operation"),
                new Permission("ViewCoursesLearning", "查看课程学习空间", "project.coursesLearning.view")
        );

        boolean b = permissionService.saveBatch(permissions);
        log.info("插入结果 " + b);
    }

    @Test
    public void deletePermission(){
        boolean b = permissionService.removeById(1L);
        log.info("删除权限 " + "ID is 2L "  + b);
    }

    /**
     * 删除指定用户指定权限
     */
    @Test
    public void deleteRolePermission(){
        QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("RoleID", 1L);
        queryWrapper.eq("PermissionID", 1L);
        boolean remove = rolePermissionService.remove(queryWrapper);
        log.info("删除角色 1L 的权限 1L " + remove);

    }

    /**
     * 增加指定用户指定权限
     * 超级管理员
     */
    @Test
    public void addRolePermission(){
        for(int i = 4; i < 32; i++) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleID(4L);
            rolePermission.setPermissionID((long) i);
            rolePermissionService.save(rolePermission);
        }
    }

    /**
     * 增加指定用户指定权限
     * 培训部教务员
     */
    @Test
    public void addRolePermission3(){
        for(int i = 4; i < 28; i++) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleID(6L);
            rolePermission.setPermissionID((long) i);
            rolePermissionService.save(rolePermission);
        }
    }

    /**
     * 增加指定用户指定权限
     * 学生
     */
    @Test
    public void addRolePermission1(){
        for(int i = 24; i < 29; i++) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleID(1L);
            rolePermission.setPermissionID((long) i);
            rolePermissionService.save(rolePermission);
        }
    }

    /**
     * 增加指定用户指定权限
     * 讲师
     */
    @Test
    public void addRolePermission2(){
        for(int i = 24; i < 31; i++) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleID(2L);
            rolePermission.setPermissionID((long) i);
            rolePermissionService.save(rolePermission);
        }
    }

    /**
     * 获取指定用户权限
     * 讲师
     */
    @Test
    public void getUserPermission(){
        List<UserPermissions> userPermissions = platformUserService.getUserPermissions(4L);
        log.info(userPermissions.toString());
    }

    /**
     * 获取指定角色的所有权限
     */
    @Test
    public void getRolePermissions(){
        QueryWrapper<RolePermission> rolePermissionQueryWrapper = new QueryWrapper<>();
        rolePermissionQueryWrapper.eq("RoleID", 1L);
        List<RolePermission> rolePermissions = rolePermissionService.list(rolePermissionQueryWrapper);
        log.info(rolePermissions.toString());
    }


    /**
     * 获取所有用户的权限信息
     */
    @Test
    public void getAllUserPermissions(){
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
        log.info(userRolePermissionsArrayList.toString());
    }
}
