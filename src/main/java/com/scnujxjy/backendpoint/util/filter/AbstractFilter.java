package com.scnujxjy.backendpoint.util.filter;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.constant.enums.LiveStatusEnum;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.DegreeInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.TeachingAssistantsCourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.*;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInfoVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 定义信息筛选接口，用于实现平台各种角色的信息查询
 */
public abstract class AbstractFilter {
    @Resource
    protected PlatformUserMapper platformUserMapper;

    @Resource
    protected CollegeAdminInformationMapper collegeAdminInformationMapper;

    @Resource
    protected CollegeInformationMapper collegeInformationMapper;

    @Resource
    protected CourseScheduleMapper courseScheduleMapper;

    @Resource
    protected StudentStatusMapper studentStatusMapper;

    @Resource
    protected ClassInformationMapper classInformationMapper;

    @Resource
    protected CourseInformationMapper courseInformationMapper;

    @Resource
    protected PaymentInfoMapper paymentInfoMapper;

    @Resource
    protected VideoStreamRecordsMapper videoStreamRecordsMapper;

    @Resource
    protected ScoreInformationMapper scoreInformationMapper;

    @Resource
    protected TeacherInformationMapper teacherInformationMapper;

    @Resource
    protected TeachingAssistantsCourseScheduleMapper teachingAssistantsCourseScheduleMapper;

    @Resource
    protected ScnuXueliTools scnuXueliTools;

    @Resource
    protected RedisTemplate<String, Object> redisTemplate;

    /**
     * 筛选学籍数据的方法
     * @param data 获取的学籍数据
     * @return 学籍数据集合
     */
    public List<StudentStatusPO> filterStudentInfo(List<StudentStatusPO> data) {
        // 默认实现，子类可以选择性地重写
        return data;
    }

    /**
     * 筛选学位数据的方法
     * @param degreeFilter 获取的学位数据
     * @return 学位数据集合
     */
    public List<DegreeInfoPO> filterDegreeInfo(DegreeInfoRO degreeFilter) {
        // 默认实现，子类可以选择性地重写
        return null;
    }

    /**
     * 筛选排课表数据的方法
     * @param courseScheduleFilter 获取的排课表数据
     * @return 排课表数据集合
     */
    public CourseScheduleFilterDataVO filterCourseSchedule(PageRO<CourseScheduleRO> courseScheduleFilter) {
        // 默认实现，子类可以选择性地重写
        return null;
    }


    /**
     * 筛选教学计划数据的方法
     * @param courseInformationFilter 获取的教学计划筛选数据
     * @return 教学计划集合
     */
    public FilterDataVO filterCourseInformation(PageRO<CourseInformationRO> courseInformationFilter) {
        // 默认实现，子类可以选择性地重写
        return null;
    }

    /**
     * 获取二级学院教学计划筛选参数
     * @return 教学计划筛选参数
     */
    public CourseInformationSelectArgs filterCourseInformationSelectArgs() {
        // 默认实现，子类可以选择性地重写
        return null;
    }

    /**
     * 筛选学籍数据的方法
     * @param studentStatusFilter 获取的学籍筛选数据
     * @return 学籍数据集合
     */
    public FilterDataVO filterStudentStatus(PageRO<StudentStatusFilterRO> studentStatusFilter) {
        // 默认实现，子类可以选择性地重写
        return null;
    }

    /**
     * 导出学籍数据到指定用户 的消息中
     * @param pageRO
     */
    public void exportStudentStatusData(PageRO<StudentStatusFilterRO> pageRO, String userId) {
    }

    /**
     * 获取学籍筛选参数
     * @return
     */
    public StudentStatusSelectArgs filterStudentStatusSelectArgs() {
        return null;
    }


    /**
     * 获取缴费信息
     * @param paymentInfoFilterROPageRO 缴费筛选参数
     * @return
     */
    public FilterDataVO filterPayInfo(PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO) {
        return null;
    }

    /**
     * 获取成绩信息
     * @param scoreInformationFilterROPageRO 成绩筛选参数
     * @return
     */
    public FilterDataVO filterGradeInfo(PageRO<ScoreInformationFilterRO> scoreInformationFilterROPageRO) {
        return null;
    }

    /**
     * 获取成绩筛选参数
     * @return
     */
    public ScoreInformationSelectArgs filterScoreInformationSelectArgs() {
        return null;
    }

    /**
     * 导出成绩数据到指定用户 的消息中
     * @param pageRO
     */
    public void exportScoreInformationData(PageRO<ScoreInformationFilterRO> pageRO, String userId) {

    }

    /**
     * 获取缴费信息筛选参数
     * @return
     */
    public PaymentInformationSelectArgs filterPaymentInformationSelectArgs() {
        return null;
    }

    /**
     * 获取班级信息
     * @param classInformationFilterROPageRO 班级筛选参数
     * @return
     */
    public FilterDataVO filterClassInfo(PageRO<ClassInformationFilterRO> classInformationFilterROPageRO) {
        return null;
    }

    /**
     * 获取班级信息筛选参数
     * @return
     */
    public ClassInformationSelectArgs filterClassInformationSelectArgs() {
        return null;
    }

    /**
     * 导出班级数据到指定用户 的消息中
     * @param pageRO
     */
    public void exportClassInformationData(PageRO<ClassInformationFilterRO> pageRO, String userId, PlatformMessagePO platformMessagePO) {

    }

