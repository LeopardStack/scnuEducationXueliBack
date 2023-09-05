package com.scnujxjy.backendpoint.dao.mapper.teaching_process;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseSchedulePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 排课表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-18
 */
@Mapper
public interface CourseScheduleMapper extends BaseMapper<CourseSchedulePO> {
    /**
     * 根据 年级 专业 层次 班级名称 学习形式来获取某一个班级的教学计划
     * @param grade 年级
     * @param major_name 专业名称
     * @param level 层次
     * @param study_form 学习形式
     * @param admin_class 行政班名称
     * @return 教师信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM course_information WHERE grade = #{grade} AND major_name = #{major_name} AND level = #{level}" +
            " AND study_form = #{study_form} AND admin_class = #{admin_class}")
    List<CourseSchedulePO> selectCourseSchedules1(String grade, String major_name, String level, String study_form,
                                                        String admin_class);
}
