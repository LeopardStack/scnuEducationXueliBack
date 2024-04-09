package com.scnujxjy.backendpoint.service.teaching_process;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeAdminInformationPO;
import com.scnujxjy.backendpoint.dao.entity.college.CollegeInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.basic.PlatformUserMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.ClassInformationMapper;
import com.scnujxjy.backendpoint.dao.mapper.registration_record_card.StudentStatusMapper;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.inverter.teaching_process.CourseInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseScheduleRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.AddEditCourseClassInfoSelectArgs;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseClassInfoVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseCreateArgsVO;
import com.scnujxjy.backendpoint.model.vo.registration_record_card.StudentStatusVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.*;
import com.scnujxjy.backendpoint.util.filter.AbstractFilter;
import com.scnujxjy.backendpoint.util.filter.CollegeAdminFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.Collator;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * <p>
 * 课程信息表 服务实现类
 * </p>
 *
 * @author leopard
 * @since 2023-08-14
 */
@Service
@Slf4j
public class CourseInformationService extends ServiceImpl<CourseInformationMapper, CourseInformationPO> implements IService<CourseInformationPO> {
    @Resource
    private CourseInformationInverter courseInformationInverter;

    @Resource
    private PlatformUserMapper platformUserMapper;

    @Resource
    private StudentStatusMapper studentStatusMapper;

    @Resource
    private ClassInformationMapper classInformationMapper;

    @Resource
    private CourseInformationMapper courseInformationMapper;

