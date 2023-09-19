package com.scnujxjy.backendpoint.controller.teaching_process;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import com.scnujxjy.backendpoint.service.teaching_process.CourseInformationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 课程信息表
 *
 * @author leopard
 * @since 2023-08-14
 */
@RestController
@RequestMapping("/course-information")
public class CourseInformationController {
    @Resource
    private CourseInformationService courseInformationService;

    /**
     * 根据id查询课程信息
     *
     * @param id 课程信息id
     * @return 课程信息
     */
    @GetMapping("/detail")
    public SaResult detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 查询
        CourseInformationVO courseInformationVO = courseInformationService.detailById(id);
        // 参数校验
        if (Objects.isNull(courseInformationVO)) {
            throw dataNotFoundError();
        }
        // 转换并返回
        return SaResult.data(courseInformationVO);
    }

    /**
     * 分页查询课程信息
     *
     * @param courseInformationROPageRO 分页参数
     * @return 分页查询的课程信息列表
     */
    @PostMapping("/page")
    public SaResult pageQueryCourseInformation(@RequestBody PageRO<CourseInformationRO> courseInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(courseInformationROPageRO)) {
            throw dataMissError();
        }
        if (Objects.isNull(courseInformationROPageRO.getEntity())) {
            courseInformationROPageRO.setEntity(new CourseInformationRO());
        }
        // 查询数据
        PageVO<CourseInformationVO> courseInformationVOPageVO = courseInformationService.pageQueryCourseInformation(courseInformationROPageRO);
        // 数据校验
        if (Objects.isNull(courseInformationVOPageVO)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseInformationVOPageVO);
    }

    /**
     * 根据id更新课程信息
     *
     * @param courseInformationRO 课程信息
     * @return 更新后的课程信息
     */
    @PutMapping("/edit")
    public SaResult editById(@RequestBody CourseInformationRO courseInformationRO) {
        // 参数校验
        if (Objects.isNull(courseInformationRO) || Objects.nonNull(courseInformationRO.getId())) {
            throw dataMissError();
        }
        // 更新
        CourseInformationVO courseInformationVO = courseInformationService.editById(courseInformationRO);
        // 校验数据
        if (Objects.isNull(courseInformationVO)) {
            throw dataUpdateError();
        }
        // 返回数据
        return SaResult.data(courseInformationVO);
    }

    /**
     * 根据id删除课程信息
     *
     * @param id 课程信息id
     * @return 删除数量
     */
    @DeleteMapping("/delete")
    public SaResult deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            throw dataMissError();
        }
        // 删除数据
        int count = courseInformationService.deleteById(id);
        // 校验数据
        if (count <= 0) {
            throw dataDeleteError();
        }
        // 返回数据
        return SaResult.data(count);
    }


    /**
     * 根据学生登录的账号查询课程信息
     *
     * @return 课程信息
     */
    @GetMapping("/detail_student_course_information")
    public SaResult detailByStudentID() {
        // 获取访问者 ID
        Object loginId = StpUtil.getLoginId();
        // 查询
        List<CourseInformationRO> studentTeachingPlan = courseInformationService.getStudentTeachingPlan((String) loginId);
        // 参数校验
        if (Objects.isNull(studentTeachingPlan)) {
            throw dataNotFoundError();
        }
        // 转换并返回
        return SaResult.data(studentTeachingPlan);
    }


    /**
     * 根据学生登录的账号查询课程信息
     *
     * @return 课程信息
     */
    @GetMapping("/get_student_teaching_plans")
    public SaResult getTeachingPlansByStudentID() {
        // 获取访问者 ID
        Object loginId = StpUtil.getLoginId();
        // 查询
        List<CourseInformationPO> studentTeachingPlan = courseInformationService.getBaseMapper().getStudentTeachingPlans((String) loginId);
        // 参数校验
        if (Objects.isNull(studentTeachingPlan)) {
            throw dataNotFoundError();
        }
        // 转换并返回
        return SaResult.data(studentTeachingPlan);
    }


}

