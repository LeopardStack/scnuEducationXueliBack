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
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleFilterDataVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleWithLiveInfoVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.TeacherCourseScheduleVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.TeacherCoursesVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class TeacherFilter extends AbstractFilter{
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
            courseSchedulePOS = courseScheduleMapper.getCourseSchedulesByTeacherUserNameRecent(platformUserPO.getUsername(),
                    (courseScheduleFilter.getPageNumber()-1) * courseScheduleFilter.getPageSize(),
                    courseScheduleFilter.getPageSize());
            total =  courseScheduleMapper.getCourseSchedulesByTeacherUserNameRecentCount(platformUserPO.getUsername(),
                    (courseScheduleFilter.getPageNumber()-1) * courseScheduleFilter.getPageSize(),
                    courseScheduleFilter.getPageSize());
        }else{
            courseSchedulePOS = courseScheduleMapper.getCourseSchedulesByTeacherUserName(platformUserPO.getUsername(),
                    courseScheduleFilter.getEntity(), (courseScheduleFilter.getPageNumber()-1) * courseScheduleFilter.getPageSize(),
                    courseScheduleFilter.getPageSize());
            total =  courseScheduleMapper.countCourseSchedulesByTeacherUserName(platformUserPO.getUsername(),
                    courseScheduleFilter.getEntity());
        }


        for(TeacherCourseScheduleVO teacherCourseScheduleVO: courseSchedulePOS){
            String onlinePlatform = teacherCourseScheduleVO.getOnlinePlatform();
            if(onlinePlatform == null){
                teacherCourseScheduleVO.setLivingStatus(LiveStatusEnum.UN_START0.status);
            }else{
                VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(new LambdaQueryWrapper<VideoStreamRecordPO>().
                        eq(VideoStreamRecordPO::getId, onlinePlatform));
                if(videoStreamRecordPO != null){
                    String channelId = videoStreamRecordPO.getChannelId();
                    teacherCourseScheduleVO.setLivingStatus(videoStreamRecordPO.getWatchStatus());
                    teacherCourseScheduleVO.setChannelId(channelId);
                }else{
                    log.info("获取排课表信息没有直播间信息" + teacherCourseScheduleVO);

                    CourseSchedulePO courseSchedulePO = courseScheduleMapper.selectById(teacherCourseScheduleVO.getId());
                    teacherCourseScheduleVO.setOnlinePlatform(null);
                    courseSchedulePO.setOnlinePlatform(null);
                    int i = courseScheduleMapper.updateById(courseSchedulePO);
                    log.info("删除不存在的直播间 " + teacherCourseScheduleVO);
                }

            }
        }


        courseScheduleFilterDataVO.setCourseSchedulePOS(courseSchedulePOS);
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
}
