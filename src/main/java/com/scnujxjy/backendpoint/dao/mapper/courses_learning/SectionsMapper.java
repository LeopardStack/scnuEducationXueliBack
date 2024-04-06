package com.scnujxjy.backendpoint.dao.mapper.courses_learning;

import com.scnujxjy.backendpoint.dao.entity.courses_learning.SectionsPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseSectionRO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-03-05
 */
public interface SectionsMapper extends BaseMapper<SectionsPO> {
    @Update("TRUNCATE TABLE sections")
    void truncateTable();

    List<SectionsPO> selectSectionsInfo(@Param("entity") CourseSectionRO courseSectionRO);

    @Select("SELECT * FROM sections WHERE start_time >= #{date} AND start_time < DATE_ADD(#{date}, INTERVAL 1 DAY)")
    List<SectionsPO> selectSectionsByDate(@Param("date") String date);

    @Select("SELECT * FROM sections WHERE course_id = #{courseId}")
    List<SectionsPO> selectSectionsByCourseId(@Param("courseId") Long courseId);
}
