package com.scnujxjy.backendpoint.controller.courses_learning;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CoursesLearningRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import com.scnujxjy.backendpoint.service.courses_learning.CoursesLearningService;
// 引入Swagger 2的注解
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * 课程学习主接口控制器
 */

@RestController
@RequestMapping("/courses_learning")
@Api(tags = "课程学习主接口") // Swagger 2的@Api注解
public class CoursesLearningController {

    @Resource
    private CoursesLearningService coursesLearningService;

    /**
     * 分页查询排课表信息
     *
     * @param courseScheduleROPageRO 分页参数
     * @return 排课表分页信息
     */
    @PostMapping("/page_query_courses_info")
    @ApiOperation(value = "分页查询课程信息", notes = "根据分页参数查询课程信息. 示例请求体: { 'grade': '2023', 'college': '计算机学院', ... }")
    public SaResult pageQueryCoursesInfo(
            @ApiParam(value = "分页查询参数", required = true)
            @RequestBody PageRO<CoursesLearningRO> courseScheduleROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(courseScheduleROPageRO.getEntity())) {
            courseScheduleROPageRO.setEntity(new CoursesLearningRO());
        }
        // 查询数据
        PageVO<CourseScheduleVO> courseScheduleVOPageVO = coursesLearningService.pageQueryCoursesInfo(courseScheduleROPageRO);
        // 数据校验
        if (Objects.isNull(courseScheduleVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseScheduleVOPageVO);
    }
}

