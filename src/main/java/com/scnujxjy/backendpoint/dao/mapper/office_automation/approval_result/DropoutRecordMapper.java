package com.scnujxjy.backendpoint.dao.mapper.office_automation.approval_result;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval_result.DropoutRecordPO;
import com.scnujxjy.backendpoint.model.ro.oa.DropoutRecordRO;
import com.scnujxjy.backendpoint.model.vo.oa.DropoutRecordWithClassInfoVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 退学记录表 Mapper 接口
 * </p>
 *
 * @author 谢辉龙
 * @since 2023-11-25
 */
public interface DropoutRecordMapper extends BaseMapper<DropoutRecordPO> {

    List<DropoutRecordWithClassInfoVO> getDropoutInfos(@Param("entity")DropoutRecordRO entity, @Param("pageNumber")Long pageNumber,
                                                       @Param("pageSize")Long pageSize);

    long getDropoutInfosCount(@Param("entity")DropoutRecordRO entity);

    List<String> getDistinctGrades(@Param("entity")DropoutRecordRO entity);

    List<DropoutRecordWithClassInfoVO> getSingleDropoutInfos(@Param("entity")DropoutRecordRO entity);
}