    /**
     * 获取排课表的课程信息
     * @param courseScheduleFilterROPageRO
     * @return
     */
    public FilterDataVO filterScheduleCoursesInformation(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        return null;
    }

    /**
     * 获取排课表课程筛选参数
     * @return
     */
    public ScheduleCourseInformationSelectArgs filterScheduleCourseInformationSelectArgs() {
        return null;
    }

    /**
     * 获取排课表详细信息
     * @return
     */
    public FilterDataVO filterSchedulesInformation(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        return null;
    }

    public void exportPaymentInfoData(PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO, String userId, PlatformMessagePO platformMessagePO){
    }

    /**
     * 批量导出教学计划
     * @param courseInformationROPageRO
     * @return
     */
    public byte[] downloadTeachingPlans(PageRO<CourseInformationRO> courseInformationROPageRO) {
        return null;
    }

    /**
     * 获取排课表课程的筛选条件
     * @param courseScheduleFilterRO
     * @return
     */
    public ScheduleCourseInformationSelectArgs getCoursesArgs(CourseScheduleFilterRO courseScheduleFilterRO) {
        return null;
    }

    /**
     * 获取教师的排课表信息 只返回不同时间的各个课程
     * @param courseScheduleROPageRO
     * @return
     */
    public PageVO<TeacherSchedulesVO> getTeacherCourschedules(PageRO<CourseScheduleRO> courseScheduleROPageRO) {
        return null;
    }

    /**
     * 教师获取学生信息
     * @param studentStatusROPageRO
     * @return
     */
    public FilterDataVO getStudentStatusInfoByTeacher(PageRO<StudentStatusTeacherFilterRO> studentStatusROPageRO) {
        return  null;
    }

    /**
     * 获取排课表课程管理信息
     * @param courseScheduleFilterROPageRO
     * @return
     */
    public FilterDataVO getScheduleCourses(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        return null;
    }


    public void processScheduleCoursesInformationVO(
            ScheduleCoursesInformationVO scheduleCoursesInformationVO,
            List<String> errorCourses) {

        // 获取所有的行政班
        List<CourseSchedulePO> courseSchedulePOS = courseScheduleMapper.selectList(
                new LambdaQueryWrapper<CourseSchedulePO>()
                        .eq(CourseSchedulePO::getBatchIndex, scheduleCoursesInformationVO.getBatchIndex())
        );

        List<String> colleges = new ArrayList<>();
        List<String> majorNames = new ArrayList<>();
        for (CourseSchedulePO courseSchedulePO : courseSchedulePOS) {
            // 从数据库获取班级信息
            ClassInformationPO classInformationPO = classInformationMapper.selectOne(
                    new LambdaQueryWrapper<ClassInformationPO>()
                            .eq(ClassInformationPO::getGrade, courseSchedulePO.getGrade())
                            .eq(ClassInformationPO::getMajorName, courseSchedulePO.getMajorName())
                            .eq(ClassInformationPO::getLevel, courseSchedulePO.getLevel())
                            .eq(ClassInformationPO::getStudyForm, courseSchedulePO.getStudyForm())
                            .eq(ClassInformationPO::getClassName, courseSchedulePO.getAdminClass())
            );

            if (classInformationPO == null) {
                // 记录错误信息
                String error = "班级信息获取失败，存在排课表记录获取不到班级信息 " +
                        courseSchedulePO.getGrade() + " " + courseSchedulePO.getMajorName() + " " +
                        courseSchedulePO.getStudyForm() + " " + courseSchedulePO.getLevel() + " " +
                        courseSchedulePO.getMainTeacherName() + " " + courseSchedulePO.getCourseName() + " " +
                        courseSchedulePO.getTeachingDate() + " " + courseSchedulePO.getTeachingTime();
                // 这里应该是线程安全的列表操作
                synchronized (errorCourses) {
                    errorCourses.add(error);
                }
            } else {
                colleges.add(classInformationPO.getCollege());
                majorNames.add(classInformationPO.getMajorName());
            }
        }

        // 去重学院信息 去重专业名称信息
        colleges = colleges.stream().distinct().collect(Collectors.toList());
        majorNames = majorNames.stream().distinct().collect(Collectors.toList());
        // 获取班级列表
        List<String> adminClassList = courseSchedulePOS.stream()
                .map(CourseSchedulePO::getAdminClass)
                .distinct()
                .collect(Collectors.toList());

        // 设置学院和班级信息
        scheduleCoursesInformationVO.setClassName(adminClassList);
        scheduleCoursesInformationVO.setColleges(colleges);
        scheduleCoursesInformationVO.setMajorNames(majorNames);

        // 处理在线平台信息
        String onlinePlatform = scheduleCoursesInformationVO.getOnlinePlatform();
        if (onlinePlatform != null) {
            // ... 处理在线平台信息的代码逻辑
            // 这里从数据库获取视频流信息
            VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(
                    new LambdaQueryWrapper<VideoStreamRecordPO>().eq(VideoStreamRecordPO::getId, onlinePlatform)
            );

            if (videoStreamRecordPO != null) {
                // 设置直播状态和频道ID
                scheduleCoursesInformationVO.setLivingStatus(videoStreamRecordPO.getWatchStatus());
                scheduleCoursesInformationVO.setChannelId(videoStreamRecordPO.getChannelId());
            }
        } else {
            scheduleCoursesInformationVO.setLivingStatus(LiveStatusEnum.UN_START0.status);
        }
    }

}
