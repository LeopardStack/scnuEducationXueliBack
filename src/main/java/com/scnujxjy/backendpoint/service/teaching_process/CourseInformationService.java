package com.scnujxjy.backendpoint.service.teaching_process;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.mapper.teaching_process.CourseInformationMapper;
import com.scnujxjy.backendpoint.inverter.teaching_process.CourseInformationInverter;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.teaching_process.CourseInformationRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.teaching_process.CourseInformationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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
                .eq(Objects.nonNull(entity.getTeachingSemester()), CourseInformationPO::getTeachingSemester, entity.getTeachingSemester());
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
}
