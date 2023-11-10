package com.scnujxjy.backendpoint.PlatformUserTest;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformRolePO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.basic.RolePermissionPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.service.basic.PermissionService;
import com.scnujxjy.backendpoint.service.basic.PlatformRoleService;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.basic.RolePermissionService;
import com.scnujxjy.backendpoint.service.college.CollegeAdminInformationService;
import com.scnujxjy.backendpoint.service.college.CollegeInformationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
@Slf4j
public class TestPermissionChange {
    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private PermissionService permissionService;
    @Resource
    private CollegeInformationService collegeInformationService;

    @Resource
    private PlatformRoleService platformRoleService;

    @Resource
    private CollegeAdminInformationService collegeAdminInformationService;

    @Resource
    private RolePermissionService rolePermissionService;


    /**
     * 查询各个学院的教务员的账号
     */
    @Test
    public void Test1(){
        CollegeInformationPO collegeInformationPO = collegeInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                .eq(CollegeInformationPO::getCollegeName, "哲学与社会发展学院")
        );
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeAdminInformationPO>()
                .eq(CollegeAdminInformationPO::getCollegeId, collegeInformationPO.getCollegeId())
                .eq(CollegeAdminInformationPO::getName, "王芹")
        );
        PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                .eq(PlatformUserPO::getUserId, collegeAdminInformationPO.getUserId())
        );

        PlatformRolePO platformRolePO = platformRoleService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformRolePO>()
                .eq(PlatformRolePO::getRoleId, platformUserPO.getRoleId())
        );

        List<RolePermissionPO> rolePermissionPOS = rolePermissionService.getBaseMapper().selectList(new LambdaQueryWrapper<RolePermissionPO>()
                .eq(RolePermissionPO::getRoleId, platformUserPO.getRoleId())
        );

        log.info("\n王芹的账号信息为 " + platformUserPO + " 角色信息为 " + platformRolePO + "\n权限信息包括 " + rolePermissionPOS);

        // 添加权限 15 教学管理
        RolePermissionPO rolePermissionPO = new RolePermissionPO();
        rolePermissionPO.setRoleId(platformUserPO.getRoleId());
        rolePermissionPO.setPermissionId(15L);
        int insert = rolePermissionService.getBaseMapper().insert(rolePermissionPO);
        log.info("添加权限的插入结果 " + insert);
    }

    /**
     * 查询各个学院的教务员的账号
     */
    @Test
    public void Test2(){
        String college = "计算机学院";
        CollegeInformationPO collegeInformationPO = collegeInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                .eq(CollegeInformationPO::getCollegeName, college)
        );
        List<CollegeAdminInformationPO> collegeAdminInformationPOs = collegeAdminInformationService.getBaseMapper().selectList(new LambdaQueryWrapper<CollegeAdminInformationPO>()
                .eq(CollegeAdminInformationPO::getCollegeId, collegeInformationPO.getCollegeId())
        );
        log.info(college + " 的账号信息如下 \n" + collegeAdminInformationPOs);
    }


    /**
     * 赋予教学点教务员查询权限
     */
    @Test
    public void test3(){
        ArrayList<Long> list = new ArrayList<>(Arrays.asList(3L, 6L, 8L, 9L, 11L, 12L, 14L, 15L)); // Create an ArrayList of Long values

        for(Long permissionId : list){
            RolePermissionPO rolePermissionPO = new RolePermissionPO(); // Create a new RolePermissionPO object
            rolePermissionPO.setRoleId(7L); // Set the roleId to 7L
            rolePermissionPO.setPermissionId(permissionId); // Set the permissionId to 3L

            rolePermissionService.getBaseMapper().insert(rolePermissionPO); // Insert the RolePermissionPO object into the database using a service or mapper
        }


    }
}
