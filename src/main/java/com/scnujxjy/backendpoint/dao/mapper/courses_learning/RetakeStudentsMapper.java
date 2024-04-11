package com.scnujxjy.backendpoint.dao.mapper.courses_learning;

import com.scnujxjy.backendpoint.dao.entity.courses_learning.RetakeStudentsPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-03-05
 */
public interface RetakeStudentsMapper extends BaseMapper<RetakeStudentsPO> {

    @Select("select * from retake_students where course_id=#{courseId}")
    List<RetakeStudentsPO> selectByCourseId(Long courseId);
}
