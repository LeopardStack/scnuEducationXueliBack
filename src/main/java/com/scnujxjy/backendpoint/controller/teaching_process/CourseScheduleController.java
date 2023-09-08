package com.scnujxjy.backendpoint.controller.teaching_process;


import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * <p>
 * 排课表 前端控制器
 * </p>
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


}

