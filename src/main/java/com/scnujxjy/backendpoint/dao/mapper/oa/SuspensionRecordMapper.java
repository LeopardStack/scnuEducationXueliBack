package com.scnujxjy.backendpoint.dao.mapper.oa;

import com.scnujxjy.backendpoint.dao.entity.oa.SuspensionRecordPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.model.ro.oa.SuspensionRecordRO;
import com.scnujxjy.backendpoint.model.vo.oa.SuspensionRecordVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 休学记录表 Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
public interface SuspensionRecordMapper extends BaseMapper<SuspensionRecordPO> {

    List<SuspensionRecordVO> getRetentionInfos(@Param("entity")SuspensionRecordRO entity,
                                               @Param("pageNumber")long l,
                                               @Param("pageSize")Long pageSize);

    long getRetentionInfosCount(@Param("entity")SuspensionRecordRO entity);

    List<String> getDistinctGrades(@Param("entity")SuspensionRecordRO entity);
}
