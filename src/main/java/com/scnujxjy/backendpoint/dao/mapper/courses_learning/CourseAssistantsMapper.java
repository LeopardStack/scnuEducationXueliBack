package com.scnujxjy.backendpoint.dao.mapper.courses_learning;

import com.scnujxjy.backendpoint.dao.entity.courses_learning.CourseAssistantsPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-03-15
 */
public interface CourseAssistantsMapper extends BaseMapper<CourseAssistantsPO> {

    @Update("TRUNCATE TABLE course_assistants")
    void truncateTable();
}
