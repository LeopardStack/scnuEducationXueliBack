package com.scnujxjy.backendpoint.service.courses_learning;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.CourseContentType;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.LiveRequestBody;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.getLivingInfo.ChannelInfoResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ChannelResponseData;
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
import com.scnujxjy.backendpoint.service.SingleLivingService;
import com.scnujxjy.backendpoint.service.core_data.TeacherInformationService;
import com.scnujxjy.backendpoint.service.minio.MinioService;
import com.scnujxjy.backendpoint.service.registration_record_card.ClassInformationService;
import com.scnujxjy.backendpoint.util.ResultCode;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import com.scnujxjy.backendpoint.util.video_stream.SingleLivingSetting;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
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
import java.time.LocalDateTime;
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
    private SectionsService sectionsService;

    @Resource
    private VideoStreamUtils videoStreamUtils;

    @Resource
    private SingleLivingSetting singleLivingSetting;

    @Resource
    private SingleLivingService singleLivingService;

    @Resource
    private VideoResourcesService videoResourcesService;

    @Resource
    private LiveResourceService liveResourceService;

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
    public boolean createCourse(CourseLearningCreateRO courseLearningCreateRO) {
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

            // 如果该门课程的类型是直播 或者 混合类型 则需要创建一个直播间给它
            // 获取当前时间
            LocalDateTime now = LocalDateTime.now();

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

            if (channel.getCode().equals(200)) {
                ChannelResponseData channelResponseData = channel.getData();
                VideoStreamRecordPO videoStreamRecordPO = new VideoStreamRecordPO();
                videoStreamRecordPO.setChannelId("" + channelResponseData.getChannelId());

                ChannelInfoResponse channelInfoByChannelId1 = videoStreamUtils.getChannelInfo("" + channelResponseData.getChannelId());
                log.info("频道信息包括 " + channelInfoByChannelId1);
                if (channelInfoByChannelId1.getCode().equals(200) && channelInfoByChannelId1.getSuccess()) {
                    log.info("创建频道成功");
                }
            }
        }catch (Exception e){
            log.info("创建课程失败 " + e);
            return false;
        }



        return true;
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

            // 统一删除直播间资源
            if (hasLivingRoom) {
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

            // 删除课程本身
            int i = getBaseMapper().deleteById(courseId);

            return true;
        }catch (Exception e){
            log.error("删除课程失败 " + e);
            return false;
        }
    }


    /**
     * 设置这门课是否有效
     * @param courseId
     * @return
     */
    public boolean setCourseInvalid(Long courseId) {
        CoursesLearningPO coursesLearningPO = getBaseMapper().selectOne(new LambdaQueryWrapper<CoursesLearningPO>()
                .eq(CoursesLearningPO::getId, courseId));
        if(coursesLearningPO.getValid().equals("Y")){
            coursesLearningPO.setValid("N");
        }else{
            coursesLearningPO.setValid("Y");
        }
        int i = getBaseMapper().updateById(coursesLearningPO);
        if(i > 0){
            return true;
        }
        return false;
    }
}
