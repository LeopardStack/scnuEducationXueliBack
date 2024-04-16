package com.scnujxjy.backendpoint.controller.courses_learning;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseNotificationsPO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseLearningCreateRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseNotificationsRO;
import com.scnujxjy.backendpoint.service.courses_learning.CourseNotificationsService;
import com.scnujxjy.backendpoint.util.ResultCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;

/**
 * <p>
 * 课程通知表 前端控制器
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-15
 */
@RestController
@RequestMapping("/course_notifications")
public class CourseNotificationsController {

    @Resource
    private CourseNotificationsService courseNotificationsService;

    /**
     * 创建通知 支持通知标题、通知内容 上传附件等设置
     *
     * @param courseNotificationsRO
     * @return
     */
    @PostMapping("/create_course_notification")
    @ApiOperation(value = "创建课程学习中的课程通知")
    @SaCheckPermission("课程学习.创建课程公告")
    public SaResult createCourseNotification(
            @ApiParam(value = "课程公告创建参数", required = true)
            @ModelAttribute CourseNotificationsRO courseNotificationsRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseNotificationsRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 创建课程公告
        return courseNotificationsService.createCourseNotification(courseNotificationsRO);

    }

    /**
     * 更新通知 支持通知标题、通知内容 上传附件等设置
     *
     * @param courseNotificationsRO
     * @return
     */
    @PostMapping("/edit_course_notification")
    @ApiOperation(value = "编辑课程学习中的课程通知")
    @SaCheckPermission("课程学习.创建课程公告")
    public SaResult editCourseNotification(
            @ApiParam(value = "课程公告编辑参数", required = true)
            @ModelAttribute CourseNotificationsRO courseNotificationsRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseNotificationsRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 编辑课程公告
        return courseNotificationsService.editCourseNotification(courseNotificationsRO);

    }


    /**
     * 删除通知 支持通知标题、通知内容 上传附件等设置
     *
     * @param courseNotificationId
     * @return
     */
    @DeleteMapping("/delete_course_notification")
    @ApiOperation(value = "删除课程学习中的课程通知")
    @SaCheckPermission("课程学习.创建课程公告")
    public SaResult deleteCourseNotification(
            @ApiParam(value = "课程公告删除参数", required = true)
            Long courseNotificationId) {

        // 删除课程公告
        return courseNotificationsService.deleteCourseNotification(courseNotificationId);

    }


    /**
     * 获取通知 支持通知标题、通知内容 上传附件等设置
     *
     * @param courseNotificationsRO
     * @return
     */
    @PostMapping("/get_course_notification_basic_info")
    @ApiOperation(value = "获取课程学习中的课程通知")
    public SaResult getCourseNotificationBasicInfo(
            @ApiParam(value = "课程公告获取参数", required = true)
            @RequestBody CourseNotificationsRO courseNotificationsRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseNotificationsRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 获取课程公告
        return courseNotificationsService.getCourseNotificationBasicInfo(courseNotificationsRO);

    }

    /**
     * 获取通知的详细信息 支持通知标题、通知内容 上传附件等设置
     *
     * @param courseNotificationsRO
     * @return
     */
    @PostMapping("/get_course_notification_detail_info")
    @ApiOperation(value = "获取课程学习中的课程通知")
    public SaResult getCourseNotificationDetailInfo(
            @ApiParam(value = "课程公告获取参数", required = true)
            @RequestBody CourseNotificationsRO courseNotificationsRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseNotificationsRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 获取课程公告
        return courseNotificationsService.getCourseNotificationDetailInfo(courseNotificationsRO);

    }
}

