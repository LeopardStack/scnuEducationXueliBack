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
     * @return 排课表集合，类型为 CourseSchedulePO
     */
    @Select("SELECT * FROM course_schedule WHERE grade = #{grade} AND major_name = #{major_name} AND level = #{level}" +
            " AND study_form = #{study_form} AND admin_class = #{admin_class}")
    List<CourseSchedulePO> selectCourseSchedules1(String grade, String major_name, String level, String study_form,
                                                        String admin_class);


    /**
     * 根据 授课老师姓名来获取所有的教学计划
     * @param main_teacher_name 授课老师姓名
     * @return 排课表集合，类型为 CourseSchedulePO
     */
    @Select("SELECT * FROM course_schedule WHERE main_teacher_name = #{main_teacher_name}")
    List<CourseSchedulePO> selectCourseSchedules3(String main_teacher_name);


    /**
     * 根据 学院来获取所有的教学计划
     * @param collegeName 学院
     * @return 排课表集合，类型为 CourseSchedulePO
     */
    @Select("SELECT cs.* " +
            "FROM course_schedule cs " +
            "JOIN class_information ci " +
            "ON cs.grade = ci.grade " +
            "AND cs.level = ci.level " +
            "AND cs.study_form = ci.study_form " +
            "AND cs.admin_class = ci.class_name " +  // 注意这里添加了一个空格
            "WHERE ci.college = #{collegeName};")
    List<CourseSchedulePO> selectCourseSchedules2(String collegeName);
}
