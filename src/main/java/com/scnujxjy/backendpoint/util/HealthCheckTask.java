package com.scnujxjy.backendpoint.util;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.context.event.EventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.constant.enums.RedisKeysEnum;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesClassMappingPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.LiveResourcesPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.RetakeStudentsPO;
import com.scnujxjy.backendpoint.dao.entity.courses_learning.SectionsPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.PersonalInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.TutorInformation;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.CoursesClassMappingMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.LiveResourceMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.RetakeStudentsMapper;
import com.scnujxjy.backendpoint.dao.mapper.courses_learning.SectionsMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.TutorInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoRequest;
import com.scnujxjy.backendpoint.model.bo.course_learning.CourseRecordBO;
import com.scnujxjy.backendpoint.model.bo.platform_message.ManagerInfoBO;
import com.scnujxjy.backendpoint.model.vo.video_stream.StudentWhiteListVO;
import com.scnujxjy.backendpoint.service.courses_learning.CoursesLearningService;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingService;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import net.polyv.live.v1.entity.web.auth.LiveChannelWhiteListRequest;
import net.polyv.live.v1.entity.web.auth.LiveChannelWhiteListResponse;
import net.polyv.live.v1.service.channel.impl.LiveChannelStateServiceImpl;
import net.polyv.live.v1.service.web.impl.LiveWebAuthServiceImpl;
import net.polyv.live.v2.entity.channel.state.LiveListChannelStreamStatusV2Request;
import net.polyv.live.v2.entity.channel.state.LiveListChannelStreamStatusV2Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HealthCheckTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private SingleLivingService singleLivingService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;


    @Value("${run.checkLivingStatusScan}")
    private boolean checkLivingStatusScan;

    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;

    @Resource
    private ScnuXueliTools scnuXueliTools;

    @Resource
    private CoursesLearningService coursesLearningService;

    @Resource
    private TutorInformationMapper tutorInformationMapper;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private SectionsMapper sectionsMapper;

    @Resource
    private LiveResourceMapper liveResourceMapper;

    @Resource
    private CoursesClassMappingMapper coursesClassMappingMapper;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private PersonalInfoMapper personalInfoMapper;

    @Resource
    private RetakeStudentsMapper retakeStudentsMapper;


    @Scheduled(fixedRate = 1000000)  // 每100秒执行一次
    public void checkConnections() {
        try {
            // 尝试向Redis执行一个简单的命令
            stringRedisTemplate.opsForValue().get("health_check");
            // 从Redis中获取键为"your_key"的值
            stringRedisTemplate.opsForValue().set("test", "2023", 100L, TimeUnit.SECONDS);
            String test = stringRedisTemplate.opsForValue().get("test");
//            log.info("The value of 'test' is: " + test);

            // 如果没有异常则打印连接成功的日志
            log.info("Redis连接成功");
        } catch (Exception e) {
            // 如果有异常则打印连接失败的日志
            log.error("Redis连接失败 " + e.toString());
        }

        try {
            // 尝试向MySQL执行一个简单的SQL
            platformUserMapper.healthCheck();
            // 如果没有异常则打印连接成功的日志
            log.info("MySQL连接成功");
        } catch (Exception e) {
            // 如果有异常则打印连接失败的日志
            log.error("MySQL连接失败" + e.toString());
        }

        try {
            // 尝试执行一个简单的 MongoDB 操作
            mongoTemplate.executeCommand("{ ping: 1 }");
            // 如果没有异常则打印连接成功的日志
            log.info("MongoDB连接成功");
        } catch (Exception e) {
            // 如果有异常则打印连接失败的日志
            log.error("MongoDB连接失败 " + e.toString());
        }
    }


    /**
     * 使用@PostConstruct注解确保在SpringBoot启动时执行此方法
     */
    @PostConstruct
    public void clearRedisDataOnStartup() {
        try {
            // 清除Redis中的所有数据
            stringRedisTemplate.getConnectionFactory().getConnection().flushDb();
            log.info("Redis数据已清除");
        } catch (Exception e) {
            log.error("清除Redis数据时出错: " + e.toString());
        }
    }

    public int getUniqueAdminClassCount(List<CourseSchedulePO> schedulePOList) {
        Set<String> uniqueAdminClasses = new HashSet<>();

        for (CourseSchedulePO schedule : schedulePOList) {
            // 假设getAdminClass()是SchedulePO对象获取adminClass值的方法
            if (schedule.getAdminClass() != null) {
                uniqueAdminClasses.add(schedule.getAdminClass());
            }
        }

        // uniqueAdminClasses的大小就是不重复的adminClass的数量
        return uniqueAdminClasses.size();
    }

    //    @Scheduled(fixedRate = 60_000) // 每60s触发一次
    public void getCourses() {
        if (!checkLivingStatusScan) {
            // 如果配置为 false，直接返回
            return;
        }
        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        // 设置时区为北京（东八区）,获取当前北京日期
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        LocalDate localDate = LocalDate.now(zoneId);

        QueryWrapper<CourseSchedulePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teaching_date", localDate.toString())
                .and(i -> i.isNull("online_platform").or().eq("online_platform", ""));
        //先用日期为今天筛选掉绝大多数课程，再将已经创建过直播间的课程排除。
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(queryWrapper);

        if (courseSchedulePOS.size() > 0) {
            out:
            for (CourseSchedulePO courseSchedulePO : courseSchedulePOS) {

                //1、获得排课表的开始和结束时间
                String teachingTime = courseSchedulePO.getTeachingTime().replace("-", "—");//14:30—17:00, 2:00-5:00
                String courseStartTime = teachingTime.substring(0, teachingTime.indexOf("—"));//获取14:30, 2:00
                String courseEndTime = teachingTime.substring(teachingTime.indexOf("—") + 1);//获取17:00, 5:00

                //2、需要得到现在的时间。
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                String nowTime = now.format(formatter);//查出来14:30的前一小时时间13:30

                //3、看看课程开始时间和现在有没有一小时差值。
                LocalTime formattedCourseStartTime = LocalTime.parse(courseStartTime, DateTimeFormatter.ofPattern("H:mm"));
                // 将 二者的时间统一转化为 HH:mm 格式的字符串
                LocalTime formattedNowTime = LocalTime.parse(nowTime, DateTimeFormatter.ofPattern("H:mm"));

                Duration duration = Duration.between(formattedNowTime, formattedCourseStartTime);//时间较晚的后面，课程时间在后面
                long minuteDiff = duration.toMinutes();
                //如果二者之差大于0小于60分钟，并且online为空，才去创建直播间

                courseSchedulePO = courseScheduleMapper.selectById(courseSchedulePO.getId());
                String OnlinePlatform = courseSchedulePO.getOnlinePlatform();

                if (minuteDiff >= 0 && minuteDiff <= 60 && StrUtil.isBlank(OnlinePlatform)) {

                    //如果没有直播间并且批次有和之前一样的。复用之前同批次的直播间
                    QueryWrapper<CourseSchedulePO> poQueryWrapper = new QueryWrapper<>();
                    poQueryWrapper.eq("batch_index", courseSchedulePO.getBatchIndex());
                    //获取到该课同批次的课程信息
                    List<CourseSchedulePO> poList = courseScheduleMapper.selectList(poQueryWrapper);
                    if (poList.size() != 0) {
                        for (CourseSchedulePO schedulePO : poList) {
                            if (StrUtil.isNotBlank(schedulePO.getOnlinePlatform())) {
                                UpdateWrapper<CourseSchedulePO> updateWrapper = new UpdateWrapper<>();
                                updateWrapper.set("online_platform", schedulePO.getOnlinePlatform())
                                        .eq("id", courseSchedulePO.getId());
                                int update = courseScheduleMapper.update(null, updateWrapper);
                                if (update > 0) {
                                    log.info(courseSchedulePO + "该课程批次已存在直播间，直接复用无需新建");
                                    continue out;
                                }
                            }
                        }

                    }

                    try {
                        ChannelCreateRequestBO channelCreateRequestBO = new ChannelCreateRequestBO();
                        channelCreateRequestBO.setLivingRoomTitle(courseSchedulePO.getCourseName());
                        //默认开启无延迟
                        channelCreateRequestBO.setPureRtcEnabled("N");
                        //默认关闭回放
                        channelCreateRequestBO.setPlayRollback(false);
                        channelCreateRequestBO.setStartDate(sdf.parse(localDate + " " + courseStartTime));//2023-07-22 14:30
                        channelCreateRequestBO.setEndDate(sdf.parse(localDate + " " + courseEndTime));//2023-07-22 15:30
                        SaResult channel = singleLivingService.createChannel(channelCreateRequestBO, courseSchedulePO);
                        if (channel.getCode() == 200 && Objects.nonNull(channel.getData())) {
                            ApiResponse apiResponse = (ApiResponse) channel.getData();
                            String channelId = apiResponse.getData().getChannelId().toString();
                            log.info("创建频道成功,频道号为：" + channelId);

                            //创建时，将排课表中的所有该门课的直播间都建为同一个online。
                            QueryWrapper<CourseSchedulePO> courseQueryWrapper = new QueryWrapper<>();
                            courseQueryWrapper.eq("course_name", courseSchedulePO.getCourseName())
                                    .eq("main_teacher_name", courseSchedulePO.getMainTeacherName())
                                    .eq("batch_index", courseSchedulePO.getBatchIndex())
                                    .and(i -> i.isNull("online_platform").or().eq("online_platform", ""));

                            List<CourseSchedulePO> schedulePOList = courseScheduleMapper.selectList(courseQueryWrapper);

                            Integer maxTutorCount = 10;
                            //创建监播权的助教并得到该助教链接及密码
                            for (int i = 0; i < maxTutorCount; i++) {
                                String totorName = StrUtil.isBlank(courseSchedulePO.getTutorName()) ? "老师" + i : courseSchedulePO.getTutorName();
                                singleLivingService.createTutor(channelId, totorName);
                            }

                            //查询助教表的该频道的助教数
                            QueryWrapper<TutorInformation> tutorQueryWrapper = new QueryWrapper<>();
                            tutorQueryWrapper.eq("channel_id", channelId);
                            Integer integer = tutorInformationMapper.selectCount(tutorQueryWrapper);

                            if (integer.equals(maxTutorCount)) {
                                log.info(channelId + "创建10个助教成功");
                            }

                            //将直播间数据插入直播记录表中
                            VideoStreamRecordPO videoStreamRecordPO = new VideoStreamRecordPO();
                            videoStreamRecordPO.setChannelId(channelId);
                            videoStreamRecordPO.setName(courseSchedulePO.getCourseName());
                            videoStreamRecordPO.setChannelPasswd(apiResponse.getData().getChannelPasswd());
                            videoStreamRecordPO.setWatchStatus("等待中");
                            videoStreamRecordPO.setStartTime(sdf.parse(localDate + " " + courseStartTime));
                            videoStreamRecordPO.setEndTime(sdf.parse(localDate + " " + courseEndTime));
                            int insert = videoStreamRecordsMapper.insert(videoStreamRecordPO);
                            if (insert > 0) {
                                log.info("直播间数据插入直播表成功，频道id为：" + channelId);
                                int count = 0;
                                for (CourseSchedulePO schedulePO : schedulePOList) {
                                    UpdateWrapper<CourseSchedulePO> updateWrapper = new UpdateWrapper<>();
                                    updateWrapper.set("online_platform", videoStreamRecordPO.getId())
                                            .eq("id", schedulePO.getId());
                                    int update = courseScheduleMapper.update(null, updateWrapper);
                                    count = count + update;
                                }
                                if (count == schedulePOList.size()) {
                                    log.info("创建直播间且合班情况，成功");
                                }

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            }
        }

    }


    /**
     * 预热课程数据 便于查询和搜索
     */
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("应用启动完成，开始加载管理员信息到Redis");
        try {
            retryUpdateManagerInfoBO(0);
        } catch (Exception e) {
            log.error("管理员信息加载失败: " + e.getMessage(), e);
        }
    }

    private void retryUpdateManagerInfoBO(int attempt) throws InterruptedException {
        try {
            updateManagerInfoBO();
        } catch (Exception e) {
            if (attempt < 3) {  // 尝试次数，这里尝试3次
                long delay = (long) Math.pow(10, attempt + 1) * 30; // 计算延时，30秒、300秒、3000秒
                log.warn("加载失败, 将在 {} 秒后重试", delay / 1000);
                Thread.sleep(delay); // 等待一定时间后重试
                retryUpdateManagerInfoBO(attempt + 1); // 递归调用，增加尝试次数
            } else {
                log.error("重试加载管理员信息失败", e);
                throw e;  // 最终失败抛出异常
            }
        }
    }

    //    @Scheduled(cron = "0 */1 * * * *") // 每分钟执行一次
    public void refreshCourseSectionsInRedis() {
        try {
            log.info("开始执行课程信息预热");
            List<CourseRecordBO> courseSections = coursesLearningService.getCourseSections(null);
            log.info("预热完毕，共获取 " + courseSections.size() + " 条数据");

            redisTemplate.opsForValue().set("courseSections", courseSections); // 将数据存储在 Redis 中
        } catch (Exception e) {
            log.error("Error updating course sections in Redis", e);
        }
    }

    //    @Scheduled(cron = "0 */1 * * * *") // 每分钟执行一次
    public void getChannelStatus() {
        LiveListChannelStreamStatusV2Request liveListChannelStreamStatusV2Request = new LiveListChannelStreamStatusV2Request();
        List<LiveListChannelStreamStatusV2Response> liveListChannelStreamStatusV2Respons;
        try {
            //建议拿到当天的直播频道回放状态即可。
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String todayString = today.format(formatter);

            List<SectionsPO> sectionsPOS = sectionsMapper.selectSectionsByDate(todayString);
            List<String> channelIdList = new ArrayList<>();
            for (SectionsPO sectionsPO : sectionsPOS) {
                LiveResourcesPO query = liveResourceMapper.query(sectionsPO.getCourseId());
                if (query != null) {
                    channelIdList.add(query.getChannelId());
                }
            }
            if (channelIdList.isEmpty()) {
                log.info("当天无直播，无需获取直播间的直播状态");
                return;
            }
            String channelIds = String.join(",", channelIdList);
            liveListChannelStreamStatusV2Request.setChannelIds(channelIds);
            liveListChannelStreamStatusV2Respons = new LiveChannelStateServiceImpl().listChannelLiveStreamV2(
                    liveListChannelStreamStatusV2Request);
            if (liveListChannelStreamStatusV2Respons != null) {
                log.info("批量查询频道直播状态成功:{}", JSON.toJSONString(liveListChannelStreamStatusV2Respons));
                //拿到是List<"channelId","status">的数据，自行处理。   感觉可以放redis，没必要每分钟都查，不过怕出现当天加课加直播间，再看看ba
            }
        } catch (Exception e) {
            log.error("批量查询直播间状态失败，异常信息为", e);
        }
    }



    public void updateManagerInfoBO() {
        String tempKey = RedisKeysEnum.PLATFORM_MANAGER_INFO.getRedisKeyOrPrefix() + ":temp";
        String mainKey = RedisKeysEnum.PLATFORM_MANAGER_INFO.getRedisKeyOrPrefix();

        List<ManagerInfoBO> managerInfoList = scnuXueliTools.getManagerInfoList();
        redisTemplate.opsForValue().set(tempKey, managerInfoList);
        redisTemplate.rename(tempKey, mainKey);
    }




    @Scheduled(fixedRate = 3600_000) // 每1h触发一次
    @Async
    public void updateWhiteList() {
        List<LiveChannelWhiteListResponse.ChannelWhiteList> whiteLists = new ArrayList<>();
        try {
            //先找出今天的所有直播
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String todayString = today.format(formatter);

            List<SectionsPO> sectionsPOS = sectionsMapper.selectSectionsByDate(todayString);
            List<String> channelIdList = new ArrayList<>();
            for (SectionsPO sectionsPO : sectionsPOS) {
                LiveResourcesPO liveResourcesPO = liveResourceMapper.query(sectionsPO.getCourseId());
                if (liveResourcesPO != null && StringUtils.isNotBlank(liveResourcesPO.getChannelId())) {
                    channelIdList.add(liveResourcesPO.getChannelId());
                }
            }
            if (channelIdList.isEmpty()) {
                log.info("当天无直播，无需更新直播间白名单");
                return;
            }
            //建议只有当天有直播的才需要去更新白名单
            for (String channelId : channelIdList) {
                for (int i = 1; i < 10; i++) {
                    LiveChannelWhiteListRequest liveChannelWhiteListRequest = new LiveChannelWhiteListRequest();
                    liveChannelWhiteListRequest.setChannelId(channelId)
                            .setRank(1)
                            .setCurrentPage(i)
                            .setPageSize(1000);
                    LiveChannelWhiteListResponse liveChannelWhiteListResponse = new LiveWebAuthServiceImpl().getChannelWhiteList(liveChannelWhiteListRequest);
                    if (liveChannelWhiteListResponse.getContents().size() != 0) {
                        List<LiveChannelWhiteListResponse.ChannelWhiteList> contents = liveChannelWhiteListResponse.getContents();
                        whiteLists.addAll(contents);
                    } else {
                        break;
                    }
                }
                List<String> whiteCodeList = new ArrayList<>();
                for (LiveChannelWhiteListResponse.ChannelWhiteList channelWhiteList : whiteLists) {
                    whiteCodeList.add(channelWhiteList.getPhone());
                }
                //这样就拿到了该直播间的所有白名单。再查出当堂课的所有班级对应的所有学生。看二者是否相同

                LiveResourcesPO liveResourcesPO = liveResourceMapper.queryCourseId(channelId);
                List<CoursesClassMappingPO> coursesClassMappingPOS = coursesClassMappingMapper.selectByCourseId(liveResourcesPO.getCourseId());
                List<String> uniqueClassIdentifier = coursesClassMappingPOS.stream()
                        .map(CoursesClassMappingPO::getClassIdentifier)
                        .distinct()
                        .collect(Collectors.toList());
                //如果课程对应的班级没有，就跳过
                if (uniqueClassIdentifier.size() == 0) {
                    continue;
                }

                //找出所有在这些行政班的学生
                List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectList(
                        Wrappers.<StudentStatusPO>lambdaQuery().in(StudentStatusPO::getClassIdentifier, uniqueClassIdentifier)
                );

                //还有重修的学生
                List<RetakeStudentsPO> retakeStudentsPOS = retakeStudentsMapper.selectByCourseId(liveResourcesPO.getCourseId());
                //拿到所有重修学生的学号
                List<String> uniqueRetakeStudents = retakeStudentsPOS.stream()
                        .map(RetakeStudentsPO::getStudentNumber)
                        .distinct()
                        .collect(Collectors.toList());
                if (retakeStudentsPOS.size() != 0) {
                    List<StudentStatusPO> retakeStudentStatusPOS = studentStatusMapper.selectList(
                            Wrappers.<StudentStatusPO>lambdaQuery().in(StudentStatusPO::getStudentNumber, uniqueRetakeStudents)
                    );
                    studentStatusPOS.addAll(retakeStudentStatusPOS);
                }


                //只有白名单中不存在该学生时才需要去更新该频道的白名单
//                List<StudentWhiteListVO> studentWhiteList = studentStatusPOS.stream()
//                        .filter(studentStatusPO -> !whiteCodeList.contains(studentStatusPO.getIdNumber()))
//                        .map(studentStatusPO -> {
//                            StudentWhiteListVO studentWhiteListVO = new StudentWhiteListVO();
//                            studentWhiteListVO.setCode(studentStatusPO.getIdNumber());
//                            PersonalInfoPO personalInfoPO = personalInfoMapper.selectInfoByGradeAndIdNumberOne(studentStatusPO.getIdNumber());
//                            if (personalInfoPO != null && StringUtils.isNotBlank(personalInfoPO.getName())) {
//                                studentWhiteListVO.setName(personalInfoPO.getName());
//                            }
//                            return studentWhiteListVO;
//                        })
//                        .collect(Collectors.toList());
                //只有不相等的时候才需要更新白名单
                List<String> idNumbers = studentStatusPOS.stream()
                        .map(StudentStatusPO::getIdNumber) // Extracting idNumber
                        .collect(Collectors.toList());

                //只有二者大小和内容都一致时，才不需要去更新白名单
                if (whiteCodeList.containsAll(idNumbers) && idNumbers.containsAll(whiteCodeList)) {
                    continue;
                }

                List<StudentWhiteListVO> studentWhiteList = new ArrayList<>();
                for (StudentStatusPO studentStatusPO : studentStatusPOS) {
                    StudentWhiteListVO listVO = new StudentWhiteListVO();
                    listVO.setCode(studentStatusPO.getIdNumber());
                    PersonalInfoPO personalInfoPO = personalInfoMapper.
                            selectInfoByGradeAndIdNumberOne1(studentStatusPO.getIdNumber());
                    if (personalInfoPO != null && StringUtils.isNotBlank(personalInfoPO.getName())) {
                        listVO.setName(personalInfoPO.getName());
                    }
                    studentWhiteList.add(listVO);
                }

                ChannelInfoRequest channelInfoRequest1 = new ChannelInfoRequest();
                channelInfoRequest1.setChannelId(channelId);
                channelInfoRequest1.setIsClear("Y");
                singleLivingService.deleteChannelWhiteStudent(channelInfoRequest1);
                //先清除全部再添加
                ChannelInfoRequest channelInfoRequest = new ChannelInfoRequest();
                channelInfoRequest.setChannelId(channelId);
                channelInfoRequest.setStudentWhiteList(studentWhiteList);
                SaResult saResult = singleLivingService.addChannelWhiteStudentByFile(channelInfoRequest);
                if (saResult.getCode() == 200) {
                    log.info(channelId + "更新白名单成功");
                }
            }

        } catch (Exception e) {
            log.error("批量更新直播间白名单失败，异常信息为", e);
        }


    }
}

