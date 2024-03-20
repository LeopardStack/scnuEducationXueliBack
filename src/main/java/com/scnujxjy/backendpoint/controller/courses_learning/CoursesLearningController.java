package com.scnujxjy.backendpoint.controller.courses_learning;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseLearningCreateRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CoursesLearningRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseLearningVO;
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
     * 分页查询课程信息
     *
     * @param coursesLearningROPageRO 分页参数
     * @return 课程分页信息
     */
    @PostMapping("/page_query_courses_info")
    @ApiOperation(value = "分页查询课程信息", notes = "根据分页参数查询课程信息. 示例请求体: { 'grade': '2023', 'college': '计算机学院', ... }")
    public SaResult pageQueryCoursesInfo(
            @ApiParam(value = "分页查询参数", required = true)
            @RequestBody PageRO<CoursesLearningRO> coursesLearningROPageRO) {
        // 校验参数
        if (Objects.isNull(coursesLearningROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(coursesLearningROPageRO.getEntity())) {
            coursesLearningROPageRO.setEntity(new CoursesLearningRO());
        }
        // 查询数据
        PageVO<CourseLearningVO> courseLearningVOPageVO = coursesLearningService.pageQueryCoursesInfo(coursesLearningROPageRO);
        // 数据校验
        if (Objects.isNull(courseLearningVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseLearningVOPageVO);
    }


    /**
     * 分页查询课程信息，不调用 redis 直接访问数据库 超级慢
     *
     * @param coursesLearningROPageRO 分页参数
     * @return 课程分页信息
     */
    @PostMapping("/page_query_courses_info1")
    @ApiOperation(value = "分页查询课程信息", notes = "根据分页参数查询课程信息. 示例请求体: { 'grade': '2023', 'college': '计算机学院', ... }")
    public SaResult pageQueryCoursesInfo1(
            @ApiParam(value = "分页查询参数", required = true)
            @RequestBody PageRO<CoursesLearningRO> coursesLearningROPageRO) {
        // 校验参数
        if (Objects.isNull(coursesLearningROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(coursesLearningROPageRO.getEntity())) {
            coursesLearningROPageRO.setEntity(new CoursesLearningRO());
        }
        // 查询数据
        PageVO<CourseLearningVO> courseLearningVOPageVO = coursesLearningService.pageQueryCoursesInfo1(coursesLearningROPageRO);
        // 数据校验
        if (Objects.isNull(courseLearningVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseLearningVOPageVO);
    }


    /**
     * 创建课程 此时可以没有任何信息 比如 直播、点播 的 Section
     * @param coursesLearningROPageRO
     * @return
     */
    @PostMapping("/create_course")
    @ApiOperation(value = "创建课程学习中的一门课")
    public SaResult createCourse(
            @ApiParam(value = "课程创建参数", required = true)
            @RequestBody CourseLearningCreateRO coursesLearningROPageRO) {
        // 校验参数
        if (Objects.isNull(coursesLearningROPageRO)) {
            throw dataMissError();
        }

        // 查询数据
        PageVO<CourseLearningVO> courseLearningVOPageVO = coursesLearningService.createCourse(coursesLearningROPageRO);
        // 数据校验
        if (Objects.isNull(courseLearningVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseLearningVOPageVO);
    }
}

