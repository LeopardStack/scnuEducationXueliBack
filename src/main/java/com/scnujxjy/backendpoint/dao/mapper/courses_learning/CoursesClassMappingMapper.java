package com.scnujxjy.backendpoint.dao.mapper.courses_learning;

import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesClassMappingPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

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
}
