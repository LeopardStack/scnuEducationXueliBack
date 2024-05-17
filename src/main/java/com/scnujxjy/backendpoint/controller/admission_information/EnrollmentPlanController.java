package com.scnujxjy.backendpoint.controller.admission_information;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.constant.enums.RoleEnum;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.admission_information.EnrollmentPlanApplyRO;
import com.scnujxjy.backendpoint.model.ro.admission_information.EnrollmentPlanRO;
import com.scnujxjy.backendpoint.service.admission_information.EnrollmentPlanService;
import com.scnujxjy.backendpoint.service.basic.GlobalConfigService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 招生计划申报表 前端控制器
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-05-17
 */
@RestController
@RequestMapping("/enrollment_plan")
@Api(tags = "招生计划管理") // Swagger 2的@Api注解
public class EnrollmentPlanController {

    @Resource
    private GlobalConfigService globalConfigService;

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private EnrollmentPlanService enrollmentPlanService;

    @PostMapping("/setup_enrollment_plan_apply")
    @SaCheckLogin
    @ApiOperation(value = "设置招生计划申报")
    public SaResult setupEnrollmentPlanApply(@RequestBody EnrollmentPlanRO enrollmentPlanRO) {
        if (Objects.isNull(enrollmentPlanRO)) {
            return SaResult.error("招生计划申报参数缺失，无法插入");
        }

        if(enrollmentPlanRO.getApplyOpen() == null){
            return ResultCode.ENROLLMENT_PLAN_FAIL1.generateErrorResultInfo();
        }

        if(enrollmentPlanRO.getTeachingPointIdList() == null ||
                enrollmentPlanRO.getTeachingPointIdList().isEmpty()){
            return ResultCode.ENROLLMENT_PLAN_FAIL2.generateErrorResultInfo();
        }


        return globalConfigService.setupEnrollmentPlanApply(enrollmentPlanRO);
    }

    @PostMapping("/apply_enrollment_plan")
    @SaCheckLogin
    @ApiOperation(value = "申报招生计划")
    public SaResult applyEnrollmentPlan(@RequestBody EnrollmentPlanApplyRO enrollmentPlanApplyRO) {
        if (Objects.isNull(enrollmentPlanApplyRO)) {
            return SaResult.error("招生计划申报参数缺失，无法申报");
        }

        if(enrollmentPlanApplyRO.getYear() == null){
            return ResultCode.ENROLLMENT_PLAN_FAIL4.generateErrorResultInfo();
        }

        if(enrollmentPlanApplyRO.getMajorName() == null){
            return ResultCode.ENROLLMENT_PLAN_FAIL5.generateErrorResultInfo();
        }

        if(enrollmentPlanApplyRO.getStudyForm() == null){
            return ResultCode.ENROLLMENT_PLAN_FAIL6.generateErrorResultInfo();
        }

        if(enrollmentPlanApplyRO.getEducationLength() == null){
            return ResultCode.ENROLLMENT_PLAN_FAIL7.generateErrorResultInfo();
        }

        if(enrollmentPlanApplyRO.getTrainingLevel() == null){
            return ResultCode.ENROLLMENT_PLAN_FAIL8.generateErrorResultInfo();
        }

        if(enrollmentPlanApplyRO.getEnrollmentNumber() == null){
            return ResultCode.ENROLLMENT_PLAN_FAIL9.generateErrorResultInfo();
        }

        if(enrollmentPlanApplyRO.getTargetStudents() == null){
            return ResultCode.ENROLLMENT_PLAN_FAIL10.generateErrorResultInfo();
        }

        if(enrollmentPlanApplyRO.getEnrollmentRegion() == null){
            return ResultCode.ENROLLMENT_PLAN_FAIL11.generateErrorResultInfo();
        }

        if(enrollmentPlanApplyRO.getSchoolLocation() == null){
            return ResultCode.ENROLLMENT_PLAN_FAIL12.generateErrorResultInfo();
        }

        if(enrollmentPlanApplyRO.getContactNumber() == null){
            return ResultCode.ENROLLMENT_PLAN_FAIL13.generateErrorResultInfo();
        }

        if(enrollmentPlanApplyRO.getCollege() == null){
            return ResultCode.ENROLLMENT_PLAN_FAIL14.generateErrorResultInfo();
        }

        return enrollmentPlanService.applyEnrollmentPlan(enrollmentPlanApplyRO);
    }

    @PostMapping("/query_enrollment_plan")
    @SaCheckLogin
    @ApiOperation(value = "查询招生计划")
    public SaResult queryEnrollmentPlan(@RequestBody PageRO<EnrollmentPlanApplyRO> enrollmentPlanApplyROPageRO) {
        List<String> roleList = StpUtil.getRoleList();
        if(roleList.contains(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())){
            // 招生部管理员 不受任何限制 直接获取所有的招生计划
        }else if(roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())){
            // 二级学院教务员 只能获取本学院的招生计划
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            enrollmentPlanApplyROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());
        }else if(roleList.contains(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())){
            // 教学点教务员 只能获取 自己本教学点的招生计划
            TeachingPointInformationPO userBelongTeachingPoint = scnuXueliTools.getUserBelongTeachingPoint();
            enrollmentPlanApplyROPageRO.getEntity().setTeachingPointName(userBelongTeachingPoint.getTeachingPointName());
        }

        return enrollmentPlanService.queryEnrollmentPlan(enrollmentPlanApplyROPageRO);
    }

    @PostMapping("/get_enrollment_plan_filter_items")
    @SaCheckLogin
    @ApiOperation(value = "获取招生计划筛选项")
    public SaResult getEnrollmentPlanFilterItems(@RequestBody EnrollmentPlanApplyRO enrollmentPlanApplyRO) {


        List<String> roleList = StpUtil.getRoleList();
        if(roleList.contains(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())){
            // 招生部管理员 不受任何限制 直接获取所有的招生计划
        }else if(roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())){
            // 二级学院教务员 只能获取本学院的招生计划
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            enrollmentPlanApplyRO.setCollege(userBelongCollege.getCollegeName());
        }else if(roleList.contains(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())){
            // 教学点教务员 只能获取 自己本教学点的招生计划
            TeachingPointInformationPO userBelongTeachingPoint = scnuXueliTools.getUserBelongTeachingPoint();
            enrollmentPlanApplyRO.setTeachingPointName(userBelongTeachingPoint.getTeachingPointName());
        }

        return enrollmentPlanService.getEnrollmentPlanFilterItems(enrollmentPlanApplyRO);
    }
}

