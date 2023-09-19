package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleFilterDataVO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class StudentFilter extends AbstractFilter {
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

        // 使用 studentStatusMapper 获取学生的班级数据
        StudentStatusPO studentStatusPOS = studentStatusMapper.
                selectOne(Wrappers.<StudentStatusPO>lambdaQuery().eq(StudentStatusPO::getIdNumber, platformUserPO.getUsername()));
        // 根据学籍信息中的班级标识 来获取班级信息 主要是年级、层次、学习形式、行政班别 从而进一步获取相应的排课表
        ClassInformationPO classInformationPO = classInformationMapper.selectOne(Wrappers.<ClassInformationPO>lambdaQuery().
                eq(ClassInformationPO::getClassIdentifier, studentStatusPOS.getClassIdentifier()));



        // 使用 courseScheduleMapper 获取数据
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.getCourseSchedulesByStudentIdNumber(classInformationPO,
                courseScheduleFilter);
        long total =  courseScheduleMapper.countCourseSchedulesByStudentIdNumber(classInformationPO,
                courseScheduleFilter);
        courseScheduleFilterDataVO.setCourseSchedulePOS(courseSchedulePOS);
        courseScheduleFilterDataVO.setTotal(total);
        return courseScheduleFilterDataVO;
    }
}
