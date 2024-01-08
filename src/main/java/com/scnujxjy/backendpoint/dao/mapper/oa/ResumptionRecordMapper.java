package com.scnujxjy.backendpoint.dao.mapper.oa;

import com.scnujxjy.backendpoint.dao.entity.oa.ResumptionRecordPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.model.ro.oa.ResumptionRecordRO;
import com.scnujxjy.backendpoint.model.vo.oa.ResumptionRecordVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 复学记录表 Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
public interface ResumptionRecordMapper extends BaseMapper<ResumptionRecordPO> {

    List<ResumptionRecordVO> getRetentionInfos(@Param("entity")ResumptionRecordRO entity,
                                               @Param("pageNumber")long l,
                                               @Param("pageSize")Long pageSize);

    long getRetentionInfosCount(@Param("entity")ResumptionRecordRO entity);

    List<String> getDistinctGrades(@Param("entity")ResumptionRecordRO entity);
}
