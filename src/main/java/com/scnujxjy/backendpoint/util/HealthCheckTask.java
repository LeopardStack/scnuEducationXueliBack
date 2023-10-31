package com.scnujxjy.backendpoint.util;
import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.livingCreate.ApiResponse;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelCreateRequestBO;
import com.scnujxjy.backendpoint.model.bo.SingleLiving.ChannelInfoResponse;
import com.scnujxjy.backendpoint.service.SingleLivingService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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

    @Autowired(required = false)
//    private PlatformUserMapper platformUserMapper;

    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;

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

    public static void main(String[] args) {
        LocalDateTime targetTime = LocalDateTime.now().plusHours(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        String formattedStartTime = targetTime.format(formatter);//查出来14:30的前一小时时间13:30
        System.out.println(formattedStartTime);
    }

    @Scheduled(fixedRate = 60_000) // 每60s触发一次
    public void getCourses() {
        String pattern = "yyyy-MM-dd HH:mm";

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        // 设置时区为北京（东八区）
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        // 获取当前北京日期
        LocalDate localDate = LocalDate.now(zoneId);

        QueryWrapper<CourseSchedulePO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teaching_date", localDate.toString())
                .and(i -> i.isNull("online_platform").or().eq("online_platform", ""));
        //先用日期是今天筛选掉绝大多数课程，再去得到现在到未来一小时内的所有课程信息。
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(queryWrapper);

        if (courseSchedulePOS.size()>0) {
            for (CourseSchedulePO courseSchedulePO : courseSchedulePOS) {
                LocalDateTime targetTime = LocalDateTime.now().plusHours(1);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
                String channelStartTime = targetTime.format(formatter);//查出来14:30的前一小时时间13:30

                String teachingTime = courseSchedulePO.getTeachingTime();
                String replaceTeachingTime = teachingTime.replace("-", "—");
                String courseStartTime = replaceTeachingTime.substring(0, replaceTeachingTime.indexOf("—"));//14:30—17:00获取-前面时间
                String courseEndTime = replaceTeachingTime.substring(replaceTeachingTime.indexOf("—") + 1);

                if (channelStartTime.charAt(0) == '0') {
                    channelStartTime = "00" + channelStartTime.substring(1);
                }
                LocalTime time1 = LocalTime.parse(channelStartTime);
                LocalTime time2 = LocalTime.parse(courseStartTime);
                Duration duration = Duration.between(time1, time2);
                long l = duration.toHours();
                //如果二者之差小于一小时，则创建直播间
                if (l<=1L){
                    try {
                        ChannelCreateRequestBO channelCreateRequestBO = new ChannelCreateRequestBO();
                        channelCreateRequestBO.setLivingRoomTitle(courseSchedulePO.getCourseName());
                        //默认开启无延迟
                        channelCreateRequestBO.setPureRtcEnabled("Y");
                        //默认关闭回放
                        channelCreateRequestBO.setPlayRollback(false);
                        channelCreateRequestBO.setStartDate(sdf.parse(localDate + " " + courseStartTime));//2023-07-22 14:30
                        channelCreateRequestBO.setEndDate(sdf.parse(localDate+" "+courseEndTime));
                        SaResult channel = singleLivingService.createChannel(channelCreateRequestBO);
                        if (channel.getCode()==200 && Objects.nonNull(channel.getData())){
                            ApiResponse apiResponse=(ApiResponse)channel.getData();
                            String channelId = apiResponse.getData().getChannelId().toString();
                            log.info("创建频道成功"+channelId);

                            //创建助教并得到助教链接及密码
                            String totorName= StrUtil.isBlank(courseSchedulePO.getTutorName())?"老师":courseSchedulePO.getTutorName();
                            SaResult tutorInformation = singleLivingService.createTutor(channelId, totorName);
                            ChannelInfoResponse channelInfoResponse=(ChannelInfoResponse)tutorInformation.getData();

                            VideoStreamRecordPO videoStreamRecordPO=new VideoStreamRecordPO();
                            videoStreamRecordPO.setChannelId(channelId);
                            videoStreamRecordPO.setName(courseSchedulePO.getCourseName());
                            videoStreamRecordPO.setChannelPasswd(apiResponse.getData().getChannelPasswd());
                            videoStreamRecordPO.setWatchStatus("waiting");
                            videoStreamRecordPO.setTutorUrl(channelInfoResponse.getUrl());
                            videoStreamRecordPO.setTutorPasswd(channelInfoResponse.getPassword());

                            int insert = videoStreamRecordsMapper.insert(videoStreamRecordPO);
                            if (insert>0){
                                log.info("插入直播表成功，频道id为："+apiResponse.getData().getChannelId().toString());
                                UpdateWrapper<CourseSchedulePO> updateWrapper = new UpdateWrapper<>();
                                updateWrapper.set("online_platform", videoStreamRecordPO.getId()).eq("id", courseSchedulePO.getId());
                                int update = courseScheduleMapper.update(null, updateWrapper);
                                if (update>0){
                                    log.info("该课程更新成功");
                                }
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

            }
        }


    }
}

