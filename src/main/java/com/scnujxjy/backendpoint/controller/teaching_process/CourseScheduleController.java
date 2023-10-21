package com.scnujxjy.backendpoint.controller.teaching_process;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseExtraInformationPO;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseExtraInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleUpdateRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleWithLiveInfoVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScheduleCourseInformationSelectArgs;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import com.scnujxjy.backendpoint.util.filter.StudentFilter;
import com.scnujxjy.backendpoint.util.filter.TeacherFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.SECOND_COLLEGE_ADMIN;
import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.XUELIJIAOYUBU_ADMIN;
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
    private TeacherFilter teacherFilter;

    @Resource
    private StudentFilter studentFilter;

    @Resource
    private ManagerFilter managerFilter;

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
        PageVO<CourseScheduleWithLiveInfoVO> courseScheduleVOPageVO = courseScheduleService.
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
        PageVO<CourseScheduleWithLiveInfoVO> courseScheduleVOPageVO = courseScheduleService.
                allPageQueryCourseScheduleFilter(courseScheduleROPageRO, teacherFilter);
        // 数据校验
        if (Objects.isNull(courseScheduleVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseScheduleVOPageVO);
    }

    /**
     * 获取学生所有的排课表信息 不限制日期 除非她/他本人提供筛选条件
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
        PageVO<CourseScheduleWithLiveInfoVO> courseScheduleVOPageVO = courseScheduleService.
                allPageQueryCourseScheduleFilter(courseScheduleROPageRO, studentFilter);
        // 数据校验
        if (Objects.isNull(courseScheduleVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseScheduleVOPageVO);
    }

    /**
     * 获取所有的排课表信息 不限制日期
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
        PageVO<CourseScheduleWithLiveInfoVO> courseScheduleVOPageVO = courseScheduleService.
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
     * 获取不同角色权限范围内的排课表课程信息
     * @param courseScheduleFilterROPageRO
     * @return
     */
    @PostMapping("/select_schedule_courses")
    public SaResult getScheduleCoursesInformation(@RequestBody PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO){
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
                FilterDataVO scheduleCoursesFilterDataVO = courseScheduleService.allPageQueryScheduleCoursesInformationFilter(courseScheduleFilterROPageRO, collegeAdminFilter);

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
                // 查询继续教育管理员权限范围内的教学计划
                FilterDataVO scheduleCoursesFilterDataVO = courseScheduleService.allPageQueryScheduleCoursesInformationFilter(courseScheduleFilterROPageRO, managerFilter);

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
     * 获取排课表课程筛选条件
     *
     * @return 排课表筛选条件
     */
    @GetMapping("/select_schedule_courses_args")
    public SaResult getSelectScheduleCourseInformationArgs() {
        List<String> roleList = StpUtil.getRoleList();
        PageVO<FilterDataVO> filterDataVO = null;

        ScheduleCourseInformationSelectArgs  scheduleCourseInformationSelectArgs = new ScheduleCourseInformationSelectArgs();
        // 获取访问者 ID
        if (roleList.isEmpty()) {
            throw dataNotFoundError();
        } else {
            if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                scheduleCourseInformationSelectArgs = courseScheduleService.getSelectScheduleCourseInformationArgs(collegeAdminFilter);
            } else if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
                scheduleCourseInformationSelectArgs = courseScheduleService.getSelectScheduleCourseInformationArgs(managerFilter);
            }
        }

        return SaResult.data(scheduleCourseInformationSelectArgs);
    }


    /**
     * 获取不同角色权限范围内的排课表信息
     * @param courseScheduleFilterROPageRO
     * @return
     */
    @PostMapping("/select_schedules")
    public SaResult getScheduleInformation(@RequestBody PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO){
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
            }

        }
        return SaResult.data(filterDataVO);
    }


    /**
     * 修改排课表相关信息
     * @param courseScheduleUpdateROPageRO
     * @return
     */
    @PostMapping("/update_schedule_courses")
    public SaResult updateScheduleCoursesInformation(@RequestBody CourseScheduleUpdateRO courseScheduleUpdateROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleUpdateROPageRO)) {
            return SaResult.error("未做任何修改");
        }
        try {
            courseScheduleService.updateScheduleInfor(courseScheduleUpdateROPageRO);
        }catch (Exception e){
            return SaResult.error(e.toString());
        }

        log.info("更新的排课表信息为 " + courseScheduleUpdateROPageRO);
        return SaResult.ok();
    }

    /**
     * 删除排课表相关信息
     * @return
     */
    @DeleteMapping("/delete_schedule_courses")
    public SaResult deleteScheduleCoursesInformation(@RequestParam("scheduldId") Long scheduldId) {
        // 校验参数
        if (Objects.isNull(scheduldId)) {
            return SaResult.error("scheduldId 不能为空");
        }

        try {
            // 这里调用删除方法，不是更新方法
            courseScheduleService.deleteScheduleInfor(scheduldId);
        }catch (Exception e){
            return SaResult.error(e.toString());
        }

        log.info("删除排课表 " + scheduldId);
        return SaResult.ok();
    }

    /**
     * 获取直播间信息
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
        }catch (Exception e){
            return SaResult.error(e.toString());
        }
    }

    /**
     * 修改教学班的课程简介信息
     * @param courseExtraInformationRO
     * @return
     */
    @PostMapping("/update_schedule_courses_extra_info")
    public SaResult updateScheduleCoursesExtraInformation(@RequestBody CourseExtraInformationRO courseExtraInformationRO) {
        // 校验参数
        if (Objects.isNull(courseExtraInformationRO)) {
            return SaResult.error("未做任何修改");
        }
        try {
            courseScheduleService.updateScheduleExtraInfo(courseExtraInformationRO);
        }catch (Exception e){
            return SaResult.error(e.toString());
        }

        log.info("更新的教学班课程简介信息为 " + courseExtraInformationRO);
        return SaResult.ok();
    }

    /**
     * 获取教学班的课程简介信息
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
            courseExtraInformationPO =  courseScheduleService.getScheduleExtraInfo(courseExtraInformationRO);
        }catch (Exception e){
            return SaResult.error(e.toString());
        }

        return SaResult.data(courseExtraInformationPO);
    }

}

