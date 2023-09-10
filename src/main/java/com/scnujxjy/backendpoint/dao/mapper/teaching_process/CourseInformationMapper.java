package com.scnujxjy.backendpoint.dao.mapper.teaching_process;

import com.scnujxjy.backendpoint.dao.entity.registration_record_card.StudentStatusPO;
import com.scnujxjy.backendpoint.dao.entity.teaching_process.CourseInformationPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 课程信息表 Mapper 接口
 * </p>
 *
 * @author leopard
 * @since 2023-08-14
 */
public interface CourseInformationMapper extends BaseMapper<CourseInformationPO> {
    /**
     * 根据 年级 专业 层次 班级名称 学习形式来获取某一个班级的教学计划
     * @param grade 年级
     * @param major_name 专业名称
     * @param level 层次
     * @param study_form 学习形式
     * @param admin_class 班级标识
     * @return 教师信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM course_information WHERE grade = #{grade} AND major_name = #{major_name} AND level = #{level}" +
            " AND study_form = #{study_form} AND admin_class = #{admin_class}")
    List<CourseInformationPO> selectCourseInformations1(String grade, String major_name, String level, String study_form,
                                                       String admin_class);

    /**
     * 根据 班级名称 学习形式来获取某一个班级的教学计划
     * @param admin_class 班级标识
     * @param course_code 课程名称编号
     * @return 教师信息集合，类型为 TeacherInformationPO
     */
    @Select("SELECT * FROM course_information WHERE admin_class = #{admin_class} AND course_code = #{course_code}")
    List<CourseInformationPO> selectByAdminClassId(String admin_class, String course_code);
}
