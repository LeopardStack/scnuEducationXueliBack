package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.constant.enums.LiveStatusEnum;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.core_data.TeacherInformationVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class StudentFilter extends AbstractFilter {
    /**
     * 获取排课表信息
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

        // 使用 studentStatusMapper 获取学生的班级数据
        StudentStatusPO studentStatusPOS = studentStatusMapper.
                selectOne(Wrappers.<StudentStatusPO>lambdaQuery().eq(StudentStatusPO::getIdNumber, platformUserPO.getUsername()));
        // 根据学籍信息中的班级标识 来获取班级信息 主要是年级、层次、学习形式、行政班别 从而进一步获取相应的排课表
        ClassInformationPO classInformationPO = classInformationMapper.selectOne(Wrappers.<ClassInformationPO>lambdaQuery().
                eq(ClassInformationPO::getClassIdentifier, studentStatusPOS.getClassIdentifier()));



        // 使用 courseScheduleMapper 获取数据
        List<TeacherCourseScheduleVO> courseSchedulePOS = courseScheduleMapper.getCourseSchedulesByStudentIdNumber(classInformationPO,
                courseScheduleFilter);
        for(TeacherCourseScheduleVO teacherCourseScheduleVO: courseSchedulePOS){
            String onlinePlatform = teacherCourseScheduleVO.getOnlinePlatform();
            if(onlinePlatform == null){
                teacherCourseScheduleVO.setLivingStatus(LiveStatusEnum.UN_START0.status);
            }else{
                VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(new LambdaQueryWrapper<VideoStreamRecordPO>().
                        eq(VideoStreamRecordPO::getId, onlinePlatform));
                String channelId = videoStreamRecordPO.getChannelId();
                teacherCourseScheduleVO.setLivingStatus(videoStreamRecordPO.getWatchStatus());
                teacherCourseScheduleVO.setChannelId(channelId);
            }
        }

        // 对courseSchedulePOS列表进行排序
        Collections.sort(courseSchedulePOS, new Comparator<TeacherCourseScheduleVO>() {
            @Override
            public int compare(TeacherCourseScheduleVO o1, TeacherCourseScheduleVO o2) {
                String status1 = o1.getLivingStatus();
                String status2 = o2.getLivingStatus();

                // 如果两个元素的livingStatus都是“未开始”或“已终止”，保持它们的顺序不变
                if ((status1.equals(LiveStatusEnum.UN_START0.status) || status1.equals(LiveStatusEnum.OVER.status)) &&
                        (status2.equals(LiveStatusEnum.UN_START0.status) || status2.equals(LiveStatusEnum.OVER.status))) {
                    return 0;
                }

                // 如果第一个元素的livingStatus是“未开始”或“已终止”，将其放在后面
                if (status1.equals(LiveStatusEnum.UN_START0.status) || status1.equals(LiveStatusEnum.OVER.status)) {
                    return 1;
                }

                // 如果第二个元素的livingStatus是“未开始”或“已终止”，将其放在前面
                if (status2.equals(LiveStatusEnum.UN_START0.status) || status2.equals(LiveStatusEnum.OVER.status)) {
                    return -1;
                }

                // 在所有其他情况下，保持两个元素的顺序不变
                return 0;
            }
        });


        long total =  courseScheduleMapper.countCourseSchedulesByStudentIdNumber(classInformationPO,
                courseScheduleFilter);
        courseScheduleFilterDataVO.setCourseSchedulePOS(courseSchedulePOS);
        courseScheduleFilterDataVO.setTotal(total);
        return courseScheduleFilterDataVO;
    }

    /**
     * 获取排课表的课程信息
     * @param courseScheduleFilterROPageRO
     * @return
     */
    @Override
    public FilterDataVO filterScheduleCoursesInformation(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {

        String idNumber = StpUtil.getLoginIdAsString();
        // 获取学生的班级信息
        List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectList(new LambdaQueryWrapper<StudentStatusPO>().
                eq(StudentStatusPO::getIdNumber, idNumber));
        if(studentStatusPOS.size() == 0){
            throw new IllegalArgumentException("找不到该学生信息 " + studentStatusPOS + " " + idNumber);
        }
        // 默认返回最新年份的课程信息
        StudentStatusPO student = studentStatusPOS.get(0);
        String maxGrade = student.getGrade();
        for(StudentStatusPO studentStatusPO: studentStatusPOS){
            if(Integer.parseInt(studentStatusPO.getGrade()) > Integer.parseInt(maxGrade)){
                student = studentStatusPO;
                maxGrade = studentStatusPO.getGrade();
            }
        }


        // 获取教学计划
        CourseInformationRO courseInformationRO = new CourseInformationRO();
        BeanUtils.copyProperties(courseScheduleFilterROPageRO.getEntity(), courseInformationRO);
        // 添加学生的班级标识
        courseInformationRO.setAdminClass(student.getClassIdentifier());

        List<CourseInformationVO> courseInformationVOS = courseInformationMapper.selectByFilterAndPage(courseInformationRO,
                courseScheduleFilterROPageRO.getPageSize(),
                (courseScheduleFilterROPageRO.getPageNumber() - 1) * courseScheduleFilterROPageRO.getPageSize());

        List<CourseInformationScheduleVO> courseInformationScheduleVOS = new ArrayList<>();
        // 首先获取教学计划中与排课表相对应的课程
        for (CourseInformationVO courseInformationVO : courseInformationVOS) {
            CourseInformationScheduleVO courseInformationScheduleVO = new CourseInformationScheduleVO();
            BeanUtils.copyProperties(courseInformationVO, courseInformationScheduleVO);
            courseInformationScheduleVO.setMainTeachers(new ArrayList<>());
            courseInformationScheduleVO.setTutors(new ArrayList<>());

            // 比较年级、专业、学习形式、层次、班级名称、课程名称
            CourseScheduleFilterRO courseScheduleFilterRO = new CourseScheduleFilterRO();
            courseScheduleFilterRO.setGrade(courseInformationVO.getGrade());
            courseScheduleFilterRO.setMajorName(courseInformationVO.getMajorName());
            courseScheduleFilterRO.setLevel(courseInformationVO.getLevel());
            courseScheduleFilterRO.setStudyForm(courseInformationVO.getStudyForm());
            courseScheduleFilterRO.setAdminClassName(courseInformationVO.getClassName());
            courseScheduleFilterRO.setCourseName(courseInformationVO.getCourseName());
            List<ScheduleCourseInformationVO> scheduleCourseInformationVOS = courseScheduleMapper.selectCoursesInformationWithoutPage(courseScheduleFilterRO);

            for (ScheduleCourseInformationVO scheduleCourseInformationVO : scheduleCourseInformationVOS) {
//                courseInformationScheduleVO.setTeachingMethod(scheduleCourseInformationVO.getTeachingMethod());
                // 获取主讲教师
                if (scheduleCourseInformationVO.getTeacherUsername() != null) {
                    TeacherInformationVO mainTeacher = new TeacherInformationVO();
                    mainTeacher.setTeacherUsername(scheduleCourseInformationVO.getTeacherUsername());
                    mainTeacher.setName(scheduleCourseInformationVO.getMainTeacherName());
                    mainTeacher.setIdCardNumber(scheduleCourseInformationVO.getMainTeacherIdentity());
                    mainTeacher.setWorkNumber(scheduleCourseInformationVO.getMainTeacherId());
                    courseInformationScheduleVO.getMainTeachers().add(mainTeacher);
                }

                if (scheduleCourseInformationVO.getTeachingAssistantUsername() != null) {
                    TeacherInformationVO tutor = new TeacherInformationVO();
                    tutor.setTeacherUsername(scheduleCourseInformationVO.getTeachingAssistantUsername());
                    tutor.setName(scheduleCourseInformationVO.getTutorName());
                    tutor.setIdCardNumber(scheduleCourseInformationVO.getTutorIdentity());
                    tutor.setWorkNumber(scheduleCourseInformationVO.getTutorId());
                    courseInformationScheduleVO.getTutors().add(tutor);
                }
            }

            if (courseScheduleFilterROPageRO.getEntity().getTeachingMethod() == null) {
                courseInformationScheduleVOS.add(courseInformationScheduleVO);
            } else if (courseScheduleFilterROPageRO.getEntity().getTeachingMethod() != null &&
                    (!"线下".equals(courseScheduleFilterROPageRO.getEntity().getTeachingMethod()))) {
                courseInformationScheduleVOS.add(courseInformationScheduleVO);
            } else {

            }


        }

        FilterDataVO<CourseInformationScheduleVO> filterDataVO = new FilterDataVO<>();
        log.info(StpUtil.getLoginId() + " 查询排课表课程信息的参数是 " + courseScheduleFilterROPageRO);

        long l = courseInformationMapper.getCountByFilterAndPage(courseInformationRO);
        filterDataVO.setTotal(l);
        filterDataVO.setData(courseInformationScheduleVOS);

        return filterDataVO;
    }
}