    /**
     * 根据id查询课程信息
     *
     * @param id 课程信息id
     * @return 课程信息
     */
    public CourseInformationVO detailById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 查询
        CourseInformationPO courseInformationPO = baseMapper.selectById(id);
        // 转换并返回
        return courseInformationInverter.po2VO(courseInformationPO);
    }

    /**
     * 分页查询课程信息
     *
     * @param courseInformationROPageRO 分页参数
     * @return 分页查询的课程信息列表
     */
    public PageVO<CourseInformationVO> pageQueryCourseInformation(PageRO<CourseInformationRO> courseInformationROPageRO) {
        // 参数校验
        if (Objects.isNull(courseInformationROPageRO)) {
            log.error("参数缺失");
            return null;
        }
        CourseInformationRO entity = courseInformationROPageRO.getEntity();
        if (Objects.isNull(entity)) {
            entity = new CourseInformationRO();
        }
        // 构建查询参数
        LambdaQueryWrapper<CourseInformationPO> wrapper = Wrappers.<CourseInformationPO>lambdaQuery()
                .eq(Objects.nonNull(entity.getId()), CourseInformationPO::getId, entity.getId())
                .eq(StrUtil.isNotBlank(entity.getGrade()), CourseInformationPO::getGrade, entity.getGrade())
                .like(StrUtil.isNotBlank(entity.getMajorName()), CourseInformationPO::getMajorName, entity.getMajorName())
                .eq(StrUtil.isNotBlank(entity.getLevel()), CourseInformationPO::getLevel, entity.getLevel())
                .eq(StrUtil.isNotBlank(entity.getStudyForm()), CourseInformationPO::getStudyForm, entity.getStudyForm())
                .eq(StrUtil.isNotBlank(entity.getAdminClass()), CourseInformationPO::getAdminClass, entity.getAdminClass())
                .like(StrUtil.isNotBlank(entity.getCourseName()), CourseInformationPO::getCourseName, entity.getCourseName())
                .eq(Objects.nonNull(entity.getStudyHours()), CourseInformationPO::getStudyHours, entity.getStudyHours())
                .eq(StrUtil.isNotBlank(entity.getAssessmentType()), CourseInformationPO::getAssessmentType, entity.getAssessmentType())
                .eq(StrUtil.isNotBlank(entity.getTeachingMethod()), CourseInformationPO::getTeachingMethod, entity.getTeachingMethod())
                .eq(StrUtil.isNotBlank(entity.getCourseType()), CourseInformationPO::getCourseType, entity.getCourseType())
                .eq(Objects.nonNull(entity.getCredit()), CourseInformationPO::getCredit, entity.getCredit())
                .eq(Objects.nonNull(entity.getTeachingSemester()), CourseInformationPO::getTeachingSemester, entity.getTeachingSemester())
                .last(StrUtil.isNotBlank(courseInformationROPageRO.getOrderBy()), courseInformationROPageRO.lastOrderSql());

        // 列表查询 和 分页查询 并返回
        if (Objects.equals(true, courseInformationROPageRO.getIsAll())) {
            List<CourseInformationPO> courseInformationPOS = baseMapper.selectList(wrapper);
            return new PageVO<>(courseInformationInverter.po2VO(courseInformationPOS));
        } else {
            Page<CourseInformationPO> courseInformationPOPage = baseMapper.selectPage(courseInformationROPageRO.getPage(), wrapper);
            return new PageVO<>(courseInformationPOPage, courseInformationInverter.po2VO(courseInformationPOPage.getRecords()));
        }
    }

    /**
     * 根据id更新课程信息
     *
     * @param courseInformationRO 课程信息
     * @return 更新后的课程信息
     */
    public CourseInformationVO editById(CourseInformationRO courseInformationRO) {
        // 参数校验
        if (Objects.isNull(courseInformationRO) || Objects.nonNull(courseInformationRO.getId())) {
            log.error("参数缺失");
            return null;
        }
        // 转换类型
        CourseInformationPO courseInformationPO = courseInformationInverter.ro2PO(courseInformationRO);
        // 查询
        int count = baseMapper.updateById(courseInformationPO);
        // 校验数据
        if (count <= 0) {
            log.error("更新失败，数据：{}", courseInformationPO);
            return null;
        }
        // 返回数据
        return detailById(courseInformationRO.getId());
    }

    /**
     * 根据id删除课程信息
     *
     * @param id 课程信息id
     * @return 删除数量
     */
    public Integer deleteById(Long id) {
        // 参数校验
        if (Objects.isNull(id)) {
            log.error("参数缺失");
            return null;
        }
        // 删除数据
        int count = baseMapper.deleteById(id);
        // 校验数据
        if (count <= 0) {
            log.error("删除失败，id：{}", id);
            return null;
        }
        // 返回数据
        return count;
    }

    /**
     * 根据用户登录账号所对应的 id 查询其教学计划，年级最近的排在前面，有可能学生以前也修过
     * @param userID 从 SaToken 中获取的登录 id，靠 token 识别
     * @return
     */
    public List<CourseInformationRO> getStudentTeachingPlan(String userID){
        try {
            long loginId = Long.parseLong(userID);
            PlatformUserPO platformUserPO = platformUserMapper.selectById(loginId);
            String username = platformUserPO.getUsername();
            List<StudentStatusVO> studentStatusVOS = studentStatusMapper.selectStudentByidNumber(username);
            if(studentStatusVOS.size() == 0){
                return new ArrayList<CourseInformationRO>();
            }else{
                List<CourseInformationPO> returnCourseInformationPOS = new ArrayList<>();
                for(StudentStatusVO studentStatusVO: studentStatusVOS){
                    String class_identifier = studentStatusVO.getClassIdentifier();
                    List<ClassInformationPO> classInformationPOS = classInformationMapper.selectClassByclassIdentifier(class_identifier);
                    if(classInformationPOS.size() > 1){
                        log.error(username + " 该学生所对应的班级标识 " + class_identifier + " 存在多个班级信息");
                        return new ArrayList<CourseInformationRO>();
                    }else if(classInformationPOS.size() == 1){
                        ClassInformationPO classInformationPO = classInformationPOS.get(0);
                        List<CourseInformationPO> courseInformationPOS = baseMapper.selectCourseInformations1(classInformationPO.getGrade(), classInformationPO.getMajorName(), classInformationPO.getLevel(),
                                classInformationPO.getStudyForm(), classInformationPO.getClassName());
                        returnCourseInformationPOS.addAll(courseInformationPOS);
                    }
                    else{
                        log.error(username + " 该学生所对应的班级标识 " + class_identifier + " 找不到任何班级信息");
                        return new ArrayList<CourseInformationRO>();
                    }
                }
                // 在返回数据之前，对 returnCourseInformationPOS 进行排序
                Collections.sort(returnCourseInformationPOS, new Comparator<CourseInformationPO>() {
                    @Override
                    public int compare(CourseInformationPO o1, CourseInformationPO o2) {
                        // 假设 grade 属性是 "2022" 这样的年份格式，直接转换为整数进行比较
                        int grade1 = Integer.parseInt(o1.getGrade());
                        int grade2 = Integer.parseInt(o2.getGrade());
                        // 由于你想要年份在前的排在前面，所以我们使用 grade2 - grade1
                        return grade2 - grade1;
                    }
                });
            }

        }catch (Exception e){
            log.error(e.toString());
        }
        return new ArrayList<CourseInformationRO>();
    }


    /**
     * 根据筛选器来获取不同角色的排课表数据
     * @param courseInformationROPageRO
     * @param filter
     * @return
     */
    public FilterDataVO allPageQueryCourseInformationFilter(PageRO<CourseInformationRO> courseInformationROPageRO, AbstractFilter filter) {
        // 校验参数
        if (Objects.isNull(courseInformationROPageRO)) {
            log.error("参数缺失");
            return null;
        }

        return filter.filterCourseInformation(courseInformationROPageRO);
    }

    /**
     * 获取学院的教学计划筛选参数
     * @param loginId
     * @return
     */
    public CourseInformationSelectArgs getTeachingPlansArgsByCollege(String loginId, AbstractFilter filter) {
        return filter.filterCourseInformationSelectArgs();
    }

    public byte[] downloadTeachingPlans(PageRO<CourseInformationRO> courseInformationROPageRO, AbstractFilter filter) {
        return filter.downloadTeachingPlans(courseInformationROPageRO);
    }

    /**
     * 获取课程创建所需参数
     * 比如年级的筛选参数、课程名称的筛选参数
     * @param courseInformationRO
     * @return
     */
    public CourseCreateArgsVO getTeachingPlansArgs(CourseInformationRO courseInformationRO) {

        List<String> grades = courseInformationMapper.selectDistinctGrades(courseInformationRO);
        List<String> courseNames = courseInformationMapper.selectDistinctCourseNames(courseInformationRO);

        CourseCreateArgsVO courseCreateArgsVO = new CourseCreateArgsVO();
        courseCreateArgsVO.setGrades(grades);
        courseCreateArgsVO.setCourseNames(courseNames);

        return courseCreateArgsVO;
    }

    /**
     * courseNameSet GradeSet
     * @param courseInformationRO
     * @return
     */
    public Set<CourseClassInfoVO> getClassInfosByCoursesInfo(CourseInformationRO courseInformationRO) {
        Set<CourseClassInfoVO> courseClassInfoVOS = new HashSet<>();
        for(String grade: courseInformationRO.getGrades()){
            for(String courseName: courseInformationRO.getCourseNames()){
                List<CourseClassInfoVO> courseClassInfoVOS1 = getBaseMapper().selectClassByCourseCreateCondition(
                        new CourseInformationRO()
                                .setGrade(grade)
                                .setCourseName(courseName)
                                .setCollege(courseInformationRO.getCollege())
                                .setMajorName(courseInformationRO.getMajorName())
                                .setLevel(courseInformationRO.getLevel())
                                .setStudyForm(courseInformationRO.getStudyForm())
                                .setClassName(courseInformationRO.getClassName())
                );
                courseClassInfoVOS.addAll(courseClassInfoVOS1);
            }
        }

        // 按照班级名称 拼音 排序
        Collator collator = Collator.getInstance(Locale.CHINA); // 获取中文比较器
        return courseClassInfoVOS.stream()
                .sorted(Comparator.comparing(CourseClassInfoVO::getClassName, collator))
                .collect(Collectors.toCollection(LinkedHashSet::new)); // 收集结果为LinkedHashSet以保持排序
    }

    /**
     * 获取班级的筛选参数项
     * @param courseInformationRO
     * @return
     */
    public AddEditCourseClassInfoSelectArgs getClassInfosByCoursesInfoSelectArgs(CourseInformationRO courseInformationRO) {
        ExecutorService executor = Executors.newFixedThreadPool(4); // 创建一个固定大小的线程池，这里假设是4个线程

        AddEditCourseClassInfoSelectArgs addEditCourseClassInfoSelectArgs = new AddEditCourseClassInfoSelectArgs();

        // 使用Future来异步获取结果
        Future<List<String>> collegeNamesFuture = executor.submit(() -> getBaseMapper().selectDistinctCollegeNames(courseInformationRO));
        Future<List<String>> majorNamesFuture = executor.submit(() -> getBaseMapper().selectDistinctMajorNames(courseInformationRO));
        Future<List<String>> levelsFuture = executor.submit(() -> getBaseMapper().selectDistinctLevels(courseInformationRO));
        Future<List<String>> studyFormsFuture = executor.submit(() -> getBaseMapper().selectDistinctStudyForms(courseInformationRO));

        try {
            // 获取异步操作的结果，如果未完成则会阻塞
            List<String> collegeNames = collegeNamesFuture.get();
            List<String> majorNames = majorNamesFuture.get();
            List<String> levels = levelsFuture.get();
            List<String> studyForms = studyFormsFuture.get();

            // 设置获取到的结果
            addEditCourseClassInfoSelectArgs
                    .setColleges(collegeNames)
                    .setMajorNames(majorNames)
                    .setLevels(levels)
                    .setStudyForms(studyForms);
        } catch (InterruptedException | ExecutionException e) {
            log.error("线程池获取添加/编辑课程的班级筛选项信息失败 " + e);
            return null;
        } finally {
            executor.shutdown(); // 不要忘记关闭线程池
        }

        return addEditCourseClassInfoSelectArgs;
    }
}
