package com.scnujxjy.backendpoint.service.courses_learning;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.CourseContentType;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseAssistantsPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesClassMappingPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesLearningPO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CourseAssistantsMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesClassMappingMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesLearningMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.model.bo.course_learning.CourseRecordBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseLearningCreateRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CoursesLearningRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseLearningVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
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
    private MinioService minioService;

    @Value("minio.courseCoverDir")
    private String minioCourseCoverDir;

    @Resource
    private ClassInformationService classInformationService;

    @Resource
    private TeacherInformationService teacherInformationService;

    @Resource
    private CourseAssistantsService courseAssistantsService;

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

    /**
     * 创建 课程
     * @param courseLearningCreateRO
     * @return
     */
    public PageVO<CourseLearningVO> createCourse(CourseLearningCreateRO courseLearningCreateRO) {
        try {
            CoursesLearningPO coursesLearningPO = new CoursesLearningPO();
            coursesLearningPO.setCourseName(convertListToString(courseLearningCreateRO.getCourseNames()));
            coursesLearningPO.setCourseType(courseLearningCreateRO.getCourseType());
            coursesLearningPO.setCourseDescription(courseLearningCreateRO.getCourseDescription());

            if (!isValidCourseType(courseLearningCreateRO.getCourseType())) {
                throw new IllegalArgumentException("无效的课程类型: " + courseLearningCreateRO.getCourseType());
            }
            coursesLearningPO.setCourseType(courseLearningCreateRO.getCourseType());

            /**
             * 将班级做好映射 先校验 合法后 再存入数据库
             */
            List<CoursesClassMappingPO> coursesClassMappingPOS = new ArrayList<>();
            for(String s: courseLearningCreateRO.getClassIdentifier()){
                ClassInformationPO classInformationPO = classInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                        .eq(ClassInformationPO::getClassIdentifier, s));
                if(classInformationPO == null){
                    throw new RuntimeException("传入的班级前缀找不到，非法前缀 ");
                }
                CoursesClassMappingPO classMappingPO = new CoursesClassMappingPO();
                classMappingPO.setClassIdentifier(classInformationPO.getClassIdentifier());
                coursesClassMappingPOS.add(classMappingPO);
            }
            // 对传入的主讲教师 ID 做校验
            String defaultMainTeacherUsername = courseLearningCreateRO.getDefaultMainTeacherUsername();
            if(!isValidUsername(defaultMainTeacherUsername)){
                throw new RuntimeException("教师用户名为空 或者 数据库中找不到 " + defaultMainTeacherUsername);
            }
            coursesLearningPO.setDefaultMainTeacherUsername(defaultMainTeacherUsername);

            List<CourseAssistantsPO> courseAssistantsPOS = new ArrayList<>();
            for(String s: courseLearningCreateRO.getAssistantUsername()){
                if(!isValidUsername(s)){
                    throw new RuntimeException("助教用户名为空 或者 数据库中找不到 " + s);
                }
                CourseAssistantsPO courseAssistantsPO = new CourseAssistantsPO()
                        .setUsername(s)
                        ;
                courseAssistantsPOS.add(courseAssistantsPO);
            }


            String uniqueFileName = generateUniqueFileName(coursesLearningPO, courseLearningCreateRO.getCourseCover().getName());
            try (InputStream inputStream = Files.newInputStream(courseLearningCreateRO.getCourseCover().toPath())) {
                boolean uploadSuccess = minioService.uploadStreamToMinio(inputStream, uniqueFileName, minioCourseCoverDir);
                if (uploadSuccess) {
                    coursesLearningPO.setCourseCoverUrl(minioCourseCoverDir + "/" + uniqueFileName);
                } else {
                    throw new RuntimeException("将课程封面文件上传到 Minio 失败 " + uploadSuccess);
                }
            } catch (IOException e) {
                throw new RuntimeException("将课程封面文件上传到 Minio 失败 " + "文件读取失败: " + e);
            }

            int insert = getBaseMapper().insert(coursesLearningPO);
            if(insert <= 0){
                throw new RuntimeException("课程信息插入失败 " + insert);
            }
            Long courseId = coursesLearningPO.getId(); // 假设 getId() 方法能获取到自动生成的主键

            // 设置每个 CourseAssistantsPO 的 courseId 并收集起来
            courseAssistantsPOS = courseLearningCreateRO.getAssistantUsername().stream()
                    .filter(this::isValidUsername)
                    .map(s -> new CourseAssistantsPO().setCourseId(courseId).setUsername(s))
                    .collect(Collectors.toList());

            // 批量插入 CourseAssistantsPOS
            boolean assistantInsert = courseAssistantsService.saveBatch(courseAssistantsPOS);
            if (!assistantInsert) {
                throw new RuntimeException("批量插入助教失败");
            }
            courseLearningCreateRO.getClassIdentifier().stream()
                    .filter(this::isValidClassName)
                    .map(s -> new CoursesClassMappingPO().setCourseId(courseId).setClassIdentifier(s))
                    .collect(Collectors.toList());

        }catch (Exception e){
            log.info("创建课程失败 " + e);
        }

        return null;
    }


    private String convertListToString(List<String> courseNames) {
        if (courseNames == null || courseNames.isEmpty()) {
            return "";
        }
        return String.join(" ", courseNames);
    }

    /**
     * 根据用户上传的课程封面图 来重命名课程封面在 Minio 中的存储值
     * @param coursesLearningPO
     * @param originalFileName
     * @return
     */
    public String generateUniqueFileName(CoursesLearningPO coursesLearningPO, String originalFileName) {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int hashCode = coursesLearningPO.hashCode();
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        return timeStamp + "_" + hashCode + fileExtension;
    }

    /**
     * 校验输入字符串是否是 课程学习常量类的字符串
     * @param courseType
     * @return
     */
    public boolean isValidCourseType(String courseType) {
        for (CourseContentType type : CourseContentType.values()) {
            if (type.getContentType().equals(courseType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isValidClassName(String className){
        if(StringUtils.isBlank(className)){
            return false;
        }
        ClassInformationPO classInformationPO = classInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                .eq(ClassInformationPO::getClassIdentifier, className));
        if(classInformationPO != null){
            return true;
        }
        return false;
    }

    /**
     * 对 前端传入的 教师用户名做校验
     * @param username
     * @return
     */
    private boolean isValidUsername(String username) {
        if(StringUtils.isBlank(username)){
            return false;
        }
        TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getTeacherUsername, username));
        if(teacherInformationPO != null){
            return true;
        }
        return false;
    }

}
