package com.scnujxjy.backendpoint.dao.mapper.exam;

import com.scnujxjy.backendpoint.dao.entity.exam.CourseExamInfoPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.model.ro.exam.BatchSetTeachersInfoRO;
import com.scnujxjy.backendpoint.model.vo.exam.ExamTeachersInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 存储考试信息 Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-15
 */
public interface CourseExamInfoMapper extends BaseMapper<CourseExamInfoPO> {

    /**
     * 通过不同的筛选条件获取考试信息记录
     * @param batchSetTeachersInfoRO
     * @return
     */
    List<CourseExamInfoPO> batchSelectData(@Param("entity") BatchSetTeachersInfoRO batchSetTeachersInfoRO);

    /**
     * 获取考试信息
     */
    List<ExamTeachersInfoVO> downloadExamTeachersInfoByManager0(@Param("entity") BatchSetTeachersInfoRO entity);
}
