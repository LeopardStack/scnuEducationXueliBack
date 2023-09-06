package com.scnujxjy.backendpoint.service.teaching_process;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
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
        if (Objects.isNull(entity)) {
            entity = new CourseScheduleRO();
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

    public List<CourseSchedulePO> getStudentCourseSchedules(String userID) {
        try {
            long loginId = Long.parseLong(userID);
            PlatformUserPO platformUserPO = platformUserMapper.selectById(loginId);
            String username = platformUserPO.getUsername();
            List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectStudentByidNumber(username);
            if (studentStatusPOS.size() == 0) {
                return new ArrayList<CourseSchedulePO>();
            } else {
                List<CourseSchedulePO> returnCourseSchedules = new ArrayList<>();
                for (StudentStatusPO studentStatusPO : studentStatusPOS) {
                    String class_identifier = studentStatusPO.getClassIdentifier();
                    List<ClassInformationPO> classInformationPOS = classInformationMapper.selectClassByclassIdentifier(class_identifier);
                    if (classInformationPOS.size() > 1) {
                        log.error(username + " 该学生所对应的班级标识 " + class_identifier + " 存在多个班级信息");
                        return new ArrayList<CourseSchedulePO>();
                    } else if (classInformationPOS.size() == 1) {
                        ClassInformationPO classInformationPO = classInformationPOS.get(0);
                        List<CourseSchedulePO> courseInformationPOS = baseMapper.selectCourseSchedules1(classInformationPO.getGrade(), classInformationPO.getMajorName(), classInformationPO.getLevel(),
                                classInformationPO.getStudyForm(), classInformationPO.getClassName());
                        returnCourseSchedules.addAll(courseInformationPOS);
                    } else {
                        log.error(username + " 该学生所对应的班级标识 " + class_identifier + " 找不到任何班级信息");
                        return new ArrayList<CourseSchedulePO>();
                    }
                }
                // 在返回数据之前，对 returnCourseInformationPOS 进行排序
                Collections.sort(returnCourseSchedules, new Comparator<CourseSchedulePO>() {
                    @Override
                    public int compare(CourseSchedulePO o1, CourseSchedulePO o2) {
                        // 假设 grade 属性是 "2022" 这样的年份格式，直接转换为整数进行比较
                        int grade1 = Integer.parseInt(o1.getGrade());
                        int grade2 = Integer.parseInt(o2.getGrade());
                        // 由于你想要年份在前的排在前面，所以我们使用 grade2 - grade1
                        return grade2 - grade1;
                    }
                });
            }

        } catch (Exception e) {
            log.error(e.toString());
        }
        return new ArrayList<CourseSchedulePO>();
    }

}
