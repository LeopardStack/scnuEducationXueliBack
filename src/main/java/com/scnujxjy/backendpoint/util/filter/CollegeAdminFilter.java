package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleFilterDataVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.FilterDataVO;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 为二级学院管理员定义筛选器
 */
@Component
@Slf4j
public class CollegeAdminFilter extends AbstractFilter {

    @Override
    public List<StudentStatusPO> filterStudentInfo(List<StudentStatusPO> data) {
        // 为二级学院管理员实现学籍数据筛选逻辑
        return data;
    }

    @Override
    public CourseScheduleFilterDataVO filterCourseSchedule(PageRO<CourseScheduleRO> courseScheduleFilter) {
        CourseScheduleFilterDataVO courseScheduleFilterDataVO = new CourseScheduleFilterDataVO();
        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationMapper.selectById(platformUserPO.getUserId());
        if (Objects.isNull(collegeAdminInformationPO)) {
            return null;
        }
        CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectById(collegeAdminInformationPO.getCollegeId());
        if (Objects.isNull(collegeInformationPO)) {
            return null;
        }

        // 使用 courseScheduleMapper 获取数据

        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.getCourseSchedulesByConditions(collegeInformationPO.getCollegeName(),
                courseScheduleFilter);
        long total =  courseScheduleMapper.countCourseSchedulesByConditions(collegeInformationPO.getCollegeName(),
                courseScheduleFilter);
        courseScheduleFilterDataVO.setCourseSchedulePOS(courseSchedulePOS);
        courseScheduleFilterDataVO.setTotal(total);
        return courseScheduleFilterDataVO;
    }


    /**
     * 筛选教学计划
     * @param courseInformationFilter 获取的教学计划筛选数据
     * @return
     */
    @Override
    public FilterDataVO filterCourseInformation(PageRO<CourseInformationRO> courseInformationFilter) {
        FilterDataVO<CourseInformationVO> courseInformationFilterDataVO = new FilterDataVO<>();
        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationMapper.selectById(platformUserPO.getUserId());
        if (Objects.isNull(collegeAdminInformationPO)) {
            return null;
        }
        CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectById(collegeAdminInformationPO.getCollegeId());
        if (Objects.isNull(collegeInformationPO)) {
            return null;
        }

        log.info("查询参数 " + courseInformationFilter.getEntity());
        // 使用 courseInformationMapper 获取数据
        List<CourseInformationVO> courseInformationVOS = courseInformationMapper.selectByFilterAndPage(courseInformationFilter.getEntity(),
                courseInformationFilter.getPageSize(),
                courseInformationFilter.getPageSize() * (courseInformationFilter.getPageNumber() -1),
                collegeInformationPO.getCollegeName());
        long total =  courseInformationMapper.getCountByFilterAndPage(courseInformationFilter.getEntity(),
                collegeInformationPO.getCollegeName());
        courseInformationFilterDataVO.setData(courseInformationVOS);
        courseInformationFilterDataVO.setTotal(total);

        return courseInformationFilterDataVO;
    }

    /**
     * 获取二级学院教学计划筛选参数
     * @return
     */
    @Override
    public CourseInformationSelectArgs filterCourseInformationSelectArgs() {
        CourseInformationSelectArgs courseInformationSelectArgs = new CourseInformationSelectArgs();

        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationMapper.selectById(platformUserPO.getUserId());
        if (Objects.isNull(collegeAdminInformationPO)) {
            return null;
        }
        CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectById(collegeAdminInformationPO.getCollegeId());
        if (Objects.isNull(collegeInformationPO)) {
            return null;
        }

        List<String> grades = courseInformationMapper.selectDistinctGrades(collegeInformationPO.getCollegeName());
        List<String> majorNames = courseInformationMapper.selectDistinctMajorNames(collegeInformationPO.getCollegeName());
        List<String> levels = courseInformationMapper.selectDistinctLevels(collegeInformationPO.getCollegeName());
        List<String> courseNames = courseInformationMapper.selectDistinctCourseNames(collegeInformationPO.getCollegeName());
        List<String> studyForms = courseInformationMapper.selectDistinctStudyForms(collegeInformationPO.getCollegeName());
        List<String> classNames = courseInformationMapper.selectDistinctClassNames(collegeInformationPO.getCollegeName());
        courseInformationSelectArgs.setGrades(grades);
        courseInformationSelectArgs.setMajorNames(majorNames);
        courseInformationSelectArgs.setLevels(levels);
        courseInformationSelectArgs.setCourseNames(courseNames);
        courseInformationSelectArgs.setStudyForms(studyForms);
        courseInformationSelectArgs.setClassNames(classNames);
        return courseInformationSelectArgs;
    }

    // 注意：这个子类没有重写filterDegreeInfo方法，所以它将使用AbstractFilter中的默认实现
}

