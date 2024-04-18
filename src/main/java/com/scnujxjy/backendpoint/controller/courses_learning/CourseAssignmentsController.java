package com.scnujxjy.backendpoint.controller.courses_learning;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseAssignmentRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseNotificationsRO;
import com.scnujxjy.backendpoint.service.courses_learning.CourseAssignmentsService;
import com.scnujxjy.backendpoint.util.ResultCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 课程作业表 前端控制器
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-15
 */
@RestController
@RequestMapping("/course_assignments")
public class CourseAssignmentsController {

    @Resource
    private CourseAssignmentsService courseAssignmentsService;

    /**
     * 创建课程作业
     *
     * @param courseAssignmentRO
     * @return
     */
    @PostMapping("/create_course_assignment")
    @ApiOperation(value = "创建课程学习中的课程作业")
    @SaCheckPermission("课程学习.创建课程作业")
    public SaResult createCourseAssignment(
            @ApiParam(value = "课程作业创建参数", required = true)
            @ModelAttribute CourseAssignmentRO courseAssignmentRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseAssignmentRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 创建课程公告
        return courseAssignmentsService.createCourseAssignment(courseAssignmentRO);

    }


    /**
     * 编辑课程作业
     *
     * @param courseAssignmentRO
     * @return
     */
    @PostMapping("/edit_course_assignment")
    @ApiOperation(value = "编辑课程学习中的课程作业")
    @SaCheckPermission("课程学习.创建课程作业")
    public SaResult editCourseAssignment(
            @ApiParam(value = "课程作业编辑参数", required = true)
            @ModelAttribute CourseAssignmentRO courseAssignmentRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseAssignmentRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 编辑课程作业
        return courseAssignmentsService.editCourseAssignment(courseAssignmentRO);

    }


    /**
     * 查询课程作业
     *
     * @param courseAssignmentRO
     * @return
     */
    @PostMapping("/query_course_assignment")
    @ApiOperation(value = "查询课程学习中的课程作业")
    public SaResult queryCourseAssignment(
            @ApiParam(value = "课程作业查询参数", required = true)
            @ModelAttribute CourseAssignmentRO courseAssignmentRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseAssignmentRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 查询课程作业
        return courseAssignmentsService.queryCourseAssignment(courseAssignmentRO);

    }

    /**
     * 查询某门课程的作业布置总数
     *
     * @param courseAssignmentRO
     * @return
     */
    @PostMapping("/query_course_assignment_total_info")
    @ApiOperation(value = "查询某门课程的作业布置总数")
    public SaResult queryCourseAssignmentTotalInfo(
            @ApiParam(value = "查询某门课程的作业布置总数参数", required = true)
            @ModelAttribute CourseAssignmentRO courseAssignmentRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseAssignmentRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 查询某门课程的作业布置总数
        return courseAssignmentsService.queryCourseAssignmentTotalInfo(courseAssignmentRO);

    }


    /**
     * 删除课程作业
     *
     * @param courseAssignmentId
     * @return
     */
    @DeleteMapping("/delete_course_assignment")
    @ApiOperation(value = "删除课程学习中的课程作业")
    @SaCheckPermission("课程学习.创建课程作业")
    public SaResult deleteCourseAssignment(
            @ApiParam(value = "课程作业删除参数", required = true)
            Long courseAssignmentId) {
        // 校验参数 traceId
        if (Objects.isNull(courseAssignmentId)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 创建课程公告
        return courseAssignmentsService.deleteCourseAssignment(courseAssignmentId);

    }
}

