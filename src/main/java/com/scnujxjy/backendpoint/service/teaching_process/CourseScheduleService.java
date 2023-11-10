package com.scnujxjy.backendpoint.service.teaching_process;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.MessageEnum;
import com.scnujxjy.backendpoint.constant.enums.UploadType;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.core_data.TeacherInformationPO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.PlatformMessagePO;
import com.scnujxjy.backendpoint.dao.entity.platform_message.UserUploadsPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseExtraInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.entity.video_stream.VideoStreamRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.core_data.TeacherInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.platform_message.PlatformMessageMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseExtraInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.UserUploadsMapper;
import com.scnujxjy.backendpoint.dao.mapper.video_stream.VideoStreamRecordsMapper;
import com.scnujxjy.backendpoint.inverter.teaching_process.CourseScheduleInverter;
import com.scnujxjy.backendpoint.model.bo.video_stream.ChannelResponseBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseExtraInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleFilterRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleUpdateRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import com.scnujxjy.backendpoint.util.filter.AbstractFilter;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import com.scnujxjy.backendpoint.util.filter.ManagerFilter;
import com.scnujxjy.backendpoint.util.filter.TeacherFilter;
import com.scnujxjy.backendpoint.util.video_stream.VideoStreamUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.analysis.function.Abs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static cn.hutool.core.date.DateField.DAY_OF_MONTH;
import static com.scnujxjy.backendpoint.constant.enums.RoleEnum.*;

/**
 * <p>
 * 排课表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-18
 */
@Service
@Slf4j
public class CourseScheduleService extends ServiceImpl<CourseScheduleMapper, CourseSchedulePO> implements IService<CourseSchedulePO> {

    @Resource
    private CourseScheduleInverter courseScheduleInverter;

    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private TeacherInformationMapper teacherInformationMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Resource
    private CollegeAdminInformationMapper collegeAdminInformationMapper;

    @Resource
    private CollegeInformationMapper collegeInformationMapper;

    @Resource
    private UserUploadsMapper userUploadsMapper;

    @Resource
    private PlatformMessageMapper platformMessageMapper;

    @Resource
    private CourseExtraInformationMapper courseExtraInformationMapper;


