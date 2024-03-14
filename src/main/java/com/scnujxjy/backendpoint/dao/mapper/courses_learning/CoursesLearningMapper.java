package com.scnujxjy.backendpoint.dao.mapper.courses_learning;

import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesLearningPO;
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
public interface CoursesLearningMapper extends BaseMapper<CoursesLearningPO> {
    // 使用 TRUNCATE 清除表中的数据并重置自增主键
    @Update("TRUNCATE TABLE courses_learning")
    void truncateTable();
}
