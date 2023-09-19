package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleFilterDataVO;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class TeacherFilter extends AbstractFilter{
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

        // 使用 courseScheduleMapper 获取数据
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.getCourseSchedulesByTeacherUserName(platformUserPO.getUsername(),
                courseScheduleFilter);
        long total =  courseScheduleMapper.countCourseSchedulesByTeacherUserName(platformUserPO.getUsername(),
                courseScheduleFilter);
        courseScheduleFilterDataVO.setCourseSchedulePOS(courseSchedulePOS);
        courseScheduleFilterDataVO.setTotal(total);
        return courseScheduleFilterDataVO;
    }
}
