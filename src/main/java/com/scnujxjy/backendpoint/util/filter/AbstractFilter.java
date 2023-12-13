package com.scnujxjy.backendpoint.util.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.scnujxjy.backendpoint.constant.enums.LiveStatusEnum;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.DegreeInfoPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.AdmissionInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.admission_information.MajorInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.PaymentInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamAssistantsMapper;
import com.scnujxjy.backendpoint.dao.mapper.exam.CourseExamInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.PersonalInfoMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_point.TeachingPointInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.ScoreInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.model.bo.teaching_process.CourseScheduleStudentExcelBO;
import com.scnujxjy.backendpoint.model.bo.teaching_process.ScheduleCoursesInformationBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.core_data.PaymentInfoFilterRO;
import com.scnujxjy.backendpoint.model.ro.exam.BatchSetTeachersInfoRO;
import com.scnujxjy.backendpoint.model.ro.exam.ExamFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.ClassInformationFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.DegreeInfoRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusFilterRO;
import com.scnujxjy.backendpoint.model.ro.registration_record_card.StudentStatusTeacherFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.ScoreInformationFilterRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.core_data.PaymentInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.ClassInformationSelectArgs;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusSelectArgs;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.util.tool.ScnuTimeInterval;
import com.scnujxjy.backendpoint.util.tool.ScnuXueliTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 定义信息筛选接口，用于实现平台各种角色的信息查询
 */
@Slf4j
public abstract class AbstractFilter {
    @Resource
    protected PlatformUserMapper platformUserMapper;
    @Resource
    protected PlatformUserService platformUserService;

    @Resource
    protected AdmissionInformationMapper admissionInformationMapper;

    @Resource
    protected PersonalInfoMapper personalInfoMapper;

    @Resource
    protected MajorInformationMapper majorInformationMapper;

    @Resource
    protected CollegeAdminInformationMapper collegeAdminInformationMapper;

    @Resource
    protected CollegeInformationMapper collegeInformationMapper;

    @Resource
    protected TeachingPointInformationMapper teachingPointInformationMapper;

    @Resource
    protected CourseScheduleMapper courseScheduleMapper;

    @Resource
    protected StudentStatusMapper studentStatusMapper;

    @Resource
    protected ClassInformationMapper classInformationMapper;

    @Resource
    protected CourseInformationMapper courseInformationMapper;

    @Resource
    protected CourseExamInfoMapper courseExamInfoMapper;

    @Resource
    protected CourseExamAssistantsMapper courseExamAssistantsMapper;

    @Resource
    protected PaymentInfoMapper paymentInfoMapper;

    @Resource
    protected VideoStreamRecordsMapper videoStreamRecordsMapper;

    @Resource
    protected TeachingPointAdminInformationMapper teachingPointAdminInformationMapper;

    @Resource
    protected ScoreInformationMapper scoreInformationMapper;

    @Resource
    protected TeacherInformationMapper teacherInformationMapper;

    @Resource
    protected ScnuXueliTools scnuXueliTools;

    @Resource
    protected RedisTemplate<String, Object> redisTemplate;

    /**
     * 筛选学籍数据的方法
     *
     * @param data 获取的学籍数据
     * @return 学籍数据集合
     */
    public List<StudentStatusPO> filterStudentInfo(List<StudentStatusPO> data) {
        // 默认实现，子类可以选择性地重写
        return data;
    }

    /**
     * 筛选学位数据的方法
     *
     * @param degreeFilter 获取的学位数据
     * @return 学位数据集合
     */
    public List<DegreeInfoPO> filterDegreeInfo(DegreeInfoRO degreeFilter) {
        // 默认实现，子类可以选择性地重写
        return null;
    }

    /**
     * 筛选排课表数据的方法
     *
     * @param courseScheduleFilter 获取的排课表数据
     * @return 排课表数据集合
     */
    public CourseScheduleFilterDataVO filterCourseSchedule(PageRO<CourseScheduleRO> courseScheduleFilter) {
        // 默认实现，子类可以选择性地重写
        return null;
    }


    /**
     * 筛选教学计划数据的方法
     *
     * @param courseInformationFilter 获取的教学计划筛选数据
     * @return 教学计划集合
     */
    public FilterDataVO filterCourseInformation(PageRO<CourseInformationRO> courseInformationFilter) {
        // 默认实现，子类可以选择性地重写
        return null;
    }

    /**
     * 获取二级学院教学计划筛选参数
     *
     * @return 教学计划筛选参数
     */
    public CourseInformationSelectArgs filterCourseInformationSelectArgs() {
        // 默认实现，子类可以选择性地重写
        return null;
    }

