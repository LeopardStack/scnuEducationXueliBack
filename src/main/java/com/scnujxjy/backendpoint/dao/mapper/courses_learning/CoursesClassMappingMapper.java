package com.scnujxjy.backendpoint.dao.mapper.courses_learning;

import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesClassMappingPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface CoursesClassMappingMapper extends BaseMapper<CoursesClassMappingPO> {
    @Update("TRUNCATE TABLE courses_class_mapping")
    void truncateTable();

    @Select("select * from courses_class_mapping where course_id = #{courseId}")
    List<CoursesClassMappingPO> selectByCourseId(Long courseId);
}
