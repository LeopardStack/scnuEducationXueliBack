package com.scnujxjy.backendpoint.controller.teaching_process;


import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseExtraInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.exam.ExamFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.*;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import com.scnujxjy.backendpoint.util.MessageSender;
import com.scnujxjy.backendpoint.util.filter.*;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;
import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 排课管理，比如直播、点播安排
 *
 * @author leopard
 * @since 2023-08-18
 */
@RestController
@RequestMapping("/course-schedule")
@Slf4j
public class CourseScheduleController {

    @Resource
    private CourseScheduleService courseScheduleService;

    @Resource
    private CollegeAdminFilter collegeAdminFilter;

    @Resource
    private TeachingPointFilter teachingPointFilter;

    @Resource
    private TeacherFilter teacherFilter;

    @Resource
    private StudentFilter studentFilter;

    @Resource
    private ManagerFilter managerFilter;


    @Resource
    private MinioService minioService;

    @Resource
    private MessageSender messageSender;

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Value("${minio.importBucketName}")
    private String importBucketName;

    /**
     * 根据id查询排课表信息
     *
     * @param id 主键id
     * @return 排课表详细信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询数据
        CourseScheduleVO courseScheduleVO = courseScheduleService.detailById(id);
        // 参数校验
        if (Objects.isNull(courseScheduleVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseScheduleVO);
    }

    /**
     * 分页查询排课表信息
     *
     * @param courseScheduleROPageRO 分页参数
     * @return 排课表分页信息
     */
    @PostMapping("/page")
    SaResult pageQueryCourseSchedule(@RequestBody PageRO<CourseScheduleRO> courseScheduleROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(courseScheduleROPageRO.getEntity())) {
            courseScheduleROPageRO.setEntity(new CourseScheduleRO());
        }
        // 查询数据
        PageVO<CourseScheduleVO> courseScheduleVOPageVO = courseScheduleService.pageQueryCourseSchedule(courseScheduleROPageRO);
        // 数据校验
        if (Objects.isNull(courseScheduleVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseScheduleVOPageVO);
    }

    /**
     * 获取二级学院管理员所有的排课表信息 不限制日期
     *
     * @param courseScheduleROPageRO
     * @return
     */
    @PostMapping("/allPageByCollegeAdmin")
    public SaResult allPageQueryCourseScheduleByCollegeAdmin(@RequestBody PageRO<CourseScheduleRO> courseScheduleROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(courseScheduleROPageRO.getEntity())) {
            courseScheduleROPageRO.setEntity(new CourseScheduleRO());
        }
        // 查询数据
        PageVO<TeacherCourseScheduleVO> courseScheduleVOPageVO = courseScheduleService.
                allPageQueryCourseScheduleFilter(courseScheduleROPageRO, collegeAdminFilter);
        // 数据校验
        if (Objects.isNull(courseScheduleVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseScheduleVOPageVO);
    }

    /**
     * 获取教师所有的排课表信息 不限制日期
     *
     * @param courseScheduleROPageRO
     * @return
     */
    @PostMapping("/allPageByTeacher")
    public SaResult allPageQueryCourseScheduleByTeacher(@RequestBody PageRO<CourseScheduleRO> courseScheduleROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(courseScheduleROPageRO.getEntity())) {
            courseScheduleROPageRO.setEntity(new CourseScheduleRO());
        }
        // 查询数据
        PageVO<TeacherSchedulesVO> courseScheduleVOPageVO = courseScheduleService.
                getTeacherCourschedules(courseScheduleROPageRO, teacherFilter);
        // 数据校验
        if (Objects.isNull(courseScheduleVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseScheduleVOPageVO);
    }


    /**
     * 获取教师所有的排课表课程信息
     *
     * @param courseScheduleROPageRO
     * @return
     */
    @PostMapping("/get-teacher-courses")
    public SaResult getTeacherCourses(@RequestBody PageRO<CourseScheduleRO> courseScheduleROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(courseScheduleROPageRO.getEntity())) {
            courseScheduleROPageRO.setEntity(new CourseScheduleRO());
        }
        // 查询数据
        PageVO courseScheduleVOPageVO = teacherFilter.getTeacherCourses(courseScheduleROPageRO);
        // 数据校验
        if (Objects.isNull(courseScheduleVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseScheduleVOPageVO);
    }

