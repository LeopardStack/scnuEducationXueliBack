package com.scnujxjy.backendpoint.exportAttendanceData;

import cn.dev33.satoken.util.SaResult;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.ViewLogResponse;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleDetailVO;
import com.scnujxjy.backendpoint.service.video_stream.SingleLivingServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.polyv.live.v1.entity.web.auth.LiveChannelWhiteListRequest;
import net.polyv.live.v1.entity.web.auth.LiveChannelWhiteListResponse;
import net.polyv.live.v1.service.web.impl.LiveWebAuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class Test1 {

    @Resource
    private CourseScheduleMapper courseScheduleMapper;

    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Resource
    private SingleLivingServiceImpl singleLivingService;

    public String formatDuration(long totalSeconds) {
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(String.format("%02d天 ", days));
        }
        if (hours > 0 || days > 0) { // 如果有小时数或者天数，显示小时
            sb.append(String.format("%02d小时 ", hours));
        }
        if (minutes > 0 || hours > 0 || days > 0) { // 如果有分钟数、小时数或者天数，显示分钟
            sb.append(String.format("%02d分钟 ", minutes));
        }
        sb.append(String.format("%02d秒", seconds)); // 秒数总是显示

        return sb.toString();
    }

    private List<AttendanceTestVO> getAttendacneList(String courseId, CourseScheduleDetailVO scheduleCoursesInformationVO){
        try {
//            //获取该排课表的频道直播间id
//            CourseSchedulePO schedulePO = courseScheduleMapper.selectById(courseId);
            if (StrUtil.isBlank(scheduleCoursesInformationVO.getOnlinePlatform())) {
                throw new IllegalArgumentException("该排课表没有 直播间信息 " + scheduleCoursesInformationVO);
            }

            VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectById(scheduleCoursesInformationVO.getOnlinePlatform());
            SaResult channelCardPush = singleLivingService.getStudentViewLog(videoStreamRecordPO);
            List<ViewLogResponse> viewLogResponseList = (List<ViewLogResponse>) channelCardPush.getData();
            if (viewLogResponseList.size() == 0) {
                throw new IllegalArgumentException("该排课时间段内没有学生观看数据");
            }

            //将观看数据根据param1字段聚合后playDuration相加，再去重。
            Map<String, List<ViewLogResponse>> groupByParam1 = viewLogResponseList.stream()
                    .collect(Collectors.groupingBy(ViewLogResponse::getParam1));

            // 对每个学生分组做操作，有的学生一场课多次进入，观看时长需要累加
            Map<String, Integer> viewResult = new HashMap<>();
            for (Map.Entry<String, List<ViewLogResponse>> entry : groupByParam1.entrySet()) {
                int totalPlayDuration = entry.getValue().stream()
                        .mapToInt(ViewLogResponse::getPlayDuration)
                        .sum();
                viewResult.put(entry.getKey(), totalPlayDuration);
            }
            //这样就获取到了每个学生的观看时长数据viewResult。key是身份证号，value是时长

            //去重后的观众观看数据，同时对观看时长更新下
            List<ViewLogResponse> distinctViewLogResponse = groupByParam1.values()
                    .stream()
                    .map(subList -> subList.get(0))
                    .collect(Collectors.toList());
            for (ViewLogResponse viewLogResponse : distinctViewLogResponse) {
                viewLogResponse.setPlayDuration(viewResult.get(viewLogResponse.getParam1()));
            }

            //考勤导出attendanceVOList,拥有出勤的所有学生数据
            List<AttendanceTestVO> attendanceVOList = new ArrayList<>();
            for (ViewLogResponse viewLogResponse : distinctViewLogResponse) {
                try {
                    AttendanceTestVO attendanceVO = new AttendanceTestVO();

                    BeanUtils.copyProperties(scheduleCoursesInformationVO, attendanceVO);
                    attendanceVO.setName(viewLogResponse.getParam2());

                    // 使用方法
                    long durationInSeconds = viewLogResponse.getPlayDuration();
                    String formattedDuration = formatDuration(durationInSeconds);
                    attendanceVO.setPlayDuration(formattedDuration);
                    attendanceVO.setTotalSeconds(viewLogResponse.getPlayDuration().toString());

                    attendanceVO.setAttendance("是");
                    // 将时间戳转换为 Instant
                    Long firstActiveTime = viewLogResponse.getFirstActiveTime();
                    Long lastActiveTime = viewLogResponse.getLastActiveTime();
                    Instant instant = Instant.ofEpochMilli(firstActiveTime == null ? lastActiveTime : firstActiveTime);

                    // 转换为东八区时间
                    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.of("Asia/Shanghai"));

                    // 格式化日期时间
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = zonedDateTime.format(formatter);

                    attendanceVO.setStartTime(formattedDateTime);
                    attendanceVOList.add(attendanceVO);
                    StudentStatusVO studentStatusVO = studentStatusMapper.selectStudentByidNumberGrade(viewLogResponse.getParam1(), scheduleCoursesInformationVO.getGrade());
                    if (studentStatusVO != null) {
                        attendanceVO.setCode(studentStatusVO.getStudentNumber());
                        QueryWrapper<ClassInformationPO> queryWrapper = new QueryWrapper<>();
                        queryWrapper.eq("class_identifier", studentStatusVO.getClassIdentifier());
                        List<ClassInformationPO> classInformationPOS = classInformationMapper.selectList(queryWrapper);
                        if (classInformationPOS.size() != 0) {
                            attendanceVO.setClassName(classInformationPOS.get(0).getClassName());//根据身份证拿到学生的学号，班别。
                        }
                    }
                }catch (Exception e){
                    log.error("解析考勤数据异常 " + viewLogResponse);
                }
            }
            Collections.sort(attendanceVOList, Comparator.comparingInt(a -> Integer.parseInt(((AttendanceTestVO) a).getTotalSeconds())).reversed());


            //拿到直播间所有学生白名单数据whiteLists
            List<LiveChannelWhiteListResponse.ChannelWhiteList> whiteLists = new ArrayList<>();
            for (int i = 1; i < 10; i++) {
                LiveChannelWhiteListRequest liveChannelWhiteListRequest = new LiveChannelWhiteListRequest();
                liveChannelWhiteListRequest.setChannelId(videoStreamRecordPO.getChannelId())
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

            List<LiveChannelWhiteListResponse.ChannelWhiteList> noAttendList = new ArrayList<>();
            for (LiveChannelWhiteListResponse.ChannelWhiteList channel : whiteLists) {
                boolean found = false;
                for (ViewLogResponse viewLogResponse : distinctViewLogResponse) {
                    // 假设有一个名为id的属性用于比较
                    if (channel.getPhone().equals(viewLogResponse.getParam1())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    noAttendList.add(channel);
                }
            }
            //这样就拿到了没有出勤的学生数据noAttendList

            for (LiveChannelWhiteListResponse.ChannelWhiteList channelWhiteList : noAttendList) {
                AttendanceTestVO attendanceVO = new AttendanceTestVO();

                BeanUtils.copyProperties(scheduleCoursesInformationVO, attendanceVO);

                attendanceVO.setName(channelWhiteList.getName());
                attendanceVO.setPlayDuration("0");
                attendanceVO.setAttendance("否");
                attendanceVOList.add(attendanceVO);
                StudentStatusVO studentStatusVO = studentStatusMapper.selectStudentByidNumberGrade(channelWhiteList.getPhone(), scheduleCoursesInformationVO.getGrade());

                if (studentStatusVO != null) {
                    attendanceVO.setCode(studentStatusVO.getStudentNumber());
                    QueryWrapper<ClassInformationPO> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("class_identifier", studentStatusVO.getClassIdentifier());
                    List<ClassInformationPO> classInformationPOS = classInformationMapper.selectList(queryWrapper);
                    if (classInformationPOS.size() != 0) {
                        attendanceVO.setClassName(classInformationPOS.get(0).getClassName());//根据身份证拿到学生的学号，班别。
                    }
                }
            }

            log.info("产生了考勤表 记录总数为 " + attendanceVOList.size());
            return attendanceVOList;
        } catch (Exception e) {
            log.error("调用导出考勤表接口失败，异常信息为" + e);
        }

        return null;
    }

    /**
     * 测试 指定 排课表 ID 的 考勤数据
     */
    @Test
    public void test1(){
//        getAttendacneList("10294");
    }

    /**
     * 导出指定学院、年级的教学考勤表
     */
    @Test
    public void test2(){
        List<CourseScheduleDetailVO> scheduleCourseInformationVOList = courseScheduleMapper.selectAllCourseScheduleInformationWithoutPage(new CourseScheduleFilterRO().setCollege("文学院").setGrade("2023"));
        log.info("文学院 2023级 的排课表 总记录 " + scheduleCourseInformationVOList.size());

        Map<String, List<AttendanceTestVO>> stringListMap = new HashMap<>();
        scheduleCourseInformationVOList
//                .stream()
//                .limit(5)
                .forEach(scheduleCoursesInformationVO -> {
                    Long id = scheduleCoursesInformationVO.getId();
                    String key = "";  // 这个 key 用来标识  一次排课 即 同一个老师  同一门课程 同一个时间
                    key = scheduleCoursesInformationVO.getGrade() + "-" + scheduleCoursesInformationVO.getTeacherUsername()
                            + "-" + scheduleCoursesInformationVO.getCourseName()
                            + "-" + scheduleCoursesInformationVO.getTeachingDate().toString()
                            + "-" + scheduleCoursesInformationVO.getTeachingTime();
                    if(stringListMap.containsKey(key)){
                        // 存在 不需要再次请求
                    }else{
                        List<AttendanceTestVO> attendacneList = getAttendacneList("" + id, scheduleCoursesInformationVO);
                        if(attendacneList == null){
                            log.info("该排课表没有考勤记录 " + scheduleCoursesInformationVO);
                        }else{
                            stringListMap.put(key, attendacneList);
                        }
                    }
                });

//        for(CourseScheduleDetailVO scheduleCoursesInformationVO: scheduleCourseInformationVOList){
//
//
//        }
        String filePath = "D:\\ScnuWork\\xueli\\xueliBackEnd\\src\\main\\resources\\data\\考勤表信息导出\\2023文学院考勤表.xlsx"; // 指定文件路径
        exportToExcel(stringListMap, filePath);
    }

    public void exportToExcel(Map<String, List<AttendanceTestVO>> stringListMap, String filePath) {
        // 创建 Excel 写入对象
        ExcelWriter excelWriter = EasyExcel.write(filePath, AttendanceTestVO.class).build();

        // 创建一个 Sheet
        WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").build();

        // 合并所有数据到一个列表中
        List<AttendanceTestVO> allData = new ArrayList<>();
        for (List<AttendanceTestVO> dataList : stringListMap.values()) {
            allData.addAll(dataList);
        }

        // 将合并后的数据写入 Sheet
        excelWriter.write(allData, writeSheet);

        // 关闭 Writer，释放资源
        excelWriter.close();
    }
}
