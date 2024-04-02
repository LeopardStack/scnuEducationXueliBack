package com.scnujxjy.backendpoint.service.courses_learning;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.CourseContentType;
import com.scnujxjy.backendpoint.constant.enums.StudentCoursesStatusEnum;
import com.scnujxjy.backendpoint.constant.enums.TeacherTypeEnum;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_point.TeachingPointInformationPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ViewStudentResponse.Content;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelInfoResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ChannelResponseData;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesLearningMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.bo.course_learning.CourseRecordBO;
import com.scnujxjy.backendpoint.model.bo.course_learning.StudentWhiteListInfoBO;
import com.scnujxjy.backendpoint.model.bo.course_learning.TeacherInfo;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.*;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherSelectVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.*;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.ScoreInformationDownloadVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.TeacherCoursesWithTypeVO;
import com.scnujxjy.backendpoint.model.vo.video_stream.StudentWhiteListVO;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import com.scnujxjy.backendpoint.service.teaching_process.ScoreInformationService;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingService;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
import com.scnujxjy.backendpoint.util.HealthCheckTask;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import com.scnujxjy.backendpoint.util.video_stream.SingleLivingSetting;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import net.polyv.common.v1.exception.PloyvSdkException;
import net.polyv.live.v1.constant.LiveConstant;
import net.polyv.live.v1.entity.channel.operate.LiveChannelSettingRequest;
import net.polyv.live.v1.entity.web.auth.LiveUpdateChannelAuthRequest;
import net.polyv.live.v1.service.web.impl.LiveWebAuthServiceImpl;
import org.apache.tika.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;

