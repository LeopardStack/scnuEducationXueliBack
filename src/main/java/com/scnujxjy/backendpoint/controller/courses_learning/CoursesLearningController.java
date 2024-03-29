package com.scnujxjy.backendpoint.controller.courses_learning;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.*;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.*;
import com.scnujxjy.backendpoint.service.courses_learning.CoursesLearningService;
import com.scnujxjy.backendpoint.util.ResultCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
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
     * 分页查询课程的筛选参数
     *
     * @param coursesLearningRO 参数
     * @return 分页查询课程的筛选参数项
     */
    @PostMapping("/page_query_courses_info_select_params")
    @ApiOperation(value = "分页查询课程的筛选参数项", notes = "根据分页参数查询课程信息. 示例请求体: { 'grade': '2023', 'college': '计算机学院', ... }")
    public SaResult pageQueryCoursesInfoParams(
            @ApiParam(value = "分页查询参数", required = true)
            @RequestBody CoursesLearningRO coursesLearningRO) {

        if (Objects.isNull(coursesLearningRO)) {
            coursesLearningRO = new CoursesLearningRO();
        }
        // 查询数据
        PageQueryCoursesInfoParamsVO pageQueryCoursesInfoParams = coursesLearningService.pageQueryCoursesInfoParams(coursesLearningRO);
        // 数据校验
        if (Objects.isNull(pageQueryCoursesInfoParams)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(pageQueryCoursesInfoParams);
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
            return SaResult.error("入参不能为空");
        }

        if(coursesLearningROPageRO.getClassIdentifier() == null){
            coursesLearningROPageRO.setClassIdentifier(new ArrayList<>());
        }



        // 查询数据
        return coursesLearningService.updateCourse(coursesLearningROPageRO);
    }


    /**
     * 获取课程的班级信息
     *
     * @param courseId
     * @return
     */
    @PostMapping("/get_course_class_infos")
    @ApiOperation(value = "获取课程所对应的班级信息")
    public SaResult getCourseClassInfos(
            @ApiParam(value = "获取班级信息参数", required = true)
            Long courseId) {

        // 查询数据
        return coursesLearningService.getCourseClassInfos(courseId);
    }


    /**
     * 获取课程白名单
     *
     * @param courseId
     * @return
     */
    @PostMapping("/get_course_living_whitelist")
    @ApiOperation(value = "获取课程白名单")
    public SaResult getCourseLivingWhiteList(
            @ApiParam(value = "课程主键ID", required = true)
            Long courseId) {

        // 查询数据
        return coursesLearningService.getCourseLivingWhiteList(courseId);
    }

    /**
     * 获取课程白名单是否与保利威的白名单相等
     *
     * @param courseId
     * @return
     */
    @PostMapping("/get_course_living_whitelist_equal_state")
    @ApiOperation(value = "获取课程白名单是否与保利威的白名单相等")
    public SaResult getCourseLivingWhiteListEqualState(
            @ApiParam(value = "课程主键ID", required = true)
            Long courseId) {

        // 查询数据
        return coursesLearningService.getCourseLivingWhiteListEqualState(courseId);
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
        return coursesLearningService.deleteCourse(courseId);
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
    @PostMapping("/get_course_students_info")
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
     * 管理员端获取学生群体 它是可以看到身份证号码的
     *
     * @param courseStudentSearchROPageRO
     * @return
     */
    @PostMapping("/get_course_students_info_manger")
    @ApiOperation(value = "管理员端获取学生群体 它是可以看到身份证号码的")
    public SaResult getCourseStudentsInfoForManager(
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
     * 获取课程的学生群体的筛选参数
     *
     * @param courseStudentSearchRO
     * @return
     */
    @PostMapping("/get_course_students_info_select_params")
    @ApiOperation(value = "获取课程的学生群体的筛选参数")
    public SaResult getCourseStudentsInfoSelectParams(
            @ApiParam(value = "课程筛选参数的条件", required = true)
            @RequestBody CourseStudentSearchRO courseStudentSearchRO) {

        // 查询数据
        CourseStudentInfoSearchParamsVO courseStudentInfoSearchParamsVO = coursesLearningService.getCourseStudentsInfoSelectParams(courseStudentSearchRO);

        return SaResult.ok().setData(courseStudentInfoSearchParamsVO);
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
    @PostMapping("/get_student_course_info")
    @ApiOperation(value = "获取学生的课程信息")
    public SaResult getStudentCoursesInfo(@RequestBody StudentsCoursesInfoSearchRO studentsCoursesInfoSearchRO) {

        List<CourseInfoVO> courseClassInfoVOS =  coursesLearningService.getCourseInfo(studentsCoursesInfoSearchRO);
        // 转换并返回
        return SaResult.data(courseClassInfoVOS);
    }

    /**
     * 获取学生的单门课程信息
     *
     * @return 课程信息
     */
    @PostMapping("/get_student_single_course_info")
    @ApiOperation(value = "获取学生的单门课程信息")
    public SaResult getStudentSingleCoursesInfo(@RequestBody StudentsCoursesInfoSearchRO studentsCoursesInfoSearchRO) {

        CourseInfoVO courseInfoVO =  coursesLearningService.getSingleCourseInfo(studentsCoursesInfoSearchRO);
        // 转换并返回
        return SaResult.data(courseInfoVO);
    }

    /**
     * 获取教师的课程信息
     *
     * @return 课程信息
     */
    @GetMapping("/get_teacher_course_info")
    @ApiOperation(value = "获取教师的课程信息")
    public SaResult getTeacherCoursesInfo() {
        String username = StpUtil.getLoginIdAsString();

        List<CourseInfoVO> courseClassInfoVOS =  coursesLearningService.getTeacherCoursesInfo(username);
        // 转换并返回
        return SaResult.data(courseClassInfoVOS);
    }

    /**
     * 获取老师的单门课程信息
     *
     * @return 课程信息
     */
    @PostMapping("/get_teacher_single_course_info")
    @ApiOperation(value = "获取老师的单门课程信息")
    public SaResult getTeacherSingleCoursesInfo(@RequestBody StudentsCoursesInfoSearchRO studentsCoursesInfoSearchRO) {

        CourseInfoVO courseInfoVO =  coursesLearningService.getTeacherSingleCoursesInfo(studentsCoursesInfoSearchRO);
        // 转换并返回
        return SaResult.data(courseInfoVO);
    }


    /**
     * 获取学生的指定的一门课的详细信息 即 节点信息
     *
     * @return 课程节点信息
     */
    @PostMapping("/get_student_section_info")
    public SaResult getStudentCourseSectionsInfo(@RequestBody CourseSectionRO courseSectionRO) {

        List<CourseSectionVO> courseSectionVOS =  coursesLearningService.getStudentCourseSectionsInfo(courseSectionRO);
        // 转换并返回
        return SaResult.data(courseSectionVOS);
    }


    /**
     * 获取学生的观看链接 假如这门课是直播课的话
     *
     * @return 课程节点信息
     */
    @GetMapping("/get_student_living_watch_url")
    public SaResult getStudentLivingWatchUrl(Long courseId) {

        // 转换并返回
        return coursesLearningService.getStudentLivingWatchUrl(courseId);
    }


    /**
     * 获取课程师资信息
     *
     * @return 课程节点信息
     */
    @GetMapping("/get_course_teacher_information")
    public SaResult getCourseTeacherInformation(Long courseId) {

        // 转换并返回
        return coursesLearningService.getCourseTeacherInformation(courseId);
    }


    /**
     * 获取课程排课明细
     *
     * @return 课程节点信息
     */
    @PostMapping("/get_course_schedule_info")
    public SaResult getCourseScheduleInformation(@RequestBody PageRO<CourseScheduleSearchRO> courseScheduleSearchRO) {
        if(courseScheduleSearchRO == null){
            return SaResult.error("入参不能为空 ").setCode(ResultCode.COMMON_OPERATION_ERROR.getCode());
        }
        if(courseScheduleSearchRO.getEntity() == null){
            courseScheduleSearchRO.setEntity(new CourseScheduleSearchRO());
        }
        // 转换并返回
        return coursesLearningService.getCourseScheduleInformation(courseScheduleSearchRO);
    }


    /**
     * 预览课程 即查看排课明细 仅仅目前针对直播
     *
     * @return 课程节点信息
     */
    @PostMapping("/view_course")
    public SaResult viewCourse(@RequestBody CourseScheduleSearchRO courseScheduleSearchRO) {
        if(courseScheduleSearchRO.getCourseId() == null){
            return SaResult.error("入参不能为空 ").setCode(ResultCode.COMMON_OPERATION_ERROR.getCode());
        }
        // 转换并返回
        return coursesLearningService.viewCourse(courseScheduleSearchRO);
    }


    /**
     * 获取 主讲教师、助教、课程类型
     *
     * @return 课程节点信息
     */
    @GetMapping("/get_course_create_params")
    public SaResult getCourseCreateParams() {

        // 转换并返回
        return coursesLearningService.getCourseCreateParams();
    }


    /**
     * 获取直播间信息
     *
     * @return 直播间基本信息
     */
    @PostMapping("/get_living_room_infos")
    public SaResult getLivingRoomInfos(Long channelId) {
        // 转换并返回
        return coursesLearningService.getLivingRoomInfos(channelId);
    }
}

