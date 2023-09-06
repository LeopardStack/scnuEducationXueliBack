package com.scnujxjy.backendpoint.controller.teaching_process;


import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.basic.PlatformUserVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.college.CollegeAdminInformationService;
import com.scnujxjy.backendpoint.service.college.CollegeInformationService;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.teaching_process.CourseScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
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

    @Resource
    private CollegeInformationService collegeInformationService;

    @Resource
    private PlatformUserService platformUserService;

    @Resource
    private CollegeAdminInformationService collegeAdminInformationService;

    @Resource
    private TeacherInformationService teacherInformationService;

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
     * 根据登录用户 id 查询二级学院教务员相关的排课表
     *
     * @return 排课表详细信息
     */
    @GetMapping("/detail_by_userId")
    public SaResult detailByUserId() {
        String loginId = (String) StpUtil.getLoginId();
        // 参数校验
        if (Objects.isNull(loginId)) {
            throw dataMissError();
        }

        String account = loginId;
        PlatformUserVO platformUserVO = platformUserService.detailByuserName(account);
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationService.getById(platformUserVO.getUserId());
        CollegeInformationPO collegeInformationServiceById = collegeInformationService.getById(collegeAdminInformationPO.getCollegeId());
        String collegeName = collegeInformationServiceById.getCollegeName();

        List<CourseSchedulePO> courseSchedulePOS = courseScheduleService.getBaseMapper().selectCourseSchedules2(collegeName);

        if (Objects.isNull(courseSchedulePOS)) {
            throw dataNotFoundError();
        }
        // 返回数据
        return SaResult.data(courseSchedulePOS);
    }


    /**
     * 根据登录用户 id 查询教师相关的排课表
     *
     * @return 排课表详细信息
     */
    @GetMapping("/detail_by_teacherId")
    public SaResult detailByTeacherId() {
        String loginId = (String) StpUtil.getLoginId();
        // 参数校验
        if (Objects.isNull(loginId)) {
            throw dataMissError();
        }

        String findStr = loginId.substring(1);
        TeacherInformationPO teacherInformationPO = null;

        List<TeacherInformationPO> teacherInformationPOS = teacherInformationService.getBaseMapper().selectByWorkNumber(findStr);
        if(teacherInformationPOS.size() > 0){
            teacherInformationPO = teacherInformationPOS.get(0);
        }else{
            List<TeacherInformationPO> teacherInformationPOS1 = teacherInformationService.getBaseMapper().selectByIdCardNumber(findStr);
            if(teacherInformationPOS.size() > 0){
                teacherInformationPO = teacherInformationPOS.get(0);
            }else{
                List<TeacherInformationPO> teacherInformationPOS2 = teacherInformationService.getBaseMapper().selectByPhone(findStr);
                if(teacherInformationPOS.size() > 0){
                    teacherInformationPO = teacherInformationPOS.get(0);
                }else{

                }
            }
        }
        if(teacherInformationPO == null){
            log.error("没有找到该老师信息 " + loginId);
        }else{
            List<CourseSchedulePO> courseSchedulePOS = courseScheduleService.getBaseMapper().selectCourseSchedules3(teacherInformationPO.getName());
            log.info("它是哪个老师 " + teacherInformationPO.getName());
            log.info(teacherInformationPO.getName() + " 老师的教学计划总数 " + courseSchedulePOS.size());
            log.info("最后一条记录 " + courseSchedulePOS.get(courseSchedulePOS.size()-1));
            return SaResult.data(courseSchedulePOS);
        }
        // 返回数据
        return SaResult.error("未能获取到该教师相关的排课表信息 " + loginId);
    }
}

