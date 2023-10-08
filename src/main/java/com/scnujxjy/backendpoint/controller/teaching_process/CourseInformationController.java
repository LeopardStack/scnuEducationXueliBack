package com.scnujxjy.backendpoint.controller.teaching_process;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import com.scnujxjy.backendpoint.service.teaching_process.CourseInformationService;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.SECOND_COLLEGE_ADMIN;
import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.XUELIJIAOYUBU_ADMIN;
import static com.scnujxjy.backendpoint.exception.DataException.*;

/**
 * 课程信息表
 *
 * @author leopard
 * @since 2023-08-14
 */
@RestController
@RequestMapping("/course-information")
@Slf4j
public class CourseInformationController {
    @Resource
    private CourseInformationService courseInformationService;

    @Resource
    private CollegeAdminFilter collegeAdminFilter;

    @Resource
    private ManagerFilter managerFilter;

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

    /**
     * 根据二级学院教务员获取筛选参数
     *
     * @return 教学计划
     */
    @GetMapping("/get_select_args_admin")
    public SaResult getTeachingPlansArgsByCollege() {
        List<String> roleList = StpUtil.getRoleList();
        log.info("登录角色 " + roleList);

        // 获取访问者 ID
        Object loginId = StpUtil.getLoginId();
        CourseInformationSelectArgs courseInformationSelectArgs = null;
        if(roleList.isEmpty()){
            throw dataNotFoundError();
        }else{
            if(roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())){
                log.info("现在登录的是二级学院的管理员");
                // 查询二级学院管理员筛选教学计划的参数
                courseInformationSelectArgs = courseInformationService.getTeachingPlansArgsByCollege((String) loginId, collegeAdminFilter);
                // 参数校验
                if (Objects.isNull(courseInformationSelectArgs)) {
                    throw dataNotFoundError();
                }
            }else if(roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())){
                log.info("现在登录的是学历教育部的管理员");
                // 查询继续教育学院管理员筛选教学计划的参数
                courseInformationSelectArgs = courseInformationService.getTeachingPlansArgsByCollege((String) loginId, managerFilter);
                // 参数校验
                if (Objects.isNull(courseInformationSelectArgs)) {
                    throw dataNotFoundError();
                }
            }
        }

        // 转换并返回
        return SaResult.data(courseInformationSelectArgs);
    }

    /**
     * 获取二级学院管理员所有学院内专业的所有教学计划信息 不限制日期
     * @param courseInformationROPageRO
     * @return
     */
    @PostMapping("/allPageByCollegeAdmin")
    public SaResult allPageQueryCourseScheduleByCollegeAdmin(@RequestBody PageRO<CourseInformationRO> courseInformationROPageRO) {

        List<String> roleList = StpUtil.getRoleList();
        log.info("登录角色 " + roleList);
        PageVO<FilterDataVO>  filterDataVO = null;
        // 获取访问者 ID
        if(roleList.isEmpty()){
            throw dataNotFoundError();
        }else{
            if(roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())){
                log.info("现在登录的是二级学院的管理员");
                // 查询二级学院管理员权限范围内的教学计划
                FilterDataVO courseInformationFilterDataVO = courseInformationService.
                        allPageQueryCourseInformationFilter(courseInformationROPageRO, collegeAdminFilter);

                // 创建并返回分页信息
                filterDataVO = new PageVO<>(courseInformationFilterDataVO.getData());
                filterDataVO.setTotal(courseInformationFilterDataVO.getTotal());
                filterDataVO.setCurrent(courseInformationROPageRO.getPageNumber());
                filterDataVO.setSize(courseInformationROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) courseInformationFilterDataVO.getData().size()
                        / courseInformationROPageRO.getPageSize()));

                // 数据校验
                if (Objects.isNull(filterDataVO)) {
                    throw dataNotFoundError();
                }

            }else if(roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())){
                log.info("现在登录的是学历教育部的管理员");
                // 查询继续教育学院管理员权限范围内的教学计划
                FilterDataVO courseInformationFilterDataVO =  courseInformationService.
                        allPageQueryCourseInformationFilter(courseInformationROPageRO, managerFilter);
                // 创建并返回分页信息
                filterDataVO = new PageVO<>(courseInformationFilterDataVO.getData());
                filterDataVO.setTotal(courseInformationFilterDataVO.getTotal());
                filterDataVO.setCurrent(courseInformationROPageRO.getPageNumber());
                filterDataVO.setSize(courseInformationROPageRO.getPageSize());
                filterDataVO.setPages((long) Math.ceil((double) courseInformationFilterDataVO.getData().size()
                        / courseInformationROPageRO.getPageSize()));
                // 数据校验
                if (Objects.isNull(filterDataVO)) {
                    throw dataNotFoundError();
                }
            }
        }


        // 校验参数
        if (Objects.isNull(courseInformationROPageRO)) {
            throw dataMissError();
        }
        log.info("查询参数1 " + courseInformationROPageRO);
        if (Objects.isNull(courseInformationROPageRO.getEntity())) {
            courseInformationROPageRO.setEntity(new CourseInformationRO());
        }

        // 返回数据
        return SaResult.data(filterDataVO);
    }


}