    @Resource
    private VideoStreamRecordsMapper videoStreamRecordsMapper;
    @Resource
    private VideoStreamUtils videoStreamUtils;
    /**
     * 根据id查询排课表信息
     *
     * @param id 主键id
     * @return 排课表详细信息
     */
    public CourseScheduleVO detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 查询数据
        CourseSchedulePO courseSchedulePO = baseMapper.selectById(id);
        // 类型转换并返回
        return courseScheduleInverter.po2VO(courseSchedulePO);
    }

    /**
     * 根据身份填充筛选条件
     *
     * @param entity 条件
     */
    private void fillFilterRO(CourseScheduleRO entity) {
        if (Objects.isNull(entity)) {
            entity = new CourseScheduleRO();
        }
        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return;
        }
        // 如果是学生：根据loginId去学籍表查询，根据班级信息表获取年级、专业、班名、层次、学习形式
        if (CollUtil.contains(StpUtil.getRoleList(), STUDENT.getRoleName())) {
            Long username = Long.parseLong(loginId);
            PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, username));
            StudentStatusPO studentStatusPO = studentStatusMapper.selectOne(Wrappers.<StudentStatusPO>lambdaQuery().eq(StudentStatusPO::getIdNumber, platformUserPO.getUsername()));
            if (Objects.isNull(studentStatusPO)) {
                return;
            }
            ClassInformationPO classInformationPO = classInformationMapper.selectOne(Wrappers.<ClassInformationPO>lambdaQuery().eq(ClassInformationPO::getClassIdentifier, studentStatusPO.getClassIdentifier()));
            if (Objects.isNull(classInformationPO)) {
                return;
            }
            entity.setGrade(classInformationPO.getGrade())
                    .setMajorName(classInformationPO.getMajorName())
                    .setLevel(classInformationPO.getLevel())
                    .setStudyForm(classInformationPO.getStudyForm())
                    .setAdminClass(classInformationPO.getClassName());
            return;
        }
        // 如果是老师：根据账户查询
        if (CollUtil.contains(StpUtil.getRoleList(), TEACHER.getRoleName())) {
            String userId = StrUtil.sub(loginId, 1, loginId.length());
            entity.setTeacherUsername(userId);
        }
    }

    /**
     * 分页查询排课表信息
     *
     * @param courseScheduleROPageRO 分页参数
     * @return 排课表分页信息
     */
    public PageVO<CourseScheduleVO> pageQueryCourseSchedule(PageRO<CourseScheduleRO> courseScheduleROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        CourseScheduleRO entity = courseScheduleROPageRO.getEntity();
        fillFilterRO(entity);
        // 默认取前后两周的数据
        if (Objects.isNull(entity.getTeachingStartDate()) && Objects.isNull(entity.getTeachingEndDate()) && Objects.equals(courseScheduleROPageRO.getIsAll(), false)) {
            entity.setTeachingStartDate(DateUtil.offset(new Date(), DAY_OF_MONTH, -7));
            entity.setTeachingEndDate(DateUtil.offset(new Date(), DAY_OF_MONTH, 7));
        }
        // 二级学院查询
        if (CollUtil.contains(StpUtil.getRoleList(), SECOND_COLLEGE_ADMIN.getRoleName())) {
            List<CourseSchedulePO> courseSchedulePOS = pageByCollegeAdminId();
            if (CollUtil.isEmpty(courseSchedulePOS)) {
                return null;
            }
            // 根据行政班级、课程名、主讲教师名筛选
            courseSchedulePOS = courseSchedulePOS.stream().filter(ele -> {
                        if (StrUtil.isNotBlank(entity.getAdminClass())) {
                            return StrUtil.contains(ele.getAdminClass(), entity.getAdminClass());
                        }
                        return true;
                    })
                    .filter(ele -> {
                        if (StrUtil.isNotBlank(entity.getCourseName())) {
                            return StrUtil.contains(ele.getCourseName(), entity.getCourseName());
                        }
                        return true;
                    })
                    .filter(ele -> {
                        if (StrUtil.isNotBlank(entity.getMainTeacherName())) {
                            return StrUtil.contains(ele.getMainTeacherName(), entity.getMainTeacherName());
                        }
                        return true;
                    })
                    .filter(ele -> {
                        if (Objects.nonNull(entity.getTeachingStartDate()) && Objects.nonNull(entity.getTeachingEndDate()) && Objects.equals(courseScheduleROPageRO.getIsAll(), false)) {
                            return ele.getTeachingDate().after(entity.getTeachingStartDate()) && ele.getTeachingDate().before(entity.getTeachingEndDate());
                        }
                        return true;
                    }).collect(Collectors.toList());
            if (Objects.equals(courseScheduleROPageRO.getIsAll(), true)) {
                return new PageVO<>(courseScheduleInverter.po2VO(courseSchedulePOS));
            } else {
                List<CourseSchedulePO> schedulePOS = ListUtil.page(Math.toIntExact(courseScheduleROPageRO.getPageNumber()), Math.toIntExact(courseScheduleROPageRO.getPageSize()), courseSchedulePOS);
                return new PageVO<>(courseScheduleInverter.po2VO(schedulePOS));
            }
        }
        // 构造查询条件
        LambdaQueryWrapper<CourseSchedulePO> wrapper = Wrappers.<CourseSchedulePO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), CourseSchedulePO::getId, entity.getId())
                .like(StrUtil.isNotBlank(entity.getGrade()), CourseSchedulePO::getGrade, entity.getGrade())
                .like(StrUtil.isNotBlank(entity.getMajorName()), CourseSchedulePO::getMajorName, entity.getMajorName())
                .eq(StrUtil.isNotBlank(entity.getLevel()), CourseSchedulePO::getLevel, entity.getLevel())
                .eq(StrUtil.isNotBlank(entity.getStudyForm()), CourseSchedulePO::getStudyForm, entity.getStudyForm())
                .eq(StrUtil.isNotBlank(entity.getAdminClass()), CourseSchedulePO::getAdminClass, entity.getAdminClass())
                .eq(StrUtil.isNotBlank(entity.getTeachingClass()), CourseSchedulePO::getTeachingClass, entity.getTeachingClass())
                .eq(Objects.nonNull(entity.getStudentCount()), CourseSchedulePO::getStudentCount, entity.getStudentCount())
                .like(StrUtil.isNotBlank(entity.getCourseName()), CourseSchedulePO::getCourseName, entity.getCourseName())
                .eq(Objects.nonNull(entity.getClassHours()), CourseSchedulePO::getClassHours, entity.getClassHours())
                .eq(StrUtil.isNotBlank(entity.getExamType()), CourseSchedulePO::getExamType, entity.getExamType())
                .like(StrUtil.isNotBlank(entity.getMainTeacherName()), CourseSchedulePO::getMainTeacherName, entity.getMainTeacherName())
                .eq(StrUtil.isNotBlank(entity.getMainTeacherId()), CourseSchedulePO::getMainTeacherId, entity.getMainTeacherId())
                .eq(StrUtil.isNotBlank(entity.getMainTeacherIdentity()), CourseSchedulePO::getMainTeacherIdentity, entity.getMainTeacherIdentity())
                .like(StrUtil.isNotBlank(entity.getTutorName()), CourseSchedulePO::getTutorName, entity.getTutorName())
                .eq(StrUtil.isNotBlank(entity.getTutorId()), CourseSchedulePO::getTutorId, entity.getTutorId())
                .eq(StrUtil.isNotBlank(entity.getTutorIdentity()), CourseSchedulePO::getTutorIdentity, entity.getTutorIdentity())
                .eq(StrUtil.isNotBlank(entity.getTeachingMethod()), CourseSchedulePO::getTeachingMethod, entity.getTeachingMethod())
                .like(StrUtil.isNotBlank(entity.getClassLocation()), CourseSchedulePO::getClassLocation, entity.getClassLocation())
                .eq(StrUtil.isNotBlank(entity.getOnlinePlatform()), CourseSchedulePO::getOnlinePlatform, entity.getOnlinePlatform())
                .between(Objects.nonNull(entity.getTeachingStartDate()) && Objects.nonNull(entity.getTeachingEndDate()),
                        CourseSchedulePO::getTeachingDate, entity.getTeachingStartDate(), entity.getTeachingEndDate())
                .eq(StrUtil.isNotBlank(entity.getTeachingTime()), CourseSchedulePO::getTeachingTime, entity.getTeachingTime())
                .eq(StrUtil.isNotBlank(entity.getTeacherUsername()), CourseSchedulePO::getTeacherUsername, entity.getTeacherUsername())
                .last(StrUtil.isNotBlank(courseScheduleROPageRO.getOrderBy()), courseScheduleROPageRO.lastOrderSql());

        // 列表查询 或 分页查询 并返回数据
        if (Objects.equals(true, courseScheduleROPageRO.getIsAll())) {
            List<CourseSchedulePO> courseSchedulePOS = baseMapper.selectList(wrapper);
            return new PageVO<>(courseScheduleInverter.po2VO(courseSchedulePOS));
        } else {
            Page<CourseSchedulePO> courseSchedulePOPage = baseMapper.selectPage(courseScheduleROPageRO.getPage(), wrapper);
            return new PageVO<>(courseSchedulePOPage, courseScheduleInverter.po2VO(courseSchedulePOPage.getRecords()));
        }
    }

    /**
     * 分页查询所有排课表信息
     *
     * @param courseScheduleROPageRO 分页参数
     * @return 排课表分页信息
     */
    public PageVO<TeacherCourseScheduleVO> allPageQueryCourseScheduleService(PageRO<CourseScheduleRO> courseScheduleROPageRO) {
        // 校验参数
        if (Objects.isNull(courseScheduleROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        CourseScheduleRO entity = courseScheduleROPageRO.getEntity();
        fillFilterRO(entity);

        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationMapper.selectById(platformUserPO.getUserId());
        if (Objects.isNull(collegeAdminInformationPO)) {
            return null;
        }
        CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectById(collegeAdminInformationPO.getCollegeId());
        if (Objects.isNull(collegeInformationPO)) {
            return null;
        }

        // 获取满足条件的总记录数
        long totalCount = baseMapper.countCourseSchedulesByConditions(collegeInformationPO.getCollegeName(), courseScheduleROPageRO);

        // 获取当前页的数据
        List<TeacherCourseScheduleVO> courseSchedulePOS = baseMapper.getCourseSchedulesByConditions(collegeInformationPO.getCollegeName(), courseScheduleROPageRO);

        // 创建并返回分页信息
        PageVO<TeacherCourseScheduleVO> result = new PageVO<>(courseSchedulePOS);
        result.setTotal(totalCount);
        result.setCurrent(courseScheduleROPageRO.getPageNumber());
        result.setSize(courseScheduleROPageRO.getPageSize());
        result.setPages((long) Math.ceil((double) totalCount / courseScheduleROPageRO.getPageSize()));

        return result;

    }


    /**
     * 根据筛选器来获取不同角色的排课表数据
     * @param courseScheduleROPageRO
     * @param filter
     * @return
     */
    public PageVO<TeacherCourseScheduleVO> allPageQueryCourseScheduleFilter(PageRO<CourseScheduleRO> courseScheduleROPageRO, AbstractFilter filter) {
        // 校验参数
        if (Objects.isNull(courseScheduleROPageRO)) {
            log.error("参数缺失");
            return null;
        }

        CourseScheduleFilterDataVO courseSchedulePOS = filter.filterCourseSchedule(courseScheduleROPageRO);

        // 创建并返回分页信息
        PageVO<TeacherCourseScheduleVO> result = new PageVO<>(courseSchedulePOS.getCourseSchedulePOS());
        result.setTotal(courseSchedulePOS.getTotal());
        result.setCurrent(courseScheduleROPageRO.getPageNumber());
        result.setSize(courseScheduleROPageRO.getPageSize());
        result.setPages((long) Math.ceil((double) courseSchedulePOS.getCourseSchedulePOS().size() / courseScheduleROPageRO.getPageSize()));

        return result;
    }


    /**
     * 根据id更新排课表信息
     *
     * @param courseScheduleRO 更新的排课表信息
     * @return 更新后的排课表信息
     */
    public CourseScheduleVO editById(CourseScheduleRO courseScheduleRO) {
        // 参数校验
        if (Objects.isNull(courseScheduleRO) || Objects.isNull(courseScheduleRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        // 类型转换
        CourseSchedulePO courseSchedulePO = courseScheduleInverter.ro2PO(courseScheduleRO);
        // 更新数据
        int count = baseMapper.updateById(courseSchedulePO);
        // 更新校验
        if (count <= 0) {
            log.error("更新失败，数据：{}", courseSchedulePO);
            return null;
        }
        // 详情查询
        return detailById(courseScheduleRO.getId());
    }

    /**
     * 根据传入参数更新排课表
     * <p>根据课程名称、授课讲师、授课日期、授课时间来合班教学，这种情况下应该是同一个直播间</p>
     *
     * @param courseScheduleRO
     * @return
     */
    public List<CourseScheduleVO> generateVideoStream(CourseScheduleRO courseScheduleRO) {
        if (Objects.isNull(courseScheduleRO) || Objects.isNull(courseScheduleRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        CourseScheduleVO courseScheduleVO = detailById(courseScheduleRO.getId());
        LambdaQueryWrapper<CourseSchedulePO> wrapper = Wrappers.<CourseSchedulePO>lambdaQuery()
                .eq(CourseSchedulePO::getCourseName, courseScheduleVO.getCourseName())
                .eq(CourseSchedulePO::getTeachingDate, courseScheduleVO.getTeachingDate())
                .eq(CourseSchedulePO::getMainTeacherName, courseScheduleVO.getMainTeacherName())
                .eq(CourseSchedulePO::getTeachingTime, courseScheduleVO.getTeachingTime());
        List<CourseSchedulePO> courseSchedulePOS = baseMapper.selectList(wrapper);
        if (CollUtil.isEmpty(courseSchedulePOS)) {
            log.error("更新失败");
            return null;
        }
        List<CourseScheduleVO> res = new LinkedList<>();
        courseSchedulePOS.forEach(ele -> {
            ele.setOnlinePlatform(courseScheduleRO.getOnlinePlatform());
            baseMapper.updateById(ele);
            res.add(detailById(ele.getId()));
        });
        return res;
    }

    /**
     * 根据id删除排课信息
     *
     * @param id 主键id
     * @return 删除数量
     */
    public Integer deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 删除
        int count = baseMapper.deleteById(id);
        // 删除校验
        if (count <= 0) {
            log.error("删除失败，id：{}", id);
            return null;
        }
        // 返回删除数量
        return count;
    }

    /**
     * 如果是二级学院教务员：查询自己学院下的
     *
     * @return
     */
    private List<CourseSchedulePO> pageByCollegeAdminId() {
        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationMapper.selectById(platformUserPO.getUserId());
        if (Objects.isNull(collegeAdminInformationPO)) {
            return null;
        }
        CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectById(collegeAdminInformationPO.getCollegeId());
        if (Objects.isNull(collegeInformationPO)) {
            return null;
        }
        return baseMapper.detailByCollegeName(collegeInformationPO.getCollegeName());
    }

    public HashMap<String, List<String>> getSelectCourseScheduleArgs() {
        String loginId = (String) StpUtil.getLoginId();
        if (StrUtil.isBlank(loginId)) {
            return null;
        }

        // 二级学院管理员获取其排课表中所有的筛选条件
        PlatformUserPO platformUserPO = platformUserMapper.selectOne(Wrappers.<PlatformUserPO>lambdaQuery().eq(PlatformUserPO::getUsername, loginId));
        if (Objects.isNull(platformUserPO)) {
            return null;
        }
        CollegeAdminInformationPO collegeAdminInformationPO = collegeAdminInformationMapper.selectById(platformUserPO.getUserId());
        if (Objects.isNull(collegeAdminInformationPO)) {
            return null;
        }
        CollegeInformationPO collegeInformationPO = collegeInformationMapper.selectById(collegeAdminInformationPO.getCollegeId());
        if (Objects.isNull(collegeInformationPO)) {
            return null;
        }

        HashMap<String, List<String>> selectCourseSchedules = new HashMap<>();

        List<String> distinctGradesByCollegeName = baseMapper.getDistinctGradesByCollegeName(collegeInformationPO.getCollegeName());
        selectCourseSchedules.put("年级", distinctGradesByCollegeName);

        List<String> distinctLevelsByCollegeName = baseMapper.getDistinctLevelsByCollegeName(collegeInformationPO.getCollegeName());
        selectCourseSchedules.put("层次", distinctLevelsByCollegeName);

        List<String> distinctMajorsByCollegeName = baseMapper.getDistinctMajorsByCollegeName(collegeInformationPO.getCollegeName());
        selectCourseSchedules.put("专业名称", distinctMajorsByCollegeName);

        List<String> distinctTeachingClassesByCollegeName = baseMapper.getDistinctTeachingClassesByCollegeName(collegeInformationPO.getCollegeName());
        selectCourseSchedules.put("教学班别", distinctTeachingClassesByCollegeName);

        List<String> distinctAdminClassesByCollegeName = baseMapper.getDistinctAdminClassesByCollegeName(collegeInformationPO.getCollegeName());
        selectCourseSchedules.put("行政班别", distinctAdminClassesByCollegeName);

        List<String> distinctExamTypesByCollegeName = baseMapper.getDistinctExamTypesByCollegeName(collegeInformationPO.getCollegeName());
        selectCourseSchedules.put("考核类型", distinctExamTypesByCollegeName);

        List<String> distinctStudyFormsByCollegeName = baseMapper.getDistinctStudyFormsByCollegeName(collegeInformationPO.getCollegeName());
        selectCourseSchedules.put("学习形式", distinctStudyFormsByCollegeName);

        List<String> distinctCourseNamesByCollegeName = baseMapper.getDistinctCourseNamesByCollegeName(collegeInformationPO.getCollegeName());
        selectCourseSchedules.put("课程名称", distinctCourseNamesByCollegeName);

        List<String> distinctMainTeachersByCollegeName = baseMapper.getDistinctMainTeachersByCollegeName(collegeInformationPO.getCollegeName());
        selectCourseSchedules.put("主讲教师姓名", distinctMainTeachersByCollegeName);

        List<String> distinctTeachingMethodsByCollegeName = baseMapper.getDistinctTeachingMethodsByCollegeName(collegeInformationPO.getCollegeName());
        selectCourseSchedules.put("教学方式", distinctTeachingMethodsByCollegeName);

        return selectCourseSchedules;
    }

    /**
     * 获取排课表的课程信息
     * @param courseScheduleFilterROPageRO
     * @param filter
     * @return
     */
    public FilterDataVO allPageQueryScheduleCoursesInformationFilter(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO, AbstractFilter filter) {
        return filter.filterScheduleCoursesInformation(courseScheduleFilterROPageRO);
    }

    public ScheduleCourseInformationSelectArgs getSelectScheduleCourseInformationArgs(AbstractFilter filter) {
        return filter.filterScheduleCourseInformationSelectArgs();
    }

    /**
     * 获取排课表详细信息
     * @param courseScheduleFilterROPageRO
     * @param filter
     * @return
     */
    public FilterDataVO allPageQuerySchedulesInformationFilter(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO, AbstractFilter filter) {
        return filter.filterSchedulesInformation(courseScheduleFilterROPageRO);
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean updateCourseScheduleInfoByTime(CourseSchedulePO courseSchedulePO, Date newDate, String newTime){
        // 获取所有在同一个教学班、同一门课程、同一个时间点的排课记录，即合班一起上的课
        List<CourseSchedulePO> courseSchedulePOS = getBaseMapper().selectList(new LambdaQueryWrapper<CourseSchedulePO>()
                .eq(CourseSchedulePO::getTeachingClass, courseSchedulePO.getTeachingClass())
                .eq(CourseSchedulePO::getCourseName, courseSchedulePO.getCourseName())
                .eq(CourseSchedulePO::getTeachingDate, courseSchedulePO.getTeachingDate())
                .eq(CourseSchedulePO::getTeachingTime, courseSchedulePO.getTeachingTime()));

        for(CourseSchedulePO courseSchedulePO1: courseSchedulePOS){
            // 更新时间
            courseSchedulePO1.setTeachingDate(newDate);
            courseSchedulePO1.setTeachingTime(newTime);

            int update = getBaseMapper().update(courseSchedulePO1, new LambdaQueryWrapper<CourseSchedulePO>().eq(CourseSchedulePO::getId, courseSchedulePO1.getId()));
            if(update <= 0){
                throw new RuntimeException("Failed to update record: " + courseSchedulePO1.getId());
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateCourseScheduleInfoByTeacher(CourseSchedulePO courseSchedulePO, TeacherInformationPO teacherInformationPO){
        // 获取所有在同一个教学班、同一门课程、同一个时间点的排课记录，即合班一起上的课
        List<CourseSchedulePO> courseSchedulePOS = getBaseMapper().selectList(new LambdaQueryWrapper<CourseSchedulePO>()
                .eq(CourseSchedulePO::getTeachingClass, courseSchedulePO.getTeachingClass())
                .eq(CourseSchedulePO::getCourseName, courseSchedulePO.getCourseName())
                .eq(CourseSchedulePO::getTeachingDate, courseSchedulePO.getTeachingDate())
                .eq(CourseSchedulePO::getTeachingTime, courseSchedulePO.getTeachingTime()));

        for(CourseSchedulePO courseSchedulePO1: courseSchedulePOS){
            // 更新时间
            courseSchedulePO1.setTeacherUsername(teacherInformationPO.getTeacherUsername());
            courseSchedulePO1.setMainTeacherName(teacherInformationPO.getName());
            courseSchedulePO1.setMainTeacherId(teacherInformationPO.getWorkNumber());
            courseSchedulePO1.setMainTeacherIdentity(teacherInformationPO.getIdCardNumber());

            int update = getBaseMapper().update(courseSchedulePO1, new LambdaQueryWrapper<CourseSchedulePO>().eq(CourseSchedulePO::getId, courseSchedulePO1.getId()));
            if(update <= 0){
                throw new RuntimeException("Failed to update record: " + courseSchedulePO1.getId());
            }
        }
        return true;
    }

    // 修改排课表记录的教师和上课时间
    @Transactional(rollbackFor = {Exception.class, IllegalArgumentException.class})
    public void updateScheduleInfor(CourseScheduleUpdateRO courseScheduleUpdateROPageRO) {
        if(courseScheduleUpdateROPageRO.getId() == null){
            throw new RuntimeException("没有找到任何排课表信息，更新失败");
        }else{
            if(courseScheduleUpdateROPageRO.getTeachingStartDate() != null){
                // 更新排课表的上课时间
                Date start = courseScheduleUpdateROPageRO.getTeachingStartDate();

                Date end = courseScheduleUpdateROPageRO.getTeachingEndDate();

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String formattedTime = sdf.format(start) + "-" + sdf.format(end);
                log.info("更新后的时间 " + start + "\n" + formattedTime);

                CourseSchedulePO courseSchedulePO = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseSchedulePO>().
                        eq(CourseSchedulePO::getId, courseScheduleUpdateROPageRO.getId()));
//                courseSchedulePO.setTeachingDate(start);
//                courseSchedulePO.setTeachingTime(formattedTime);
                // 同一个时间、同一个老师、同一个教学班级、同一门课程 要改时间 全改 而且直播间得删除 并且重新让扫描器去重新创建
                List<CourseSchedulePO> courseSchedulePOS = getBaseMapper().selectList(new LambdaQueryWrapper<CourseSchedulePO>()
                        .eq(CourseSchedulePO::getTeachingDate, courseSchedulePO.getTeachingDate())
                        .eq(CourseSchedulePO::getTeachingTime, courseSchedulePO.getTeachingTime())
                        .eq(CourseSchedulePO::getTeacherUsername, courseSchedulePO.getTeacherUsername())
                        .eq(CourseSchedulePO::getCourseName, courseSchedulePO.getCourseName())
                );

                if(courseSchedulePOS.isEmpty()){
                    throw new IllegalArgumentException("修改排课表失败，没有找到对应的排课表");
                }

                for(CourseSchedulePO courseSchedulePO1: courseSchedulePOS){
                    if(courseSchedulePO1.getOnlinePlatform() != null){
                        // 存在直播间 看一下保利威该直播间是否还存在
                        VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(new LambdaQueryWrapper<VideoStreamRecordPO>()
                                .eq(VideoStreamRecordPO::getId, courseSchedulePO1.getOnlinePlatform()));
                        if(videoStreamRecordPO == null){
                            // 不存在直播间 直接修改
                            courseSchedulePO1.setTeachingDate(start);
                            courseSchedulePO1.setTeachingTime(formattedTime);
                            getBaseMapper().updateById(courseSchedulePO1);
                        }else{
                            // 修改有直播间记录的排课 不允许
                            String channelId = videoStreamRecordPO.getChannelId();
                            if(channelId != null){
                                throw new IllegalArgumentException("不允许修改已直播过的排课记录");
                            }
                        }
                    }else{
                        // 不存在直播间 直接修改
                        courseSchedulePO1.setTeachingDate(start);
                        courseSchedulePO1.setTeachingTime(formattedTime);
                        getBaseMapper().updateById(courseSchedulePO1);
                    }
                }
                log.info("更新教学时间成功");

//                throw new RuntimeException("更新成绩失败");
            }

            if(courseScheduleUpdateROPageRO.getTeacherName() != null){
                // 更换老师
                List<TeacherInformationPO> teacherInformationPOS = teacherInformationMapper.selectList(new LambdaQueryWrapper<TeacherInformationPO>().
                        eq(TeacherInformationPO::getName, courseScheduleUpdateROPageRO.getTeacherName()));
                if(teacherInformationPOS.size() > 1){
                    // 存在同名老师 必须提供 工号或者身份证号码
                    String work_id = courseScheduleUpdateROPageRO.getTeacherId();
                    String id_number = courseScheduleUpdateROPageRO.getTeacherIdentity();
                    if(work_id != null){
                        List<TeacherInformationPO> teacherInformationPOS1 = teacherInformationMapper.selectList(new LambdaQueryWrapper<TeacherInformationPO>().
                                eq(TeacherInformationPO::getWorkNumber, work_id));
                        if(teacherInformationPOS1.size() == 1){

                            TeacherInformationPO teacherInformationPO = teacherInformationPOS1.get(0);

                            CourseSchedulePO courseSchedulePO = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseSchedulePO>().
                                    eq(CourseSchedulePO::getId, courseScheduleUpdateROPageRO.getId()));
                            boolean update = updateCourseScheduleInfoByTeacher(courseSchedulePO, teacherInformationPO);
                            if(update){
                                log.info("更新课程教师成功");
                            }else{
                                throw new RuntimeException("更新排课表上课教师失败");
                            }
                            // 确定了这一名老师，开始更新
                            log.info("\n" + courseScheduleUpdateROPageRO + " 更新教师为 " + teacherInformationPOS1.get(0));
                        }else{
                            // 工号确定不了 或者为空
                            List<TeacherInformationPO> teacherInformationPOS2 = teacherInformationMapper.selectList(new LambdaQueryWrapper<TeacherInformationPO>().
                                    eq(TeacherInformationPO::getIdCardNumber, id_number));
                            if(teacherInformationPOS2.size() == 1){
                                // 确定了这一名老师，开始更新
                                TeacherInformationPO teacherInformationPO = teacherInformationPOS2.get(0);

                                CourseSchedulePO courseSchedulePO = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseSchedulePO>().
                                        eq(CourseSchedulePO::getId, courseScheduleUpdateROPageRO.getId()));
                                boolean update = updateCourseScheduleInfoByTeacher(courseSchedulePO, teacherInformationPO);
                                if(update){
                                    log.info("更新课程教师成功");
                                }else{
                                    throw new RuntimeException("更新排课表上课教师失败");
                                }
                                log.info("\n" + courseScheduleUpdateROPageRO + " 更新教师为 " + teacherInformationPOS2.get(0));
                            }else{
                                throw new RuntimeException("更新失败，教师信息无法确定，请提供工号或者身份证号码");
                            }
                        }

                    }
                }else if(teacherInformationPOS.size() == 1){
                    TeacherInformationPO teacherInformationPO = teacherInformationPOS.get(0);

                    CourseSchedulePO courseSchedulePO = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseSchedulePO>().
                            eq(CourseSchedulePO::getId, courseScheduleUpdateROPageRO.getId()));
                    boolean update = updateCourseScheduleInfoByTeacher(courseSchedulePO, teacherInformationPO);
                    if(update){
                        log.info("更新课程教师成功");
                    }else{
                        throw new RuntimeException("更新排课表上课教师失败");
                    }
                    log.info("\n" + courseScheduleUpdateROPageRO + " 更新教师为 " + teacherInformationPOS.get(0));
                }else{
                    throw new RuntimeException("更新失败，教师信息错误");
                }
            }
        }
    }

    public void deleteScheduleInfor(Long scheduldId) {
        try {
            CourseSchedulePO courseSchedulePO = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseSchedulePO>().
                    eq(CourseSchedulePO::getId, scheduldId));
            // 查询多少条排课表记录与它同教学班名
            String teachingClassName = courseSchedulePO.getTeachingClass();
            List<CourseSchedulePO> courseSchedulePOS = getBaseMapper().selectList(new LambdaQueryWrapper<CourseSchedulePO>().
                    eq(CourseSchedulePO::getTeachingClass, teachingClassName));
            // 计算直播间是否存在
            if (courseSchedulePO.getOnlinePlatform() != null) {
                // 要确定直播间在保利威那里是否被删除了
                VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(new LambdaQueryWrapper<VideoStreamRecordPO>().
                        eq(VideoStreamRecordPO::getId, courseSchedulePO.getOnlinePlatform()));
                // 调用保利威删除 API
            Map<String, Object> stringObjectMap = videoStreamUtils.deleteView(videoStreamRecordPO.getChannelId());
                log.info("存在直播间 " + videoStreamRecordPO);
            }

            // 删除存在多个班合班一起上课的所有记录
            List<Long> idsToDelete = courseSchedulePOS.stream()
                    .map(CourseSchedulePO::getId) // Assuming getId returns the ID of the entity
                    .collect(Collectors.toList());

            int i = getBaseMapper().deleteBatchIds(idsToDelete);

            log.info("删除排课表记录 " + i);
        }catch (Exception e){
            log.error("删除失败 " + e.toString());
            throw new RuntimeException("删除失败 ");
        }
    }

    /**
     * 根据排课表中的记录来获取直播间信息
     * @param roomId
     */
    public ChannelResponseBO getLivingRoomInformation(String roomId) {
        try {
            if (roomId != null) {
                CourseSchedulePO courseSchedulePO = getBaseMapper().selectOne(new LambdaQueryWrapper<CourseSchedulePO>().
                        eq(CourseSchedulePO::getId, Long.parseLong(roomId)));
                if(courseSchedulePO == null){
                    throw new RuntimeException("不存在该排课表记录");
                }
                VideoStreamRecordPO videoStreamRecordPO = videoStreamRecordsMapper.selectOne(new LambdaQueryWrapper<VideoStreamRecordPO>().
                        eq(VideoStreamRecordPO::getId, Long.parseLong(courseSchedulePO.getOnlinePlatform()) ));
                ChannelResponseBO channelBasicInfo = videoStreamUtils.getChannelBasicInfo(videoStreamRecordPO.getChannelId());
                return channelBasicInfo;
            }
        }catch (Exception e){
            log.error("获取直播间信息失败 " + roomId + " \n" + e.toString());
            throw new RuntimeException("获取直播间信息失败");
        }
        return null;
    }

    /**
     * 如果该排课表对应教学班里面的这一门课程没有相关的简介信息 就新增一个，那么
     * 这个教学班所有上这门课的学生都会看到这个课程简介
     * @param courseExtraInformationRO
     */
    public String updateScheduleExtraInfo(CourseExtraInformationRO courseExtraInformationRO) {
        Integer integer = courseExtraInformationMapper.selectCount(new LambdaQueryWrapper<CourseExtraInformationPO>().
                eq(CourseExtraInformationPO::getCourseScheduleId, courseExtraInformationRO.getCourseScheduleId()).
                eq(CourseExtraInformationPO::getCourseName, courseExtraInformationRO.getCourseName()));
        if(integer == 0){
            // 没有课程简介信息 需要新增
            CourseExtraInformationPO courseExtraInformationPO = new CourseExtraInformationPO();
            courseExtraInformationPO.setCourseScheduleId(courseExtraInformationRO.getCourseScheduleId());
            courseExtraInformationPO.setCourseDescription(courseExtraInformationRO.getCourseDescription());
            courseExtraInformationPO.setCourseAnnouncement(courseExtraInformationRO.getCourseAnnouncement());
            courseExtraInformationPO.setCourseTitle(courseExtraInformationRO.getCourseTitle());
            courseExtraInformationPO.setCourseName(courseExtraInformationRO.getCourseName());

            try {
                int insert = courseExtraInformationMapper.insert(courseExtraInformationPO);
                if (insert > 0) {
                    return "新增课程简介成功！";
                }
            }catch (Exception e){
                log.error("新增课程简介失败" + e.toString());
                throw new RuntimeException("新增课程简介失败");
            }
        }else if(integer == 1){
            // 存在，则覆盖
            CourseExtraInformationPO courseExtraInformationPO = courseExtraInformationMapper.selectOne(new LambdaQueryWrapper<CourseExtraInformationPO>().
                    eq(CourseExtraInformationPO::getCourseScheduleId, courseExtraInformationRO.getCourseScheduleId()).
                    eq(CourseExtraInformationPO::getCourseName, courseExtraInformationRO.getCourseName()));

            CourseExtraInformationPO courseExtraInformationPO1 = new CourseExtraInformationPO();
            courseExtraInformationPO1.setCourseScheduleId(courseExtraInformationRO.getCourseScheduleId());
            courseExtraInformationPO1.setCourseDescription(courseExtraInformationRO.getCourseDescription());
            courseExtraInformationPO1.setCourseAnnouncement(courseExtraInformationRO.getCourseAnnouncement());
            courseExtraInformationPO1.setCourseTitle(courseExtraInformationRO.getCourseTitle());
            courseExtraInformationPO1.setCourseName(courseExtraInformationRO.getCourseName());
            try {
            int update = courseExtraInformationMapper.update(courseExtraInformationPO1, new LambdaQueryWrapper<CourseExtraInformationPO>().
                    eq(CourseExtraInformationPO::getCourseId, courseExtraInformationPO.getCourseId()));
            if(update > 0){
                return "更新课程简介成功！";
            }}catch (Exception e){
                log.error("更新课程简介失败" + e.toString());
                throw new RuntimeException("更新课程简介失败");
            }
        }else{
            log.error("该排课表对应的课程存在多条课程简介记录，更新失败!" + courseExtraInformationRO);
            throw new RuntimeException("该排课表对应的课程存在多条课程简介记录，更新失败!");
        }

        return "更新失败";
    }

    /**
     * 获取教学班的课程简介信息
     * @param courseExtraInformationRO
     * @return
     */
    public CourseExtraInformationPO getScheduleExtraInfo(CourseExtraInformationRO courseExtraInformationRO) {
        Long courseScheduleId = courseExtraInformationRO.getCourseScheduleId();
        String courseName = courseExtraInformationRO.getCourseName();
        CourseExtraInformationPO courseExtraInformationPO = courseExtraInformationMapper.selectOne(new LambdaQueryWrapper<CourseExtraInformationPO>().
                eq(CourseExtraInformationPO::getCourseScheduleId, courseScheduleId).
                eq(CourseExtraInformationPO::getCourseName, courseName));
        if(courseExtraInformationPO != null){
            return courseExtraInformationPO;
        }
        return null;
    }


    public long generateCourseScheduleListUploadMsg(String uploadFileUrl){
        String userName = (String) StpUtil.getLoginId();
        try{
            PlatformUserPO platformUserPO = platformUserMapper.selectOne(new LambdaQueryWrapper<PlatformUserPO>().
                    eq(PlatformUserPO::getUsername, userName));
            // 生成一个上传消息 状态为处理中
            // 获取当前的 LocalDateTime 实例
            LocalDateTime now = LocalDateTime.now();

            // 使用系统默认时区将 LocalDateTime 转换为 Instant
            Instant instant = now.atZone(ZoneId.systemDefault()).toInstant();

            // 将 Instant 转换为 java.util.Date
            Date date = Date.from(instant);

            UserUploadsPO userUploadsPO = new UserUploadsPO();
            userUploadsPO.setUserName(platformUserPO.getUsername());
            userUploadsPO.setUploadTime(date);
            userUploadsPO.setUploadType(UploadType.COURSE_SCHEDULE_LIST.getUpload_type());
            userUploadsPO.setFileUrl(uploadFileUrl);
            userUploadsPO.setIsRead(false);
            int insert = userUploadsMapper.insert(userUploadsPO);

            log.info(userName + " 上传文件的消息已生成 " + insert);
            Long generatedId = userUploadsPO.getId();
            PlatformMessagePO platformMessagePO = new PlatformMessagePO();
            platformMessagePO.setCreatedAt(date);
            platformMessagePO.setUserId(platformUserPO.getUsername());
            platformMessagePO.setIsRead(false);
            platformMessagePO.setRelatedMessageId(generatedId);
            platformMessagePO.setMessageType(MessageEnum.UPLOAD_MSG.getMessage_name());
            int insert1 = platformMessageMapper.insert(platformMessagePO);
            log.info("用户上传文件的消息插入结果 "+ insert1);
            return generatedId;

        }catch (Exception e){
            log.error(userName + " 生成上传文件的消息失败 " + e.toString());
        }
        return -1;
    }


    /**
     * 获取课程筛选参数
     * @param courseScheduleFilterRO 排课表课程筛选参数的其他筛选条件
     * @param filter
     * @return
     */
    public ScheduleCourseInformationSelectArgs getCoursesArgs(CourseScheduleFilterRO courseScheduleFilterRO, AbstractFilter filter) {
       return  filter.getCoursesArgs(courseScheduleFilterRO);
    }

    /**
     * 获取教师的排课表信息 只返回不同时间的各个课程
     * @param courseScheduleROPageRO
     * @param filter
     * @return
     */
    public PageVO<TeacherSchedulesVO> getTeacherCourschedules(PageRO<CourseScheduleRO> courseScheduleROPageRO, AbstractFilter filter) {

        return filter.getTeacherCourschedules(courseScheduleROPageRO);
    }

    /**
     * 获取排课表课程管理信息
     * @param courseScheduleFilterROPageRO
     * @param filter
     * @return
     */
    public FilterDataVO getScheduleCourses(PageRO<CourseScheduleFilterRO> courseScheduleFilterROPageRO, AbstractFilter filter) {
        return filter.getScheduleCourses(courseScheduleFilterROPageRO);
    }
}
