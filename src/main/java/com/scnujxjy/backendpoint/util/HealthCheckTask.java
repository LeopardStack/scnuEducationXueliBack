package com.scnujxjy.backendpoint.util;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.TutorInformation;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.TutorInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;
import com.scnujxjy.backendpoint.service.SingleLivingService;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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

//    @Autowired(required = false)
//    private PlatformUserMapper platformUserMapper;

    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;

    @Resource
    private ScnuXueliTools scnuXueliTools;
    @Resource
    private TutorInformationMapper tutorInformationMapper;

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

    @Scheduled(fixedRate = 60_000) // 每60s触发一次
    public void getCourses() {
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

                            //创建时，将排课表中的所有该门课的直播间都建为统一。
                            QueryWrapper<CourseSchedulePO> courseQueryWrapper = new QueryWrapper<>();
                            courseQueryWrapper.eq("course_name", courseSchedulePO.getCourseName())
                                    .eq("main_teacher_name", courseSchedulePO.getMainTeacherName())
                                    .and(i -> i.isNull("online_platform").or().eq("online_platform", ""));

                            List<CourseSchedulePO> schedulePOList = courseScheduleMapper.selectList(courseQueryWrapper);


//                            List<CourseSchedulePO> courseSchedulePOList =  courseSchedulePOS.stream().
//                                    filter(cs ->
////                                            cs.getTeachingDate().equals(courseSchedulePO.getTeachingDate()) &&
////                                            cs.getTeachingTime().equals(courseSchedulePO.getTeachingTime()) &&
//                                            cs.getTeacherUsername().equals(courseSchedulePO.getTeacherUsername()) &&
//                                            cs.getCourseName().equals(courseSchedulePO.getCourseName())).collect(Collectors.toList());

                            Integer maxTutorCount = 10;
                            if (schedulePOList.size() < 10) {
                                maxTutorCount = schedulePOList.size();
                            }

                            //创建监播权的助教并得到该助教链接及密码
                            for (int i = 0; i < maxTutorCount; i++) {
                                String totorName = StrUtil.isBlank(courseSchedulePO.getTutorName()) ? "老师" + i : courseSchedulePO.getTutorName();
                                singleLivingService.createTutor(channelId, totorName);
                            }

                            //查询助教表的该频道的助教数
                            QueryWrapper<TutorInformation> tutorQueryWrapper = new QueryWrapper<>();
                            tutorQueryWrapper.eq("channel_id", channelId);
                            Integer integer = tutorInformationMapper.selectCount(tutorQueryWrapper);

                            if (integer .equals( maxTutorCount)) {
                                log.info(channelId + "创建助教成功");
                            }
//                                ChannelInfoResponse channelInfoResponse = (ChannelInfoResponse) tutorInformation.getData();

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
                                    updateWrapper.set("online_platform", videoStreamRecordPO.getId()).eq("id", schedulePO.getId());
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

//    @Scheduled(fixedRate = 60_000) // 每60s触发一次
    public void updateWhiteList() {
        //先查出一小时内开好课的课程。然后将白名单

    }


    //    @Scheduled(fixedRate = 60_000) // 每60s触发一次
    public void updateWatchStatus() {
        //获取当天日期
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        LocalDate localDate = LocalDate.now(zoneId);//2023-11-02

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String nowTime = now.format(formatter);


        //live（直播中）、end（直播结束）、playback（回放中）、waiting（等待直播）
        QueryWrapper<VideoStreamRecordPO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teaching_date", localDate)// 先筛选日期为今天的
                .lt("TIMESTAMPDIFF(HOUR, end_time, '" + nowTime + "')", 1)//再筛选下课程结束时间-现在超过了1小时
                .in("watch_status", "waiting", "live", "end");

        List<VideoStreamRecordPO> videoStreamRecordPOS = videoStreamRecordsMapper.selectList(queryWrapper);//这样就拿到了所有的直播记录
        for (VideoStreamRecordPO videoStreamRecordPO : videoStreamRecordPOS) {
            singleLivingService.GetChannelDetail(videoStreamRecordPO.getChannelId());
            //找出所有的观看页面
        }


    }

}

