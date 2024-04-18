package com.scnujxjy.backendpoint.controller.courses_learning;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseMaterialsPostRO;
import com.scnujxjy.backendpoint.service.courses_learning.CourseMaterialsService;
import com.scnujxjy.backendpoint.util.ResultCode;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * <p>
 * 课程资料表 前端控制器
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-04-15
 */
@RestController
@RequestMapping("/course-materials")
public class CourseMaterialsController {

    @Resource
    private CourseMaterialsService courseMaterialsService;

    /**
     * 添加课程资料
     *
     * @param courseMaterialsPostRO
     * @return
     */
    @PostMapping("/post_course_materials")
    @ApiOperation(value = "添加课程资料")
    @SaCheckPermission("课程学习.课程资料")
    public SaResult postCourseMaterials(
            @ApiParam(value = "添加课程资料参数", required = true)
            @ModelAttribute CourseMaterialsPostRO courseMaterialsPostRO) {
        // 校验参数 traceId
        if (Objects.isNull(courseMaterialsPostRO)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 添加课程资料
        return courseMaterialsService.postCourseMaterials(courseMaterialsPostRO);

    }

    /**
     * 删除课程资料
     *
     * @param courseMaterialId
     * @return
     */
    @DeleteMapping("/delete_course_materials")
    @ApiOperation(value = "删除课程资料")
    @SaCheckPermission("课程学习.课程资料")
    public SaResult deleteCourseMaterial(
            @ApiParam(value = "删除课程资料参数", required = true)
            Long courseMaterialId) {
        // 校验参数 traceId
        if (Objects.isNull(courseMaterialId)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 删除课程资料
        return courseMaterialsService.deleteCourseMaterial(courseMaterialId);

    }

    /**
     * 查询课程资料
     *
     * @param courseId
     * @return
     */
    @PostMapping("/get_course_materials")
    @ApiOperation(value = "查询课程资料")
    public SaResult getCourseMaterials(
            @ApiParam(value = "查询课程资料参数", required = true)
            Long courseId) {
        // 校验参数 traceId
        if (Objects.isNull(courseId)) {
            return ResultCode.PARAM_IS_NULL.generateErrorResultInfo();
        }

        // 添加课程资料
        return courseMaterialsService.getCourseMaterials(courseId);

    }
}

