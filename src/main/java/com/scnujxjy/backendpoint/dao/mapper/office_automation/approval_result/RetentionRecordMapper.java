package com.scnujxjy.backendpoint.dao.mapper.office_automation.approval_result;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval_result.RetentionRecordPO;
import com.scnujxjy.backendpoint.model.ro.oa.RetentionRecordRO;
import com.scnujxjy.backendpoint.model.vo.oa.RetentionRecordVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 留级记录表 Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
public interface RetentionRecordMapper extends BaseMapper<RetentionRecordPO> {

    List<RetentionRecordVO> getRetentionInfos(@Param("entity")RetentionRecordRO entity,
                                            @Param("pageNumber")long l,
                                            @Param("pageSize")Long pageSize);

    long getRetentionInfosCount(@Param("entity")RetentionRecordRO entity);

    List<String> getDistinctOldGrades(@Param("entity")RetentionRecordRO entity);

    List<String> getDistinctNewGrades(@Param("entity")RetentionRecordRO entity);
}