/**
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

    @Resource
    private ScoreInformationService scoreInformationService;

    @Value("${minio.courseCoverDir:1}")
    private String minioCourseCoverDir;

    @Resource
    private ClassInformationService classInformationService;

    @Resource
    private RetakeStudentsService retakeStudentsService;

    @Resource
    private TeacherInformationService teacherInformationService;

    @Resource
    private CourseAssistantsService courseAssistantsService;

    @Resource
    private SectionsService sectionsService;

    @Resource
    private StudentStatusService studentStatusService;


    @Resource
    private SingleLivingSetting singleLivingSetting;

    @Resource
    private SingleLivingService singleLivingService;

    @Resource
    private VideoResourcesService videoResourcesService;

    @Resource
    private CoursesClassMappingService coursesClassMappingService;

    @Resource
    private LiveResourceService liveResourceService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private VideoStreamUtils videoStreamUtils;


    public PageVO<CourseLearningVO> pageQueryCoursesInfo(PageRO<CoursesLearningRO> courseScheduleROPageRO) {
        List<String> roleList = StpUtil.getRoleList();
        if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
            // 学历教育部

        } else if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 二级学院
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            courseScheduleROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());
        } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
            courseScheduleROPageRO.getEntity().setTeachingPointName(scnuXueliTools.getUserBelongTeachingPoint().getTeachingPointName());
        }
        List<CourseRecordBO> courseSections = (List<CourseRecordBO>) redisTemplate.
                opsForValue().get("courseSections");


        return getCourseData(courseSections, courseScheduleROPageRO);
    }

    public PageVO<CourseLearningVO> pageQueryCoursesInfo1(PageRO<CoursesLearningRO> courseScheduleROPageRO) {
        List<String> roleList = StpUtil.getRoleList();
        if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
            // 学历教育部

        } else if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 二级学院
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            courseScheduleROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());
        } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {

        }
        List<CourseRecordBO> courseSections = (List<CourseRecordBO>) redisTemplate.opsForValue().get("courseSections1");


        return getCourseData(courseSections, courseScheduleROPageRO);
    }

    private PageVO getCourseData(List<CourseRecordBO> courseSections, PageRO<CoursesLearningRO> courseScheduleROPageRO) {

        courseSections = getCourseSections(null);
//        if (courseSections == null) {
//            // 可以选择从数据库加载数据，或者返回错误/空结果
//            List<CourseLearningVO> courseLearningVOS = getBaseMapper().selectCourseLearningData(courseScheduleROPageRO.getEntity(),
//                    courseScheduleROPageRO.getPageNumber() - 1, courseScheduleROPageRO.getPageSize());
//            PageVO pageVO = new PageVO<CourseLearningVO>();
//            pageVO.setRecords(courseLearningVOS);
//            pageVO.setSize(courseScheduleROPageRO.getPageSize());
//            pageVO.setCurrent(courseScheduleROPageRO.getPageNumber());
//            pageVO.setTotal(getBaseMapper().selectCourseLearningDataCount(courseScheduleROPageRO.getEntity()));
//
//            return pageVO;
//        }


        // 筛选逻辑
        Stream<CourseRecordBO> filteredStream = courseSections.stream();
        CoursesLearningRO filter = courseScheduleROPageRO.getEntity();
        if (filter.getId() != null) {
            filteredStream = filteredStream.filter(c -> c.getId().equals(filter.getId()));
        }
        if (filter.getGrade() != null) {
            filteredStream = filteredStream.filter(c -> c.getGrade().equals(filter.getGrade()));
        }

        // 新增基于 classNameSet 的过滤条件
        if (filter.getClassNameSet() != null && !filter.getClassNameSet().isEmpty()) {
            List<String> classNameSet = filter.getClassNameSet();
            filteredStream = filteredStream.filter(c -> classNameSet.contains(c.getClassName()));
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

        if (filter.getTeachingPointName() != null) {
            filteredStream = filteredStream.filter(c -> filter.getTeachingPointName().equals(Optional.ofNullable(c.getTeachingPointName()).orElse("")));
        }


        if (filter.getDefaultMainTeacherUsername() != null) {
            filteredStream = filteredStream.filter(c -> c.getDefaultMainTeacherUsername().equals(filter.getDefaultMainTeacherUsername()));
        }
        if (filter.getCourseStartTime() != null) {
            filteredStream = filteredStream.filter(c -> c.getStartTime() != null && !c.getStartTime().before(filter.getCourseStartTime()));
        }
        if (filter.getCourseEndTime() != null) {
            filteredStream = filteredStream.filter(c -> c.getStartTime() != null && !c.getStartTime().after(filter.getCourseEndTime()));
        }

        // 使用 Java Streams 来获取不同 id 的数量



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
        List<CourseLearningVO> pagedCourseLearningVOs = sortedList.subList((int) start, (int) end);

        for(CourseLearningVO courseLearningVO : pagedCourseLearningVOs){
            List<CourseAssistantsPO> courseAssistantsPOS = courseAssistantsService.getBaseMapper().selectList(new LambdaQueryWrapper<CourseAssistantsPO>()
                    .eq(CourseAssistantsPO::getCourseId, courseLearningVO.getId()));

            // 提取 userNames 并转换为 Set
            Set<String> userNames = courseAssistantsPOS.stream()
                    .map(CourseAssistantsPO::getUsername) // 假设 CourseAssistantsPO 有一个 getUserName 方法
                    .collect(Collectors.toSet());

            // 创建 TeacherInformationSearchRO 对象并设置 userNames
            TeacherInformationSearchRO searchRO = new TeacherInformationSearchRO()
                    .setUsernames(userNames); // 假设 setUsernames 方法接受 Set 类型参数
            if(userNames.size() == 0){
                courseLearningVO.setAssistants(new ArrayList<>());
                continue;
            }
            // 使用 searchRO 进行进一步的操作
            List<TeacherInformationPO> teacherInformationPOList = teacherInformationService.
                    getBaseMapper().selectTeacherInfo(searchRO);

            List<TeacherInfoVO> teacherInfoVOS = new ArrayList<>();
            for(TeacherInformationPO teacherInformationPO : teacherInformationPOList){
                TeacherInfoVO teacherInfoVO = new TeacherInfoVO();
                teacherInfoVO.setName(teacherInformationPO.getName());
                teacherInfoVO.setTeacherUsername(teacherInformationPO.getTeacherUsername());
                teacherInfoVOS.add(teacherInfoVO);
            }
            courseLearningVO.setAssistants(teacherInfoVOS);
        }

        // 构建分页响应
        PageVO<CourseLearningVO> pageVO = new PageVO<>();
        pageVO.setRecords(pagedCourseLearningVOs);
        pageVO.setSize(courseScheduleROPageRO.getPageSize());
        pageVO.setCurrent(courseScheduleROPageRO.getPageNumber());
        pageVO.setTotal((long) groupedAndAggregated.size());

        return pageVO;
    }


    public List<CourseRecordBO> getCourseSections(PageRO<CoursesLearningRO> courseScheduleROPageRO) {
        return getBaseMapper().getCourseSectionsData();
    }

    private CourseLearningVO aggregateToCourseLearningVO(List<CourseRecordBO> records) {
        CourseLearningVO vo = new CourseLearningVO();
        if (!records.isEmpty()) {
            CourseRecordBO representative = records.get(0);
            vo.setId(representative.getId());
            vo.setCourseName(representative.getCourseName());
            vo.setCourseType(representative.getCourseType());
            vo.setCourseDescription(representative.getCourseDescription());
            vo.setCourseCoverUrl(representative.getCourseCoverUrl());
            vo.setDefaultMainTeacherUsername(representative.getDefaultMainTeacherUsername());
            vo.setDefaultMainTeacherName(representative.getName());
            vo.setCourseIdentifier(representative.getCourseIdentifier());
            vo.setValid(representative.getValid());
            vo.setChannelId(representative.getChannelId());
            vo.setCreatedTime(representative.getCreatedTime());
            vo.setUpdatedTime(representative.getUpdatedTime());
            vo.setYear(representative.getYear());

            // 获取 classNames
            Set<String> classNames = records.stream()
                    .map(CourseRecordBO::getClassName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            vo.setClassNames(String.join(", ", classNames));

            // 获取 grades
            Set<String> grades = records.stream()
                    .map(CourseRecordBO::getGrade)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            vo.setGrades(String.join(", ", grades));

            // 获取 colleges
            Set<String> colleges = records.stream()
                    .map(CourseRecordBO::getCollege)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            vo.setColleges(String.join(", ", colleges));

            // 获取 CourseRecordBO
            Set<String> majorNames = records.stream()
                    .map(CourseRecordBO::getMajorName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            vo.setMajorNames(String.join(", ", majorNames));

            // 获取 colleges
            Set<String> teachingPointNames = records.stream()
                    .map(CourseRecordBO::getTeachingPointName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            vo.setTeachingPointName(String.join(", ", teachingPointNames));

            // 获取当前时间
            Date now = new Date();

            // 获取最近的开始时间，首先尝试获取大于等于当前时间的最小开始时间
            Date recentStartTime = records.stream()
                    .map(CourseRecordBO::getStartTime)
                    .filter(Objects::nonNull)
                    .filter(startTime -> !startTime.before(now)) // 过滤出未来的时间
                    .min(Date::compareTo)
                    .orElseGet(() ->
                            // 如果没有未来的开始时间，获取过去的最接近当前时间的开始时间
                            records.stream()
                                    .map(CourseRecordBO::getStartTime)
                                    .filter(Objects::nonNull)
                                    .filter(startTime -> startTime.before(now)) // 过滤出过去的时间
                                    .max(Date::compareTo)
                                    .orElse(null));

            Date closestStartTime = records.stream()
                    .map(CourseRecordBO::getStartTime)
                    .filter(Objects::nonNull)
                    .min((date1, date2) -> {
                        long diff1 = Math.abs(date1.getTime() - now.getTime());
                        long diff2 = Math.abs(date2.getTime() - now.getTime());
                        return Long.compare(diff1, diff2);
                    })
                    .orElse(null); // 如果没有找到合适的时间，返回 null

            vo.setRecentCourseScheduleTime(closestStartTime);
        }

        return vo;

    }

    /**
     * 创建 课程
     *
     * @param courseLearningCreateRO
     * @return
     */
    public boolean createCourse(CourseLearningCreateRO courseLearningCreateRO) {
        try {
            CoursesLearningPO coursesLearningPO = new CoursesLearningPO();
            coursesLearningPO.setCourseName(convertListToString(courseLearningCreateRO.getCourseNames()));
            coursesLearningPO.setCourseType(courseLearningCreateRO.getCourseType());
            // 获取当前年份
            int currentYear = LocalDate.now().getYear();
            coursesLearningPO.setGrade(String.valueOf(currentYear));
            coursesLearningPO.setCourseDescription(courseLearningCreateRO.getCourseDescription());

            if (!isValidCourseType(courseLearningCreateRO.getCourseType())) {
                throw new IllegalArgumentException("无效的课程类型: " + courseLearningCreateRO.getCourseType());
            }
            coursesLearningPO.setCourseType(courseLearningCreateRO.getCourseType());

            /**
             * 将班级做好映射 先校验 合法后 再存入数据库
             */
            List<CoursesClassMappingPO> coursesClassMappingPOS = new ArrayList<>();
            if(courseLearningCreateRO.getClassIdentifier() != null && courseLearningCreateRO.getClassIdentifier().size() > 0){
                for (String s : courseLearningCreateRO.getClassIdentifier()) {
                    ClassInformationPO classInformationPO = classInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                            .eq(ClassInformationPO::getClassIdentifier, s));
                    if (classInformationPO == null) {
                        throw new RuntimeException("传入的班级前缀找不到，非法前缀 ");
                    }
                    CoursesClassMappingPO classMappingPO = new CoursesClassMappingPO();
                    classMappingPO.setClassIdentifier(classInformationPO.getClassIdentifier());
                    coursesClassMappingPOS.add(classMappingPO);
                }
            }

            // 对传入的主讲教师 ID 做校验
            String defaultMainTeacherUsername = courseLearningCreateRO.getDefaultMainTeacherUsername();
            if (!isValidUsername(defaultMainTeacherUsername)) {
                throw new RuntimeException("教师用户名为空 或者 数据库中找不到 " + defaultMainTeacherUsername);
            }
            coursesLearningPO.setDefaultMainTeacherUsername(defaultMainTeacherUsername);

            List<CourseAssistantsPO> courseAssistantsPOS = new ArrayList<>();
            boolean assistantInsert1 = false;
            if(courseLearningCreateRO.getAssistantUsername() != null){
                assistantInsert1 = true;
                for (String s : courseLearningCreateRO.getAssistantUsername()) {
                    if (!isValidUsername(s)) {
                        throw new RuntimeException("助教用户名为空 或者 数据库中找不到 " + s);
                    }
                    CourseAssistantsPO courseAssistantsPO = new CourseAssistantsPO().setUsername(s);
                    courseAssistantsPOS.add(courseAssistantsPO);
                }
            }

            // 检测用户是否上传了 课程封面
            if(courseLearningCreateRO.getCourseCover() != null){
                if (courseLearningCreateRO.getCourseCover().getOriginalFilename() == null) {
                    throw new RuntimeException("课程文件不能为空");
                }
                if (courseLearningCreateRO.getCourseCover().getOriginalFilename() == null) {
                    throw new RuntimeException("课程封面图名称不能为空");
                }
                String uniqueFileName = generateUniqueFileName(coursesLearningPO, courseLearningCreateRO.getCourseCover().getOriginalFilename());
                try (InputStream inputStream = courseLearningCreateRO.getCourseCover().getInputStream()) {
                    boolean uploadSuccess = minioService.uploadStreamToMinio(inputStream, uniqueFileName, minioCourseCoverDir);
                    if (uploadSuccess) {
                        coursesLearningPO.setCourseCoverUrl(minioCourseCoverDir + "/" + uniqueFileName);
                    } else {
                        throw new RuntimeException("将课程封面文件上传到 Minio 失败 " + uploadSuccess);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("将课程封面文件上传到 Minio 失败 " + "文件读取失败: " + e);
                }
            }

            // 设置该门课有效
            coursesLearningPO.setValid("Y");

            int insert = getBaseMapper().insert(coursesLearningPO);
            if (insert <= 0) {
                throw new RuntimeException("课程信息插入失败 " + insert);
            }
            Long courseId = coursesLearningPO.getId(); // 假设 getId() 方法能获取到自动生成的主键

            if(assistantInsert1){
                // 设置每个 CourseAssistantsPO 的 courseId 并收集起来
                courseAssistantsPOS = courseLearningCreateRO.getAssistantUsername().stream()
                        .filter(this::isValidUsername)
                        .map(s -> new CourseAssistantsPO().setCourseId(courseId).setUsername(s))
                        .collect(Collectors.toList());

                // 批量插入 CourseAssistantsPOS
                if (courseAssistantsPOS.size() != 0) {
                    boolean assistantInsert = courseAssistantsService.saveBatch(courseAssistantsPOS);
                    if (!assistantInsert) {
                        throw new RuntimeException("批量插入助教失败");
                    }
                }
            }

            List<CoursesClassMappingPO> coursesClassMappingPOList = courseLearningCreateRO.getClassIdentifier().stream()
                    .filter(this::isValidClassName)
                    .map(s -> new CoursesClassMappingPO().setCourseId(courseId).setClassIdentifier(s))
                    .collect(Collectors.toList());
            if (coursesClassMappingPOList.size() != 0) {
                boolean assistantInsert = coursesClassMappingService.saveBatch(coursesClassMappingPOList);
                if (!assistantInsert) {
                    throw new RuntimeException("批量插入课程班级映射失败");
                }
            }

            // 如果该门课程的类型是直播 或者 混合类型 则需要创建一个直播间给它
            if (coursesLearningPO.getCourseType().equals(CourseContentType.MIX.getContentType()) ||
                    coursesLearningPO.getCourseType().equals(CourseContentType.LIVING.getContentType())) {

                //只有创建直播间的时候才需要查询学生信息。
                List<StudentWhiteListInfoBO> studentWhiteListInfoBOList = new ArrayList<>();
                for (CoursesClassMappingPO coursesClassMappingPO : coursesClassMappingPOList) {
                    List<StudentWhiteListInfoBO> studentWhiteListInfoBOList1 =
                            studentStatusService.getBaseMapper().selectLivingWhiteList(
                                    new StudentStatusFilterRO().setClassIdentifier(
                                            coursesClassMappingPO.getClassIdentifier()));
                    studentWhiteListInfoBOList.addAll(studentWhiteListInfoBOList1);
                }

                // 获取当前时间
                LocalDateTime now = LocalDateTime.now().plusHours(1);

                // 在当前时间基础上加两个小时
                LocalDateTime twoHoursLater = now.plusHours(2);

                // 将LocalDateTime转换为Date
                Date startDate = java.sql.Timestamp.valueOf(now);
                Date endDate = java.sql.Timestamp.valueOf(twoHoursLater);

                ApiResponse channel = singleLivingSetting.createChannel(
                        coursesLearningPO.getCourseName(),
                        startDate,
                        endDate,
                        true,
                        "N"
                );

                if (Integer.valueOf(200).equals(channel.getCode())) {
                    ChannelResponseData channelResponseData = channel.getData();
                    VideoStreamRecordPO videoStreamRecordPO = new VideoStreamRecordPO();
                    videoStreamRecordPO.setChannelId("" + channelResponseData.getChannelId());

//                    ChannelInfoResponse channelInfoByChannelId1 = videoStreamUtils.getChannelInfo("" + channelResponseData.getChannelId());
//                    log.info("频道信息包括 " + channelInfoByChannelId1);
//                    if (Integer.valueOf(200).equals(channelInfoByChannelId1.getCode()) && channelInfoByChannelId1.getSuccess()) {
                    String channelId = videoStreamRecordPO.getChannelId();
                    log.info("创建频道成功"+channelId);
                    LiveResourcesPO liveResourcesPO = new LiveResourcesPO().setCourseId(courseId)
                            .setChannelId(channelId).setValid("Y");
                    int insert1 = liveResourceService.getBaseMapper().insert(liveResourcesPO);
                    if (insert1 <= 0) {
                        throw new RuntimeException("保存直播资源失败 " + insert1);
                    }

                    importWhiteStudents(studentWhiteListInfoBOList, channelId);
//                    }
                }
            }

        } catch (Exception e) {
            log.info(StpUtil.getLoginIdAsString() + " 创建课程失败 " + e);
            return false;
        }


        return true;
    }

    @Async
    protected void importWhiteStudents(List<StudentWhiteListInfoBO> students, String channelId) {
        // 跑导入白名单的逻辑就好了
        ChannelInfoRequest channelInfoRequest = new ChannelInfoRequest();
        channelInfoRequest.setChannelId(channelId);
        channelInfoRequest.setStudentWhiteList(new ArrayList<>());
        for (StudentWhiteListInfoBO studentWhiteListInfoBO : students) {
            channelInfoRequest.getStudentWhiteList().add(new StudentWhiteListVO()
                            .setCode(studentWhiteListInfoBO.getIdNumber())
                            .setName(studentWhiteListInfoBO.getName())
                    );
        }
        SaResult saResult1 = singleLivingService.addChannelWhiteStudentByFile(channelInfoRequest);
        if (!Integer.valueOf(200).equals(saResult1.getCode())){
            log.info(channelId + "添加白名单失败");
            throw new RuntimeException("添加频道白名单失败，请联系管理员");
        }

        LiveUpdateChannelAuthRequest liveUpdateChannelAuthRequest = new LiveUpdateChannelAuthRequest();
        Boolean liveUpdateChannelAuthResponse;
        try {
            LiveChannelSettingRequest.AuthSetting authSetting = new LiveChannelSettingRequest.AuthSetting().
                    setAuthType(LiveConstant.AuthType.PHONE.getDesc())
                    .setRank(1)
                    .setEnabled("Y")
                    .setAuthTips("请输入你的身份证号码");

            LiveChannelSettingRequest.AuthSetting authSetting2 = new LiveChannelSettingRequest.AuthSetting().setAuthType(
                    LiveConstant.AuthType.DIRECT.getDesc())
                    .setRank(2)
                    .setEnabled("Y")
                    .setDirectKey(RandomUtil.randomString(8));

            List<LiveChannelSettingRequest.AuthSetting> authSettings = new ArrayList<>();
            authSettings.add(authSetting);
            authSettings.add(authSetting2);

            liveUpdateChannelAuthRequest.setChannelId(channelId).setAuthSettings(authSettings);
            liveUpdateChannelAuthResponse = new LiveWebAuthServiceImpl().updateChannelAuth(liveUpdateChannelAuthRequest);
            //如果返回结果不为空并且为true，说明修改成功
            if (liveUpdateChannelAuthResponse != null && liveUpdateChannelAuthResponse) {
                log.info(channelId+"设置白名单观看条件成功");
            }else {
                throw new RuntimeException("设置白名单观看条件失败");
            }
        } catch (Exception e) {
            log.error("添加白名单与设置频道的白名单接口调用异常，异常信息为", e);
        }
        log.info("导入白名单结果 " + saResult1.getCode() + " " + saResult1.getMsg());
        // 间隔性扫描  我们存在学籍异动的学生  比如说 这个学生 它转专业了
        // 扫描 每个直播间 所对应的课程的班级映射 里面的所有学生 + 重修的学生 是否与保利威白名单的学生一致
        // OA 转专业这个事件的时候 它就会去找这个学生所在的所有课程 （每门课程只有一个直播间）
//        SaResult saResult = singleLivingSetting.addChannelWhiteStudent(channelId, students);
    }


    private String convertListToString(List<String> courseNames) {
        if (courseNames == null || courseNames.isEmpty()) {
            return "";
        }
        return String.join(", ", courseNames);
    }

    /**
     * 根据用户上传的课程封面图 来重命名课程封面在 Minio 中的存储值
     *
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
     *
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

    private boolean isValidClassName(String className) {
        if (StringUtils.isBlank(className)) {
            return false;
        }
        ClassInformationPO classInformationPO = classInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                .eq(ClassInformationPO::getClassIdentifier, className));
        if (classInformationPO != null) {
            return true;
        }
        return false;
    }

    /**
     * 对 前端传入的 教师用户名做校验
     *
     * @param username
     * @return
     */
    private boolean isValidUsername(String username) {
        if (StringUtils.isBlank(username)) {
            return false;
        }
        TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getTeacherUsername, username));
        if (teacherInformationPO != null) {
            return true;
        }
        return false;
    }


    /**
     * 删除课程 包含删除该课程的信息 、Section、助教信息、每一个 Section 对应的资源信息
     *
     * @param courseId
     * @return
     */
    public SaResult deleteCourse(Long courseId) {
        try {
            if (getBaseMapper().selectCount(new LambdaQueryWrapper<CoursesLearningPO>()
                    .eq(CoursesLearningPO::getId, courseId)) == 0) {
                // 没有这门课 说明已经被删除了
                return SaResult.ok("已删除 无需再删除");
            }
            // 先删除 Section
            List<SectionsPO> sectionsPOS = sectionsService.getBaseMapper().selectList(new LambdaQueryWrapper<SectionsPO>()
                    .eq(SectionsPO::getCourseId, courseId));
            // 使用一个直播的标志 来标识该门课是否有直播间 有的话需要调用保利威来删除直播间
            boolean hasLivingRoom = false;

            for (SectionsPO sectionsPO : sectionsPOS) {
                if (sectionsPO.getContentType().equals(CourseContentType.NODE)) {
                    // 父节点 无意义 直接删除
                } else if (sectionsPO.getContentType().equals(CourseContentType.LIVING)) {
                    hasLivingRoom = true;
                    // 开启直播间标志 让其统一删除这门课的所有直播间资源
                } else if (sectionsPO.getContentType().equals(CourseContentType.VIDEO)) {
                    if (sectionsPO.getContentId() != null) {
                        // contentID 不为空 则说明指向了点播视频 资源
                        // 一个 Section 最多对应一个视频
                        int delete = videoResourcesService.getBaseMapper().delete(new LambdaQueryWrapper<VideoResourcesPO>()
                                .eq(VideoResourcesPO::getSectionId, sectionsPO.getId()));
                    }
                    // 为空 说明没有分配视频资源
                } else if (sectionsPO.getContentType().equals(CourseContentType.OFF_LINE)) {
                    // 线下 说明存储了一些 其他资料 附件 这个暂时没做
                }
                int i = sectionsService.getBaseMapper().deleteById(sectionsPO.getId());
            }

            Integer livingRoomCount = liveResourceService.getBaseMapper().selectCount(new LambdaQueryWrapper<LiveResourcesPO>()
                    .eq(LiveResourcesPO::getCourseId, courseId));

            // 统一删除直播间资源
            if (hasLivingRoom || livingRoomCount > 0) {
                List<LiveResourcesPO> liveResourcesPOS = liveResourceService.getBaseMapper().selectList(new LambdaQueryWrapper<LiveResourcesPO>()
                        .eq(LiveResourcesPO::getCourseId, courseId));
                for (LiveResourcesPO liveResourcesPO : liveResourcesPOS) {
                    String channelId = liveResourcesPO.getChannelId();
                    try {
                        SaResult saResult = singleLivingService.deleteChannel(channelId);
                        if (!saResult.getCode().equals(ResultCode.SUCCESS.getCode())) {
                            log.error("删除保利威直播间失败 " + saResult.getMsg());
                        }
                    } catch (Exception e) {
                        log.info("删除直播间失败 " + e);
                    }
                    // 清除直播资源映射记录
                    int i = liveResourceService.getBaseMapper().deleteById(liveResourcesPO.getId());
                }
            }

            // 删除助教信息
            int delete = courseAssistantsService.getBaseMapper().delete(new LambdaQueryWrapper<CourseAssistantsPO>()
                    .eq(CourseAssistantsPO::getCourseId, courseId));

            int delete1 = coursesClassMappingService.getBaseMapper().delete(new LambdaQueryWrapper<CoursesClassMappingPO>()
                    .eq(CoursesClassMappingPO::getCourseId, courseId));

            // 删除课程本身
            int i = getBaseMapper().deleteById(courseId);

            return SaResult.ok("成功删除");
        } catch (Exception e) {
            log.error("删除课程失败 " + e);
            return SaResult.error(ResultCode.UPDATE_COURSE_FAIL6.getMessage()).setCode(ResultCode.UPDATE_COURSE_FAIL6.getCode());
        }
    }


    /**
     * 设置这门课是否有效
     *
     * @param courseId
     * @return
     */
    public boolean setCourseInvalid(Long courseId) {
        CoursesLearningPO coursesLearningPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                .eq(CoursesLearningPO::getId, courseId));
        if (coursesLearningPO.getValid().equals("Y")) {
            coursesLearningPO.setValid("N");
        } else {
            coursesLearningPO.setValid("Y");
        }
        int i = getBaseMapper().updateById(coursesLearningPO);
        if (i > 0) {
            return true;
        }
        return false;
    }

    public PageVO<CourseLearningStudentInfoVO> getCourseStudentsInfo(PageRO<CourseStudentSearchRO> courseStudentSearchROPageRO) {
        List<String> roleList = StpUtil.getRoleList();
        if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
            // 学历教育部

        } else if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 二级学院
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            courseStudentSearchROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());
        } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
            TeachingPointInformationPO userBelongTeachingPoint = scnuXueliTools.getUserBelongTeachingPoint();
            courseStudentSearchROPageRO.getEntity().setTeachingPointName(userBelongTeachingPoint.getTeachingPointName());
        }

        List<CourseLearningStudentInfoVO> courseLearningStudentInfoVOList = new ArrayList<>();
        if("N".equals(courseStudentSearchROPageRO.getEntity().getIsRetake())){
            courseLearningStudentInfoVOList = getBaseMapper().selectCourseStudentsInfo(courseStudentSearchROPageRO.getEntity());
        }else if("Y".equals(courseStudentSearchROPageRO.getEntity().getIsRetake())){
            courseLearningStudentInfoVOList = getBaseMapper().
                    selectCourseRetakeStudentsInfo(courseStudentSearchROPageRO.getEntity());
        }else{
            courseLearningStudentInfoVOList = getBaseMapper().selectCourseStudentsInfo(courseStudentSearchROPageRO.getEntity());
            courseLearningStudentInfoVOList.addAll(getBaseMapper().selectCourseRetakeStudentsInfo(courseStudentSearchROPageRO.getEntity()));
        }

        // 将一门课里面的所有学生 存起来 再排序 再分页
        // 排序 - 基于多个字段
        courseLearningStudentInfoVOList.sort(Comparator.comparing(CourseLearningStudentInfoVO::getGrade).reversed()
                .thenComparing(CourseLearningStudentInfoVO::getCollege)
                .thenComparing(CourseLearningStudentInfoVO::getMajorName)
                .thenComparing(CourseLearningStudentInfoVO::getStudyForm)
                .thenComparing(CourseLearningStudentInfoVO::getLevel)
                .thenComparing(CourseLearningStudentInfoVO::getClassName));

        // 分页参数
        Long pageNumber = courseStudentSearchROPageRO.getPageNumber();
        Long pageSize = courseStudentSearchROPageRO.getPageSize();

        // 分页逻辑
        int total = courseLearningStudentInfoVOList.size();
        int startIndex = (int) ((pageNumber - 1) * pageSize);
        int endIndex = (int) Math.min(startIndex + pageSize, total);

        List<CourseLearningStudentInfoVO> pagedCourseLearningStudentInfoVOList = courseLearningStudentInfoVOList.subList(startIndex, endIndex);

        // 构造分页对象
        PageVO<CourseLearningStudentInfoVO> pageVO = new PageVO<>();
        pageVO.setRecords(pagedCourseLearningStudentInfoVOList);
        pageVO.setSize(pageSize);
        pageVO.setCurrent(pageNumber);
        pageVO.setTotal((long) total);

        return pageVO;

    }

    /**
     *
     * 修改课程信息 仅允许修改主讲、助教、以及上课的班级
     * 当然 课程名称集合发生了 变化 那么 课程的总名称也会变化
     * 包括课程封面、课程简介
     * @param coursesLearningROPageRO
     * @return
     */
    public SaResult updateCourse(CourseLearningCreateRO coursesLearningROPageRO) {
        // 对参数进行校验
        if(coursesLearningROPageRO.getCourseId() == null){
            return SaResult.error(ResultCode.UPDATE_COURSE_FAIL1.getMessage()).
                    setCode(ResultCode.UPDATE_COURSE_FAIL1.getCode());
        }
        CoursesLearningPO coursesLearningPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                .eq(CoursesLearningPO::getId, coursesLearningROPageRO.getCourseId()));
        if(coursesLearningPO == null){
            return SaResult.error(ResultCode.UPDATE_COURSE_FAIL2.getMessage()).
                    setCode(ResultCode.UPDATE_COURSE_FAIL2.getCode());
        }

        // 校验主讲是否一致 不一致就修改
        if(!coursesLearningROPageRO.getDefaultMainTeacherUsername().equals(coursesLearningPO.getDefaultMainTeacherUsername())){
            TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                    .eq(TeacherInformationPO::getTeacherUsername, coursesLearningROPageRO.getDefaultMainTeacherUsername()));
            coursesLearningPO.setDefaultMainTeacherUsername(teacherInformationPO.getTeacherUsername());
        }

        // 校验助教集合是否一致
        // 获取现有的助教列表
        List<CourseAssistantsPO> courseAssistantsPOS = courseAssistantsService.getBaseMapper().selectList(
                new LambdaQueryWrapper<CourseAssistantsPO>().eq(CourseAssistantsPO::getCourseId, coursesLearningPO.getId()));

        // 获取前端传递的助教用户名列表
        List<String> assistantUsername = coursesLearningROPageRO.getAssistantUsername();
        boolean classInfoChange = false;
        if(coursesLearningROPageRO.getCourseNames() != null && coursesLearningROPageRO.getCourseNames().size() > 0){
            List<String> courseNames = coursesLearningROPageRO.getCourseNames();
            String s = courseNames.stream()
                    .collect(Collectors.joining(", "));
            coursesLearningPO.setCourseName(s);
            classInfoChange = true;
        }else{
            coursesLearningPO.setCourseName("课程名未命名");
        }

        // 检查是否需要清空所有助教信息
        if (assistantUsername == null || assistantUsername.isEmpty()) {
            // 清空原有的助教信息
            for (CourseAssistantsPO existingAssistant : courseAssistantsPOS) {
                courseAssistantsService.getBaseMapper().deleteById(existingAssistant.getId());
            }
        } else {
            // 校验每个用户名是否都存在于教师信息中
            boolean allUsernamesValid = assistantUsername.stream().allMatch(username ->
                    teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                            .eq(TeacherInformationPO::getTeacherUsername, username)) != null);

            // 如果所有用户名都有效
            if (allUsernamesValid) {
                // 清空原有的映射关系
                for (CourseAssistantsPO existingAssistant : courseAssistantsPOS) {
                    courseAssistantsService.getBaseMapper().deleteById(existingAssistant.getId());
                }

                // 为每个有效用户名创建新的映射关系
                for (String validUsername : assistantUsername) {
                    CourseAssistantsPO newAssistant = new CourseAssistantsPO()
                            .setCourseId(coursesLearningPO.getId())
                            .setUsername(validUsername);
                    courseAssistantsService.getBaseMapper().insert(newAssistant);
                }
            }
        }

        if(classInfoChange){
            // 如果班级信息集合 不同 则需要清空 班级信息 并且删除 保利威的白名单 进行重新覆盖
            // 获取前端传递的 classIdentifier 列表
            // 获取前端传递的 classIdentifier 列表
            List<String> classIdentifier = coursesLearningROPageRO.getClassIdentifier();

            // 获取现有的班级与课程映射关系
            List<CoursesClassMappingPO> coursesClassMappingPOS = coursesClassMappingService.getBaseMapper().selectList(
                    new LambdaQueryWrapper<CoursesClassMappingPO>()
                            .eq(CoursesClassMappingPO::getCourseId, coursesLearningPO.getId())
            );

            // 将前端传来的列表和数据库中的映射转换为集合
            Set<String> existingClassIdentifiers = coursesClassMappingPOS.stream()
                    .map(CoursesClassMappingPO::getClassIdentifier)
                    .collect(Collectors.toSet());
            Set<String> newClassIdentifiers = new HashSet<>(classIdentifier);

            // 计算交集、差集
            Set<String> toDelete = new HashSet<>(existingClassIdentifiers);
            toDelete.removeAll(newClassIdentifiers); // 差集：需要删除的元素

            Set<String> toAdd = new HashSet<>(newClassIdentifiers);
            toAdd.removeAll(existingClassIdentifiers); // 差集：需要添加的元素

            // 删除不再需要的映射
            for (CoursesClassMappingPO existingMapping : coursesClassMappingPOS) {
                if (toDelete.contains(existingMapping.getClassIdentifier())) {
                    coursesClassMappingService.getBaseMapper().deleteById(existingMapping.getId());
                }
            }

            // 添加新映射
            for (String newClassIdentifier : toAdd) {
                // 添加班级和课程的映射
                CoursesClassMappingPO coursesClassMappingPO = new CoursesClassMappingPO()
                        .setClassIdentifier(newClassIdentifier)
                        .setCourseId(coursesLearningPO.getId())
                        ;
                int insert = coursesClassMappingService.getBaseMapper().insert(coursesClassMappingPO);


                List<StudentWhiteListInfoBO> studentWhiteListInfoBOList =
                        studentStatusService.getBaseMapper().selectLivingWhiteList(
                                new StudentStatusFilterRO().setClassIdentifier(
                                        newClassIdentifier));
                // 清除以前的白名单 导入新的白名单
                List<StudentWhiteListVO> listVOS = new ArrayList<>();
                for(StudentWhiteListInfoBO studentWhiteListInfoBO: studentWhiteListInfoBOList){
                    StudentWhiteListVO studentWhiteListVO = new StudentWhiteListVO()
                            .setCode(studentWhiteListInfoBO.getIdNumber())
                            .setName(studentWhiteListInfoBO.getName())
                            ;
                    listVOS.add(studentWhiteListVO);
                }

                SaResult saResult = singleLivingService.addChannelWhiteStudent(new ChannelInfoRequest()
                        .setChannelId(getLiveCourseChannelId(coursesLearningPO.getId()))
                        .setStudentWhiteList(listVOS));
            }

            for (String newClassIdentifier : toDelete) {
                List<StudentWhiteListInfoBO> studentWhiteListInfoBOList =
                        studentStatusService.getBaseMapper().selectLivingWhiteList(
                                new StudentStatusFilterRO().setClassIdentifier(
                                        newClassIdentifier));
                List<String> idNumbers = new ArrayList<>();
                // 将学生的 ID 号码添加到 idNumbers 列表中
                if (studentWhiteListInfoBOList != null && !studentWhiteListInfoBOList.isEmpty()) {
                    List<String> currentIdNumbers = studentWhiteListInfoBOList.stream()
                            .map(StudentWhiteListInfoBO::getIdNumber)
                            .collect(Collectors.toList());

                    idNumbers.addAll(currentIdNumbers);
                }

                try {
                    SaResult saResult = singleLivingService.deleteChannelWhiteStudent(new ChannelInfoRequest()
                            .setChannelId(getLiveCourseChannelId(coursesLearningPO.getId()))
                            .setIsClear("N")
                            .setDeleteCodeList(idNumbers));
                }catch (net.polyv.common.v1.exception.PloyvSdkException e){
                    // 出现删除异常，可能是因为全部删除了
                    SaResult saResult = singleLivingService.deleteChannelWhiteStudent(new ChannelInfoRequest()
                            .setChannelId(getLiveCourseChannelId(coursesLearningPO.getId()))
                            .setIsClear("Y")
                            .setDeleteCodeList(idNumbers));
                }
            }

            if("Y".equals(coursesLearningROPageRO.getFreshWhiteList())){
                // 强制刷新白名单 首先获取 班级里的所有学生 以及 重修名单 直接覆盖重写
                List<CourseLearningStudentInfoVO> courseLearningStudentInfoVOList = getBaseMapper().selectCourseStudentsInfo(new CourseStudentSearchRO().setCourseId(coursesLearningROPageRO.getCourseId()));
                courseLearningStudentInfoVOList.addAll(getBaseMapper().selectCourseRetakeStudentsInfo(new CourseStudentSearchRO().setCourseId(coursesLearningROPageRO.getCourseId())));

                String liveCourseChannelId = getLiveCourseChannelId(coursesLearningPO.getId());
                // 全部清空
                SaResult saResult = singleLivingService.deleteChannelWhiteStudent(new ChannelInfoRequest()
                        .setChannelId(liveCourseChannelId)
                        .setDeleteCodeList(new ArrayList<>())
                        .setIsClear("Y")
                );
                List<StudentWhiteListVO> studentWhiteListVOList = new ArrayList<>();
                for(CourseLearningStudentInfoVO courseLearningStudentInfoVO: courseLearningStudentInfoVOList){
                    String name = courseLearningStudentInfoVO.getName();
                    String idNumber = courseLearningStudentInfoVO.getIdNumber();
                    StudentWhiteListVO studentWhiteListVO = new StudentWhiteListVO()
                            .setCode(idNumber)
                            .setName(name)
                            ;
                    studentWhiteListVOList.add(studentWhiteListVO);
                }

                SaResult saResult1 = singleLivingService.addChannelWhiteStudent(new ChannelInfoRequest().setChannelId(liveCourseChannelId)
                        .setStudentWhiteList(studentWhiteListVOList));
            }
        }

        // 校验课程简介是否 相同 不同则替换
        String courseDescription = coursesLearningPO.getCourseDescription();
        if(courseDescription != coursesLearningROPageRO.getCourseDescription()){
            coursesLearningPO.setCourseDescription(coursesLearningROPageRO.getCourseDescription());
        }

        // 图片不为空 直接替换
        if(coursesLearningROPageRO.getCourseCover() != null){
            MultipartFile courseCover = coursesLearningROPageRO.getCourseCover();
            if (courseCover.getOriginalFilename() == null) {
                log.warn("课程封面文件为空，无法替换课程封面");
            }
            if (courseCover.getOriginalFilename() == null) {
                log.warn("课程封面图名称为空，无法替换课程封面");
            }
            String uniqueFileName = generateUniqueFileName(coursesLearningPO, courseCover.getOriginalFilename());
            try (InputStream inputStream = courseCover.getInputStream()) {
                boolean uploadSuccess = minioService.uploadStreamToMinio(inputStream, uniqueFileName, minioCourseCoverDir);
                // 先看看课程封面是否设置了 如果没有 直接设置 如果设置了 则需要清除
                String courseCoverUrl = coursesLearningPO.getCourseCoverUrl();
                if(courseCoverUrl != null) {
                    try {
                        minioService.deleteFileByAbsolutePath(courseCoverUrl);
                    } catch (Exception e) {
                        log.error("删除 Minio 文件失败");
                    }
                }

                if (uploadSuccess) {
                    coursesLearningPO.setCourseCoverUrl(minioCourseCoverDir + "/" + uniqueFileName);
                } else {
                    throw new RuntimeException("将课程封面文件更新到 Minio 失败 " + uploadSuccess);
                }
            } catch (IOException e) {
                throw new RuntimeException("将课程封面文件更新到 Minio 失败 " + "文件读取失败: " + e);
            }
        }

        // 更新课程的是否有效状态
        if(!coursesLearningROPageRO.getValid().equals(coursesLearningPO.getValid())){
            coursesLearningPO.setValid(coursesLearningROPageRO.getValid());
        }

        boolean b = updateById(coursesLearningPO);
        log.info("更新课程结果 " + b);

        // 刷新 redis
        List<CourseRecordBO> courseSections = getCourseSections(null);
        redisTemplate.opsForValue().set("courseSections", courseSections); // 将数据存储在 Redis 中

        return SaResult.ok();
    }

    /**
     * 获取课程章节信息
     * @param courseSectionRO
     * @return
     */
    public SaResult getCourseSectionInfo(CourseSectionRO courseSectionRO) {

        CoursesLearningPO coursesLearningPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                .eq(CoursesLearningPO::getId, courseSectionRO.getCourseId()));
        if(coursesLearningPO == null){
            return SaResult.error(ResultCode.UPDATE_COURSE_FAIL2.getMessage()).
                    setCode(ResultCode.UPDATE_COURSE_FAIL2.getCode());
        }

        List<SectionsPO> sectionsPOS = sectionsService.getBaseMapper().selectSectionsInfo(courseSectionRO);

        List<CourseSectionVO> courseSectionVOs = new ArrayList<>();

        for(SectionsPO sectionsPO: sectionsPOS){
            CourseSectionVO courseSectionVO = new CourseSectionVO();
            courseSectionVO.setId(sectionsPO.getId());
            courseSectionVO.setCourseId(sectionsPO.getCourseId());
            courseSectionVO.setCourseSectionParent(sectionsPO.getParentSectionId());
            courseSectionVO.setSequence(sectionsPO.getSequence());
            courseSectionVO.setContentType(sectionsPO.getContentType());
            courseSectionVO.setStartTime(sectionsPO.getStartTime());
            courseSectionVO.setDeadLine(sectionsPO.getDeadline());
            courseSectionVO.setSectionName(sectionsPO.getSectionName());
            courseSectionVO.setValid(sectionsPO.getValid());

            if(sectionsPO.getContentType().equals(CourseContentType.LIVING.getContentType())){
                // 直播节点
                LiveResourcesPO liveResourcesPO = liveResourceService.getBaseMapper().selectOne(new LambdaQueryWrapper<LiveResourcesPO>()
                        .eq(LiveResourcesPO::getCourseId, sectionsPO.getCourseId())
                        .eq(LiveResourcesPO::getSectionId, sectionsPO.getId())
                );
                LiveResourceVO liveResourceVO = new LiveResourceVO();
                BeanUtils.copyProperties(liveResourcesPO, liveResourceVO);
                liveResourceVO.setPlayBack(singleLivingSetting.getPlayBackState(liveResourcesPO.getChannelId()));
                courseSectionVO.setCourseSectionContentVO(liveResourceVO);

            }else if(sectionsPO.getContentType().equals(CourseContentType.VIDEO.getContentType())){
                // 直播节点
                VideoResourcesPO videoResourcesPO = videoResourcesService.getBaseMapper().selectOne(new LambdaQueryWrapper<VideoResourcesPO>()
                        .eq(VideoResourcesPO::getSectionId, sectionsPO.getId())
                );
                VideoResourceVO videoResourceVO = new VideoResourceVO();
                BeanUtils.copyProperties(videoResourcesPO, videoResourceVO);
                courseSectionVO.setCourseSectionContentVO(videoResourceVO);
            }


            // 添加主讲教师信息
            String mainTeacherUsername = sectionsPO.getMainTeacherUsername();
            TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                    .eq(TeacherInformationPO::getTeacherUsername, mainTeacherUsername));
            courseSectionVO.setMainTeacherUsername(teacherInformationPO.getTeacherUsername());
            courseSectionVO.setMainTeacherName(teacherInformationPO.getName());
            // 添加助教信息
            List<CourseAssistantsPO> courseAssistantsPOS = courseAssistantsService.getBaseMapper().selectList(
                    new LambdaQueryWrapper<CourseAssistantsPO>()
                            .eq(CourseAssistantsPO::getCourseId, sectionsPO.getCourseId()));

            // 将列表转换为流，提取用户名，并收集到 Set 中以去除重复项
            Set<String> usernames = courseAssistantsPOS.stream() // 将列表转换为流
                    .map(CourseAssistantsPO::getUsername) // 将流中的每个元素映射（转换）为用户名
                    .collect(Collectors.toSet()); // 收集结果到一个 Set<String> 中

            // 根据用户名集合查询教师信息
            List<TeacherInformationPO> teacherInformationPOS = teacherInformationService.getBaseMapper().selectTeacherInfo(
                    new TeacherInformationSearchRO().setUsernames(usernames));

            List<TeacherInfoVO> tutorList = teacherInformationPOS.stream() // 将teacherInformationPOS列表转换为Stream
                    .map(t -> {
                        TeacherInfoVO teacherInfoVO = new TeacherInfoVO(); // 创建一个新的TeacherInfoVO对象
                        // 设置TeacherInfoVO对象的属性
                        teacherInfoVO.setName(t.getName())
                                .setTeacherUsername(t.getTeacherUsername());
                        return teacherInfoVO; // 返回设置好的TeacherInfoVO对象
                    })
                    .collect(Collectors.toList()); // 收集Stream中的所有TeacherInfoVO到一个新的List
            courseSectionVO.setTutorList(tutorList);

            // 目前只处理这两种节点内容

            // 获取回放状态


            courseSectionVOs.add(courseSectionVO);
        }

        return SaResult.ok().setData(courseSectionVOs);
    }

    /**
     * 修改章节信息
     *
     * 目前不支持修改挂靠新的父节点 防止出现环
     * @param courseSectionRO
     * @return
     */
    public SaResult updateCourseSectionInfo(CourseSectionRO courseSectionRO) {
        SectionsPO sectionsPO = sectionsService.getBaseMapper().selectById(courseSectionRO.getId());
        // 如果老师发生了变化 修改老师
        if(courseSectionRO.getMainTeacherUsername() != null && !sectionsPO.getMainTeacherUsername().equals(courseSectionRO.getMainTeacherUsername())){
            TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                    .eq(TeacherInformationPO::getTeacherUsername, courseSectionRO.getMainTeacherUsername()));
            if(teacherInformationPO == null){
                return SaResult.error("修改节点信息失败 该教师找不到 " + courseSectionRO.getMainTeacherUsername());
            }
            sectionsPO.setMainTeacherUsername(courseSectionRO.getMainTeacherUsername());
        }
        // 如果上课时间发生了变化 则修改上课时间 前提是 这个时间 没有到来 即比此刻要早
        // 获取当前时间
        Date now = new Date();

        // 检查课程的开始时间和结束时间是否都在当前时间之后，以及结束时间是否在开始时间之后
        Date newStartTime = courseSectionRO.getStartTime();
        Date newDeadLine = courseSectionRO.getDeadLine();
        if (newStartTime.after(now) &&
                newDeadLine.after(now) &&
                newDeadLine.after(newStartTime)) {

            sectionsPO.setStartTime(newStartTime);
            sectionsPO.setDeadline(newDeadLine);

        } else {
            return SaResult.error("上课时间 必须晚于当前时间");
        }

        // 如果章节名字发生了改变 则更改
        if(!courseSectionRO.getSectionName().equals(sectionsPO.getSectionName()) ){
            sectionsPO.setSectionName(courseSectionRO.getSectionName());
        }

        int insert = sectionsService.getBaseMapper().updateById(sectionsPO);
        if(insert <= 0){
            SaResult.error("更新节点信息失败 插入数据库失败");
        }
        return SaResult.ok("成功修改节点信息");
    }

    /**
     * 创建课程章节信息  即为一门课第一次创建节点信息
     * @param courseSectionRO
     * @return
     */
    public SaResult createCourseSectionInfo(CourseSectionRO courseSectionRO) {
        Long courseId = courseSectionRO.getCourseId();
        CoursesLearningPO coursesLearningPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                .eq(CoursesLearningPO::getId, courseId));
        LiveResourcesPO liveResourcesPO = null;
        if(coursesLearningPO.getCourseType().equals(CourseContentType.LIVING.getContentType()) ||
                coursesLearningPO.getCourseType().equals(CourseContentType.MIX.getContentType())){
            // 如果这门课是一门直播或者混合模式的课程 那么就需要将课程的直播资源分配给指定的节点
            liveResourcesPO = liveResourceService.getBaseMapper().selectLiveResource(courseId);
        }

        return createCourseSectionInfoRecurrent(courseSectionRO, null, liveResourcesPO);
    }

    public SaResult createCourseSectionInfoRecurrent(CourseSectionRO courseSectionRO, CourseSectionRO parent, LiveResourcesPO liveResourcesPO) {

        CoursesLearningPO coursesLearningPO = getBaseMapper().selectById(courseSectionRO.getCourseId());
        if(coursesLearningPO == null){
            return SaResult.error(ResultCode.UPDATE_COURSE_FAIL5.getMessage()).setCode(ResultCode.UPDATE_COURSE_FAIL5.getCode());
        }


        if(courseSectionRO.getCourseSectionChildren() != null && !courseSectionRO.getCourseSectionChildren().isEmpty()){
            for(CourseSectionRO courseSectionRO1: courseSectionRO.getCourseSectionChildren()){
                if(courseSectionRO1.getCourseSectionChildren() != null &&
                        !courseSectionRO1.getCourseSectionChildren().isEmpty()){
                    SaResult courseSectionInfoRecurrent = createCourseSectionInfoRecurrent(courseSectionRO1, courseSectionRO, liveResourcesPO);
                }

                // 子节点深度最多三层
                if(getBaseMapper().selectCount(new LambdaQueryWrapper<CoursesLearningPO>()
                        .eq(CoursesLearningPO::getId, courseSectionRO1.getCourseId())) !=1 ){
                    throw new RuntimeException("非法的课程 ID " + courseSectionRO.getCourseId());
                }
                SectionsPO sectionsPO = new SectionsPO()
                        .setCourseId(courseSectionRO1.getCourseId())
                        .setValid("Y")
                        .setSectionName(courseSectionRO1.getSectionName())
                        .setSequence(courseSectionRO1.getSequence())
                        .setStartTime(courseSectionRO1.getStartTime())
                        .setDeadline(courseSectionRO1.getDeadLine())
                        .setContentType(getValidCourseType(courseSectionRO1.getContentType()))
                        ;
                // 对主讲老师的身份进行校验
                TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                        .eq(TeacherInformationPO::getTeacherUsername, courseSectionRO1.getMainTeacherUsername()));
                if(teacherInformationPO == null){
                    throw new RuntimeException("传递过来的教师用户名不存在 " + teacherInformationPO);
                }
                sectionsPO.setMainTeacherUsername(courseSectionRO1.getMainTeacherUsername());
                sectionsPO.setParentSectionId(parent != null ? parent.getCourseId() : null);

                int insert = sectionsService.getBaseMapper().insert(sectionsPO);
                if(insert <= 0){
                    throw new RuntimeException("插入节点失败 " + insert);
                }

                // 如果是直播类型 则需要 插入直播资源映射
                if(courseSectionRO1.getContentType().equals(CourseContentType.LIVING.getContentType())){

                    // 构造节点直播资源映射 当该节点为 直播时
                    LiveResourcesPO liveResourcesPO1 = new LiveResourcesPO()
                            .setCourseId(courseSectionRO.getCourseId())
                            .setSectionId(sectionsPO.getId())
                            .setValid("Y")
                            .setChannelId(liveResourcesPO.getChannelId())
                            ;
                    int insert1 = liveResourceService.getBaseMapper().insert(liveResourcesPO1);
                    if(insert1 <= 0){
                        throw new RuntimeException("插入直播节点 直播资源映射记录失败");
                    }
                }

            }
        }else{
            // 如果没有子节点 说明 就是 在已有课程上继续新增节点
            // 子节点深度最多三层
            if(getBaseMapper().selectCount(new LambdaQueryWrapper<CoursesLearningPO>()
                    .eq(CoursesLearningPO::getId, courseSectionRO.getCourseId())) !=1 ){
                throw new RuntimeException("非法的课程 ID " + courseSectionRO.getCourseId());
            }
            SectionsPO sectionsPO = new SectionsPO()
                    .setCourseId(courseSectionRO.getCourseId())
                    .setValid("Y")
                    .setSectionName(courseSectionRO.getSectionName())
                    .setSequence(courseSectionRO.getSequence())
                    .setStartTime(courseSectionRO.getStartTime())
                    .setDeadline(courseSectionRO.getDeadLine())
                    .setContentType(getValidCourseType(courseSectionRO.getContentType()))
                    ;
            // 对主讲老师的身份进行校验
            TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                    .eq(TeacherInformationPO::getTeacherUsername, courseSectionRO.getMainTeacherUsername()));
            if(teacherInformationPO == null){
                throw new RuntimeException("传递过来的教师用户名不存在 " + teacherInformationPO);
            }
            sectionsPO.setMainTeacherUsername(courseSectionRO.getMainTeacherUsername());
            sectionsPO.setParentSectionId(parent != null ? parent.getCourseId() : null);

            int insert = sectionsService.getBaseMapper().insert(sectionsPO);
            if(insert <= 0){
                throw new RuntimeException("插入节点失败 " + insert);
            }

            // 如果是直播类型 则需要 插入直播资源映射
            if(courseSectionRO.getContentType().equals(CourseContentType.LIVING.getContentType())){

                // 构造节点直播资源映射 当该节点为 直播时
                LiveResourcesPO liveResourcesPO1 = new LiveResourcesPO()
                        .setCourseId(courseSectionRO.getCourseId())
                        .setSectionId(sectionsPO.getId())
                        .setValid("Y")
                        .setChannelId(liveResourcesPO.getChannelId())
                        ;
                int insert1 = liveResourceService.getBaseMapper().insert(liveResourcesPO1);
                if(insert1 <= 0){
                    throw new RuntimeException("插入直播节点 直播资源映射记录失败");
                }
            }
        }

        // 在插入后 对 一门课的 Section 进行一次排序 主要是直播课
        updateSectionsSequence(coursesLearningPO.getId());


        return SaResult.ok("创建课程节点成功");
    }

    private void updateSectionsSequence(Long courseId){
        // 获取一门课的所有 Sections
        List<SectionsPO> sectionsPOS = sectionsService.getBaseMapper().selectList(new LambdaQueryWrapper<SectionsPO>()
                .eq(SectionsPO::getCourseId, courseId));

        // 根据 startTime 排序
        List<SectionsPO> sortedSectionsPOS = sectionsPOS.stream()
                .sorted(Comparator.comparing(SectionsPO::getStartTime))
                .collect(Collectors.toList());

        sortedSectionsPOS.forEach(section ->
                System.out.println(section.getStartTime()));


        // 更新 sequence，从 1 开始
        int sequence = 1;
        for (SectionsPO section : sortedSectionsPOS) {
            section.setSequence(sequence++);
        }

        // 更新到数据库，这里假设 sectionsService 提供了批量更新的方法
        // 请替换为你的实际批量更新方法
        // 注意：MyBatis Plus 默认的 updateBatchById 方法可能对大量数据的批量更新不够高效
        // 如果数据量很大，考虑分批更新或使用更高效的批量更新策略
        sectionsService.updateBatchById(sortedSectionsPOS);
    }



    /**
     * 校验输入字符串是否是 课程学习常量类的字符串
     *
     * @param courseType
     * @return
     */
    public String getValidCourseType(String courseType) {
        for (CourseContentType type : CourseContentType.values()) {
            if (type.getContentType().equals(courseType)) {
                return courseType;
            }
        }
        throw new RuntimeException("错误的节点类型 " + courseType);
    }

    /**
     * 删除 指定课程的指定节点信息 连同子节点 一并删除
     * @param courseSectionRO
     * @return
     */
    public SaResult deleteCourseSectionInfo(CourseSectionRO courseSectionRO) {
        Long courseId = courseSectionRO.getCourseId();
        Long id = courseSectionRO.getId();
        // 获取该课程 指定节点的 节点
        List<SectionsPO> sectionsPOS = sectionsService.getBaseMapper().selectList(new LambdaQueryWrapper<SectionsPO>()
                .eq(SectionsPO::getCourseId, courseId)
                .eq(SectionsPO::getId, id)
        );
        // 先删除这些节点 然后 递归删除这些节点的子节点
        if(sectionsPOS.isEmpty()){
            return SaResult.ok("该节点信息不存在 不需要删除");
        }

        int delete = sectionsService.getBaseMapper().delete(new LambdaQueryWrapper<SectionsPO>()
                .eq(SectionsPO::getCourseId, courseId)
                .eq(SectionsPO::getId, id));
        if(delete < 0){
            return SaResult.error("删除节点信息失败");
        }
        for(SectionsPO sectionsPO: sectionsPOS){
            // 删除这些节点有实际内容的 节点内容
            Long contentId = sectionsPO.getContentId();
            if(contentId != null){
                if(sectionsPO.getContentType().equals(CourseContentType.LIVING.getContentType())){
                    // 删除直播 资源映射 并非删除直播间
                    int delete1 = liveResourceService.getBaseMapper().delete(new LambdaQueryWrapper<LiveResourcesPO>()
                            .eq(LiveResourcesPO::getCourseId, sectionsPO.getCourseId())
                            .eq(LiveResourcesPO::getSectionId, sectionsPO.getId())
                    );
                    if(delete1 < 0){
                        return SaResult.error("删除节点的直播资源映射信息失败");
                    }
                }
            }

            SaResult saResult = deleteCourseSectionInfoRecurrent(sectionsPO);
        }

        // 在插入后 对 一门课的 Section 进行一次排序 主要是直播课
        updateSectionsSequence(courseId);

        return SaResult.ok("删除成功");
    }

    private SaResult deleteCourseSectionInfoRecurrent(SectionsPO sectionsPO){
        Long courseId = sectionsPO.getCourseId();
        Long id = sectionsPO.getId();
        // 获取该课程 该节点的子节点
        List<SectionsPO> sectionsPOS = sectionsService.getBaseMapper().selectList(new LambdaQueryWrapper<SectionsPO>()
                .eq(SectionsPO::getCourseId, courseId)
                .eq(SectionsPO::getParentSectionId, id)
        );
        int delete = sectionsService.getBaseMapper().delete(new LambdaQueryWrapper<SectionsPO>()
                .eq(SectionsPO::getCourseId, courseId)
                .eq(SectionsPO::getParentSectionId, id));
        if(delete < 0){
            return SaResult.error("删除节点信息失败");
        }
        for(SectionsPO sectionsPO1: sectionsPOS){
            if(sectionsPO1.getContentType().equals(CourseContentType.LIVING.getContentType())){
                // 删除直播 资源映射 并非删除直播间
                int delete1 = liveResourceService.getBaseMapper().delete(new LambdaQueryWrapper<LiveResourcesPO>()
                        .eq(LiveResourcesPO::getCourseId, sectionsPO1.getCourseId())
                        .eq(LiveResourcesPO::getSectionId, sectionsPO1.getId())
                );
                if(delete1 < 0){
                    return SaResult.error("删除节点的直播资源映射信息失败");
                }
            }

            SaResult saResult = deleteCourseSectionInfoRecurrent(sectionsPO1);
        }
        return SaResult.ok("删除成功");
    }


    private String getLiveCourseChannelId(Long courseId){
        CoursesLearningPO coursesLearningPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                .eq(CoursesLearningPO::getId, courseId));
        LiveResourcesPO liveResourcesPO = null;
        if(coursesLearningPO.getCourseType().equals(CourseContentType.LIVING.getContentType()) ||
                coursesLearningPO.getCourseType().equals(CourseContentType.MIX.getContentType())){
            // 如果这门课是一门直播或者混合模式的课程 那么就需要将课程的直播资源分配给指定的节点
            liveResourcesPO = liveResourceService.getBaseMapper().selectLiveResource(courseId);
            return liveResourcesPO.getChannelId();
        }
        return null;
    }

    /**
     * 获取学生的课程信息
     * 获取学生全部的课程信息 包含已修未修在修的
     * @param studentsCoursesInfoSearchRO
     * @return
     */
    public List<CourseInfoVO> getCourseInfo(StudentsCoursesInfoSearchRO studentsCoursesInfoSearchRO) {
        String username = StpUtil.getLoginIdAsString();
        // 因为存在多学籍学生 所以需要 获取最近的学籍信息 来匹配它的班级信息
        List<StudentStatusPO> studentStatusPOS = studentStatusService.getBaseMapper().selectList(new LambdaQueryWrapper<StudentStatusPO>()
                .eq(StudentStatusPO::getIdNumber, username));
        if (studentStatusPOS == null || studentStatusPOS.isEmpty()) {
            // 列表为空，适当处理这种情况，例如返回 null 或抛出异常
            return new ArrayList<>();
        }

        // 假设 getGrade() 返回的是 String，我们需要转换为整数来比较
        // 找出具有最高年级的学籍信息
        StudentStatusPO highestGradeStudent = studentStatusPOS.stream()
                .max(Comparator.comparingInt(s -> Integer.parseInt(s.getGrade())))
                .orElse(null); // 或者其他适当的默认值

        // 使用 highestGradeStudent 变量做进一步的操作
        // 假设 highestGradeStudent 是一个有效的 StudentStatusPO 对象
        String classIdentifier = highestGradeStudent.getClassIdentifier();

        String courseStatus = studentsCoursesInfoSearchRO.getCourseStatus();
        List<CourseInfoVO> courseInfoVOS = new ArrayList<>();


        if(courseStatus != null && courseStatus.equals(StudentCoursesStatusEnum.ENROLLED_COURSE.getCourseStatus())){
            // 创建一个新的 ArrayList 并添加 classIdentifier
            List<String> classNameList = new ArrayList<>();
            classNameList.add(classIdentifier);
            // 看她是否是重修的学生
            List<RetakeStudentsPO> retakeStudentsPOS = retakeStudentsService.getBaseMapper().selectList(
                    new LambdaQueryWrapper<RetakeStudentsPO>()
                            .eq(RetakeStudentsPO::getStudentNumber, highestGradeStudent.getStudentNumber()));

            // 从 retakeStudentsPOS 中提取所有 courseId，并收集到 Set 中
            Set<Long> courseIds = retakeStudentsPOS.stream()
                    .map(RetakeStudentsPO::getCourseId)
                    .collect(Collectors.toSet());

            // 使用 classNameList 创建 CoursesLearningRO 并设置 courseIds
            CoursesLearningRO coursesLearningRO = new CoursesLearningRO().setClassNameSet(classNameList);
//            CoursesLearningRO coursesLearningRO = new CoursesLearningRO();
            coursesLearningRO.setCourseIds(courseIds);


            // 现在 coursesLearningRO 包含了正确的列表，可以传递给 selectCourseLearningData 方法
            List<CourseLearningVO> courseLearningVOS = getBaseMapper().selectCourseLearningDataWithoutPaging(coursesLearningRO);


            // 使用 Stream API 过滤掉 valid 为 'N' 的课程
            List<CourseLearningVO> filteredCourseLearningVOS = courseLearningVOS.stream()
                    .filter(course -> "Y".equals(course.getValid()))
                    .collect(Collectors.toList());


            for(CourseLearningVO courseLearningVO : courseLearningVOS){
                CourseInfoVO courseInfoVO = new CourseInfoVO()
                        .setCourseId(courseLearningVO.getId())
                        .setYear(courseLearningVO.getYear())
                        .setGrade(highestGradeStudent.getGrade())
                        .setCourseName(courseLearningVO.getCourseName())
                        .setCourseType(courseLearningVO.getCourseType())
                        .setCourseDescription(courseLearningVO.getCourseDescription())
                        .setDefaultMainTeacherUsername(courseLearningVO.getDefaultMainTeacherUsername())
                        .setRecentCourseScheduleTime(courseLearningVO.getRecentCourseScheduleTime())
                        ;
                ClassInformationPO classInformationPO = classInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                        .eq(ClassInformationPO::getClassIdentifier, highestGradeStudent.getClassIdentifier()));
                courseInfoVO.setClassName(classInformationPO.getClassName());

                List<CourseAssistantsPO> courseAssistantsPOS = courseAssistantsService.getBaseMapper().selectList(
                        new LambdaQueryWrapper<CourseAssistantsPO>()
                                .eq(CourseAssistantsPO::getCourseId, courseLearningVO.getId()));

                // 从 courseAssistantsPOS 中提取所有 username，并收集到 Set 中
                Set<String> usernames = courseAssistantsPOS.stream()
                        .map(CourseAssistantsPO::getUsername)
                        .collect(Collectors.toSet());

                // 创建 TeacherInformationSearchRO 对象并设置 usernames
                TeacherInformationSearchRO teacherInformationSearchRO = new TeacherInformationSearchRO();
                teacherInformationSearchRO.setUsernames(usernames);
                // 获取助教信息
                List<TeacherInformationPO> teacherInformationPOList = teacherInformationService.
                        getBaseMapper().selectTeacherInfo(teacherInformationSearchRO);
                List<AssistantInfoVO> assistantInfoVOList = new ArrayList<>();
                for(TeacherInformationPO teacherInformationPO : teacherInformationPOList){
                    AssistantInfoVO assistantInfoVO = new AssistantInfoVO()
                            .setUsername(teacherInformationPO.getTeacherUsername())
                            .setName(teacherInformationPO.getName())
                            ;
                    assistantInfoVOList.add(assistantInfoVO);
                }
                courseInfoVO.setAssistantInfoVOList(assistantInfoVOList);

                TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                        .eq(TeacherInformationPO::getTeacherUsername, courseLearningVO.getDefaultMainTeacherUsername()));
                courseInfoVO.setDefaultMainTeacherName(teacherInformationPO.getName());
                String courseCoverUrl = courseLearningVO.getCourseCoverUrl();
                if(courseCoverUrl != null){
                    courseInfoVO.setCourseCoverUrl(minioService.generatePresignedUrl(courseCoverUrl));
                }
                courseInfoVOS.add(courseInfoVO);
            }

            // 批量添加在修状态
            courseInfoVOS.forEach(courseInfo -> courseInfo.setState(StudentCoursesStatusEnum.ENROLLED_COURSE.getCourseStatus()));
        }else if(courseStatus != null && courseStatus.equals(StudentCoursesStatusEnum.COMPLETED_COURSE.getCourseStatus())){

            List<ScoreInformationDownloadVO> scoreInformationDownloadVOList =  scoreInformationService.
                    getBaseMapper().selectScoreInfoData(new ScoreInformationFilterRO().
                            setStudentId(highestGradeStudent.getStudentNumber()));

            // 对于该学生的成绩信息来判断已修的课程
            for(ScoreInformationDownloadVO scoreInformationDownloadVO : scoreInformationDownloadVOList){
                String finalScore = scoreInformationDownloadVO.getFinalScore();
                String makeupExam1Score = scoreInformationDownloadVO.getMakeupExam1Score();
                String makeupExam2Score = scoreInformationDownloadVO.getMakeupExam2Score();
                String postGraduationScore = scoreInformationDownloadVO.getPostGraduationScore();
                String result = null;

                if (finalScore != null && finalScore.matches("\\d+")) {
                    result = finalScore;
                }
                if (makeupExam1Score != null && makeupExam1Score.matches("\\d+")) {
                    result = makeupExam1Score;
                }
                if (makeupExam2Score != null && makeupExam2Score.matches("\\d+")) {
                    result = makeupExam2Score;
                }
                if (postGraduationScore != null && postGraduationScore.matches("\\d+")) {
                    result = postGraduationScore;
                }
                if(result != null){
                    CourseInfoVO courseInfoVO = new CourseInfoVO()
                            .setGrade(scoreInformationDownloadVO.getGrade())
                            .setCourseName(scoreInformationDownloadVO.getCourseName())
                            .setScore(result)
                            ;
                    courseInfoVOS.add(courseInfoVO);
                }

            }

            // 批量添加已修状态
            courseInfoVOS.forEach(courseInfo -> courseInfo.setState(StudentCoursesStatusEnum.COMPLETED_COURSE.getCourseStatus()));
        }


        return courseInfoVOS;
    }

    /**
     * 获取学生的指定的一门课的节点信息
     * @param courseSectionRO
     * @return
     */
    public List<CourseSectionVO> getStudentCourseSectionsInfo(CourseSectionRO courseSectionRO) {


        CoursesLearningPO coursesLearningPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                .eq(CoursesLearningPO::getId, courseSectionRO.getCourseId()));
        if(coursesLearningPO == null){
            return new ArrayList<>();
        }

        List<SectionsPO> sectionsPOS = sectionsService.getBaseMapper().selectSectionsInfo(courseSectionRO);

        List<CourseSectionVO> courseSectionVOs = new ArrayList<>();

        for(SectionsPO sectionsPO: sectionsPOS){
            CourseSectionVO courseSectionVO = new CourseSectionVO();
            courseSectionVO.setId(sectionsPO.getId());
            courseSectionVO.setCourseId(sectionsPO.getCourseId());
            courseSectionVO.setCourseSectionParent(sectionsPO.getParentSectionId());
            courseSectionVO.setSequence(sectionsPO.getSequence());
            courseSectionVO.setContentType(sectionsPO.getContentType());
            courseSectionVO.setStartTime(sectionsPO.getStartTime());
            courseSectionVO.setDeadLine(sectionsPO.getDeadline());
            courseSectionVO.setValid(sectionsPO.getValid());

            if(sectionsPO.getContentType().equals(CourseContentType.LIVING.getContentType())){
                // 直播节点
                LiveResourcesPO liveResourcesPO = liveResourceService.getBaseMapper().selectOne(new LambdaQueryWrapper<LiveResourcesPO>()
                        .eq(LiveResourcesPO::getCourseId, sectionsPO.getCourseId())
                        .eq(LiveResourcesPO::getSectionId, sectionsPO.getId())
                );
                LiveResourceVO liveResourceVO = new LiveResourceVO();
                BeanUtils.copyProperties(liveResourcesPO, liveResourceVO);
                liveResourceVO.setPlayBack(singleLivingSetting.getPlayBackState(liveResourcesPO.getChannelId()));
                courseSectionVO.setCourseSectionContentVO(liveResourceVO);

            }else if(sectionsPO.getContentType().equals(CourseContentType.VIDEO.getContentType())){
                // 点播节点
                VideoResourcesPO videoResourcesPO = videoResourcesService.getBaseMapper().selectOne(new LambdaQueryWrapper<VideoResourcesPO>()
                        .eq(VideoResourcesPO::getSectionId, sectionsPO.getId())
                );
                VideoResourceVO videoResourceVO = new VideoResourceVO();
                BeanUtils.copyProperties(videoResourcesPO, videoResourceVO);
                courseSectionVO.setCourseSectionContentVO(videoResourceVO);
            }
            // 目前只处理这两种节点内容

            courseSectionVOs.add(courseSectionVO);
        }

        return courseSectionVOs;
    }

    /**
     * 根据用户名 获取 教师的课程信息
     * @param username
     * @return
     */
    public List<CourseInfoVO> getTeacherCoursesInfo(String username) {
        TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getTeacherUsername, username));
        if(teacherInformationPO == null){
            log.warn("该用户名 并没有匹配的教师信息 " + username);
        }

        Set<CourseLearningVO> combinedSet = new HashSet<>();

        if(teacherInformationPO.getTeacherType2().equals(TeacherTypeEnum.MAIN_TEACHER.getType())){
            List<CourseLearningVO> courseLearningVOS = getBaseMapper().selectCourseLearningDataWithoutPaging(
                    new CoursesLearningRO().setDefaultMainTeacherUsername(username));
            // 有些老师不是默认主讲 而是中途更改了老师来完成剩余的课程
            List<SectionsPO> sectionsPOS = sectionsService.getBaseMapper().selectList(new LambdaQueryWrapper<SectionsPO>()
                    .eq(SectionsPO::getMainTeacherUsername, username));
            Set<Long> courseIds = new HashSet<>();
            for(SectionsPO sectionsPO : sectionsPOS){
                Long courseId = sectionsPO.getCourseId();
                courseIds.add(courseId);
            }
            // 根据 节点信息反推课程信息
            List<CourseLearningVO> courseLearningVOS1 = new ArrayList<>();
            if(!courseIds.isEmpty()){
                courseLearningVOS1 = getBaseMapper().
                        selectCourseLearningDataWithoutPaging(new CoursesLearningRO().setCourseIds(courseIds));
            }

            combinedSet.addAll(courseLearningVOS);
            combinedSet.addAll(courseLearningVOS1);

        }else{
            // 辅导教师
            List<CourseAssistantsPO> courseAssistantsPOS = courseAssistantsService.getBaseMapper().selectList(new LambdaQueryWrapper<CourseAssistantsPO>()
                    .eq(CourseAssistantsPO::getUsername, teacherInformationPO.getTeacherUsername()));
            Set<Long> courseIds = courseAssistantsPOS.stream()
                    .map(CourseAssistantsPO::getCourseId) // 提取每个 CourseAssistantsPO 的 courseId
                    .collect(Collectors.toSet()); // 将结果收集到 Set 中
            List<CourseLearningVO> courseLearningVOS = new ArrayList<>();
            if(!courseIds.isEmpty()){
                courseLearningVOS = getBaseMapper().selectCourseLearningDataWithoutPaging(new CoursesLearningRO().setCourseIds(courseIds));
            }
            combinedSet.addAll(courseLearningVOS);
        }

        List<CourseInfoVO> courseInfoVOS = new ArrayList<>();
        Set<Long> resultCourseIds = new HashSet<>();


        for(CourseLearningVO courseLearningVO : combinedSet){
            Long id = courseLearningVO.getId();
            if(resultCourseIds.contains(id)){
                continue;
            }
            CourseInfoVO courseInfoVO = new CourseInfoVO()
                    .setCourseId(id)
                    .setYear(courseLearningVO.getYear())
                    .setCourseName(courseLearningVO.getCourseName())
                    .setCourseType(courseLearningVO.getCourseType())
                    .setCourseDescription(courseLearningVO.getCourseDescription())
                    .setCourseDescription(courseLearningVO.getCourseDescription())
                    .setDefaultMainTeacherUsername(courseLearningVO.getDefaultMainTeacherUsername())
                    .setRecentCourseScheduleTime(courseLearningVO.getRecentCourseScheduleTime())
                    ;
            String courseCoverUrl = courseLearningVO.getCourseCoverUrl();
            if(courseCoverUrl != null){
                courseInfoVO.setCourseCoverUrl(minioService.generatePresignedUrl(courseCoverUrl));
            }
            TeacherInformationPO teacherInformationPO1 = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                    .eq(TeacherInformationPO::getTeacherUsername, courseLearningVO.getDefaultMainTeacherUsername()));
            courseInfoVO.setDefaultMainTeacherName(teacherInformationPO1.getName());

            // 获取助教信息
            List<CourseAssistantsPO> courseAssistantsPOS = courseAssistantsService.getBaseMapper().selectList(
                    new LambdaQueryWrapper<CourseAssistantsPO>()
                            .eq(CourseAssistantsPO::getCourseId, courseLearningVO.getId()));

            // 从 courseAssistantsPOS 中提取所有 username，并收集到 Set 中
            Set<String> usernames = courseAssistantsPOS.stream()
                    .map(CourseAssistantsPO::getUsername)
                    .collect(Collectors.toSet());
            // 创建 TeacherInformationSearchRO 对象并设置 usernames
            TeacherInformationSearchRO teacherInformationSearchRO = new TeacherInformationSearchRO();
            teacherInformationSearchRO.setUsernames(usernames);
            List<TeacherInformationPO> teacherInformationPOList = teacherInformationService.
                    getBaseMapper().selectTeacherInfo(teacherInformationSearchRO);
            List<AssistantInfoVO> assistantInfoVOList = new ArrayList<>();
            for(TeacherInformationPO tea : teacherInformationPOList){
                AssistantInfoVO assistantInfoVO = new AssistantInfoVO()
                        .setUsername(tea.getTeacherUsername())
                        .setName(tea.getName())
                        ;
                assistantInfoVOList.add(assistantInfoVO);
            }
            courseInfoVO.setAssistantInfoVOList(assistantInfoVOList);

            // 设置年级 和 班级集合
            List<CoursesClassMappingPO> coursesClassMappingPOS = coursesClassMappingService.getBaseMapper().selectList(
                    new LambdaQueryWrapper<CoursesClassMappingPO>()
                            .eq(CoursesClassMappingPO::getCourseId, courseLearningVO.getId()));

            // 创建 HashSet 来存储 classIdentifier
            HashSet<String> classIdentifiers = new HashSet<>();
            for (CoursesClassMappingPO mappingPO : coursesClassMappingPOS) {
                classIdentifiers.add(mappingPO.getClassIdentifier());
            }

            // 使用 classIdentifiers 作为参数查询班级信息
            List<ClassInformationVO> classInformationVOList = classInformationService.getBaseMapper()
                    .selectClassInfoData(new ClassInformationRO().setClassIdentifiers(classIdentifiers));
            Set<String> uniqueClassNames = new HashSet<>();
            Set<String> uniqueGrades = new HashSet<>();
            for (ClassInformationVO classInfo : classInformationVOList) {
                uniqueClassNames.add(classInfo.getClassName());
                uniqueGrades.add(classInfo.getGrade());
            }

            StringBuilder classNames = new StringBuilder();
            for (String className : uniqueClassNames) {
                if (classNames.length() > 0) {
                    classNames.append(" ");
                }
                classNames.append(className);
            }

            StringBuilder grades = new StringBuilder();
            for (String grade : uniqueGrades) {
                if (grades.length() > 0) {
                    grades.append(" ");
                }
                grades.append(grade);
            }

            String classNamesResult = classNames.toString();
            String gradesResult = grades.toString();


            courseInfoVO.setGrade(gradesResult);
            courseInfoVO.setClassNames(classNamesResult);

            courseInfoVOS.add(courseInfoVO);
            resultCourseIds.add(id);
        }

        return courseInfoVOS;
    }

    /**
     * 获取学生单门课程信息
     * @param studentsCoursesInfoSearchRO
     * @return
     */
    public CourseInfoVO getSingleCourseInfo(StudentsCoursesInfoSearchRO studentsCoursesInfoSearchRO) {
        String username = StpUtil.getLoginIdAsString();
        // 因为存在多学籍学生 所以需要 获取最近的学籍信息 来匹配它的班级信息
        List<StudentStatusPO> studentStatusPOS = studentStatusService.getBaseMapper().selectList(new LambdaQueryWrapper<StudentStatusPO>()
                .eq(StudentStatusPO::getIdNumber, username));
        if (studentStatusPOS == null || studentStatusPOS.isEmpty()) {
            // 列表为空，适当处理这种情况，例如返回 null 或抛出异常
            return null;
        }

        // 假设 getGrade() 返回的是 String，我们需要转换为整数来比较
        // 找出具有最高年级的学籍信息
        StudentStatusPO highestGradeStudent = studentStatusPOS.stream()
                .max(Comparator.comparingInt(s -> Integer.parseInt(s.getGrade())))
                .orElse(null); // 或者其他适当的默认值

        List<CourseLearningVO> courseLearningVOS = getBaseMapper().selectCourseLearningDataWithoutPaging(new CoursesLearningRO()
                .setId(studentsCoursesInfoSearchRO.getCourseId()));
        if(courseLearningVOS.size() == 1){
            CourseLearningVO courseLearningVO = courseLearningVOS.get(0);
            CourseInfoVO courseInfoVO = new CourseInfoVO()
                    .setCourseId(courseLearningVO.getId())
                    .setYear(courseLearningVO.getYear())
                    .setCourseName(courseLearningVO.getCourseName())
                    .setCourseType(courseLearningVO.getCourseType())
                    .setCourseDescription(courseLearningVO.getCourseDescription())
                    .setDefaultMainTeacherUsername(courseLearningVO.getDefaultMainTeacherUsername())
                    .setRecentCourseScheduleTime(courseLearningVO.getRecentCourseScheduleTime())
                    ;
            ClassInformationPO classInformationPO = classInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                    .eq(ClassInformationPO::getClassIdentifier, highestGradeStudent.getClassIdentifier()));
            courseInfoVO.setClassName(classInformationPO.getClassName());
            courseInfoVO.setGrade(classInformationPO.getGrade());

            List<CourseAssistantsPO> courseAssistantsPOS = courseAssistantsService.getBaseMapper().selectList(
                    new LambdaQueryWrapper<CourseAssistantsPO>()
                            .eq(CourseAssistantsPO::getCourseId, courseLearningVO.getId()));

            // 从 courseAssistantsPOS 中提取所有 username，并收集到 Set 中
            Set<String> usernames = courseAssistantsPOS.stream()
                    .map(CourseAssistantsPO::getUsername)
                    .collect(Collectors.toSet());

            // 创建 TeacherInformationSearchRO 对象并设置 usernames
            TeacherInformationSearchRO teacherInformationSearchRO = new TeacherInformationSearchRO();
            teacherInformationSearchRO.setUsernames(usernames);
            // 获取助教信息
            List<TeacherInformationPO> teacherInformationPOList = teacherInformationService.
                    getBaseMapper().selectTeacherInfo(teacherInformationSearchRO);
            List<AssistantInfoVO> assistantInfoVOList = new ArrayList<>();
            for(TeacherInformationPO teacherInformationPO : teacherInformationPOList){
                AssistantInfoVO assistantInfoVO = new AssistantInfoVO()
                        .setUsername(teacherInformationPO.getTeacherUsername())
                        .setName(teacherInformationPO.getName())
                        ;
                assistantInfoVOList.add(assistantInfoVO);
            }
            courseInfoVO.setAssistantInfoVOList(assistantInfoVOList);

            TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                    .eq(TeacherInformationPO::getTeacherUsername, courseLearningVO.getDefaultMainTeacherUsername()));
            courseInfoVO.setDefaultMainTeacherName(teacherInformationPO.getName());
            return courseInfoVO;
        }
        return null;
    }


    /**
     * 根据学生这门课的情况 来获取其直播间信息
     * @param courseId
     * @return
     */
    public SaResult getStudentLivingWatchUrl(Long courseId) {
        CoursesLearningPO coursesLearningPO = getBaseMapper().selectById(courseId);
        if(coursesLearningPO != null){
            String courseType = coursesLearningPO.getCourseType();

            if(courseType.equals(CourseContentType.LIVING.getContentType())
                    || courseType.equals(CourseContentType.MIX.getContentType())){
                String liveCourseChannelId = getLiveCourseChannelId(courseId);
                return singleLivingService.getStudentChannelUrl(liveCourseChannelId);
            }
        }

        return SaResult.error(ResultCode.UPDATE_COURSE_FAIL4.getMessage())
                .setCode(ResultCode.UPDATE_COURSE_FAIL4.getCode());
    }

    /**
     * 获取课程所对应的班级信息
     * @param courseId
     * @return
     */
    public SaResult getCourseClassInfos(Long courseId) {
        List<CoursesClassMappingPO> coursesClassMappingPOS = coursesClassMappingService.getBaseMapper().selectList(new LambdaQueryWrapper<CoursesClassMappingPO>()
                .eq(CoursesClassMappingPO::getCourseId, courseId));

        return SaResult.ok("成功获取班级信息").setData(coursesClassMappingPOS);
    }

    /**
     * 获取课程师资信息
     * @param courseId
     * @return
     */
    public SaResult getCourseTeacherInformation(Long courseId) {
        CoursesLearningPO coursesLearningPO = getBaseMapper().selectById(courseId);
        if(coursesLearningPO != null){
            // 获取所有的主讲老师
            List<SectionsPO> sectionsPOS = sectionsService.getBaseMapper().selectList(new LambdaQueryWrapper<SectionsPO>()
                    .eq(SectionsPO::getCourseId, courseId));
            // 使用Java Stream API过滤并收集username不为空的条目
            Set<String> usernames = sectionsPOS.stream() // 将列表转换为Stream
                    .map(SectionsPO::getMainTeacherUsername) // 获取每个SectionsPO的MainTeacherUsername
                    .filter(username -> username != null && !username.isEmpty()) // 过滤出不为空的username
                    .collect(Collectors.toSet()); // 收集结果到Set中
            usernames.add(coursesLearningPO.getDefaultMainTeacherUsername());

            // 获取助教老师
            List<CourseAssistantsPO> courseAssistantsPOS = courseAssistantsService.getBaseMapper().selectList(new LambdaQueryWrapper<CourseAssistantsPO>()
                    .eq(CourseAssistantsPO::getCourseId, courseId));
            Set<String> assistants = courseAssistantsPOS.stream()
                    .map(CourseAssistantsPO::getUsername)
                    .collect(Collectors.toSet());// 收集结果到Set中

            usernames.addAll(assistants);

            // 根据这些用户名 批量获取教师信息
            List<TeacherInformationPO> teacherInformationPOS = teacherInformationService.getBaseMapper().selectTeacherInfo(new TeacherInformationSearchRO().setUsernames(usernames));

            List<CourseTeacherInformationVO> courseTeacherInformationVOS = new ArrayList<>();
            for (TeacherInformationPO teacherInformationPO : teacherInformationPOS) {
                CourseTeacherInformationVO courseTeacherInformationVO = new CourseTeacherInformationVO()
                        .setTeacherName(teacherInformationPO.getName())
                        .setTeacherType1(teacherInformationPO.getTeacherType1())
                        .setTeacherType2(teacherInformationPO.getTeacherType2())
                        .setPhone(teacherInformationPO.getPhone())
                        ;
                courseTeacherInformationVOS.add(courseTeacherInformationVO);
            }
            return SaResult.ok("成功获取课程师资库信息").setData(courseTeacherInformationVOS);

        }
        return SaResult.error(ResultCode.UPDATE_COURSE_FAIL5.getMessage()).setCode(ResultCode.UPDATE_COURSE_FAIL5.getCode());
    }

    /**
     * 获取课程排课明细信息
     * @param courseScheduleSearchRO
     * @return
     */
    public SaResult getCourseScheduleInformation(PageRO<CourseScheduleSearchRO> courseScheduleSearchRO) {
        List<String> roleList = StpUtil.getRoleList();
        if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
            // 学历教育部

        } else if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 二级学院
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            courseScheduleSearchRO.getEntity().setCollege(userBelongCollege.getCollegeName());
        } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {
            TeachingPointInformationPO userBelongTeachingPoint = scnuXueliTools.getUserBelongTeachingPoint();
            courseScheduleSearchRO.getEntity().setTeachingPointName(userBelongTeachingPoint.getTeachingPointName());
        }


        List<CourseScheduleVO> courseScheduleVOList = getBaseMapper().selectCoursesScheduleInfo(courseScheduleSearchRO.getEntity(),
                courseScheduleSearchRO.getPageNumber() - 1, courseScheduleSearchRO.getPageSize(), CourseContentType.NODE.getContentType());
        PageVO pageVO = new PageVO<CourseLearningVO>();
        pageVO.setRecords(courseScheduleVOList);
        pageVO.setSize(courseScheduleSearchRO.getPageSize());
        pageVO.setCurrent(courseScheduleSearchRO.getPageNumber());
        pageVO.setTotal(getBaseMapper().selectCoursesScheduleInfoCount(courseScheduleSearchRO.getEntity(), CourseContentType.NODE.getContentType()));


        return SaResult.ok("成功获取排课明细数据").setData(pageVO);
    }

    /**
     * 预览课程排课内容
     * @param courseScheduleSearchRO
     * @return
     */
    public SaResult viewCourse(CourseScheduleSearchRO courseScheduleSearchRO) {
        List<SectionsPO> sectionsPOList = sectionsService.getBaseMapper().selectList(new LambdaQueryWrapper<SectionsPO>()
                .eq(SectionsPO::getCourseId, courseScheduleSearchRO.getCourseId())
                .ne(SectionsPO::getContentType, CourseContentType.NODE.getContentType())
        );
        List<CourseViewVO> courseViewVOS = new ArrayList<>();
        Date now = new Date();

        // 获取今天的开始和结束时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date todayStart = calendar.getTime();

        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date tomorrowStart = calendar.getTime();

        for (SectionsPO sectionsPO : sectionsPOList) {
            CourseViewVO courseViewVO = new CourseViewVO();
            String liveCourseChannelId = getLiveCourseChannelId(sectionsPO.getCourseId());
            boolean playBackState = singleLivingSetting.getPlayBackState(liveCourseChannelId);
            BeanUtils.copyProperties(sectionsPO, courseViewVO);
            courseViewVO.setChannelId(liveCourseChannelId);

            Date startTime = sectionsPO.getStartTime();
            Date deadline = sectionsPO.getDeadline();

            // 判断直播状态
            if (startTime.before(todayStart)) {
                courseViewVO.setLivingRoomState("已结束");
            } else if (startTime.after(now) && startTime.before(tomorrowStart)) {
                if (playBackState) {
                    courseViewVO.setLivingRoomState("直播中");
                } else {
                    courseViewVO.setLivingRoomState("未开播");
                }
            } else if (startTime.after(tomorrowStart)) {
                courseViewVO.setLivingRoomState("未开始");
            } else if (!startTime.after(now) && !deadline.before(now)) {
                // 正在直播或者回放
                if (playBackState) {
                    courseViewVO.setLivingRoomState("直播中");
                } else {
                    courseViewVO.setLivingRoomState("已结束");
                }
            }

            courseViewVOS.add(courseViewVO);
        }

        return SaResult.ok().setData(courseViewVOS);
    }


    /**
     * 获取课程创建参数
     * @return
     */
    public SaResult getCourseCreateParams() {
        List<TeacherSelectVO> mainTeacherList = new ArrayList<>();
        List<TeacherInformationPO> teacherInformationPOS = teacherInformationService.getBaseMapper().selectList(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getTeacherType2, "主讲教师"));
        for(TeacherInformationPO teacherInformationPO: teacherInformationPOS){
            String name = teacherInformationPO.getName();
            String teacherUsername = teacherInformationPO.getTeacherUsername();
            String label = name + " " + teacherUsername;
            TeacherSelectVO teacherSelectVO = new TeacherSelectVO(label, teacherInformationPO.getUserId());
            mainTeacherList.add(teacherSelectVO);
        }

        List<TeacherSelectVO> tutorList = new ArrayList<>();
        List<TeacherInformationPO> teacherInformationPOS1 = teacherInformationService.getBaseMapper().selectList(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getTeacherType2, "辅导教师"));
        for(TeacherInformationPO teacherInformationPO: teacherInformationPOS1){
            String name = teacherInformationPO.getName();
            String teacherUsername = teacherInformationPO.getTeacherUsername();
            String label = name + " " + teacherUsername;
            TeacherSelectVO teacherSelectVO = new TeacherSelectVO(label, teacherInformationPO.getUserId());
            tutorList.add(teacherSelectVO);
        }

        List<String> contentTypes = new ArrayList<>();
        for (CourseContentType type : CourseContentType.values()) {
            contentTypes.add(type.getContentType());
        }

        CourseCreateParamsVO courseCreateParamsVO = new CourseCreateParamsVO()
                .setMainTeacherList(mainTeacherList)
                .setTutorList(tutorList)
                .setCourseTypes(contentTypes)
                ;


        return SaResult.ok().setData(courseCreateParamsVO);

    }

    public CourseStudentInfoSearchParamsVO getCourseStudentsInfoSelectParams(CourseStudentSearchRO courseStudentSearchRO) {

        CourseStudentInfoSearchParamsVO courseStudentInfoSearchParamsVO = new CourseStudentInfoSearchParamsVO();

        List<String> grades = new ArrayList<>();
        List<String> grades1 = new ArrayList<>();
        List<String> colleges = new ArrayList<>();
        List<String> colleges1 = new ArrayList<>();
        List<String> majorNames = new ArrayList<>();
        List<String> majorNames1 = new ArrayList<>();
        List<String> levels = new ArrayList<>();
        List<String> levels1 = new ArrayList<>();
        List<String> studyForms = new ArrayList<>();
        List<String> studyForms1 = new ArrayList<>();
        List<String> classNames = new ArrayList<>();
        List<String> classNames1 = new ArrayList<>();
        List<String> teachingPointNames = new ArrayList<>();
        List<String> teachingPointNames1 = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(14); // 选择适当的线程池大小

        if("N".equals(courseStudentSearchRO.getIsRetake())){
            Future<List<String>> gradesFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsGrades(courseStudentSearchRO));
            Future<List<String>> collegesFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsColleges(courseStudentSearchRO));
            Future<List<String>> majorNamesFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsMajorNames(courseStudentSearchRO));
            Future<List<String>> levelsFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsLevels(courseStudentSearchRO));
            Future<List<String>> studyFormsFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsStudyForms(courseStudentSearchRO));
            Future<List<String>> classNamesFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsClassNames(courseStudentSearchRO));
            Future<List<String>> teachingPointNamesFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsTeachingPointNames(courseStudentSearchRO));

            try{
                grades = gradesFuture.get();
                colleges = collegesFuture.get();
                majorNames1 = majorNamesFuture.get();
                levels = levelsFuture.get();
                studyForms = studyFormsFuture.get();
                classNames = classNamesFuture.get();
                teachingPointNames = teachingPointNamesFuture.get();
            }catch (Exception e){
                log.error("从线程池获取重修学生筛选参数失败 " + e);
            }
        }else if("Y".equals(courseStudentSearchRO.getIsRetake())){
            Future<List<String>> gradesFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsGradesForRetake(courseStudentSearchRO));
            Future<List<String>> collegesFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsCollegesForRetake(courseStudentSearchRO));
            Future<List<String>> majorNamesFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsMajorNamesForRetake(courseStudentSearchRO));
            Future<List<String>> levelsFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsLevelsForRetake(courseStudentSearchRO));
            Future<List<String>> studyFormsFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsStudyFormsForRetake(courseStudentSearchRO));
            Future<List<String>> classNamesFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsClassNamesForRetake(courseStudentSearchRO));
            Future<List<String>> teachingPointNamesFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsTeachingPointNamesForRetake(courseStudentSearchRO));
            try{
                grades1 = gradesFuture1.get();
                colleges1 = collegesFuture1.get();
                majorNames1 = majorNamesFuture1.get();
                levels1 = levelsFuture1.get();
                studyForms1 = studyFormsFuture1.get();
                classNames1 = classNamesFuture1.get();
                teachingPointNames1 = teachingPointNamesFuture1.get();
            }catch (Exception e){
                log.error("从线程池获取重修学生筛选参数失败 " + e);
            }

        }else{
            Future<List<String>> gradesFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsGrades(courseStudentSearchRO));
            Future<List<String>> collegesFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsColleges(courseStudentSearchRO));
            Future<List<String>> majorNamesFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsMajorNames(courseStudentSearchRO));
            Future<List<String>> levelsFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsLevels(courseStudentSearchRO));
            Future<List<String>> studyFormsFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsStudyForms(courseStudentSearchRO));
            Future<List<String>> classNamesFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsClassNames(courseStudentSearchRO));
            Future<List<String>> teachingPointNamesFuture = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsTeachingPointNames(courseStudentSearchRO));

            try{
                grades = gradesFuture.get();
                colleges = collegesFuture.get();
                majorNames1 = majorNamesFuture.get();
                levels = levelsFuture.get();
                studyForms = studyFormsFuture.get();
                classNames = classNamesFuture.get();
                teachingPointNames = teachingPointNamesFuture.get();
            }catch (Exception e){
                log.error("从线程池获取重修学生筛选参数失败 " + e);
            }

            Future<List<String>> gradesFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsGradesForRetake(courseStudentSearchRO));
            Future<List<String>> collegesFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsCollegesForRetake(courseStudentSearchRO));
            Future<List<String>> majorNamesFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsMajorNamesForRetake(courseStudentSearchRO));
            Future<List<String>> levelsFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsLevelsForRetake(courseStudentSearchRO));
            Future<List<String>> studyFormsFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsStudyFormsForRetake(courseStudentSearchRO));
            Future<List<String>> classNamesFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsClassNamesForRetake(courseStudentSearchRO));
            Future<List<String>> teachingPointNamesFuture1 = executor.submit(() -> getBaseMapper().selectCourseStudentsInfoSelectParamsTeachingPointNamesForRetake(courseStudentSearchRO));
            try{
                grades1 = gradesFuture1.get();
                colleges1 = collegesFuture1.get();
                majorNames1 = majorNamesFuture1.get();
                levels1 = levelsFuture1.get();
                studyForms1 = studyFormsFuture1.get();
                classNames1 = classNamesFuture1.get();
                teachingPointNames1 = teachingPointNamesFuture1.get();
            }catch (Exception e){
                log.error("从线程池获取重修学生筛选参数失败 " + e);
            }
        }

        // 针对重修的学生

        grades.addAll(grades1);
        grades = grades.stream().distinct().collect(Collectors.toList());

        colleges.addAll(colleges1);
        colleges = colleges.stream().distinct().collect(Collectors.toList());

        majorNames.addAll(majorNames1);
        majorNames = majorNames.stream().distinct().collect(Collectors.toList());

        levels.addAll(levels1);
        levels = levels.stream().distinct().collect(Collectors.toList());

        studyForms.addAll(studyForms1);
        studyForms = studyForms.stream().distinct().collect(Collectors.toList());

        classNames.addAll(classNames1);
        classNames = classNames.stream().distinct().collect(Collectors.toList());

        teachingPointNames.addAll(teachingPointNames1);
        teachingPointNames = teachingPointNames.stream().distinct().collect(Collectors.toList());

        courseStudentInfoSearchParamsVO.setGrades(grades);
        courseStudentInfoSearchParamsVO.setClassNames(classNames);
        courseStudentInfoSearchParamsVO.setColleges(colleges);
        courseStudentInfoSearchParamsVO.setMajorNames(majorNames);
        courseStudentInfoSearchParamsVO.setLevels(levels);
        courseStudentInfoSearchParamsVO.setStudyForms(studyForms);
        courseStudentInfoSearchParamsVO.setTeachingPointNames(teachingPointNames);
        courseStudentInfoSearchParamsVO.setIsRetakes(Arrays.asList("Y", "N"));


        return courseStudentInfoSearchParamsVO;
    }

    /**
     * 获取课程白名单
     * @param courseId
     * @return
     */
    public SaResult getCourseLivingWhiteList(Long courseId) {
        String channelId = getLiveCourseChannelId(courseId);

        return singleLivingService.getChannelWhiteList(new ChannelInfoRequest()
                .setChannelId(channelId)
                .setCurrentPage(1)
                .setPageSize(10)
        );
    }

    /**
     * 获取课程白名单是否与保利威的白名单相等
     * @param courseId
     * @return
     */
    public SaResult getCourseLivingWhiteListEqualState(Long courseId) {
        return null;
    }

    /**
     * 获取老师的单门课程信息
     * @param studentsCoursesInfoSearchRO
     * @return
     */
    public CourseInfoVO getTeacherSingleCoursesInfo(StudentsCoursesInfoSearchRO studentsCoursesInfoSearchRO) {
        List<CourseLearningVO> courseLearningVOS = getBaseMapper().selectCourseLearningDataWithoutPaging(new CoursesLearningRO()
                .setId(studentsCoursesInfoSearchRO.getCourseId()));
        if(courseLearningVOS.size() == 1){
            CourseLearningVO courseLearningVO = courseLearningVOS.get(0);
            CourseInfoVO courseInfoVO = new CourseInfoVO()
                    .setCourseId(courseLearningVO.getId())
                    .setYear(courseLearningVO.getYear())
                    .setCourseName(courseLearningVO.getCourseName())
                    .setCourseType(courseLearningVO.getCourseType())
                    .setCourseDescription(courseLearningVO.getCourseDescription())
                    .setDefaultMainTeacherUsername(courseLearningVO.getDefaultMainTeacherUsername())
                    .setRecentCourseScheduleTime(courseLearningVO.getRecentCourseScheduleTime())
                    ;
            List<CoursesClassMappingPO> coursesClassMappingPOS = coursesClassMappingService.getBaseMapper().selectList(new LambdaQueryWrapper<CoursesClassMappingPO>()
                    .eq(CoursesClassMappingPO::getCourseId, studentsCoursesInfoSearchRO.getCourseId()));

            // Assuming CoursesClassMappingPO has methods like getClassName and getGrade
            StringBuilder classNames = new StringBuilder();
            StringBuilder grades = new StringBuilder();

            for (CoursesClassMappingPO mapping : coursesClassMappingPOS) {
                ClassInformationPO classInformationPO = classInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                        .eq(ClassInformationPO::getClassIdentifier, mapping.getClassIdentifier()));

                if (classNames.length() > 0) {
                    classNames.append(" ");
                }
                classNames.append(classInformationPO.getClassName()); // Replace getClassName with the actual method name if different

                if (grades.length() > 0) {
                    grades.append(" ");
                }
                grades.append(classInformationPO.getGrade()); // Replace getGrade with the actual method name if different
            }

            courseInfoVO.setClassName(classNames.toString());
            courseInfoVO.setGrade(grades.toString());

            List<CourseAssistantsPO> courseAssistantsPOS = courseAssistantsService.getBaseMapper().selectList(
                    new LambdaQueryWrapper<CourseAssistantsPO>()
                            .eq(CourseAssistantsPO::getCourseId, courseLearningVO.getId()));

            // 从 courseAssistantsPOS 中提取所有 username，并收集到 Set 中
            Set<String> usernames = courseAssistantsPOS.stream()
                    .map(CourseAssistantsPO::getUsername)
                    .collect(Collectors.toSet());

            // 创建 TeacherInformationSearchRO 对象并设置 usernames
            TeacherInformationSearchRO teacherInformationSearchRO = new TeacherInformationSearchRO();
            teacherInformationSearchRO.setUsernames(usernames);
            // 获取助教信息
            List<TeacherInformationPO> teacherInformationPOList = teacherInformationService.
                    getBaseMapper().selectTeacherInfo(teacherInformationSearchRO);
            List<AssistantInfoVO> assistantInfoVOList = new ArrayList<>();
            for(TeacherInformationPO teacherInformationPO : teacherInformationPOList){
                AssistantInfoVO assistantInfoVO = new AssistantInfoVO()
                        .setUsername(teacherInformationPO.getTeacherUsername())
                        .setName(teacherInformationPO.getName())
                        ;
                assistantInfoVOList.add(assistantInfoVO);
            }
            courseInfoVO.setAssistantInfoVOList(assistantInfoVOList);

            TeacherInformationPO teacherInformationPO = teacherInformationService.getBaseMapper().selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                    .eq(TeacherInformationPO::getTeacherUsername, courseLearningVO.getDefaultMainTeacherUsername()));
            courseInfoVO.setDefaultMainTeacherName(teacherInformationPO.getName());
            return courseInfoVO;
        }
        return null;
    }

    /**
     * 获取管理端 课程学习的筛选项
     * @param coursesLearningRO
     * @return
     */
    public PageQueryCoursesInfoParamsVO pageQueryCoursesInfoParams(CoursesLearningRO coursesLearningRO) {
        ExecutorService executor = Executors.newFixedThreadPool(8); // 根据需求调整线程池的大小
        try {
            // 提交查询到线程池并获取Future对象
            Future<List<String>> gradesFuture = executor.submit(() -> getBaseMapper().selectCourseLearningDataSelectParamsGrades(coursesLearningRO));
            Future<List<String>> collegesFuture = executor.submit(() -> getBaseMapper().selectCourseLearningDataSelectParamsColleges(coursesLearningRO));
            Future<List<String>> majorNameFuture = executor.submit(() -> getBaseMapper().selectCourseLearningDataSelectParamsMajorNames(coursesLearningRO));
            Future<List<String>> studyFormFuture = executor.submit(() -> getBaseMapper().selectCourseLearningDataSelectParamsStudyForms(coursesLearningRO));
            Future<List<String>> levelFuture = executor.submit(() -> getBaseMapper().selectCourseLearningDataSelectParamsLevels(coursesLearningRO));
            Future<List<String>> teachingPointNameFuture = executor.submit(() -> getBaseMapper().selectCourseLearningDataSelectParamsTeachingPointNames(coursesLearningRO));
            Future<List<String>> classNameFuture = executor.submit(() -> getBaseMapper().selectCourseLearningDataSelectParamsClassNames(coursesLearningRO));
            Future<List<String>> courseNameFuture = executor.submit(() -> getBaseMapper().selectCourseLearningDataSelectParamsCourseNames(coursesLearningRO));

            // 等待所有查询完成并获取结果
            List<String> grades = gradesFuture.get();
            List<String> colleges = collegesFuture.get();
            List<String> majorNames = majorNameFuture.get();
            List<String> studyForms = studyFormFuture.get();
            List<String> levels = levelFuture.get();
            List<String> teachingPointNames = teachingPointNameFuture.get();
            List<String> classNames = classNameFuture.get();
            List<String> courseNames = courseNameFuture.get();

            List<TeacherInformationPO> teacherInformationPOList = teacherInformationService.getBaseMapper().selectList(new LambdaQueryWrapper<TeacherInformationPO>()
                    .eq(TeacherInformationPO::getTeacherType2, TeacherTypeEnum.MAIN_TEACHER.getType()));
            List<TeacherInfoVO> teacherInfoList = new ArrayList<>();
            for(TeacherInformationPO teacherInformationPO : teacherInformationPOList){
                TeacherInfoVO teacherInfoVO = new TeacherInfoVO()
                        .setTeacherUsername(teacherInformationPO.getTeacherUsername())
                        .setName(teacherInformationPO.getName())
                        ;
                teacherInfoList.add(teacherInfoVO);
            }

            List<String> contentTypes = EnumSet.allOf(CourseContentType.class).stream()
                    .map(CourseContentType::getContentType)
                    .collect(Collectors.toList());

                    // 创建并填充返回对象
            PageQueryCoursesInfoParamsVO pageQueryCoursesInfoParamsVO = new PageQueryCoursesInfoParamsVO()
                    .setGrades(grades)
                    .setColleges(colleges)
                    .setMajorNames(majorNames)
                    .setStudyForms(studyForms)
                    .setLevels(levels)
                    .setTeachingPointNames(teachingPointNames)
                    .setClassNames(classNames)
                    .setCourseNames(courseNames)
                    .setTeacherInfos(teacherInfoList)
                    .setCourseTypeList(contentTypes)
                    ;

            return pageQueryCoursesInfoParamsVO;

        } catch (InterruptedException | ExecutionException e) {
            // 处理异常
            log.info("线程池获取课程学习筛选参数项失败 " + e);
            return null; // 或者返回一个合适的错误响应
        } finally {
            executor.shutdown(); // 不要忘记关闭线程池
        }
    }


    /**
     * 获取直播间基本信息
     * @param channelId
     * @return
     */
    public SaResult getLivingRoomInfos(Long channelId) {

        ChannelResponseBO channelBasicInfo = videoStreamUtils.getChannelBasicInfo(String.valueOf(channelId));
        return SaResult.ok().setData(channelBasicInfo);
    }
}
