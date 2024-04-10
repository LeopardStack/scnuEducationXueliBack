package com.scnujxjy.backendpoint.controller.teaching_process;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.AddEditCourseClassInfoSelectArgs;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseClassInfoVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseCreateArgsVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseInfoVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
import com.scnujxjy.backendpoint.service.teaching_process.CourseInformationService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.excelListener.CourseInformationListener;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import com.scnujxjy.backendpoint.util.filter.TeachingPointFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;
import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 教学计划信息操作
 *
 * @author leopard
 * @since 2023-08-14
 */
@RestController
@RequestMapping("/course-information")
@Slf4j
public class CourseInformationController {
    @Resource
    private CourseInformationService courseInformationService;

    @Resource
    private ClassInformationService classInformationService;

    @Resource
    private CollegeAdminFilter collegeAdminFilter;

    @Resource
    private ManagerFilter managerFilter;

    @Resource
    private TeachingPointFilter teachingPointFilter;

    /**
     * 根据id查询课程信息
     *
     * @param id 课程信息id
     * @return 课程信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询
        CourseInformationVO courseInformationVO = courseInformationService.detailById(id);

        // 转换并返回
        return SaResult.data(courseInformationVO);
    }

    /**
     * 分页查询课程信息
     *
     * @param courseInformationROPageRO 分页参数
     * @return 分页查询的课程信息列表
     */
    @PostMapping("/page")
    public SaResult pageQueryCourseInformation(@RequestBody PageRO<CourseInformationRO> courseInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(courseInformationROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(courseInformationROPageRO.getEntity())) {
            courseInformationROPageRO.setEntity(new CourseInformationRO());
        }
        // 查询数据
        PageVO<CourseInformationVO> courseInformationVOPageVO = courseInformationService.pageQueryCourseInformation(courseInformationROPageRO);

        // 返回数据
        return SaResult.data(courseInformationVOPageVO);
    }


    /**
     * 根据id更新课程信息
     *
     * @param courseInformationRO 课程信息
     * @return 更新后的课程信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody CourseInformationRO courseInformationRO) {
        // 参数校验
        if (Objects.isNull(courseInformationRO) || Objects.nonNull(courseInformationRO.getId())) {
            throw dataMissError();
        }
        // 更新
        CourseInformationVO courseInformationVO = courseInformationService.editById(courseInformationRO);
        // 校验数据
        if (Objects.isNull(courseInformationVO)) {
            throw dataUpdateError();
        }
        // 返回数据
        return SaResult.data(courseInformationVO);
    }

    /**
     * 根据id删除课程信息
     *
     * @param id 课程信息id
     * @return 删除数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 删除数据
        int count = courseInformationService.deleteById(id);
        // 校验数据
        if (count <= 0) {
            throw dataDeleteError();
        }
        // 返回数据
        return SaResult.data(count);
    }


    /**
     * 根据学生登录的账号查询课程信息
     *
     * @return 课程信息
     */
    @GetMapping("/detail_student_course_information")
    public SaResult detailByStudentID() {
        // 获取访问者 ID
        Object loginId = StpUtil.getLoginId();
        // 查询
        List<CourseInformationRO> studentTeachingPlan = courseInformationService.getStudentTeachingPlan((String) loginId);

        // 转换并返回
        return SaResult.data(studentTeachingPlan);
    }


    /**
     * 根据学生登录的账号查询课程信息
     *
     * @return 课程信息
     */
    @GetMapping("/get_student_teaching_plans")
    public SaResult getTeachingPlansByStudentID() {
        // 获取访问者 ID
        Object loginId = StpUtil.getLoginId();
        // 查询
        List<CourseInformationPO> studentTeachingPlan = courseInformationService.getBaseMapper().getStudentTeachingPlans((String) loginId);

        // 转换并返回
        return SaResult.data(studentTeachingPlan);
    }

