package com.scnujxjy.backendpoint.dao.mapper.courses_learning;

import com.scnujxjy.backendpoint.dao.entity.courses_learning.CoursesLearningPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.model.bo.course_learning.CourseRecordBO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import com.scnujxjy.backendpoint.model.ro.courses_learning.CoursesLearningRO;
import com.scnujxjy.backendpoint.model.vo.PageVO;
import com.scnujxjy.backendpoint.model.vo.course_learning.CourseLearningVO;
import org.apache.ibatis.annotations.Param;
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
public interface CoursesLearningMapper extends BaseMapper<CoursesLearningPO> {
    // 使用 TRUNCATE 清除表中的数据并重置自增主键
    @Update("TRUNCATE TABLE courses_learning")
    void truncateTable();

    /**
     * 获取不同角色输入下 的 课程信息
     * @Param("entity")AdmissionInformationRO entity,
     * @Param("pageNumber")Long pageNumber, @Param("pageSize")Long pageSize
     * @param coursesLearningRO
     * @return
     */
    List<CourseLearningVO> selectCourseLearningData(@Param("entity")CoursesLearningRO coursesLearningRO,
                                                    @Param("pageNumber")Long pageNumber, @Param("pageSize")Long pageSize);

    List<CourseRecordBO> getCourseSectionsData();

}
