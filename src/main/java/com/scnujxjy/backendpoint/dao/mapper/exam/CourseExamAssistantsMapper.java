package com.scnujxjy.backendpoint.dao.mapper.exam;

import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamAssistantsPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * <p>
 * 存储阅卷助教 Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-15
 */
public interface CourseExamAssistantsMapper extends BaseMapper<CourseExamAssistantsPO> {

    @Select("TRUNCATE TABLE course_exam_assistants")
    void truncateTableInfo();
}
