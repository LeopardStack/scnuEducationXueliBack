package com.scnujxjy.backendpoint.controller.admission_information;


import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.scnujxjy.backendpoint.constant.enums.RoleEnum;
import com.scnujxjy.backendpoint.constant.enums.TeachingPointInformationEnum;
import com.scnujxjy.backendpoint.dao.entity.admission_information.EnrollmentPlanPO;
import com.scnujxjy.backendpoint.dao.entity.basic.GlobalConfigPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.EnrollmentPlanMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.admission_information.EnrollmentPlanApplyRO;
import com.scnujxjy.backendpoint.model.ro.admission_information.EnrollmentPlanExcelVO;
import com.scnujxjy.backendpoint.model.ro.admission_information.EnrollmentPlanRO;
import com.scnujxjy.backendpoint.model.vo.admission_information.ApprovalPlanSummaryVO;
import com.scnujxjy.backendpoint.model.vo.exam.ExamTeachersInfoVO;
import com.scnujxjy.backendpoint.service.admission_information.EnrollmentPlanService;
import com.scnujxjy.backendpoint.service.basic.GlobalConfigService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;

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
@Slf4j
@Api(tags = "招生计划管理") // Swagger 2的@Api注解
public class EnrollmentPlanController {

    @Resource
    private GlobalConfigService globalConfigService;

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private EnrollmentPlanService enrollmentPlanService;

    @Resource
    private MinioService minioService;

    @Resource
    private EnrollmentPlanMapper enrollmentPlanMapper;

    @PostMapping("/getAddress")
    @SaCheckLogin
    @ApiOperation(value = "根据教学点返回区域信息")
    public SaResult getAddress(@RequestBody EnrollmentPlanApplyRO enrollmentPlanApplyRO) {
        if (StringUtils.isBlank(enrollmentPlanApplyRO.getSchoolLocation())){
            return SaResult.error("教学点的信息不能为空");
        }

        String addressByTeachingPointName = TeachingPointInformationEnum.getAddressByTeachingPointName(enrollmentPlanApplyRO.getSchoolLocation());
        if (StringUtils.isBlank(addressByTeachingPointName)){
            return SaResult.error("不存在该教学点的区域信息");
        }
        return SaResult.data(addressByTeachingPointName);
    }

    @PostMapping("/getStudyFrom")
    @SaCheckLogin
    @ApiOperation(value = "根据学院返回专业名称")
    public SaResult getStudyFrom(@RequestBody EnrollmentPlanApplyRO enrollmentPlanApplyRO) {
       if (StringUtils.isBlank(enrollmentPlanApplyRO.getCollege()) || StringUtils.isBlank(enrollmentPlanApplyRO.getMajorName())){
           return SaResult.error("学院信息和专业信息不能为空");
       }
       if (("外国语言文化学院".equals(enrollmentPlanApplyRO.getCollege()) && "英语".equals(enrollmentPlanApplyRO.getMajorName())) ||
               ("教育信息技术学院".equals(enrollmentPlanApplyRO.getCollege()) && "传播学".equals(enrollmentPlanApplyRO.getMajorName())) ||
               ("经济与管理学院".equals(enrollmentPlanApplyRO.getCollege()) && "人力资源管理".equals(enrollmentPlanApplyRO.getMajorName())) ||
               ("哲学与社会发展学院".equals(enrollmentPlanApplyRO.getCollege()) && "行政管理".equals(enrollmentPlanApplyRO.getMajorName()))){

           return SaResult.data("业余");
       }else {
           return SaResult.data("函授");
       }
    }

    @PostMapping("/setup_enrollment_plan_apply")
    @SaCheckLogin
    @ApiOperation(value = "设置招生计划申报")
    public SaResult setupEnrollmentPlanApply(@RequestBody EnrollmentPlanRO enrollmentPlanRO) {
        if (Objects.isNull(enrollmentPlanRO)) {
            return SaResult.error("招生计划申报参数缺失，无法插入");
        }

        if (enrollmentPlanRO.getApplyOpen() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL1.generateErrorResultInfo();
        }

        if (enrollmentPlanRO.getTeachingPointIdList() == null ||
                enrollmentPlanRO.getTeachingPointIdList().isEmpty()) {
            return ResultCode.ENROLLMENT_PLAN_FAIL2.generateErrorResultInfo();
        }


        return globalConfigService.setupEnrollmentPlanApply(enrollmentPlanRO);
    }

