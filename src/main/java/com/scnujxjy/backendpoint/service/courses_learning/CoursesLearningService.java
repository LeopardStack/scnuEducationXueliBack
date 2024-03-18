package com.scnujxjy.backendpoint.service.courses_learning;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesClassMappingPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesLearningPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesClassMappingMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesLearningMapper;
import com.scnujxjy.backendpoint.model.bo.course_learning.CourseRecordBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CoursesLearningRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseLearningVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;

/**
 *
 * @author 谢辉龙
 * @since 2024-03-05
 */
@Service
@Slf4j
public class CoursesLearningService extends ServiceImpl<CoursesLearningMapper, CoursesLearningPO>
        implements IService<CoursesLearningPO> {

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public PageVO<CourseLearningVO> pageQueryCoursesInfo(PageRO<CoursesLearningRO> courseScheduleROPageRO) {
        List<String> roleList = StpUtil.getRoleList();
        if(roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())){
            // 学历教育部

        }else if(roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())){
            // 二级学院
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            courseScheduleROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());
        }else if(roleList.contains(TEACHING_POINT_ADMIN.getRoleName())){

        }
        List<CourseRecordBO> courseSections = (List<CourseRecordBO>) redisTemplate.
                opsForValue().get("courseSections");


        return getCourseData(courseSections, courseScheduleROPageRO);
    }

    public PageVO<CourseLearningVO> pageQueryCoursesInfo1(PageRO<CoursesLearningRO> courseScheduleROPageRO) {
        List<String> roleList = StpUtil.getRoleList();
        if(roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())){
            // 学历教育部

        }else if(roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())){
            // 二级学院
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            courseScheduleROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());
        }else if(roleList.contains(TEACHING_POINT_ADMIN.getRoleName())){

        }
        List<CourseRecordBO> courseSections = (List<CourseRecordBO>) redisTemplate.opsForValue().get("courseSections1");


        return getCourseData(courseSections, courseScheduleROPageRO);
    }

    private PageVO getCourseData(List<CourseRecordBO> courseSections, PageRO<CoursesLearningRO> courseScheduleROPageRO){
        if (courseSections == null) {
            // 可以选择从数据库加载数据，或者返回错误/空结果
            List<CourseLearningVO> courseLearningVOS = getBaseMapper().selectCourseLearningData(courseScheduleROPageRO.getEntity(),
                    courseScheduleROPageRO.getPageNumber() - 1, courseScheduleROPageRO.getPageSize());
            PageVO pageVO = new PageVO<CourseLearningVO>();
            pageVO.setRecords(courseLearningVOS);
            pageVO.setSize(courseScheduleROPageRO.getPageSize());
            pageVO.setCurrent(courseScheduleROPageRO.getPageNumber());
            pageVO.setTotal(Long.valueOf(getBaseMapper().selectCount(null)));

            return pageVO;
        }

        // 筛选逻辑
        Stream<CourseRecordBO> filteredStream = courseSections.stream();
        CoursesLearningRO filter = courseScheduleROPageRO.getEntity();
        if (filter.getId() != null) {
            filteredStream = filteredStream.filter(c -> c.getId().equals(filter.getId()));
        }
        if (filter.getGrade() != null) {
            filteredStream = filteredStream.filter(c -> c.getGrade().equals(filter.getGrade()));
        }
        if (filter.getCollege() != null) {
            filteredStream = filteredStream.filter(c -> c.getCollege().equals(filter.getCollege()));
        }
        if (filter.getMajorName() != null) {
            filteredStream = filteredStream.filter(c -> c.getMajorName().equals(filter.getMajorName()));
        }
        if (filter.getStudyForm() != null) {
            filteredStream = filteredStream.filter(c -> c.getStudyForm().equals(filter.getStudyForm()));
        }
        if (filter.getLevel() != null) {
            filteredStream = filteredStream.filter(c -> c.getLevel().equals(filter.getLevel()));
        }
        if (filter.getCourseName() != null) {
            filteredStream = filteredStream.filter(c -> c.getCourseName().equals(filter.getCourseName()));
        }
        if (filter.getCourseType() != null) {
            filteredStream = filteredStream.filter(c -> c.getCourseType().equals(filter.getCourseType()));
        }
        if (filter.getMainTeacherName() != null) {
            filteredStream = filteredStream.filter(c -> c.getDefaultMainTeacherUsername().equals(filter.getMainTeacherName()));
        }
        if (filter.getCourseStartTime() != null) {
            filteredStream = filteredStream.filter(c -> c.getStartTime() != null && !c.getStartTime().before(filter.getCourseStartTime()));
        }
        if (filter.getCourseEndTime() != null) {
            filteredStream = filteredStream.filter(c -> c.getStartTime() != null && !c.getStartTime().after(filter.getCourseEndTime()));
        }

        // 分组和聚合
        Map<Long, CourseLearningVO> groupedAndAggregated = filteredStream
                .collect(Collectors.groupingBy(CourseRecordBO::getId))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> aggregateToCourseLearningVO(entry.getValue())
                ));

        // 排序
        List<CourseLearningVO> sortedList = groupedAndAggregated.values().stream()
                .sorted(Comparator.comparing(CourseLearningVO::getCreatedTime).reversed())
                .collect(Collectors.toList());

        // 分页
        long start = Math.max((courseScheduleROPageRO.getPageNumber() - 1) * courseScheduleROPageRO.getPageSize(), 0);
        long end = Math.min(start + courseScheduleROPageRO.getPageSize(), sortedList.size());
        List<CourseLearningVO> pagedCourseLearningVOs = sortedList.subList((int)start, (int)end);

        // 构建分页响应
        PageVO<CourseLearningVO> pageVO = new PageVO<>();
        pageVO.setRecords(pagedCourseLearningVOs);
        pageVO.setSize(courseScheduleROPageRO.getPageSize());
        pageVO.setCurrent(courseScheduleROPageRO.getPageNumber());
        pageVO.setTotal((long) groupedAndAggregated.size());

        return pageVO;
    }


    public List<CourseRecordBO> getCourseSections(PageRO<CoursesLearningRO> courseScheduleROPageRO){
        return getBaseMapper().getCourseSectionsData();
    }

    private CourseLearningVO aggregateToCourseLearningVO(List<CourseRecordBO> records) {
        CourseLearningVO vo = new CourseLearningVO();
        if (!records.isEmpty()) {
            CourseRecordBO representative = records.get(0);
                vo.setId(representative.getId());
                vo.setGrade(representative.getGrade());
                vo.setCourseName(representative.getCourseName());
                vo.setCourseType(representative.getCourseType());
                vo.setCourseDescription(representative.getCourseDescription());
                vo.setCourseCoverUrl(representative.getCourseCoverUrl());
                vo.setDefaultMainTeacherUsername(representative.getDefaultMainTeacherUsername());
                vo.setDefaultMainTeacherName(representative.getName());
                vo.setCourseIdentifier(representative.getCourseIdentifier());
                vo.setValid(representative.getValid());
                vo.setCreatedTime(representative.getCreatedTime());
                vo.setUpdatedTime(representative.getUpdatedTime());

                // 获取 classNames
                Set<String> classNames = records.stream()
                        .map(CourseRecordBO::getClassName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
                vo.setClassNames(String.join(", ", classNames));

                // 获取最近的开始时间
                Date recentStartTime = records.stream()
                        .map(CourseRecordBO::getStartTime)
                        .filter(Objects::nonNull)
                        .min(Date::compareTo)
                        .orElse(null);
                vo.setRecentCourseScheduleTime(recentStartTime);
            }

            return vo;

    }
}
