package com.scnujxjy.backendpoint.service.teaching_process;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.basic.PlatformUserPO;
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
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
            List<StudentStatusPO> studentStatusPOS = studentStatusMapper.selectStudentByidNumber(username);
            if(studentStatusPOS.size() == 0){
                return new ArrayList<CourseInformationRO>();
            }else{
                List<CourseInformationPO> returnCourseInformationPOS = new ArrayList<>();
                for(StudentStatusPO studentStatusPO: studentStatusPOS){
                    String class_identifier = studentStatusPO.getClassIdentifier();
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
}
