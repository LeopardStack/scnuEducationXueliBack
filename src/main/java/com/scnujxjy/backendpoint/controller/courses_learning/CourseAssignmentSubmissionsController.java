package com.scnujxjy.backendpoint.controller.courses_learning;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseAssignmentRO;
import com.scnujxjy.backendpoint.service.courses_learning.CourseAssignmentSubmissionsService;
import com.scnujxjy.backendpoint.util.ResultCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 课程作业提交表 前端控制器
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-15
 */
@RestController
@RequestMapping("/course-assignment-submissions")
public class CourseAssignmentSubmissionsController {

    @Resource
    private CourseAssignmentSubmissionsService courseAssignmentSubmissionsService;

    /**
     * 上传课程作业
     *
     * @param courseAssignmentRO
     * @return
     */
    @PostMapping("/post_course_assignment")
    @ApiOperation(value = "上传课程学习中的课程作业")
    public SaResult createCourseAssignment(
            @ApiParam(value = "课程作业上传参数", required = true)
            @ModelAttribute CourseAssignmentRO courseAssignmentRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseAssignmentRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 上传课程作业
        return courseAssignmentSubmissionsService.postCourseAssignment(courseAssignmentRO);

    }

    /**
     * 查询课程作业提交情况
     *
     * @param courseAssignmentRO
     * @return
     */
    @PostMapping("/query_course_assignment_info")
    @ApiOperation(value = "查询课程作业提交情况")
    public SaResult queryCourseAssignmentSubmissionInfo(
            @ApiParam(value = "课程作业提交情况查询参数", required = true)
            @RequestBody CourseAssignmentRO courseAssignmentRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseAssignmentRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 查询课程作业提交情况
        return courseAssignmentSubmissionsService.queryCourseAssignmentSubmissionInfo(courseAssignmentRO);

    }

    /**
     * 学生查询自己的作业打分情况
     *
     * @param courseAssignmentRO
     * @return
     */
    @PostMapping("/query_student_course_assignment_marking")
    @ApiOperation(value = "学生查询自己的作业打分情况")
    public SaResult queryStudentCourseAssignmentMarking(
            @ApiParam(value = "学生查询自己的作业打分情况参数", required = true)
            @RequestBody CourseAssignmentRO courseAssignmentRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseAssignmentRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 学生查询自己的作业打分情况
        return courseAssignmentSubmissionsService.queryStudentCourseAssignmentMarking(courseAssignmentRO);

    }

    /**
     * 作业打分
     *
     * @param courseAssignmentRO
     * @return
     */
    @PostMapping("/course_assignment_marking")
    @ApiOperation(value = "作业打分")
    @SaCheckPermission("课程学习.作业打分")
    public SaResult courseAssignmentMarking(
            @ApiParam(value = "课程作业打分参数", required = true)
            CourseAssignmentRO courseAssignmentRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseAssignmentRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 作业打分
        return courseAssignmentSubmissionsService.courseAssignmentMarking(courseAssignmentRO);

    }
}