    @PostMapping("approval_enrollment_plan")
    @SaCheckLogin
    @ApiOperation(value = "审核招生计划")
    public SaResult approvalEnrollmentPlan(Long enrollmentPlanId) {
        // 往下推一步
        if (enrollmentPlanId == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL19.generateErrorResultInfo();
        }

        EnrollmentPlanPO enrollmentPlanPO = enrollmentPlanService.getBaseMapper().selectOne(new LambdaQueryWrapper<EnrollmentPlanPO>()
                .eq(EnrollmentPlanPO::getId, enrollmentPlanId));
        if (enrollmentPlanPO == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL20.generateErrorResultInfo();
        }

        return enrollmentPlanService.approvalEnrollmentPlan(enrollmentPlanId);
    }

    @PostMapping("refuse_enrollment_plan")
    @SaCheckLogin
    @ApiOperation(value = "招生计划审核不通过")
    public SaResult refuseEnrollmentPlan(Long enrollmentPlanId) {

        // 往下推一步
        if (enrollmentPlanId == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL19.generateErrorResultInfo();
        }

        EnrollmentPlanPO enrollmentPlanPO = enrollmentPlanService.getBaseMapper().selectOne(new LambdaQueryWrapper<EnrollmentPlanPO>()
                .eq(EnrollmentPlanPO::getId, enrollmentPlanId));
        if (enrollmentPlanPO == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL20.generateErrorResultInfo();
        }

        return enrollmentPlanService.refuseEnrollmentPlan(enrollmentPlanId);
    }

    @PostMapping("batch_approval_enrollment_plan")
    @SaCheckLogin
    @ApiOperation(value = "批量提交招生计划")
    public SaResult batchApprovalEnrollmentPlan(@RequestBody EnrollmentPlanApplyRO enrollmentPlanApplyRO) {
        // 涉及的所有招生计划往下推一步
        if (enrollmentPlanApplyRO == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL42.generateErrorResultInfo();
        }

        return enrollmentPlanService.batchApprovalEnrollmentPlan(enrollmentPlanApplyRO);
    }

