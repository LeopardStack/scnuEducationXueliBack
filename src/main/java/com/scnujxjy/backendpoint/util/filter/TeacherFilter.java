package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.constant.enums.LiveStatusEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.TeachingAssistantsCourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.model.bo.teaching_process.CourseScheduleTime;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusTeacherFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import com.scnujxjy.backendpoint.util.tool.LogExecutionTime;
import com.scnujxjy.backendpoint.util.tool.ScnuTimeInterval;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Slf4j
public class TeacherFilter extends AbstractFilter{
    int tempTotal = 0;

    private List<TeacherSchedulesVO> getTeacherSchedules(List<TeacherCourseScheduleVO> courseSchedulePOS, PlatformUserPO platformUserPO,
                                     PageRO<CourseScheduleRO> courseScheduleFilter){

        Date teachingStartDate;
        Date teachingEndDate;

        // 如果过滤条件不是全部为空，则获取日期范围
        if(!scnuXueliTools.areAllFieldsNull(courseScheduleFilter.getEntity())){
            teachingStartDate = courseScheduleFilter.getEntity().getTeachingStartDate();
            teachingEndDate = courseScheduleFilter.getEntity().getTeachingEndDate();
        } else {
            teachingEndDate = null;
            teachingStartDate = null;
        }

        // 对 courseSchedulePOS 进行过滤，只保留在日期范围内的课程
        if(teachingStartDate != null && teachingEndDate != null){
            courseSchedulePOS = courseSchedulePOS.stream()
                    .filter(schedule ->
                            (scnuXueliTools.getTimeInterval(schedule.getTeachingDate(), schedule.getTeachingTime()).getStart().equals(teachingStartDate)
                            || scnuXueliTools.getTimeInterval(schedule.getTeachingDate(), schedule.getTeachingTime()).getStart().after(teachingStartDate)) &&
                            (scnuXueliTools.getTimeInterval(schedule.getTeachingDate(), schedule.getTeachingTime()).getStart().equals(teachingEndDate) ||
                            scnuXueliTools.getTimeInterval(schedule.getTeachingDate(), schedule.getTeachingTime()).getStart().before(teachingEndDate)))
                    .collect(Collectors.toList());
        }

        // 对 courseSchedulePOS 进行下一步处理：按照时间分组
        HashMap<CourseScheduleTime, ArrayList<TeacherCourseScheduleVO>> teacherSchedules = new HashMap<>();
        for(TeacherCourseScheduleVO teacherCourseScheduleVO: courseSchedulePOS){
            CourseScheduleTime key = new CourseScheduleTime(teacherCourseScheduleVO.getTeachingDate(), teacherCourseScheduleVO.getTeachingTime());
            if(!teacherSchedules.containsKey(key)){
                teacherSchedules.put(key, new ArrayList<>());
            }
            teacherSchedules.get(key).add(teacherCourseScheduleVO);
        }


        Date now = new Date();

        // 1. 提取 keys 到列表
        List<CourseScheduleTime> sortedKeys = new ArrayList<>(teacherSchedules.keySet());
        tempTotal = sortedKeys.size();
        // 2. 对 keys 列表进行排序
        Collections.sort(sortedKeys);

        // 3. 分割列表
        List<CourseScheduleTime> futureKeys = sortedKeys.stream()
                .filter(key -> {
                    Calendar calKey = Calendar.getInstance();
                    calKey.setTime(key.getTeachingDate());
                    calKey.set(Calendar.HOUR_OF_DAY, 0);
                    calKey.set(Calendar.MINUTE, 0);
                    calKey.set(Calendar.SECOND, 0);
                    calKey.set(Calendar.MILLISECOND, 0);

                    Calendar calNow = Calendar.getInstance();
                    calNow.setTime(now);
                    calNow.set(Calendar.HOUR_OF_DAY, 0);
                    calNow.set(Calendar.MINUTE, 0);
                    calNow.set(Calendar.SECOND, 0);
                    calNow.set(Calendar.MILLISECOND, 0);

                    return calKey.after(calNow) || calKey.equals(calNow);
                })
                .sorted() // 使用 compareTo 方法进行排序
                .collect(Collectors.toList());

        List<CourseScheduleTime> pastKeys = sortedKeys.stream()
                .filter(key -> {
                    Calendar calKey = Calendar.getInstance();
                    calKey.setTime(key.getTeachingDate());
                    calKey.set(Calendar.HOUR_OF_DAY, 0);
                    calKey.set(Calendar.MINUTE, 0);
                    calKey.set(Calendar.SECOND, 0);
                    calKey.set(Calendar.MILLISECOND, 0);

                    Calendar calNow = Calendar.getInstance();
                    calNow.setTime(now);
                    calNow.set(Calendar.HOUR_OF_DAY, 0);
                    calNow.set(Calendar.MINUTE, 0);
                    calNow.set(Calendar.SECOND, 0);
                    calNow.set(Calendar.MILLISECOND, 0);

                    return calKey.before(calNow);
                })
                .sorted(Comparator.reverseOrder()) // 反向排序
                .collect(Collectors.toList());



        // 4. 合并列表，将未来的排在前面
        List<CourseScheduleTime> mergedKeys = new ArrayList<>();
        mergedKeys.addAll(futureKeys);
        mergedKeys.addAll(pastKeys);

        // 5. 根据分页参数提取元素
        long pageNumber = courseScheduleFilter.getPageNumber(); // 假设这是页码
        long pageSize = courseScheduleFilter.getPageSize();  // 假设这是每页大小
        long start = (pageNumber - 1) * pageSize;
        long end = start + pageSize;

        // 使用 Java 8 的 Stream API 进行分页
        List<CourseScheduleTime> pagedKeys = IntStream.range(0, mergedKeys.size())
                .filter(i -> i >= start && i < end && i < mergedKeys.size())
                .mapToObj(mergedKeys::get)
                .collect(Collectors.toList());

        // 创建最终的列表
        List<TeacherSchedulesVO> teacherSchedulesVOList = new ArrayList<>();
        for(CourseScheduleTime courseScheduleTime: pagedKeys){
            log.info(teacherSchedules.get(courseScheduleTime).toString());
            ArrayList<TeacherCourseScheduleVO> teacherCourseScheduleVOS = teacherSchedules.get(courseScheduleTime);
            TeacherSchedulesVO teacherSchedulesVO = new TeacherSchedulesVO();
            teacherSchedulesVO.setVideoStreamRecordROList(new ArrayList<>());
            teacherSchedulesVO.setClassInformationPOList(new ArrayList<>());
            int allStudentCount = 0;
            for(TeacherCourseScheduleVO teacherCourseScheduleVO: teacherCourseScheduleVOS){
                teacherSchedulesVO.setCourseName(teacherCourseScheduleVO.getCourseName());
                try {
                    ClassInformationPO classInformationPO = classInformationMapper.selectOne(new LambdaQueryWrapper<ClassInformationPO>()
                            .eq(ClassInformationPO::getGrade, teacherCourseScheduleVO.getGrade())
                            .eq(ClassInformationPO::getMajorName, teacherCourseScheduleVO.getMajorName())
                            .eq(ClassInformationPO::getLevel, teacherCourseScheduleVO.getLevel())
                            .eq(ClassInformationPO::getStudyForm, teacherCourseScheduleVO.getStudyForm())
                            .eq(ClassInformationPO::getClassName, teacherCourseScheduleVO.getAdminClass())
                    );
                    teacherSchedulesVO.getClassInformationPOList().add(classInformationPO);
                    Integer i = studentStatusMapper.selectCount(new LambdaQueryWrapper<StudentStatusPO>()
                            .eq(StudentStatusPO::getClassIdentifier, classInformationPO.getClassIdentifier()));
                    allStudentCount += i;
                    ScnuTimeInterval timeInterval = scnuXueliTools.getTimeInterval(teacherCourseScheduleVO.getTeachingDate(), teacherCourseScheduleVO.getTeachingTime());
                    teacherSchedulesVO.setStartDate(timeInterval.getStart());
                    teacherSchedulesVO.setEndDate(timeInterval.getEnd());
                    teacherSchedulesVO.setStudentCount(allStudentCount);
                    teacherSchedulesVO.setExamType(teacherCourseScheduleVO.getExamType());
                    teacherSchedulesVO.setTeachingMethod(teacherCourseScheduleVO.getTeachingMethod());
                    TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                            .eq(TeacherInformationPO::getTeacherUsername, StpUtil.getLoginIdAsString()));
                    teacherSchedulesVO.setTeachingClass(teacherCourseScheduleVO.getTeachingClass());

                    String videoStreamId = teacherCourseScheduleVO.getOnlinePlatform();
                    if (videoStreamId != null) {
                        try {
                            VideoStreamRecordPO videoStreamRecordPO1 = videoStreamRecordsMapper.selectOne(
                                    new LambdaQueryWrapper<VideoStreamRecordPO>()
                                            .eq(VideoStreamRecordPO::getId, "" + videoStreamId)
                            );

                            if (videoStreamRecordPO1 != null) {
                                // 检查列表中是否存在具有相同id和channelId的记录
                                boolean exists = teacherSchedulesVO.getVideoStreamRecordROList().stream()
                                        .anyMatch(record -> record.getId().equals(videoStreamRecordPO1.getId())
                                                && record.getChannelId().equals(videoStreamRecordPO1.getChannelId()));

                                if (!exists) {
                                    // 如果不存在，才添加到列表中
                                    teacherSchedulesVO.getVideoStreamRecordROList().add(videoStreamRecordPO1);
                                }
                            } else {
                                if(videoStreamId.equals("已结束")){
                                    continue;
                                }else{
                                    // 删除不存在的直播间信息ID
                                    Long id = teacherCourseScheduleVO.getId();
                                    courseScheduleMapper.updateOnlinePlatformToNull(id);
                                    throw new IllegalArgumentException("存在教师的排课表中有直播间信息ID，但是直播间信息不存在" + courseScheduleFilter.getEntity().toString());
                                }
                            }
                        } catch (Exception e) {
                            throw new IllegalArgumentException("获取不到合法的直播间信息 " + e.toString());
                        }
                    }
                }catch (Exception e){
                    throw new IllegalArgumentException(teacherCourseScheduleVO + " 班级信息找不到 " + e.toString());
                }

            }

            teacherSchedulesVOList.add(teacherSchedulesVO);
        }
        log.info(pagedKeys.toString());
        log.info(teacherSchedulesVOList.toString());
        return teacherSchedulesVOList;
    }

    /**
     * 获取教师的排课信息
     * @param courseScheduleFilter 获取的排课表数据
     * @return
     */
    @Override
    public CourseScheduleFilterDataVO filterCourseSchedule(PageRO<CourseScheduleRO> courseScheduleFilter) {
        CourseScheduleFilterDataVO courseScheduleFilterDataVO = new CourseScheduleFilterDataVO();
        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }

        CourseScheduleRO entity = courseScheduleFilter.getEntity();
        // 使用 courseScheduleMapper 获取数据
        List<TeacherCourseScheduleVO> courseSchedulePOS = new ArrayList<>();
        long total = 0;
        if(scnuXueliTools.areAllFieldsNull(entity)){
            // 默认获取最近的排课
//            courseSchedulePOS = courseScheduleMapper.getCourseSchedulesByTeacherUserNameRecent(platformUserPO.getUsername(),
//                    (courseScheduleFilter.getPageNumber()-1) * courseScheduleFilter.getPageSize(),
//                    courseScheduleFilter.getPageSize());
//            total =  courseScheduleMapper.getCourseSchedulesByTeacherUserNameRecentCount(platformUserPO.getUsername());
            List<TeacherSchedulesVO> teacherSchedules = getTeacherSchedules(courseSchedulePOS, platformUserPO, courseScheduleFilter);
            total = tempTotal;
        }else{
            courseSchedulePOS = courseScheduleMapper.getCourseSchedulesByTeacherUserName(platformUserPO.getUsername(),
                    courseScheduleFilter.getEntity(), (courseScheduleFilter.getPageNumber()-1) * courseScheduleFilter.getPageSize(),
                    courseScheduleFilter.getPageSize());
            total =  courseScheduleMapper.countCourseSchedulesByTeacherUserName(platformUserPO.getUsername(),
                    courseScheduleFilter.getEntity());
        }


        courseScheduleFilterDataVO.setCourseSchedulePOS(courseSchedulePOS);
        courseScheduleFilterDataVO.setTotal(total);
        return courseScheduleFilterDataVO;
    }


    public PageVO<TeacherSchedulesVO> getTeacherCourschedules(PageRO<CourseScheduleRO> courseScheduleROPageRO) {
        PageVO<TeacherSchedulesVO> courseScheduleFilterDataVO = new PageVO<>();
        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }


        CourseScheduleRO entity = courseScheduleROPageRO.getEntity();
        TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getTeacherUsername, platformUserPO.getUsername()));
        // 获取它是辅导教师还是主讲教师
        String teacherType2 = teacherInformationPO.getTeacherType2();
        // 使用 courseScheduleMapper 获取数据
        List<TeacherCourseScheduleVO> courseSchedulePOS = new ArrayList<>();
        if(teacherType2.equals("主讲教师")){
            entity.setTeacherUsername(platformUserPO.getUsername());
            courseSchedulePOS = courseScheduleMapper.getCourseSchedulesByTeacherUserNameRecentBetter((platformUserPO.getUsername()));
        }else{
            courseSchedulePOS = courseScheduleMapper.getCourseSchedulesByTutor(platformUserPO.getUsername());
        }

        long total = 0;
        List<TeacherSchedulesVO> teacherSchedules = getTeacherSchedules(courseSchedulePOS, platformUserPO, courseScheduleROPageRO);
        total = tempTotal;

        courseScheduleFilterDataVO.setRecords(teacherSchedules);
        courseScheduleFilterDataVO.setTotal(total);
        return courseScheduleFilterDataVO;
    }

    /**
     * 获取教师的课程信息
     * @param courseScheduleROPageRO
     * @return
     */
    public PageVO<TeacherCoursesVO> getTeacherCourses(PageRO<CourseScheduleRO> courseScheduleROPageRO) {

        PageVO<TeacherCoursesVO> pageVO = new PageVO<>();

        CourseScheduleRO entity = courseScheduleROPageRO.getEntity();
        String userName = (String) StpUtil.getLoginId();

        TeacherInformationPO teacherInformationPO = teacherInformationMapper.selectOne(new LambdaQueryWrapper<TeacherInformationPO>()
                .eq(TeacherInformationPO::getTeacherUsername, userName));
        // 获取它是辅导教师还是主讲教师
        String teacherType2 = teacherInformationPO.getTeacherType2();
        if(teacherType2.equals("主讲教师")){
            entity.setTeacherUsername(userName);
        }else{
            entity.setTeachingAssistantUsername(userName);
        }

        List<TeacherCoursesVO> teacherCoursesVOS = courseScheduleMapper.selectTeacherCoursesWithoutDate(entity,
                (courseScheduleROPageRO.getPageNumber() - 1) * courseScheduleROPageRO.getPageSize(),
                courseScheduleROPageRO.getPageSize());

        long count = courseScheduleMapper.selectTeacherCoursesWithoutDateCount(entity);

        pageVO.setTotal(count);
        pageVO.setRecords(teacherCoursesVOS);

        return pageVO;
    }



    /**
     * 筛选学籍数据
     * @param studentStatusROPageRO 获取学籍数据的筛选数据
     * @return
     */
    @Override
    @LogExecutionTime
    public FilterDataVO getStudentStatusInfoByTeacher(PageRO<StudentStatusTeacherFilterRO> studentStatusROPageRO) {
        FilterDataVO<StudentStatusAllVO> studentStatusVOFilterDataVO = new FilterDataVO<>();
        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }


        log.info("主讲教师 " + StpUtil.getLoginIdAsString() + "查询学籍数据查询参数" + studentStatusROPageRO.getEntity());
        // 使用 courseInformationMapper 获取数据
        StudentStatusTeacherFilterRO entity = studentStatusROPageRO.getEntity();

        List<StudentStatusAllVO> studentStatusVOS = studentStatusMapper.getStudentStatusInfoByTeacher(studentStatusROPageRO.getEntity(),
                studentStatusROPageRO.getPageSize() * (studentStatusROPageRO.getPageNumber() -1),
                studentStatusROPageRO.getPageSize()
                );
        long total =  studentStatusMapper.getStudentStatusInfoByTeacherCount(studentStatusROPageRO.getEntity());
        studentStatusVOFilterDataVO.setData(studentStatusVOS);
        studentStatusVOFilterDataVO.setTotal(total);

        return studentStatusVOFilterDataVO;
    }
}