    /**
     * 获取学生所有的排课表信息 不限制日期 除非她/他本人提供筛选条件
     *
     * @param courseScheduleROPageRO 排课表信息筛选条件
     * @return
     */
    @PostMapping("/allPageByStudent")
    public SaResult allPageQueryCourseScheduleByStudent(@RequestBody PageRO<CourseScheduleRO> courseScheduleROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(courseScheduleROPageRO.getEntity())) {
            courseScheduleROPageRO.setEntity(new CourseScheduleRO());
        }
        // 查询数据
        PageVO<TeacherCourseScheduleVO> courseScheduleVOPageVO = courseScheduleService.
                allPageQueryCourseScheduleFilter(courseScheduleROPageRO, studentFilter);
        // 数据校验
        if (Objects.isNull(courseScheduleVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseScheduleVOPageVO);
    }


    /**
     * 获取学生所有的课程信息 不限制日期 除非她/他本人提供筛选条件
     *
     * @param courseScheduleFilterROPageRO 排课表信息筛选条件
     * @return
     */
    @PostMapping("/get-student-courses")
    public SaResult getStudentCourses(@RequestBody PageRO<ExamFilterRO> courseScheduleFilterROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleFilterROPageRO)) {
            return SaResult.error("未提供筛选参数，查询课程信息失败");
        }
        if (Objects.isNull(courseScheduleFilterROPageRO.getEntity())) {
            courseScheduleFilterROPageRO.setEntity(new ExamFilterRO());
        }
        // 查询数据
        FilterDataVO filterDataVO = courseScheduleService.allPageQueryScheduleCoursesInformationFilter(courseScheduleFilterROPageRO, studentFilter);


        // 返回数据
        return SaResult.data(filterDataVO);
    }

    /**
     * 获取学生本学期在学课程
     *
     * @return
     */
    @PostMapping("/all-learning-courses-student")
    public SaResult allLearningCoursesByStudent() {

        log.info("学生访问自己的在学课程 " + StpUtil.getLoginId());
        // 返回数据
        return SaResult.data(null);
    }

    /**
     * 获取学生全部课程
     *
     * @return
     */
    @PostMapping("/allCoursesByStudent")
    public SaResult allCoursesByStudent(@RequestBody PageRO<CourseScheduleRO> courseScheduleROPageRO) {

        log.info("学生访问自己的全部课程 " + StpUtil.getLoginId());
        // 返回数据
        return SaResult.data(null);
    }

    /**
     * 获取所有的排课表信息 不限制日期
     *
     * @param courseScheduleROPageRO
     * @return
     */
    @PostMapping("/allPage")
    public SaResult allPageQueryCourseSchedule(@RequestBody PageRO<CourseScheduleRO> courseScheduleROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(courseScheduleROPageRO.getEntity())) {
            courseScheduleROPageRO.setEntity(new CourseScheduleRO());
        }
        // 查询数据
        PageVO<TeacherCourseScheduleVO> courseScheduleVOPageVO = courseScheduleService.
                allPageQueryCourseScheduleService(courseScheduleROPageRO);
        // 数据校验
        if (Objects.isNull(courseScheduleVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseScheduleVOPageVO);
    }

    /**
     * 根据id更新排课表信息
     *
     * @param courseScheduleRO 更新的排课表信息
     * @return 更新后的排课表信息
     */
    @PutMapping("/edit")
    SaResult editById(@RequestBody CourseScheduleRO courseScheduleRO) {
        // 参数校验
        if (Objects.isNull(courseScheduleRO) || Objects.isNull(courseScheduleRO.getId())) {
            throw dataMissError();
        }

        // 更新数据
        CourseScheduleVO courseScheduleVO = courseScheduleService.editById(courseScheduleRO);
        // 更新校验
        if (Objects.isNull(courseScheduleVO)) {
            throw dataUpdateError();
        }
        // 返回数据
        return SaResult.data(courseScheduleVO);
    }

    /**
     * 根据id删除排课信息
     *
     * @param id 主键id
     * @return 删除数量
     */
    @DeleteMapping("/delete")
    SaResult deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 删除
        int count = courseScheduleService.deleteById(id);
        // 删除校验
        if (count <= 0) {
            throw dataDeleteError();
        }
        // 返回删除数量
        return SaResult.data(count);
    }

    /**
     * 获取排课表筛选条件
     *
     * @return 排课表筛选条件
     */
    @GetMapping("/select_course_schedules_args")
    public SaResult getSelectCourseScheduleArgs() {
        HashMap<String, List<String>> selectArgs = courseScheduleService.getSelectCourseScheduleArgs();
        if (Objects.isNull(selectArgs) || selectArgs.isEmpty()) {
            throw dataNotFoundError();
        }
        return SaResult.data(selectArgs);
    }


    /**
     * 获取不同角色权限范围内的考试信息
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    @PostMapping("/select_schedule_courses")
    public SaResult getScheduleCoursesInformation(@RequestBody PageRO<ExamFilterRO> courseScheduleFilterROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleFilterROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(courseScheduleFilterROPageRO.getEntity())) {
            courseScheduleFilterROPageRO.setEntity(new ExamFilterRO());
        }

        List<String> roleList = StpUtil.getRoleList();
        PageVO<FilterDataVO> filterDataVO = null;
        // 获取访问者 ID
        if (roleList.isEmpty()) {
            throw dataNotFoundError();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 查询二级学院管理员权限范围内的考试信息
                FilterDataVO scheduleCoursesFilterDataVO = courseScheduleService.filterCoursesInformationExams(courseScheduleFilterROPageRO, collegeAdminFilter);

                // 创建并返回分页信息
                filterDataVO = new PageVO<>(scheduleCoursesFilterDataVO.getData());
                filterDataVO.setTotal(scheduleCoursesFilterDataVO.getTotal());
                filterDataVO.setCurrent(courseScheduleFilterROPageRO.getPageNumber());
                filterDataVO.setSize(courseScheduleFilterROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) scheduleCoursesFilterDataVO.getData().size()
                        / courseScheduleFilterROPageRO.getPageSize()));

                // 数据校验
                if (Objects.isNull(filterDataVO)) {
                    throw dataNotFoundError();
                }
            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                // 查询继续教育管理员权限范围内的考试信息
                FilterDataVO scheduleCoursesFilterDataVO = courseScheduleService.filterCoursesInformationExams(courseScheduleFilterROPageRO, managerFilter);

                // 创建并返回分页信息
                filterDataVO = new PageVO<>(scheduleCoursesFilterDataVO.getData());
                filterDataVO.setTotal(scheduleCoursesFilterDataVO.getTotal());
                filterDataVO.setCurrent(courseScheduleFilterROPageRO.getPageNumber());
                filterDataVO.setSize(courseScheduleFilterROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) scheduleCoursesFilterDataVO.getData().size()
                        / courseScheduleFilterROPageRO.getPageSize()));

                // 数据校验
                if (Objects.isNull(filterDataVO)) {
                    throw dataNotFoundError();
                }
            }

        }
        return SaResult.data(filterDataVO);
    }


    /**
     * 获取排课表课程管理筛选条件
     *
     * @return 排课表筛选条件
     */
    @PostMapping("/select_schedule_courses_manage_args")
    public SaResult getSelectScheduleCourseManageArgs(@RequestBody PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        List<String> roleList = StpUtil.getRoleList();
        ScheduleCourseManagetArgs selectArgs = null;
        if (roleList.isEmpty()) {
            return SaResult.error("角色信息为空，不允许获取筛选数据 " + StpUtil.getLoginIdAsString()).setCode(2001);
        }else{
            if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                selectArgs = courseScheduleService.getSelectScheduleCourseManageArgs(courseScheduleFilterROPageRO, managerFilter);
            } else if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                selectArgs = courseScheduleService.getSelectScheduleCourseManageArgs(courseScheduleFilterROPageRO, collegeAdminFilter);
            }else if(roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                selectArgs = courseScheduleService.getSelectScheduleCourseManageArgs(courseScheduleFilterROPageRO, teachingPointFilter);
            }
        }
        if (Objects.isNull(selectArgs)) {
            return SaResult.error("没有找到任何筛选数据 " + StpUtil.getLoginIdAsString()).setCode(2001);
        }
        return SaResult.data(selectArgs);
    }


    /**
     * 获取排课表课程筛选条件
     *
     * @param courseScheduleFilterRO 排课表课程筛选条件的其他筛选条件 便于更新筛选框的筛选项
     * @return 排课表筛选条件
     */
    @PostMapping("/select_courses_args")
    public SaResult getSelectScheduleCourseInformationArgs(@RequestBody CourseScheduleFilterRO courseScheduleFilterRO) {
        scnuXueliTools.convertEmptyStringsToNull(courseScheduleFilterRO);
        List<String> roleList = StpUtil.getRoleList();
        PageVO<FilterDataVO> filterDataVO = null;

        ScheduleCourseInformationSelectArgs scheduleCourseInformationSelectArgs = new ScheduleCourseInformationSelectArgs();
        // 获取访问者 ID
        if (roleList.isEmpty()) {
            throw dataNotFoundError();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                scheduleCourseInformationSelectArgs = courseScheduleService.getCoursesArgs(courseScheduleFilterRO, collegeAdminFilter);
            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                scheduleCourseInformationSelectArgs = courseScheduleService.getCoursesArgs(courseScheduleFilterRO, managerFilter);
            }
        }

        return SaResult.data(scheduleCourseInformationSelectArgs);
    }


    /**
     * 获取不同角色权限范围内的排课表明细信息
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    @PostMapping("/select_schedules")
    public SaResult getScheduleInformation(@RequestBody PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleFilterROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(courseScheduleFilterROPageRO.getEntity())) {
            courseScheduleFilterROPageRO.setEntity(new CourseScheduleFilterRO());
        }

        List<String> roleList = StpUtil.getRoleList();
        PageVO<FilterDataVO> filterDataVO = null;
        // 获取访问者 ID
        if (roleList.isEmpty()) {
            throw dataNotFoundError();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                // 查询二级学院管理员权限范围内的教学计划
                FilterDataVO schedulesFilterDataVO = courseScheduleService.allPageQuerySchedulesInformationFilter(courseScheduleFilterROPageRO, collegeAdminFilter);

                // 创建并返回分页信息
                filterDataVO = new PageVO<>(schedulesFilterDataVO.getData());
                filterDataVO.setTotal(schedulesFilterDataVO.getTotal());
                filterDataVO.setCurrent(courseScheduleFilterROPageRO.getPageNumber());
                filterDataVO.setSize(courseScheduleFilterROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) schedulesFilterDataVO.getData().size()
                        / courseScheduleFilterROPageRO.getPageSize()));

                // 数据校验
                if (Objects.isNull(filterDataVO)) {
                    throw dataNotFoundError();
                }
            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                // 查询继续教育管理员权限范围内的教学计划
                FilterDataVO schedulesFilterDataVO = courseScheduleService.allPageQuerySchedulesInformationFilter(courseScheduleFilterROPageRO, managerFilter);

                // 创建并返回分页信息
                filterDataVO = new PageVO<>(schedulesFilterDataVO.getData());
                filterDataVO.setTotal(schedulesFilterDataVO.getTotal());
                filterDataVO.setCurrent(courseScheduleFilterROPageRO.getPageNumber());
                filterDataVO.setSize(courseScheduleFilterROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) schedulesFilterDataVO.getData().size()
                        / courseScheduleFilterROPageRO.getPageSize()));

                // 数据校验
                if (Objects.isNull(filterDataVO)) {
                    throw dataNotFoundError();
                }
            } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                // 查询继续教育管理员权限范围内的教学计划
                FilterDataVO schedulesFilterDataVO = courseScheduleService.allPageQuerySchedulesInformationFilter(courseScheduleFilterROPageRO, teachingPointFilter);
                if (Objects.isNull(schedulesFilterDataVO)) {
                    throw dataNotFoundError();
                }
                // 创建并返回分页信息
                filterDataVO = new PageVO<>(schedulesFilterDataVO.getData());
                filterDataVO.setTotal(schedulesFilterDataVO.getTotal());
                filterDataVO.setCurrent(courseScheduleFilterROPageRO.getPageNumber());
                filterDataVO.setSize(courseScheduleFilterROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) schedulesFilterDataVO.getData().size()
                        / courseScheduleFilterROPageRO.getPageSize()));

                // 数据校验
                if (Objects.isNull(filterDataVO)) {
                    throw dataNotFoundError();
                }
            }
        }
        return SaResult.data(filterDataVO);
    }


    /**
     * 获取排课表明细筛选条件
     *
     * @return 排课表筛选条件
     */
    @GetMapping("/select_schedule_courses_args")
    public SaResult getSelectScheduleCourseDetailArgs() {
        List<String> roleList = StpUtil.getRoleList();
        PageVO<FilterDataVO> filterDataVO = null;

        ScheduleCourseInformationSelectArgs scheduleCourseInformationSelectArgs = new ScheduleCourseInformationSelectArgs();
        // 获取访问者 ID
        if (roleList.isEmpty()) {
            return SaResult.error("用户角色为空").setCode(2001);
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                scheduleCourseInformationSelectArgs = courseScheduleService.getSelectScheduleCourseInformationArgs(collegeAdminFilter);
            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                scheduleCourseInformationSelectArgs = courseScheduleService.getSelectScheduleCourseInformationArgs(managerFilter);
            } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                scheduleCourseInformationSelectArgs = courseScheduleService.getSelectScheduleCourseInformationArgs(managerFilter);
            }
        }

        return SaResult.data(scheduleCourseInformationSelectArgs);
    }


    /**
     * 获取不同角色权限范围内的排课表课程信息
     * 即 以教师 、课程、 合班的 Set 作为单位，也就是批次
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    @PostMapping("/get_schedule_courses")
    public SaResult getScheduleCourses(@RequestBody PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleFilterROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(courseScheduleFilterROPageRO.getEntity())) {
            courseScheduleFilterROPageRO.setEntity(new CourseScheduleFilterRO());
        }

        List<String> roleList = StpUtil.getRoleList();
        PageVO<FilterDataVO> filterDataVO = null;
        // 获取访问者 ID
        if (roleList.isEmpty()) {
            return SaResult.error("用户角色信息缺失 ，获取排课表课程信息失败 " + StpUtil.getLoginId()).setCode(2000);
        } else {
            try {
//                if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
//                    // 查询二级学院管理员权限范围内的排课表课程信息
//                    FilterDataVO schedulesFilterDataVO = courseScheduleService.getScheduleCourses(courseScheduleFilterROPageRO, collegeAdminFilter);
//
//                    // 创建并返回分页信息
//                    filterDataVO = new PageVO<>(schedulesFilterDataVO.getData());
//                    filterDataVO.setTotal(schedulesFilterDataVO.getTotal());
//                    filterDataVO.setCurrent(courseScheduleFilterROPageRO.getPageNumber());
//                    filterDataVO.setSize(courseScheduleFilterROPageRO.getPageSize());
//                    filterDataVO.setPages((long) Math.ceil((double) schedulesFilterDataVO.getData().size()
//                            / courseScheduleFilterROPageRO.getPageSize()));
//
//                } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
//                    // 查询继续教育管理员权限范围内的排课表课程信息
//                    FilterDataVO schedulesFilterDataVO = courseScheduleService.getScheduleCourses(courseScheduleFilterROPageRO, managerFilter);
//
//                    // 创建并返回分页信息
//                    filterDataVO = new PageVO<>(schedulesFilterDataVO.getData());
//                    filterDataVO.setTotal(schedulesFilterDataVO.getTotal());
//                    filterDataVO.setCurrent(courseScheduleFilterROPageRO.getPageNumber());
//                    filterDataVO.setSize(courseScheduleFilterROPageRO.getPageSize());
//                    filterDataVO.setPages((long) Math.ceil((double) schedulesFilterDataVO.getData().size()
//                            / courseScheduleFilterROPageRO.getPageSize()));
//
//                }
                FilterDataVO schedulesFilterDataVO = courseScheduleService.getScheduleCourses(courseScheduleFilterROPageRO, managerFilter);

                // 创建并返回分页信息
                filterDataVO = new PageVO<>(schedulesFilterDataVO.getData());
                filterDataVO.setTotal(schedulesFilterDataVO.getTotal());
                filterDataVO.setCurrent(courseScheduleFilterROPageRO.getPageNumber());
                filterDataVO.setSize(courseScheduleFilterROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) schedulesFilterDataVO.getData().size()
                        / courseScheduleFilterROPageRO.getPageSize()));
            }catch (Exception e){
                return SaResult.error("获取排课表信息失败 " + e.toString()).setCode(2000);
            }

        }
        return SaResult.data(filterDataVO);
    }


    /**
     * 修改排课表相关信息
     *
     * @param courseScheduleUpdateROPageRO
     * @return
     */
    @PostMapping("/update_schedule_courses")
    @SaCheckPermission("修改排课表上课时间和上课教师")
    public SaResult updateScheduleCoursesInformation(@RequestBody CourseScheduleUpdateRO courseScheduleUpdateROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleUpdateROPageRO)) {
            return SaResult.error("未做任何修改");
        }
        try {
            courseScheduleService.updateScheduleInfor(courseScheduleUpdateROPageRO);
        } catch (Exception e) {
            // 2000 的值 说明修改失败
            return SaResult.error(e.toString()).setCode(2000);
        }

        log.info("更新的排课表信息为 " + courseScheduleUpdateROPageRO);
        return SaResult.ok();
    }

    @PostMapping("/update_single_schedule_courses")
    @SaCheckPermission("修改排课表上课时间和上课教师")
    public SaResult updateSingleScheduleCoursesInformation(@RequestBody CourseScheduleUpdateRO courseScheduleUpdateROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleUpdateROPageRO)) {
            return SaResult.error("未做任何修改");
        }
        try {
            courseScheduleService.updateSingleScheduleInfor(courseScheduleUpdateROPageRO);
        } catch (Exception e) {
            // 2000 的值 说明修改失败
            return SaResult.error(e.toString()).setCode(2000);
        }

        log.info("更新的排课表信息为 " + courseScheduleUpdateROPageRO);
        return SaResult.ok();
    }


    /**
     * 删除排课表相关信息
     *
     * @return
     */
    @DeleteMapping("/delete_schedule_courses")
    @SaCheckPermission("删除排课表")
    public SaResult deleteScheduleCoursesInformation(@RequestParam("scheduldId") Long scheduldId) {
        // 校验参数
        if (Objects.isNull(scheduldId)) {
            return SaResult.error("scheduldId 不能为空");
        }

        try {
            // 这里调用删除方法，不是更新方法
            CourseSchedulePO courseSchedulePO = courseScheduleService.getBaseMapper().selectById(scheduldId);
            if (courseSchedulePO == null) {
                return SaResult.error("删除失败").setCode(2000);
            } else {
                // 删除排课要检查是否是有直播间 如果有 不让删除
                if (StrUtil.isNotBlank(courseSchedulePO.getOnlinePlatform())) {
                    return SaResult.error("删除失败，该排课记录已存在上课记录").setCode(2000);
                } else {
                    int i = courseScheduleService.getBaseMapper().deleteById(courseSchedulePO.getId());
                    if (i > 0) {
                        return SaResult.ok();
                    } else {
                        log.error("删除排课表记录失败 数据库执行错误 " + scheduldId + " db result " + i);
                        return SaResult.error("删除失败，请联系管理员").setCode(2000);
                    }
                }
            }

        } catch (Exception e) {
            log.error("删除排课表记录失败 " + scheduldId + " " + e.toString());
            return SaResult.error("删除失败，请联系管理员").setCode(2000);
        }
    }


    /**
     * 获取直播间信息
     *
     * @return
     */
    @GetMapping("/get_living_root_info")
    public SaResult getLivingRoomInfo(@RequestParam("roomId") String roomId) {
        // 校验参数
        if (Objects.isNull(roomId)) {
            return SaResult.error("roomId 不能为空");
        }


        try {
            // 这里调用删除方法，不是更新方法
            ChannelResponseBO livingRoomInformation = courseScheduleService.getLivingRoomInformation(roomId);
            return SaResult.data(livingRoomInformation);
        } catch (Exception e) {
            return SaResult.error(e.toString());
        }
    }

    /**
     * 修改教学班的课程简介信息
     *
     * @param courseExtraInformationRO
     * @return
     */
    @PostMapping("/update_schedule_courses_extra_info")
    @SaCheckPermission("修改直播间课程信息")
    public SaResult updateScheduleCoursesExtraInformation(@RequestBody CourseExtraInformationRO courseExtraInformationRO) {
        // 校验参数
        if (Objects.isNull(courseExtraInformationRO)) {
            return SaResult.error("未做任何修改");
        }
        try {
            courseScheduleService.updateScheduleExtraInfo(courseExtraInformationRO);
        } catch (Exception e) {
            return SaResult.error(e.toString());
        }

        log.info("更新的教学班课程简介信息为 " + courseExtraInformationRO);
        return SaResult.ok();
    }

    /**
     * 获取教学班的课程简介信息
     *
     * @param courseExtraInformationRO
     * @return
     */
    @PostMapping("/get_schedule_courses_extra_info")
    public SaResult getScheduleCoursesExtraInformation(@RequestBody CourseExtraInformationRO courseExtraInformationRO) {
        // 校验参数
        if (Objects.isNull(courseExtraInformationRO)) {
            return SaResult.error("未做任何修改");
        }
        CourseExtraInformationPO courseExtraInformationPO = new CourseExtraInformationPO();
        try {
            courseExtraInformationPO = courseScheduleService.getScheduleExtraInfo(courseExtraInformationRO);
        } catch (Exception e) {
            return SaResult.error(e.toString());
        }

        return SaResult.data(courseExtraInformationPO);
    }

    /**
     * 管理员批量上传排课表
     *
     * @param scheduleList
     * @return
     */
    @PostMapping("schedule_list_upload")
    @SaCheckPermission("排课表导入")
    public SaResult handleFileUpload(@RequestParam("scheduleList") MultipartFile scheduleList) {
        try {

            // 使用 try-with-resources 语句来确保 InputStream 在使用后被关闭
            try (InputStream inputStream = scheduleList.getInputStream()) {
                LocalDateTime now = LocalDateTime.now();

                String originalFilename = scheduleList.getOriginalFilename();

                int lastDotPosition = originalFilename.lastIndexOf('.');
                String baseName = (lastDotPosition >= 0) ? originalFilename.substring(0, lastDotPosition) : originalFilename;
                String extension = (lastDotPosition >= 0) ? originalFilename.substring(lastDotPosition) : "";

                String relativeURL = "排课表导入/import/" + StpUtil.getLoginId() + "/" + baseName + "-" + now + extension;

                // 上传文件到 Minio，并获取文件的 URL
                boolean uploadSuccess = minioService.uploadStreamToMinio(inputStream, relativeURL, importBucketName);
                if (uploadSuccess) {
                    long b = courseScheduleService.generateCourseScheduleListUploadMsg(relativeURL);
                    if (b < 0) {
                        return SaResult.error("上传排课表失败，上传消息无法生成");
                    }

                    // 向消息队列发送处理排课表导入的消息
                    boolean b1 = messageSender.sendImportMsg(b, managerFilter, (String) StpUtil.getLoginId());


                    return SaResult.ok("成功上传排课表，处理中.请通过消息列表查看处理结果");
                } else {
                    return SaResult.error("上传文件到 Minio 失败");
                }
                // 采用 EasyExcel 解析这个 excel

            }

        } catch (Exception e) {
            log.error("处理文件上传时出错: ", e);
            return SaResult.error(e.toString());
        }
    }

    @PostMapping("/delete-batch-index")
    public SaResult deleteByBatchIndex(Long batchIndex) {
        if (Objects.isNull(batchIndex)) {
            throw dataMissError();
        }
        Integer count = courseScheduleService.deleteCourseScheduleByBatchIndex(batchIndex);
        if (count == -1) {
            return SaResult.code(2000).setMsg("出现错误");
        }
        if (count == 0) {
            return SaResult.code(2000).setMsg("没有数据可以删除");
        }
        return SaResult.data(count);
    }


}

