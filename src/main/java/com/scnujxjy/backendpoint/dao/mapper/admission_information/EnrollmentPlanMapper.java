package com.scnujxjy.backendpoint.dao.mapper.admission_information;

import com.scnujxjy.backendpoint.dao.entity.admission_information.EnrollmentPlanPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.model.ro.admission_information.EnrollmentPlanApplyRO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 招生计划申报表 Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2024-05-17
 */
public interface EnrollmentPlanMapper extends BaseMapper<EnrollmentPlanPO> {

    List<EnrollmentPlanPO> queryEnrollmentPlan(@Param("entity") EnrollmentPlanApplyRO entity,
                                               @Param("pageNumber") Long pageNumber, @Param("pageSize")Long pageSize);

    Long queryEnrollmentPlanSize(@Param("entity") EnrollmentPlanApplyRO entity);
    List<EnrollmentPlanPO> queryAllEnrollmentPlans(@Param("entity") EnrollmentPlanApplyRO entity);

    List<String> getDistinctGradeList(@Param("entity") EnrollmentPlanApplyRO enrollmentPlanApplyRO);

    List<String> getDistinctMajorNameList(@Param("entity") EnrollmentPlanApplyRO enrollmentPlanApplyRO);

    List<String> getDistinctStudyFormList(@Param("entity") EnrollmentPlanApplyRO enrollmentPlanApplyRO);

    List<String> getDistinctTrainingLevelList(@Param("entity") EnrollmentPlanApplyRO enrollmentPlanApplyRO);

    List<String> getDistinctTrainingCollegeList(@Param("entity") EnrollmentPlanApplyRO enrollmentPlanApplyRO);

    List<String> getDistinctTeachingPointNameList(@Param("entity") EnrollmentPlanApplyRO enrollmentPlanApplyRO);
}
