package com.scnujxjy.backendpoint.platform_message;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.constant.enums.RoleEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.AdminInfoPO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformRolePO;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.model.bo.platform_message.ManagerInfoBO;
import com.scnujxjy.backendpoint.service.basic.AdminInfoService;
import com.scnujxjy.backendpoint.service.basic.PlatformRoleService;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.college.CollegeAdminInformationService;
import com.scnujxjy.backendpoint.service.college.CollegeInformationService;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointAdminInformationService;
import com.scnujxjy.backendpoint.service.teaching_point.TeachingPointInformationService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
public class Test2 {
    @Resource
    private AdminInfoService adminInfoService;

    @Resource
    private TeacherInformationService teacherInformationService;

    @Resource
    private TeachingPointAdminInformationService teachingPointAdminInformationService;

    @Resource
    private TeachingPointInformationService teachingPointInformationService;

    @Resource
    private CollegeAdminInformationService collegeAdminInformationService;

    @Resource
    private CollegeInformationService collegeInformationService;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private PlatformRoleService platformRoleService;

    @Test
    public void test1(){
        List<ManagerInfoBO> managerInfoBOList = new ArrayList<>();

        List<AdminInfoPO> adminInfoPOS = adminInfoService.getBaseMapper().selectList(null);

        // 获取继续教育学院各个部门的教师信息

        for(AdminInfoPO adminInfoPO : adminInfoPOS){
//            log.info(adminInfoPO.toString());

            PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUserId, adminInfoPO.getUserId()));

            PlatformRolePO platformRolePO = platformRoleService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformRolePO>()
                    .eq(PlatformRolePO::getRoleId, adminInfoPO.getRoleId()));

            ManagerInfoBO managerInfoBO = new ManagerInfoBO()
                    .setDepartment(adminInfoPO.getDepartment())
                    .setName(adminInfoPO.getName())
                    .setUsername(platformUserPO.getUsername())
                    .setPhoneNumber(adminInfoPO.getPrivatePhone())
                    .setRoleName(platformRolePO.getRoleName())
                    .setWorkNumber(adminInfoPO.getWorkNumber())
                    .setIdNumber(adminInfoPO.getIdNumber())
                    ;
            managerInfoBOList.add(managerInfoBO);
        }

        // 获取各二级学院的教师信息
        List<CollegeAdminInformationPO> collegeAdminInformationPOS = collegeAdminInformationService.getBaseMapper().selectList(null);
        for(CollegeAdminInformationPO collegeAdminInformationPO : collegeAdminInformationPOS){
//            log.info(collegeAdminInformationPO.toString());
            PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUserId, collegeAdminInformationPO.getUserId()));
            CollegeInformationPO collegeInformationPO = collegeInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<CollegeInformationPO>()
                    .eq(CollegeInformationPO::getCollegeId, collegeAdminInformationPO.getCollegeId()));


            ManagerInfoBO managerInfoBO = new ManagerInfoBO()
                    .setName(collegeAdminInformationPO.getName())
                    .setCollegeName(collegeInformationPO.getCollegeName())
                    .setUsername(platformUserPO.getUsername())
                    .setPhoneNumber(collegeAdminInformationPO.getPhone())
                    .setRoleName(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())
                    .setWorkNumber(collegeAdminInformationPO.getWorkNumber())
                    .setIdNumber(collegeAdminInformationPO.getIdNumber())
                    ;

            managerInfoBOList.add(managerInfoBO);
        }

        // 获取全部的 教学点管理员信息
        List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOS = teachingPointAdminInformationService.getBaseMapper().selectList(null);
        for(TeachingPointAdminInformationPO teachingPointAdminInformationPO : teachingPointAdminInformationPOS){
//            log.info(teachingPointAdminInformationPO.toString());

            TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                    .eq(TeachingPointInformationPO::getTeachingPointId, teachingPointAdminInformationPO.getTeachingPointId()));
            PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUserId, teachingPointAdminInformationPO.getUserId()));

            ManagerInfoBO managerInfoBO = new ManagerInfoBO()
                    .setName(teachingPointAdminInformationPO.getName())
                    .setTeachingPointName(teachingPointInformationPO.getTeachingPointName())
                    .setUsername(platformUserPO.getUsername())
                    .setPhoneNumber(teachingPointAdminInformationPO.getPhone())
                    .setRoleName(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())
                    .setIdNumber(teachingPointAdminInformationPO.getIdCardNumber())
                    ;
            managerInfoBOList.add(managerInfoBO);
        }

        // 获取全部的教师信息
        List<TeacherInformationPO> teacherInformationPOList = teacherInformationService.getBaseMapper().selectList(null);
        for(TeacherInformationPO teacherInformationPO : teacherInformationPOList){
            log.info(teacherInformationPO.toString());
            PlatformUserPO platformUserPO = platformUserService.getBaseMapper().selectOne(new LambdaQueryWrapper<PlatformUserPO>()
                    .eq(PlatformUserPO::getUserId, teacherInformationPO.getUserId()));

            ManagerInfoBO managerInfoBO = new ManagerInfoBO()
                    .setUsername(platformUserPO.getUsername())
                    .setName(teacherInformationPO.getName())
                    .setCollegeName(teacherInformationPO.getCollegeId())
                    .setRoleName(RoleEnum.TEACHER.getRoleName())
                    .setPhoneNumber(teacherInformationPO.getPhone())
                    .setWorkNumber(teacherInformationPO.getWorkNumber())
                    .setIdNumber(teacherInformationPO.getIdCardNumber())
                    ;
            managerInfoBOList.add(managerInfoBO);
        }
    }
}