    /**
     * 根据二级学院教务员获取筛选参数
     *
     * @return 教学计划
     */
    @GetMapping("/get_select_args_admin")
    public SaResult getTeachingPlansArgsByCollege() {
        List<String> roleList = StpUtil.getRoleList();
        log.info("登录角色 " + roleList);

        // 获取访问者 ID
        String loginId = StpUtil.getLoginIdAsString();
        CourseInformationSelectArgs courseInformationSelectArgs = null;
        if (roleList.isEmpty()) {
            return ResultCode.ROLE_INFO_FAIL1.generateErrorResultInfo();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                log.info("现在登录的是二级学院的管理员");
                // 查询二级学院管理员筛选教学计划的参数
                courseInformationSelectArgs = courseInformationService.getTeachingPlansArgsByCollege(loginId, collegeAdminFilter);
            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                log.info("现在登录的是学历教育部的管理员");
                // 查询继续教育学院管理员筛选教学计划的参数
                courseInformationSelectArgs = courseInformationService.getTeachingPlansArgsByCollege(loginId, managerFilter);
            } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                courseInformationSelectArgs = courseInformationService.getTeachingPlansArgsByCollege(loginId, teachingPointFilter);

            }
        }
        // 转换并返回
        return SaResult.data(courseInformationSelectArgs);
    }

    /**
     * 获取二级学院管理员所有学院内专业的所有教学计划信息 不限制日期
     *
     * @param courseInformationROPageRO
     * @return
     */
    @PostMapping("/allPageByCollegeAdmin")
    public SaResult allPageQueryCourseScheduleByCollegeAdmin(@RequestBody PageRO<CourseInformationRO> courseInformationROPageRO) {

        List<String> roleList = StpUtil.getRoleList();
        log.info("登录角色 " + roleList);
        PageVO<FilterDataVO> filterDataVO = null;
        // 获取访问者 ID
        if (roleList.isEmpty()) {
            return ResultCode.ROLE_INFO_FAIL1.generateErrorResultInfo();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                log.info("现在登录的是二级学院的管理员");
                // 查询二级学院管理员权限范围内的教学计划
                FilterDataVO courseInformationFilterDataVO = courseInformationService.
                        allPageQueryCourseInformationFilter(courseInformationROPageRO, collegeAdminFilter);

                // 创建并返回分页信息
                filterDataVO = new PageVO<>(courseInformationFilterDataVO.getData());
                filterDataVO.setTotal(courseInformationFilterDataVO.getTotal());
                filterDataVO.setCurrent(courseInformationROPageRO.getPageNumber());
                filterDataVO.setSize(courseInformationROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) courseInformationFilterDataVO.getData().size()
                        / courseInformationROPageRO.getPageSize()));

            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                log.info("现在登录的是学历教育部的管理员");
                // 查询继续教育学院管理员权限范围内的教学计划
                FilterDataVO courseInformationFilterDataVO = courseInformationService.
                        allPageQueryCourseInformationFilter(courseInformationROPageRO, managerFilter);
                // 创建并返回分页信息
                filterDataVO = new PageVO<>(courseInformationFilterDataVO.getData());
                filterDataVO.setTotal(courseInformationFilterDataVO.getTotal());
                filterDataVO.setCurrent(courseInformationROPageRO.getPageNumber());
                filterDataVO.setSize(courseInformationROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) courseInformationFilterDataVO.getData().size()
                        / courseInformationROPageRO.getPageSize()));

            } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {

                // 查询继续教育学院管理员权限范围内的教学计划
                FilterDataVO courseInformationFilterDataVO = courseInformationService.
                        allPageQueryCourseInformationFilter(courseInformationROPageRO, teachingPointFilter);
                // 创建并返回分页信息
                filterDataVO = new PageVO<>(courseInformationFilterDataVO.getData());
                filterDataVO.setTotal(courseInformationFilterDataVO.getTotal());
                filterDataVO.setCurrent(courseInformationROPageRO.getPageNumber());
                filterDataVO.setSize(courseInformationROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) courseInformationFilterDataVO.getData().size()
                        / courseInformationROPageRO.getPageSize()));

            }
        }


        // 校验参数
        if (Objects.isNull(courseInformationROPageRO)) {
            throw dataMissError();
        }
        log.info("查询参数1 " + courseInformationROPageRO);
        if (Objects.isNull(courseInformationROPageRO.getEntity())) {
            courseInformationROPageRO.setEntity(new CourseInformationRO());
        }

        // 返回数据
        return SaResult.data(filterDataVO);
    }


    /**
     * 上传教学计划
     *
     * @param file 上传的Excel文件
     * @return 解析结果
     */
    @PostMapping("/upload-excel")
    @SaCheckPermission("教学计划.导入")
    public SaResult uploadExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        } else {
            log.info("获取到了文件 " + file.getOriginalFilename());
        }
        try {
            int headRowNumber = 1;  // 根据您的Excel调整
            CourseInformationListener courseInformationListener =
                    new CourseInformationListener(courseInformationService.getBaseMapper(), classInformationService.getBaseMapper(),
                            "超级管理员参数");
            ExcelReaderBuilder readerBuilder = EasyExcel.read(file.getInputStream(), CourseInformationVO.class, courseInformationListener);
            readerBuilder.doReadAll();

            // 您可以根据需要进一步处理解析后的数据
            return SaResult.data("文件上传并解析成功");
        } catch (Exception e) {
            log.error("上传文件失败", e);
            throw new RuntimeException("上传文件失败：" + e.getMessage());
        }
    }


    /**
     * 下载教学计划
     *
     * @param courseInformationROPageRO
     * @return
     */
    @PostMapping("/download_teaching_plans")
    public SaResult DownloadTeachingPlans(@RequestBody PageRO<CourseInformationRO> courseInformationROPageRO) {

        List<String> roleList = StpUtil.getRoleList();
        log.info("登录角色 " + roleList);
        byte[] bytes = null;
        // 获取访问者 ID
        if (roleList.isEmpty()) {
            return SaResult.error("角色信息缺失，获取教学计划失败");
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                log.info("现在登录的是二级学院的管理员");
                // 查询二级学院管理员权限范围内的教学计划
                bytes = courseInformationService.downloadTeachingPlans(courseInformationROPageRO, collegeAdminFilter);


            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                log.info("现在登录的是学历教育部的管理员");
                // 查询继续教育学院管理员权限范围内的教学计划
                bytes = courseInformationService.downloadTeachingPlans(courseInformationROPageRO, managerFilter);
            }
        }

        // 返回数据
        return SaResult.data(bytes);
    }

    /**
     * 根据不同筛选参数来查询教学计划信息 包含班级信息
     *
     * @return 教学计划
     */
    @PostMapping("/get_course_infos")
    public SaResult getTeachingPlansArgs(@RequestBody CourseInformationRO courseInformationRO) {
        List<String> roleList = StpUtil.getRoleList();
        log.info("登录角色 " + roleList);

        CourseCreateArgsVO courseCreateArgsVO = null;
        if (roleList.isEmpty()) {
            return ResultCode.ROLE_INFO_FAIL1.generateErrorResultInfo();
        } else {
            courseCreateArgsVO = courseInformationService.getTeachingPlansArgs(courseInformationRO);
        }
        // 转换并返回
        return SaResult.data(courseCreateArgsVO);
    }


    /**
     * 根据不同的角色 来根据课程信息来锁定班级信息
     *
     * @return 班级信息
     */
    @PostMapping("/get_class_info_by_course_info")
    public SaResult getClassInfosByCoursesInfo(@RequestBody CourseInformationRO courseInformationRO) {
        Set<CourseClassInfoVO> courseClassInfoVOS = null;
        if (Objects.isNull(courseInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        courseClassInfoVOS = courseInformationService.getClassInfosByCoursesInfo(courseInformationRO);
        // 转换并返回
        return SaResult.ok().setData(courseClassInfoVOS);
    }


    /**
     * 根据不同的角色 来根据课程信息来锁定班级信息 的筛选参数集合
     *
     * @return 班级信息
     */
    @PostMapping("/get_class_info_by_course_info_select_args")
    public SaResult getClassInfosByCoursesInfoSelectArgs(@RequestBody CourseInformationRO courseInformationRO) {
        AddEditCourseClassInfoSelectArgs addEditCourseClassInfoSelectArgs = null;
        if (Objects.isNull(courseInformationRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }
        addEditCourseClassInfoSelectArgs = courseInformationService.getClassInfosByCoursesInfoSelectArgs(courseInformationRO);
        // 转换并返回
        return SaResult.ok().setData(addEditCourseClassInfoSelectArgs);
    }

}

