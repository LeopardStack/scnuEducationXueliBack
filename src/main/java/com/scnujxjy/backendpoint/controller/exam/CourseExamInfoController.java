package com.scnujxjy.backendpoint.controller.exam;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.exam.BatchSetTeachersInfoRO;
import com.scnujxjy.backendpoint.model.ro.exam.SingleSetTeachersInfoRO;
import com.scnujxjy.backendpoint.service.exam.CourseExamInfoService;
import com.scnujxjy.backendpoint.util.MessageSender;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.SECOND_COLLEGE_ADMIN;
import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.XUELIJIAOYUBU_ADMIN;

/**
 * 考试信息获取
 *
 * @author 谢辉龙
 * @since 2023-11-15
 */
@RestController
@RequestMapping("/course-exam-info")
@Slf4j

public class CourseExamInfoController {

    @Resource
    private CourseExamInfoService courseExamInfoService;

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private MessageSender messageSender;

    @Resource
    private ManagerFilter managerFilter;

    @Resource
    private CollegeAdminFilter collegeAdminFilter;

    /**
     * 单个课程设置为机考
     *
     * @param id 年级
     * @return
     */
    @GetMapping("/singleSetExamType/{id}")
    @SaCheckPermission("修改考试信息")
    public SaResult getImportPhoto(@PathVariable Long id) {
        boolean b = courseExamInfoService.singleSetExamType(id);
        if(b){
            return SaResult.ok("更新考试方式成功!");
        }
        return SaResult.error("更新考试方式失败!").setCode(2001);
    }


    /**
     * 根据参数批量设置机考
     * @param batchSetTeachersInfoRO
     * @return
     */
    @PostMapping("/batch_set_exam_type")
    @SaCheckPermission("修改考试信息")
    public SaResult batchSetExamType(@RequestBody BatchSetTeachersInfoRO batchSetTeachersInfoRO) {
        // 将前端 this.form 字段里为 空字符串的属性 设置为 null
        scnuXueliTools.convertEmptyStringsToNull(batchSetTeachersInfoRO);
        boolean b = courseExamInfoService.batchSetExamType(batchSetTeachersInfoRO);
        return SaResult.ok("批量设置机考结果为  " + b);
    }

    /**
     * 根据参数批量取消机考
     * @param batchSetTeachersInfoRO
     * @return
     */
    @PostMapping("/batch_unset_exam_type")
    @SaCheckPermission("修改考试信息")
    public SaResult batchUnSetExamType(@RequestBody BatchSetTeachersInfoRO batchSetTeachersInfoRO) {

        boolean b = courseExamInfoService.batchUnSetExamType(batchSetTeachersInfoRO);
        return SaResult.ok("批量设置机考结果为  " + b);
    }

    /**
     * 单个设置命题教师和阅卷助教
     * @param singleSetTeachersInfoRO
     * @return
     */
    @PostMapping("/single_set_exam_teachers")
    @SaCheckPermission("修改考试信息")
    public SaResult singleSetExamTeachers(@RequestBody SingleSetTeachersInfoRO singleSetTeachersInfoRO) {

        try {
            boolean b = courseExamInfoService.singleSetTeachers(singleSetTeachersInfoRO);
            if(b){
                return SaResult.ok("成功更新老师信息结果为  " + b);
            }else{
                return SaResult.error("更新教师信息失败 " + b).setCode(2001);
            }

        }catch (Exception e){
            log.error("单个更新教师信息失败 " + singleSetTeachersInfoRO + "\n" + e.toString());
            return SaResult.error("更新教师信息失败").setCode(2001);
        }
    }

    /**
     * 单个清除命题教师和阅卷助教
     * @param singleSetTeachersInfoRO
     * @return
     */
    @PostMapping("/single_delete_exam_teachers")
    @SaCheckPermission("修改考试信息")
    public SaResult singleDeleteExamTeachers(@RequestBody SingleSetTeachersInfoRO singleSetTeachersInfoRO) {

        try {
            int b = courseExamInfoService.singleDeleteTeachers(singleSetTeachersInfoRO);
            if(b > 0){
                return SaResult.ok("成功删除老师 " + b);
            }else{
                return SaResult.ok("老师信息已删除 " + b);
            }

        }catch (Exception e){
            log.error("单个删除教师信息失败 " + singleSetTeachersInfoRO + "\n" + e.toString());
            return SaResult.error("删除教师信息失败").setCode(2001);
        }
    }

