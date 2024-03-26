package com.scnujxjy.backendpoint.service.courses_learning;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.CourseContentType;
import com.scnujxjy.backendpoint.constant.enums.TeacherTypeEnum;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ViewStudentResponse.Content;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelInfoResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ChannelResponseData;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesLearningMapper;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.bo.course_learning.CourseRecordBO;
import com.scnujxjy.backendpoint.model.bo.course_learning.StudentWhiteListInfoBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.*;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.*;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationVO;
import com.scnujxjy.backendpoint.model.vo.video_stream.StudentWhiteListVO;
import com.scnujxjy.backendpoint.service.registration_record_card.StudentStatusService;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingService;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
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
    private VideoStreamUtils videoStreamUtils;

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

    public PageVO<CourseLearningVO> pageQueryCoursesInfo(PageRO<CoursesLearningRO> courseScheduleROPageRO) {
        List<String> roleList = StpUtil.getRoleList();
        if (roleList.contains(XUELIJIAOYUBU_ADMIN.getRoleName())) {
            // 学历教育部

        } else if (roleList.contains(SECOND_COLLEGE_ADMIN.getRoleName())) {
            // 二级学院
            CollegeInformationPO userBelongCollege = scnuXueliTools.getUserBelongCollege();
            courseScheduleROPageRO.getEntity().setCollege(userBelongCollege.getCollegeName());
        } else if (roleList.contains(TEACHING_POINT_ADMIN.getRoleName())) {

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
        List<CourseLearningVO> pagedCourseLearningVOs = sortedList.subList((int) start, (int) end);

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
            // 对传入的主讲教师 ID 做校验
            String defaultMainTeacherUsername = courseLearningCreateRO.getDefaultMainTeacherUsername();
            if (!isValidUsername(defaultMainTeacherUsername)) {
                throw new RuntimeException("教师用户名为空 或者 数据库中找不到 " + defaultMainTeacherUsername);
            }
            coursesLearningPO.setDefaultMainTeacherUsername(defaultMainTeacherUsername);

            List<CourseAssistantsPO> courseAssistantsPOS = new ArrayList<>();
            for (String s : courseLearningCreateRO.getAssistantUsername()) {
                if (!isValidUsername(s)) {
                    throw new RuntimeException("助教用户名为空 或者 数据库中找不到 " + s);
                }
                CourseAssistantsPO courseAssistantsPO = new CourseAssistantsPO().setUsername(s);
                courseAssistantsPOS.add(courseAssistantsPO);
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
        return String.join(" ", courseNames);
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
    public boolean deleteCourse(Long courseId) {
        try {
            if (getBaseMapper().selectCount(new LambdaQueryWrapper<CoursesLearningPO>()
                    .eq(CoursesLearningPO::getId, courseId)) == 0) {
                // 没有这门课 说明已经被删除了
                return true;
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

            return true;
        } catch (Exception e) {
            log.error("删除课程失败 " + e);
            return false;
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

        }

        List<CourseLearningStudentInfoVO> courseLearningStudentInfoVOList = getBaseMapper().selectCourseStudentsInfo(courseStudentSearchROPageRO.getEntity(),
                courseStudentSearchROPageRO.getPageNumber() - 1, courseStudentSearchROPageRO.getPageSize());



        PageVO pageVO = new PageVO<CourseLearningStudentInfoVO>();
        pageVO.setRecords(courseLearningStudentInfoVOList);
        pageVO.setSize(courseStudentSearchROPageRO.getPageSize());
        pageVO.setCurrent(courseStudentSearchROPageRO.getPageNumber());
        Long total = getBaseMapper().selectCountCourseStudentsInfo(courseStudentSearchROPageRO.getEntity());
        pageVO.setTotal(total);
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

        // 检查集合是否为空或大小为0
        boolean isExistingEmpty = existingClassIdentifiers == null || existingClassIdentifiers.isEmpty();
        boolean isNewEmpty = newClassIdentifiers == null || newClassIdentifiers.isEmpty();

        // 检查是否需要更新映射关系
        if (isExistingEmpty != isNewEmpty || !existingClassIdentifiers.equals(newClassIdentifiers)) {
            // 映射关系不同，需要更新
            // 首先删除所有原有的映射关系
            for (CoursesClassMappingPO existingMapping : coursesClassMappingPOS) {
                coursesClassMappingService.getBaseMapper().deleteById(existingMapping.getId());
            }

            // 然后根据新的 classIdentifier 列表创建新的映射关系
            // 更新白名单
            List<StudentWhiteListInfoBO> studentWhiteListInfoBOList = new ArrayList<>();
            for (String newClassIdentifier : classIdentifier) {
                // 忽略空值
                if (newClassIdentifier != null && !newClassIdentifier.isEmpty()) {
                    CoursesClassMappingPO newMapping = new CoursesClassMappingPO()
                            .setCourseId(coursesLearningPO.getId())
                            .setClassIdentifier(newClassIdentifier);
                    coursesClassMappingService.getBaseMapper().insert(newMapping);

                    List<StudentWhiteListInfoBO> studentWhiteListInfoBOList1 =
                            studentStatusService.getBaseMapper().selectLivingWhiteList(
                                    new StudentStatusFilterRO().setClassIdentifier(
                                            newClassIdentifier));
                    studentWhiteListInfoBOList.addAll(studentWhiteListInfoBOList1);
                }
            }
            importWhiteStudents(studentWhiteListInfoBOList, getLiveCourseChannelId(coursesLearningPO.getId()));

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
            courseSectionVO.setValid(sectionsPO.getValid());

            if(sectionsPO.getContentType().equals(CourseContentType.LIVING.getContentType())){
                // 直播节点
                LiveResourcesPO liveResourcesPO = liveResourceService.getBaseMapper().selectOne(new LambdaQueryWrapper<LiveResourcesPO>()
                        .eq(LiveResourcesPO::getCourseId, sectionsPO.getCourseId())
                        .eq(LiveResourcesPO::getSectionId, sectionsPO.getId())
                );
                LiveResourceVO liveResourceVO = new LiveResourceVO();
                BeanUtils.copyProperties(liveResourcesPO, liveResourceVO);
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
            // 目前只处理这两种节点内容

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
        }

        return SaResult.ok("创建课程节点成功");
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
     * @param username
     * @return
     */
    public List<CourseInfoVO> getCourseInfo(String username) {
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
        coursesLearningRO.setCourseIds(courseIds);


        // 现在 coursesLearningRO 包含了正确的列表，可以传递给 selectCourseLearningData 方法
        List<CourseLearningVO> courseLearningVOS = getBaseMapper().selectCourseLearningDataWithoutPaging(coursesLearningRO);


        // 使用 Stream API 过滤掉 valid 为 'N' 的课程
        List<CourseLearningVO> filteredCourseLearningVOS = courseLearningVOS.stream()
                .filter(course -> "Y".equals(course.getValid()))
                .collect(Collectors.toList());

        List<CourseInfoVO> courseInfoVOS = new ArrayList<>();
        for(CourseLearningVO courseLearningVO : courseLearningVOS){
            CourseInfoVO courseInfoVO = new CourseInfoVO()
                    .setCourseId(courseLearningVO.getId())
                    .setYear(courseLearningVO.getGrade())
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
            List<CourseLearningVO> courseLearningVOS1 = getBaseMapper().selectCourseLearningDataWithoutPaging(new CoursesLearningRO().setCourseIds(courseIds));

            combinedSet.addAll(courseLearningVOS);
            combinedSet.addAll(courseLearningVOS1);

        }else{
            // 辅导教师
            List<CourseAssistantsPO> courseAssistantsPOS = courseAssistantsService.getBaseMapper().selectList(new LambdaQueryWrapper<CourseAssistantsPO>()
                    .eq(CourseAssistantsPO::getUsername, teacherInformationPO.getTeacherUsername()));
            Set<Long> courseIds = courseAssistantsPOS.stream()
                    .map(CourseAssistantsPO::getCourseId) // 提取每个 CourseAssistantsPO 的 courseId
                    .collect(Collectors.toSet()); // 将结果收集到 Set 中
            List<CourseLearningVO> courseLearningVOS = getBaseMapper().selectCourseLearningDataWithoutPaging(new CoursesLearningRO().setCourseIds(courseIds));
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
                    .setYear(courseLearningVO.getGrade())
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
}
