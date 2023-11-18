package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;

@Aspect
@Component
public class FilterAspect {

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private TeachingPointAdminInformationMapper teachingPointAdminInformationMapper;

    @Resource
    private TeachingPointInformationMapper teachingPointInformationMapper;

    @Pointcut("execution(public * com.scnujxjy.backendpoint.util.filter.AbstractFilter.getScheduleCoursesBetter(..))")
    public void scheduleCoursesPointcut() {
        // 切点定义
    }

    @Before("scheduleCoursesPointcut()")
    public void beforeGetScheduleCourses(JoinPoint joinPoint) {
        // 获取参数
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] instanceof PageRO) {
            PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO = (PageRO<CourseScheduleFilterRO>) args[0];
            List<String> roleList = StpUtil.getRoleList();
            if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {

            } else if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
                courseScheduleFilterROPageRO.getEntity().setCollege(scnuXueliTools.getUserBelongCollege().getCollegeName());
            } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
                String loginIdAsString = StpUtil.getLoginIdAsString().replace("M", "").replace("m", "");
                List<TeachingPointAdminInformationPO> teachingPointAdminInformationPOs = teachingPointAdminInformationMapper.selectList(new LambdaQueryWrapper<TeachingPointAdminInformationPO>()
                        .eq(TeachingPointAdminInformationPO::getIdCardNumber, loginIdAsString));
                List<String> classNames = new ArrayList<>();
                for (TeachingPointAdminInformationPO teachingPointAdminInformationPO : teachingPointAdminInformationPOs) {
                    TeachingPointInformationPO teachingPointInformationPO = teachingPointInformationMapper.selectOne(new LambdaQueryWrapper<TeachingPointInformationPO>()
                            .eq(TeachingPointInformationPO::getTeachingPointId, teachingPointAdminInformationPO.getTeachingPointId()));
                    classNames.add(teachingPointInformationPO.getAlias());
                }

                // 使用 Stream API 进行去重
                List<String> distinctClassNames = classNames.stream()
                        .distinct()
                        .collect(Collectors.toList());

                courseScheduleFilterROPageRO.getEntity().setClassNames(distinctClassNames);

            }

        }
    }

    // 其他辅助方法（如果需要）
}