    /**
     * 批量设置命题教师和阅卷助教
     * @param batchSetTeachersInfoRO
     * @return
     */
    @PostMapping("/batch_set_exam_teachers")
    @SaCheckPermission("修改考试信息")
    public SaResult batchSetExamTeachers(@RequestBody BatchSetTeachersInfoRO batchSetTeachersInfoRO) {

        try {
            // 将前端 this.form 字段里为 空字符串的属性 设置为 null
            scnuXueliTools.convertEmptyStringsToNull(batchSetTeachersInfoRO);
            // 处理完非空 直接调用消息队列 异步处理 前端直接返回 OK
            boolean b1 = messageSender.sendSystemMsg(batchSetTeachersInfoRO, StpUtil.getLoginIdAsString(), MessageEnum.BATCH_SET_Exam_Teachers.getMessageName());

//            boolean b = courseExamInfoService.batchSetTeachers(batchSetTeachersInfoRO);
            if(b1){
                return SaResult.ok("成功开始设置考试命题人和阅卷人，请留意系统消息");
            }else{
                return SaResult.error("批量更新教师信息失败 " + b1).setCode(2001);
            }

        }catch (Exception e){
            log.error("批量更新教师信息失败 " + batchSetTeachersInfoRO + "\n" + e.toString());
            return SaResult.error("批量更新教师信息失败").setCode(2001);
        }
    }

    /**
     * 批量导出机考信息，包含命题人和阅卷人
     * @param batchSetTeachersInfoRO
     * @return
     */
    @PostMapping("/batch_export_exam_teachers")
    public SaResult batchExportExamTeachersInfo(@RequestBody BatchSetTeachersInfoRO batchSetTeachersInfoRO) {

        try {
            // 将前端 this.form 字段里为 空字符串的属性 设置为 null
            scnuXueliTools.convertEmptyStringsToNull(batchSetTeachersInfoRO);
            // 处理完非空 直接调用消息队列 异步处理 前端直接返回 OK
            List<String> roleList = StpUtil.getRoleList();
            String userId = (String) StpUtil.getLoginId();
            if (roleList.isEmpty()) {
                return ResultCode.ROLE_INFO_FAIL1.generateErrorResultInfo();
            } else {
                PageRO<BatchSetTeachersInfoRO> batchSetTeachersInfoROPageVO = new PageRO<>();
                batchSetTeachersInfoROPageVO.setEntity(batchSetTeachersInfoRO);
                if(roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())){
                    CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                    batchSetTeachersInfoROPageVO.getEntity().setCollege(userBelongCollege.getCollegeName());
                    boolean send = messageSender.sendExportMsg(batchSetTeachersInfoROPageVO, collegeAdminFilter, userId);
                    if (send) {
                        return SaResult.ok("导出学籍数据成功");
                    }
                }else if(roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())){
                    boolean send = messageSender.sendExportMsg(batchSetTeachersInfoROPageVO, managerFilter, userId);
                    if (send) {
                        return SaResult.ok("导出学籍数据成功");
                    }
                }

            }
            return SaResult.ok("批量导出考试信息失败");

        }catch (Exception e){
            log.error("批量更新教师信息失败 " + batchSetTeachersInfoRO + "\n" + e.toString());
            return SaResult.error("批量更新教师信息失败").setCode(2001);
        }
    }

    /**
     * 批量导出机考名单
     * @param batchSetTeachersInfoRO
     * @return
     */
    @PostMapping("/batch_export_exam_students")
    public SaResult batchExportExamStudentsInfo(@RequestBody BatchSetTeachersInfoRO batchSetTeachersInfoRO) {

        try {
            // 将前端 this.form 字段里为 空字符串的属性 设置为 null
            scnuXueliTools.convertEmptyStringsToNull(batchSetTeachersInfoRO);
            // 处理完非空 直接调用消息队列 异步处理 前端直接返回 OK
            List<String> roleList = StpUtil.getRoleList();
            String userId = (String) StpUtil.getLoginId();
            if (roleList.isEmpty()) {
                return ResultCode.ROLE_INFO_FAIL1.generateErrorResultInfo();
            } else {
                PageRO<BatchSetTeachersInfoRO> batchSetTeachersInfoROPageVO = new PageRO<>();
                batchSetTeachersInfoROPageVO.setEntity(batchSetTeachersInfoRO);
                if(roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())){
                    CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
                    batchSetTeachersInfoROPageVO.getEntity().setCollege(userBelongCollege.getCollegeName());
                    boolean send = messageSender.sendExportExamStudents(batchSetTeachersInfoROPageVO, collegeAdminFilter, userId);
                    if (send) {
                        return SaResult.ok("导出考试名单信息成功");
                    }
                }else if(roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())){
                    boolean send = messageSender.sendExportExamStudents(batchSetTeachersInfoROPageVO, managerFilter, userId);
                    if (send) {
                        return SaResult.ok("导出考试名单信息成功");
                    }
                }

            }
            return SaResult.ok("批量导出考试名单信息失败");

        }catch (Exception e){
            log.error("批量导出考试名单信息失败 " + batchSetTeachersInfoRO + "\n" + e.toString());
            return SaResult.error("批量导出考试名单信息失败").setCode(2001);
        }
    }
}

