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
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeAdminInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.college.CollegeInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseScheduleMapper;
import com.scnujxjy.backendpoint.inverter.teaching_process.CourseScheduleInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseScheduleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    private ClassInformationMapper classInformationMapper;

    @Resource
    private CollegeAdminInformationMapper collegeAdminInformationMapper;

    @Resource
    private CollegeInformationMapper collegeInformationMapper;

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
            entity.setTeacherUsername(loginId);
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
            entity.setTeachingStartDate(DateUtil.offset(new Date(), DAY_OF_MONTH, -14));
            entity.setTeachingEndDate(DateUtil.offset(new Date(), DAY_OF_MONTH, 14));
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
                    })
                    .collect(Collectors.toList());
            if (Objects.equals(courseScheduleROPageRO.getIsAll(), true)) {
                return new PageVO<>(courseScheduleInverter.po2VO(courseSchedulePOS));
            } else {
                Long pageSize = courseScheduleROPageRO.getPageSize();
                Long current = courseScheduleROPageRO.getPageNumber();
                List<CourseSchedulePO> schedulePOS = ListUtil.page(Math.toIntExact(current), Math.toIntExact(pageSize), courseSchedulePOS);
                int total = courseSchedulePOS.size();
                return new PageVO<>(pageSize, Long.valueOf(total), total / pageSize, current, courseScheduleInverter.po2VO(schedulePOS));
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

}