    /**
     * 筛选学籍数据的方法
     *
     * @param studentStatusFilter 获取的学籍筛选数据
     * @return 学籍数据集合
     */
    public FilterDataVO filterStudentStatus(PageRO<StudentStatusFilterRO> studentStatusFilter) {
        // 默认实现，子类可以选择性地重写
        return null;
    }

    /**
     * 导出学籍数据到指定用户 的消息中
     *
     * @param pageRO
     */
    public void exportStudentStatusData(PageRO<StudentStatusFilterRO> pageRO, String username) {
    }

    /**
     * 获取学籍筛选参数
     *
     * @return
     */
    public StudentStatusSelectArgs filterStudentStatusSelectArgs() {
        return null;
    }


    /**
     * 获取缴费信息
     *
     * @param paymentInfoFilterROPageRO 缴费筛选参数
     * @return
     */
    public FilterDataVO filterPayInfo(PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO) {
        return null;
    }

    /**
     * 获取成绩信息
     *
     * @param scoreInformationFilterROPageRO 成绩筛选参数
     * @return
     */
    public FilterDataVO filterGradeInfo(PageRO<ScoreInformationFilterRO> scoreInformationFilterROPageRO) {
        return null;
    }

    /**
     * 获取成绩筛选参数
     *
     * @return
     */
    public ScoreInformationSelectArgs filterScoreInformationSelectArgs() {
        return null;
    }

    /**
     * 导出成绩数据到指定用户 的消息中
     *
     * @param pageRO
     */
    public void exportScoreInformationData(PageRO<ScoreInformationFilterRO> pageRO, String username) {

    }

    /**
     * 获取缴费信息筛选参数
     *
     * @return
     */
    public PaymentInformationSelectArgs filterPaymentInformationSelectArgs() {
        return null;
    }

    /**
     * 获取班级信息
     *
     * @param classInformationFilterROPageRO 班级筛选参数
     * @return
     */
    public FilterDataVO filterClassInfo(PageRO<ClassInformationFilterRO> classInformationFilterROPageRO) {
        return null;
    }

    /**
     * 获取班级信息筛选参数
     *
     * @return
     */
    public ClassInformationSelectArgs filterClassInformationSelectArgs() {
        return null;
    }

    /**
     * 导出班级数据到指定用户 的消息中
     *
     * @param pageRO
     */
    public void exportClassInformationData(PageRO<ClassInformationFilterRO> pageRO, String userId, PlatformMessagePO platformMessagePO) {

    }

    public void exportStudentInformationBatchIndex(PageRO<CourseScheduleStudentExcelBO> courseScheduleStudentExcelBOPageRO, String username) {
    }

    /**
     * 获取排课表的课程信息
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    public FilterDataVO filterScheduleCoursesInformation(PageRO<ExamFilterRO> courseScheduleFilterROPageRO) {
        return null;
    }

    /**
     * 获取排课表课程筛选参数
     *
     * @return
     */
    public ScheduleCourseInformationSelectArgs filterScheduleCourseInformationSelectArgs() {
        return null;
    }

    /**
     * 获取排课表详细信息
     *
     * @return
     */
    public FilterDataVO filterSchedulesInformation(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        return null;
    }

    public void exportPaymentInfoData(PageRO<PaymentInfoFilterRO> paymentInfoFilterROPageRO, String userId, PlatformMessagePO platformMessagePO) {
    }

    /**
     * 批量导出教学计划
     *
     * @param courseInformationROPageRO
     * @return
     */
    public byte[] downloadTeachingPlans(PageRO<CourseInformationRO> courseInformationROPageRO) {
        return null;
    }

    /**
     * 获取排课表课程的筛选条件
     *
     * @param courseScheduleFilterRO
     * @return
     */
    public ScheduleCourseInformationSelectArgs getCoursesArgs(CourseScheduleFilterRO courseScheduleFilterRO) {
        return null;
    }

    /**
     * 获取教师的排课表信息 只返回不同时间的各个课程
     *
     * @param courseScheduleROPageRO
     * @return
     */
    public PageVO<TeacherSchedulesVO> getTeacherCourschedules(PageRO<CourseScheduleRO> courseScheduleROPageRO) {
        return null;
    }

    /**
     * 教师获取学生信息
     *
     * @param studentStatusROPageRO
     * @return
     */
    public FilterDataVO getStudentStatusInfoByTeacher(PageRO<StudentStatusTeacherFilterRO> studentStatusROPageRO) {
        return null;
    }

