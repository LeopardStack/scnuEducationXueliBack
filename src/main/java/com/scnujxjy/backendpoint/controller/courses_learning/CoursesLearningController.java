package com.scnujxjy.backendpoint.controller.courses_learning;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.*;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseInfoVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseLearningStudentInfoVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseLearningVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseSectionVO;
import com.scnujxjy.backendpoint.service.courses_learning.CoursesLearningService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.dataMissError;
import static com.scnujxjy.backendpoint.exception.DataException.dataNotFoundError;

/**
 * 课程学习主接口控制器
 */

@RestController
@RequestMapping("/courses_learning")
@Api(tags = "课程学习主接口") // Swagger 2的@Api注解
@Slf4j
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
     * 可以同时支持 文件和表单参数的上传
     *
     * @param coursesLearningROPageRO
     * @return
     */
    @PostMapping("/create_course")
    @ApiOperation(value = "创建课程学习中的一门课")
    public SaResult createCourse(
            @ApiParam(value = "课程创建参数", required = true)
            @ModelAttribute CourseLearningCreateRO coursesLearningROPageRO) {
        // 校验参数 traceId
        if (Objects.isNull(coursesLearningROPageRO)) {
            throw dataMissError();
        }

        // 查询数据
        boolean createCourse = coursesLearningService.createCourse(coursesLearningROPageRO);
        // 数据校验
        if (createCourse) {
            return SaResult.ok("创建课程成功");
        }
        // 返回数据
        return SaResult.error("创建课程失败");
    }


    /**
     * 修改课程 主要是修改课程的主讲老师 上课时间 班级 助教老师
     *
     * @param coursesLearningROPageRO
     * @return
     */
    @PostMapping("/update_course")
    @ApiOperation(value = "修改课程学习中的一门课, 主要是修改课程的主讲老师 上课时间 班级 助教老师")
    public SaResult updateCourse(
            @ApiParam(value = "课程修改参数", required = true)
            @ModelAttribute CourseLearningCreateRO coursesLearningROPageRO) {
        // 校验参数 traceId
        if (Objects.isNull(coursesLearningROPageRO)) {
            throw dataMissError();
        }

        // 查询数据
        return coursesLearningService.updateCourse(coursesLearningROPageRO);
    }

    /**
     * 根据课程 ID 删除这门课
     *
     * @param courseId
     * @return
     */
    @PostMapping("/delete_course")
    @ApiOperation(value = "删除课程学习中的一门课")
    @SaCheckPermission("课程学习.删除课程")
    public SaResult deleteCourse(
            @ApiParam(value = "课程删除参数", required = true)
                    Long courseId) {
        // 校验参数
        if (Objects.isNull(courseId)) {
            throw dataMissError();
        }

        // 查询数据
        boolean delete = coursesLearningService.deleteCourse(courseId);
        if (delete) {
            return SaResult.ok("删除成功");
        }
        // 返回数据
        return SaResult.error("删除失败 ");
    }

    /**
     * 根据课程 ID 设置这门课是否有效
     *
     * @param courseId
     * @return
     */
    @PostMapping("/set_course_valid")
    @ApiOperation(value = "删除课程学习中的一门课")
    @SaCheckPermission("课程学习.设置课程是否有效")
    public SaResult setCourseInvalid(
            @ApiParam(value = "课程删除参数", required = true)
                    Long courseId) {
        // 校验参数
        if (Objects.isNull(courseId)) {
            throw dataMissError();
        }

        // 查询数据
        boolean delete = coursesLearningService.setCourseInvalid(courseId);
        if (delete) {
            return SaResult.ok("设置成功");
        }
        // 返回数据
        return SaResult.error("设置失败 ");
    }


    /**
     * 获取课程的学生群体
     *
     * @param courseStudentSearchROPageRO
     * @return
     */
    @GetMapping("/get_course_students_info")
    @ApiOperation(value = "查询一门课程中的学生群体信息")
    public SaResult getCourseStudentsInfo(
            @ApiParam(value = "课程学生查询参数", required = true)
            @RequestBody PageRO<CourseStudentSearchRO> courseStudentSearchROPageRO) {
        // 校验参数
        if (Objects.isNull(courseStudentSearchROPageRO)) {
            throw dataMissError();
        }

        if (Objects.isNull(courseStudentSearchROPageRO.getEntity())) {
            courseStudentSearchROPageRO.setEntity(new CourseStudentSearchRO());
        }

        // 查询数据
        PageVO<CourseLearningStudentInfoVO> courseLearningStudentInfoVOPageVO = coursesLearningService.getCourseStudentsInfo(courseStudentSearchROPageRO);

        return SaResult.data(courseLearningStudentInfoVOPageVO).setCode(200).setMsg("成功获取课程中的学生信息");
    }


    /**
     * 查询课程节点信息 比如 章节
     *
     * @param courseSectionRO
     * @return
     */
    @PostMapping("/get_course_section")
    @ApiOperation(value = "查询课程节点信息 比如 章节")
    public SaResult getCourseSectionInfo(
            @ApiParam(value = "课程修改参数", required = true)
            @RequestBody CourseSectionRO courseSectionRO) {


        // 查询数据
        return coursesLearningService.getCourseSectionInfo(courseSectionRO);
    }


    /**
     * 修改课程节点信息 比如 上传一个用户的视频 或者 修改直播节点的上下课时间 主讲老师信息等
     *
     * @param courseSectionRO
     * @return
     */
    @PostMapping("/update_course_section")
    @ApiOperation(value = "修改课程节点信息 比如 章节")
    public SaResult updateCourseSectionInfo(
            @ApiParam(value = "课程节点修改参数", required = true)
            @RequestBody CourseSectionRO courseSectionRO) {


        // 查询数据
        return coursesLearningService.updateCourseSectionInfo(courseSectionRO);
    }


    /**
     * 删除课程节点 连同资源一起删除 但是 课程本身的资源 并不会真的删除 比如 保利威的直播间
     * 除非删除课程
     *
     * @param courseSectionRO
     * @return
     */
    @PostMapping("/delete_course_section")
    @ApiOperation(value = "删除课程节点信息 比如 章节")
    public SaResult deleteCourseSectionInfo(
            @ApiParam(value = "课程节点删除参数", required = true)
            @RequestBody CourseSectionRO courseSectionRO) {


        // 删除数据
        return coursesLearningService.deleteCourseSectionInfo(courseSectionRO);
    }

    /**
     * 创建课程章节信息 不涉及节点内容 比如该节点内容为一个视频、一个课件 等等
     *
     * @param courseSectionRO
     * @return
     */
    @PostMapping("/create_course_section")
    @ApiOperation(value = "创建课程节点信息 比如 章节")
    public SaResult createCourseSectionInfo(
            @ApiParam(value = "课程节点创建参数", required = true)
            @RequestBody CourseSectionRO courseSectionRO) {

        log.info(StpUtil.getLoginIdAsString() + " 前端参数 " + courseSectionRO);

        // 查询数据
        return coursesLearningService.createCourseSectionInfo(courseSectionRO);
    }

    /**
     * 获取学生的课程信息
     *
     * @return 课程信息
     */
    @GetMapping("/get_student_course_info")
    public SaResult getStudentCoursesInfo() {
        String username = StpUtil.getLoginIdAsString();

        List<CourseInfoVO> courseClassInfoVOS =  coursesLearningService.getCourseInfo(username);
        // 转换并返回
        return SaResult.data(courseClassInfoVOS);
    }

    /**
     * 获取教师的课程信息
     *
     * @return 课程信息
     */
    @GetMapping("/get_teacher_course_info")
    public SaResult getTeacherCoursesInfo() {
        String username = StpUtil.getLoginIdAsString();

        List<CourseInfoVO> courseClassInfoVOS =  coursesLearningService.getTeacherCoursesInfo(username);
        // 转换并返回
        return SaResult.data(courseClassInfoVOS);
    }


    /**
     * 获取学生的指定的一门课的详细信息 即 节点信息
     *
     * @return 课程节点信息
     */
    @GetMapping("/get_student_section_info")
    public SaResult getStudentCourseSectionsInfo(@RequestBody CourseSectionRO courseSectionRO) {

        List<CourseSectionVO> courseSectionVOS =  coursesLearningService.getStudentCourseSectionsInfo(courseSectionRO);
        // 转换并返回
        return SaResult.data(courseSectionVOS);
    }
}