    /**
//     * @param enrollmentPlanId
//     * @param roleName         打回给谁
     * @return
     */
    @PostMapping("approval_rollback")
    @SaCheckLogin
    @ApiOperation(value = "打回招生计划")
    public SaResult approvalRollbackEnrollmentPlan(@RequestBody EnrollmentPlanApplyRO enrollmentPlanApplyRO) {

        if (enrollmentPlanApplyRO.getId() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL19.generateErrorResultInfo();
        }
        if (StringUtils.isBlank(enrollmentPlanApplyRO.getRemarks())){
            return ResultCode.ENROLLMENT_PLAN_FAIL57.generateErrorResultInfo();
        }
        Long enrollmentPlanId=(long)enrollmentPlanApplyRO.getId();
        String roleName=enrollmentPlanApplyRO.getRoleName();
        String remarks = enrollmentPlanApplyRO.getRemarks();
        //

        EnrollmentPlanPO enrollmentPlanPO = enrollmentPlanService.getBaseMapper().selectOne(new LambdaQueryWrapper<EnrollmentPlanPO>()
                .eq(EnrollmentPlanPO::getId, enrollmentPlanId));
        if (enrollmentPlanPO == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL20.generateErrorResultInfo();
        }

        if (!roleName.equals(RoleEnum.TEACHING_POINT_ADMIN.getRoleName()) &&
                !roleName.equals(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 只能打回到教学点或者二级学院手里
            return ResultCode.ENROLLMENT_PLAN_FAIL21.generateErrorResultInfo();
        }

        return enrollmentPlanService.approvalRollbackEnrollmentPlan(enrollmentPlanId, roleName,remarks);
    }

    @PostMapping("batch_approval_rollback_enrollment_plan")
    @SaCheckLogin
    @ApiOperation(value = "批量打回招生计划")
    public SaResult batchApprovalRollbackEnrollmentPlan(@RequestBody EnrollmentPlanApplyRO enrollmentPlanApplyRO) {
        // 涉及的所有招生计划往前推一步
        if (enrollmentPlanApplyRO == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL45.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getRoleName() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL46.generateErrorResultInfo();
        } else {
            if (!enrollmentPlanApplyRO.getRoleName().equals(RoleEnum.TEACHING_POINT_ADMIN.getRoleName()) &&
                    !enrollmentPlanApplyRO.getRoleName().equals(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())) {
                return ResultCode.ENROLLMENT_PLAN_FAIL47.generateErrorResultInfo();
            }
        }

        return enrollmentPlanService.batchApprovalRollbackEnrollmentPlan(enrollmentPlanApplyRO);
    }

    @PostMapping("/apply_enrollment_plan")
    @SaCheckLogin
    @ApiOperation(value = "申报招生计划")
    public SaResult applyEnrollmentPlan(@RequestBody EnrollmentPlanApplyRO enrollmentPlanApplyRO) {
        if (Objects.isNull(enrollmentPlanApplyRO)) {
            return SaResult.error("招生计划申报参数缺失，无法申报");
        }

        if (enrollmentPlanApplyRO.getYear() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL4.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getMajorName() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL5.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getStudyForm() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL6.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getEducationLength() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL7.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getTrainingLevel() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL8.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getEnrollmentNumber() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL9.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getTargetStudents() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL10.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getEnrollmentRegion() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL11.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getSchoolLocation() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL12.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getContactNumber() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL13.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getCollege() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL14.generateErrorResultInfo();
        }

        return enrollmentPlanService.applyEnrollmentPlan(enrollmentPlanApplyRO);
    }

    @PostMapping("/edit_enrollment_plan")
    @SaCheckLogin
    @ApiOperation(value = "编辑招生计划")
    public SaResult editEnrollmentPlan(@RequestBody EnrollmentPlanApplyRO enrollmentPlanApplyRO) {
        if (enrollmentPlanApplyRO.getId() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL22.generateErrorResultInfo();
        }
        EnrollmentPlanPO enrollmentPlanPO = enrollmentPlanService.getBaseMapper().selectOne(new LambdaQueryWrapper<EnrollmentPlanPO>()
                .eq(EnrollmentPlanPO::getId, enrollmentPlanApplyRO.getId()));
        if (enrollmentPlanPO == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL23.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getYear() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL24.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getMajorName() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL25.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getStudyForm() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL26.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getEducationLength() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL27.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getTrainingLevel() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL28.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getEnrollmentNumber() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL29.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getTargetStudents() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL30.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getEnrollmentRegion() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL31.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getSchoolLocation() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL32.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getContactNumber() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL33.generateErrorResultInfo();
        }

        if (enrollmentPlanApplyRO.getCollege() == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL34.generateErrorResultInfo();
        }
        return enrollmentPlanService.editEnrollmentPlan(enrollmentPlanApplyRO);
    }

    @PostMapping("/delete_enrollment_plan")
    @SaCheckLogin
    @ApiOperation(value = "删除招生计划")
    public SaResult editEnrollmentPlan(Integer id) {
        if (id == null) {
            return ResultCode.ENROLLMENT_PLAN_FAIL54.generateErrorResultInfo();
        }

        int delete = enrollmentPlanService.getBaseMapper().delete(new LambdaQueryWrapper<EnrollmentPlanPO>()
                .eq(EnrollmentPlanPO::getId, id));
        if (delete > 0) {
            return SaResult.ok("删除成功");
        }

        return ResultCode.ENROLLMENT_PLAN_FAIL55.generateErrorResultInfo();
    }

    @PostMapping("/query_enrollment_plan")
    @SaCheckLogin
    @ApiOperation(value = "查询招生计划")
    public SaResult queryEnrollmentPlan(@RequestBody PageRO<EnrollmentPlanApplyRO> enrollmentPlanApplyROPageRO) {
        List<String> roleList = StpUtil.getRoleList();
        if (roleList.contains(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())) {
            // 招生部管理员 不受任何限制 直接获取所有的招生计划
        } else if (roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 二级学院教务员 只能获取本学院的招生计划
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            enrollmentPlanApplyROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());
        } else if (roleList.contains(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())) {
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
        if (roleList.contains(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())) {
            // 招生部管理员 不受任何限制 直接获取所有的招生计划
        } else if (roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 二级学院教务员 只能获取本学院的招生计划
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            enrollmentPlanApplyRO.setCollege(userBelongCollege.getCollegeName());
        } else if (roleList.contains(RoleEnum.TEACHING_POINT_ADMIN.getRoleName())) {
            // 教学点教务员 只能获取 自己本教学点的招生计划
            TeachingPointInformationPO userBelongTeachingPoint = scnuXueliTools.getUserBelongTeachingPoint();
            enrollmentPlanApplyRO.setTeachingPointName(userBelongTeachingPoint.getTeachingPointName());
        }

        return enrollmentPlanService.getEnrollmentPlanFilterItems(enrollmentPlanApplyRO);
    }

    @PostMapping("/get_enrollment_plan_filter")
    @SaCheckLogin
    @ApiOperation(value = "获取申报招生计划的筛选项")
    public SaResult getEnrollmentPlanFilter(@RequestBody EnrollmentPlanApplyRO enrollmentPlanApplyRO) {

        List<String> roleList = StpUtil.getRoleList();
        return enrollmentPlanService.getEnrollmentPlanFilter(enrollmentPlanApplyRO,roleList);
    }

    @GetMapping("/get_enrollment_plan_approval_rollback_items")
    @SaCheckLogin
    @ApiOperation(value = "获取打回角色筛选项")
    public SaResult getEnrollmentPlanApprovalRollbackItems() {


        List<String> roleList = StpUtil.getRoleList();
        List<String> retRoleList = new ArrayList<>();
        if (roleList.contains(RoleEnum.ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())) {
            // 招生部管理员 不受任何限制 直接获取所有的招生计划
            retRoleList.add(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName());
            retRoleList.add(RoleEnum.TEACHING_POINT_ADMIN.getRoleName());
        } else if (roleList.contains(RoleEnum.SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 二级学院教务员 只能获取本学院的招生计划
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            retRoleList.add(RoleEnum.TEACHING_POINT_ADMIN.getRoleName());
        }

        return SaResult.ok("成功获取打回角色列表").setData(retRoleList);
    }

    @PostMapping("/download_approval_plan_application")
    @SaCheckLogin
    @ApiOperation(value = "下载招生计划申报表")
    public SaResult downloadApprovalPlanApplication(HttpServletResponse httpServletResponse) throws IOException {

        List<String> roleList = StpUtil.getRoleList();
        String userId = (String) StpUtil.getLoginId();
        if (roleList.isEmpty()) {
            return ResultCode.ROLE_INFO_FAIL1.generateErrorResultInfo();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {

                CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                if (userBelongCollege == null) {
                    return SaResult.error("未获取到学院名称，请联系管理员");
                }
                String fileName = userBelongCollege.getCollegeName() + "招生计划申报表";

                List<EnrollmentPlanPO> enrollmentPlanPOS = enrollmentPlanMapper.queryCollegeEnrollmentPlans(userBelongCollege.getCollegeName());
                if (enrollmentPlanPOS == null || enrollmentPlanPOS.size() == 0) {
                    return SaResult.error("该学院不存在招生计划申请，无需导出");
                }

                downloadApprovalPlan(httpServletResponse, fileName, enrollmentPlanPOS);

            } else if(roleList.contains(TEACHING_POINT_ADMIN.getRoleName())){
                //教学点只可以导出自己教学点的申报表，申报表只能是已完成
                TeachingPointInformationPO userBelongTeachingPoint = scnuXueliTools.getUserBelongTeachingPoint();
                String teachingPointName = userBelongTeachingPoint.getTeachingPointName();
                String fileName = teachingPointName+ "招生计划申报表";

                List<EnrollmentPlanPO> enrollmentPlanPOS = enrollmentPlanMapper.queryTeachingEnrollmentPlans(teachingPointName);
                if (enrollmentPlanPOS == null || enrollmentPlanPOS.size() == 0) {
                    return SaResult.error("该教学点不存在已完成的招生计划申请，无需导出");
                }

                List<EnrollmentPlanPO> aggregatedEnrollmentPlans = enrollmentPlanPOS.stream()
                        .collect(Collectors.groupingBy(EnrollmentPlanPO::getCollege))
                        .values().stream()
                        .flatMap(plans -> plans.stream()
                                .sorted(Comparator.comparing(plan -> { // 先按照trainingLevel进行排序
                                    String trainingLevel = plan.getTrainingLevel();
                                    if ("专升本".equals(trainingLevel)) {
                                        return 1;
                                    } else if ("高起专".equals(trainingLevel)) {
                                        return 2;
                                    } else {
                                        return 0;
                                    }
                                }))
                        )
                        .collect(Collectors.toList());

                downloadApprovalPlan(httpServletResponse, fileName, aggregatedEnrollmentPlans);


            } else {
                return SaResult.error("导出招生计划表仅限给二级学院使用");
            }

        }

        return SaResult.ok("成功下载");
    }

    private void downloadApprovalPlan(HttpServletResponse httpServletResponse, String fileName, List<EnrollmentPlanPO> enrollmentPlanPOS) throws IOException {
        List<EnrollmentPlanExcelVO> enrollmentPlanExcelVOS = new ArrayList<>();
        for (EnrollmentPlanPO enrollmentPlanPO : enrollmentPlanPOS) {
            EnrollmentPlanExcelVO enrollmentPlanExcelVO = new EnrollmentPlanExcelVO();
            enrollmentPlanExcelVO.setMajorName(enrollmentPlanPO.getMajorName());
            enrollmentPlanExcelVO.setStudyForm(enrollmentPlanPO.getStudyForm());
            enrollmentPlanExcelVO.setEducationLength(enrollmentPlanPO.getEducationLength());
            enrollmentPlanExcelVO.setTrainingLevel(enrollmentPlanPO.getTrainingLevel());
            enrollmentPlanExcelVO.setEnrollmentNumber(enrollmentPlanPO.getEnrollmentNumber());

            enrollmentPlanExcelVO.setTargerStudents(enrollmentPlanPO.getTargetStudents());
            enrollmentPlanExcelVO.setSchoolLocation(enrollmentPlanPO.getSchoolLocation());
            enrollmentPlanExcelVO.setEnrollmentRegion(enrollmentPlanPO.getEnrollmentRegion());
            enrollmentPlanExcelVO.setContactNumber(enrollmentPlanPO.getContactNumber());
            enrollmentPlanExcelVOS.add(enrollmentPlanExcelVO);
        }

        String configValue = globalConfigService.getBaseMapper().selectOne(new LambdaQueryWrapper<GlobalConfigPO>()
                .eq(GlobalConfigPO::getConfigKey, "招生计划申报表模板")).getConfigValue();

        InputStream fileInputStreamFromMinio = minioService.getFileInputStreamFromMinio(configValue);

        ExcelWriter excelWriter = null;

        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        // 把年月日转换成字符串
        String yearStr = String.valueOf(year);
        String monthStr = String.valueOf(month);
        String dayStr = String.valueOf(day);

        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("year", yearStr);
        basicInfo.put("month", monthStr);
        basicInfo.put("day", dayStr);

        httpServletResponse.setContentType("application/vnd.ms-excel");
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

        // 获取输出流
        OutputStream outputStream = httpServletResponse.getOutputStream();

        // 使用 EasyExcel 将 Excel 文件写入输出流
        excelWriter = EasyExcel.write(outputStream, EnrollmentPlanExcelVO.class)
                .withTemplate(fileInputStreamFromMinio)
                .build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        excelWriter.fill(enrollmentPlanExcelVOS, fillConfig, writeSheet);
        // 填充填表时间
        excelWriter.fill(basicInfo, writeSheet);
        excelWriter.finish();

        // 关闭输出流
        outputStream.flush();
        outputStream.close();
    }

    @PostMapping("/download_approval_plan_summary")
    @SaCheckLogin
    @ApiOperation(value = "下载招生计划汇总表")
    public SaResult downloadApprovalPlanSummary(@RequestBody EnrollmentPlanApplyRO enrollmentPlanApplyRO, HttpServletResponse response) throws IOException {

        List<String> roleList = StpUtil.getRoleList();
        if (!roleList.contains(SUPER_ADMIN.getRoleName()) && !roleList.contains(ADMISSIONS_DEPARTMENT_ADMINISTRATOR.getRoleName())) {
            return SaResult.error("招生计划汇总表只能招生办管理员才能导出");
        }

        List<ApprovalPlanSummaryVO> approvalPlanSummaryVOList =
                enrollmentPlanService.downloadApprovalPlanSummary(enrollmentPlanApplyRO);

        String configValue = globalConfigService.getBaseMapper().selectOne(new LambdaQueryWrapper<GlobalConfigPO>()
                .eq(GlobalConfigPO::getConfigKey, "招生计划汇总表模板")).getConfigValue();

        InputStream fileInputStreamFromMinio = minioService.getFileInputStreamFromMinio(configValue);

//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 配置 Excel 写入操作
        ExcelWriter excelWriter = null;

        String year = String.valueOf(Year.now().getValue());
        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("year", year);

        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=" + "招生计划汇总表" + ".xlsx");

        // 获取输出流
        OutputStream outputStream = response.getOutputStream();

        // 使用 EasyExcel 将 Excel 文件写入输出流
        excelWriter = EasyExcel.write(outputStream, EnrollmentPlanExcelVO.class)
                .withTemplate(fileInputStreamFromMinio)
                .build();
        WriteSheet writeSheet = EasyExcel.writerSheet().build();
        FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
        excelWriter.fill(approvalPlanSummaryVOList, fillConfig, writeSheet);
        // 填充填表时间
        excelWriter.fill(basicInfo, writeSheet);
        excelWriter.finish();

        // 关闭输出流
        outputStream.flush();
        outputStream.close();

        return SaResult.ok("成功下载");
    }

//    @GetMapping("/download_approval_plan_summary2")
    @SaCheckLogin
    @ApiOperation(value = "下载招生计划汇总表")
    public ResponseEntity<InputStreamResource> downloadApprovalPlanSummary2(@RequestBody EnrollmentPlanApplyRO enrollmentPlanApplyRO) {

        List<ApprovalPlanSummaryVO> approvalPlanSummaryVOList = enrollmentPlanService.downloadApprovalPlanSummary(enrollmentPlanApplyRO);

        String configValue = globalConfigService.getBaseMapper().selectOne(new LambdaQueryWrapper<GlobalConfigPO>()
                .eq(GlobalConfigPO::getConfigKey, "考试信息导出模板")).getConfigValue();

        InputStream fileInputStreamFromMinio = minioService.getFileInputStreamFromMinio(configValue);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ExcelWriter excelWriter = null;

        String year = String.valueOf(Year.now().getValue());
        Map<String, Object> basicInfo = new HashMap<>();
        basicInfo.put("year", year);

        try {
            excelWriter = EasyExcel.write(outputStream, ExamTeachersInfoVO.class)
                    .withTemplate(fileInputStreamFromMinio)
                    .build();

            WriteSheet writeSheet = EasyExcel.writerSheet().build();
            FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.TRUE).build();
            excelWriter.fill(approvalPlanSummaryVOList, fillConfig, writeSheet);
            excelWriter.fill(basicInfo, writeSheet);
            excelWriter.finish();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(outputStream.toByteArray()));

        HttpHeaders headers = new HttpHeaders();
        String filename = "approval_plan_summary.xlsx";
        try {
            String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8.toString());
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFilename);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(outputStream.size())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }


}