    /**
     * 获取排课表课程管理信息
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    public FilterDataVO getScheduleCourses(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        return null;
    }

    /**
     * 根据不同角色获取其排课表课程管理的筛选参数
     *
     * @param courseScheduleFilterROPageRO
     * @return
     */
    public ScheduleCourseManagetArgs getSelectScheduleCourseManageArgs(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {
        return null;
    }

    /**
     * 获取排课表课程管理信息
     *
     * @return
     */
    public FilterDataVO getScheduleCoursesBetter(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO) {

        log.info(StpUtil.getLoginId() + " 查询排课表课程信息的参数是 " + courseScheduleFilterROPageRO);
        // 展示给前端的排课课程管理信息
        List<ScheduleCoursesInformationVO> scheduleCoursesInformationVOS = new ArrayList<>();

        // 获取指定条件的排课表表课程信息 但是还需要做二次处理 比如 去掉同一批次的重复信息 把时间和班级 还有直播间信息 单独摘出来
        String redisKey = "getScheduleCoursesInformation:" + courseScheduleFilterROPageRO.getEntity().toString();
        ValueOperations<String, Object> valueOps1 = redisTemplate.opsForValue();
        List<ScheduleCoursesInformationBO> schedulesVOS;

        // Check if data is present in Redis cache
        if (Boolean.TRUE.equals(redisTemplate.hasKey(redisKey))) {
            schedulesVOS = (List<ScheduleCoursesInformationBO>) valueOps1.get(redisKey);
        } else {
            // If not present in cache, retrieve data from the database
            schedulesVOS = courseScheduleMapper.getScheduleCoursesInformation(courseScheduleFilterROPageRO.getEntity());
            // Store the data in cache with a timeout of 30 minutes
            valueOps1.set(redisKey, schedulesVOS, 30, TimeUnit.MINUTES);
        }

        List<ScheduleCoursesInformationVO> scheduleCoursesInformationVOList = new ArrayList<>();

        List<String> errorCourses = new ArrayList<>();

        // 去重 把同一批次的拿到 再去根据时间排序
        for (ScheduleCoursesInformationBO schedulesVO : schedulesVOS) {
            // 使用流来处理 这个 ScheduleCoursesInformationVO 对象，它如果发现这个 List 不存在，则新建
            ScheduleCoursesInformationVO scheduleCoursesInformationVO = scheduleCoursesInformationVOList.stream()
                    .filter(vo -> vo.getBatchIndex().equals(schedulesVO.getBatchIndex()))
                    .findFirst()
                    .orElseGet(() -> {
                        ScheduleCoursesInformationVO newVO = new ScheduleCoursesInformationVO(schedulesVO.getBatchIndex());
                        scheduleCoursesInformationVOList.add(newVO);
                        newVO.setClassName(new ArrayList<>());
                        return newVO;
                    });

            // 接下来根据每个批次里的排课日期和排课时间 拿到具体现在最近的 并且拿到它的直播状态 和 channelI

            scheduleCoursesInformationVO.setMainTeacherName(schedulesVO.getMainTeacherName());
            scheduleCoursesInformationVO.setTeacherUsername(schedulesVO.getTeacherUsername());
            scheduleCoursesInformationVO.setCourseName(schedulesVO.getCourseName());

            if (scheduleCoursesInformationVO.getTeachingDate() == null) {
                scheduleCoursesInformationVO.setTeachingDate(schedulesVO.getTeachingDate());
                scheduleCoursesInformationVO.setTeachingTime(schedulesVO.getTeachingTime());
            } else {
                ScnuTimeInterval timeInterval = scnuXueliTools.getTimeInterval(schedulesVO.getTeachingDate(), schedulesVO.getTeachingTime());
                Date newStart = timeInterval.getStart();
                Date now = new Date();
                Date currentTeachingDate = scnuXueliTools.getTimeInterval(scheduleCoursesInformationVO.getTeachingDate(),
                        scheduleCoursesInformationVO.getTeachingTime()).getStart();


                // 比较时间差
                long diffNew = newStart.getTime() - now.getTime();
                long diffCurrent = currentTeachingDate.getTime() - now.getTime();

                // 如果新的开始时间比现在时间晚，并且与现在的时间差比当前记录的时间差小
                if (diffCurrent > 0 && diffNew < 0) {
                    // 当前记录的排课的上课日期和上课时间 比此时此刻的大 而新的排课的上课日期和上课时间比现在小 那么就啥也不做
                } else if (diffCurrent > 0) {
                    if (Math.abs(diffNew) < Math.abs(diffCurrent)) {
                        // 选最近的
                        scheduleCoursesInformationVO.setTeachingDate(schedulesVO.getTeachingDate());
                        scheduleCoursesInformationVO.setTeachingTime(schedulesVO.getTeachingTime());
                        scheduleCoursesInformationVO.setOnlinePlatform(schedulesVO.getOnlinePlatform());
                    }

                } else {
                    // 目前拿到的上课时间 比当下的时间 大
                    if (diffNew > 0) {
                        scheduleCoursesInformationVO.setTeachingDate(schedulesVO.getTeachingDate());
                        scheduleCoursesInformationVO.setTeachingTime(schedulesVO.getTeachingTime());
                        scheduleCoursesInformationVO.setOnlinePlatform(schedulesVO.getOnlinePlatform());

                    } else {
                        if (Math.abs(diffNew) < Math.abs(diffCurrent)) {
                            // 选最近的
                            scheduleCoursesInformationVO.setTeachingDate(schedulesVO.getTeachingDate());
                            scheduleCoursesInformationVO.setTeachingTime(schedulesVO.getTeachingTime());
                            scheduleCoursesInformationVO.setOnlinePlatform(schedulesVO.getOnlinePlatform());
                        }
                    }
                }

            }

        }


        // 将拿到的每个批次 进行时间的升序排列 与现在相比比现在大的排在前面 比现在小的排在后面
        // 分为两组后 组内 按照 teachingDate 和 teachingTime 升序排列

        // 创建一个固定大小的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(200);

        // 使用CompletableFuture来异步处理每个scheduleCoursesInformationVO
        List<CompletableFuture<Boolean>> futures = scheduleCoursesInformationVOList.stream()
                .map(scheduleCoursesInformationVO -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return processScheduleCoursesInformationVO(
                                scheduleCoursesInformationVO,
                                errorCourses,
                                courseScheduleFilterROPageRO.getEntity()
                        );
                    } catch (Exception e) {
                        // 记录详细的错误信息
                        e.printStackTrace(); // 或使用日志记录
                        return false;
                    }
                }, executorService))
                .collect(Collectors.toList());


        // 等待所有的future完成，并获取结果
        List<Boolean> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        // 根据结果移除不满足条件的对象
        for (int i = scheduleCoursesInformationVOList.size() - 1; i >= 0; i--) {
            if (!results.get(i)) {
                scheduleCoursesInformationVOList.remove(i);
            }
        }

        // 关闭线程池
        executorService.shutdown();

        // 移除所有直播状态不符合条件或被设置为null的 ScheduleCoursesInformationVO
        scheduleCoursesInformationVOList.removeIf(Objects::isNull);


        Long pageNumber = courseScheduleFilterROPageRO.getPageNumber();
        Long pageSize = courseScheduleFilterROPageRO.getPageSize();

        // 计算开始索引
        long startIndex = (pageNumber - 1) * pageSize;

        // 计算结束索引
        long endIndex = startIndex + pageSize;

        endIndex = endIndex > scheduleCoursesInformationVOList.size() ? scheduleCoursesInformationVOList.size() : endIndex;

        List<ScheduleCoursesInformationVO> pageData = scheduleCoursesInformationVOList.subList((int) startIndex, (int) endIndex);

        FilterDataVO<ScheduleCoursesInformationVO> filterDataVO = new FilterDataVO<>();

        long total = scheduleCoursesInformationVOList.size();
        filterDataVO.setTotal(total);
        filterDataVO.setData(pageData);

        log.info("所有的排课表信息出现错误的记录 \n" + errorCourses);

        return filterDataVO;
    }


    private boolean processScheduleCoursesInformationVO(
            ScheduleCoursesInformationVO scheduleCoursesInformationVO,
            List<String> errorCourses, CourseScheduleFilterRO courseScheduleFilterRO) {

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
            if (onlinePlatform.equals("已结束")) {
                scheduleCoursesInformationVO.setLivingStatus(LiveStatusEnum.END.status);
            }
        } else {
            scheduleCoursesInformationVO.setLivingStatus(LiveStatusEnum.UN_START0.status);
        }

        // 如果直播状态不与指定的直播条件一致  直接过滤掉
        // 检查直播状态
        if (scheduleCoursesInformationVO.getLivingStatus() != null && courseScheduleFilterRO.getLivingStatus() != null) {
            try {
                if (!scheduleCoursesInformationVO.getLivingStatus().equals(courseScheduleFilterRO.getLivingStatus())) {
                    return false; // 表示不保留这个对象
                }
            } catch (Exception e) {
                log.error(e.toString());
                return false;
            }
        }

        return true; // 表示保留这个对象
    }

    public FilterDataVO filterCoursesInformationExams(PageRO<ExamFilterRO> courseScheduleFilterROPageRO) {
        return null;
    }

    /**
     * 考试信息批量导出
     *
     * @param entity
     * @param loginId
     */
    public void exportExamTeachersInfo(BatchSetTeachersInfoRO entity, String username) {

    }
}
