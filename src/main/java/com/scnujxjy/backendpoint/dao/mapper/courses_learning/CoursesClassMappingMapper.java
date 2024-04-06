package com.scnujxjy.backendpoint.dao.mapper.courses_learning;

import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesClassMappingPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.registration_record_card.ClassInformationPO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CourseClassMappingRO;
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
public interface CoursesClassMappingMapper extends BaseMapper<CoursesClassMappingPO> {
    @Update("TRUNCATE TABLE courses_class_mapping")
    void truncateTable();

    List<ClassInformationPO> selectClassInfos(@Param("entity") CourseClassMappingRO courseClassMappingRO);

    @Select("select * from courses_class_mapping where course_id = #{courseId}")
    List<CoursesClassMappingPO> selectByCourseId(Long courseId);
}
