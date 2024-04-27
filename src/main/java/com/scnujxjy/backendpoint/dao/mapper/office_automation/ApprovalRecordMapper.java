package com.scnujxjy.backendpoint.dao.mapper.office_automation;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.model.ro.PageRO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApprovalRecordMapper extends BaseMapper<ApprovalRecordPO> {

    List<ApprovalRecordPO> selectApprovalRecordPage(@Param("entity") ApprovalRecordPO approvalRecordPO, @Param("page") PageRO page);

    Long selectApprovalRecordCount(@Param("entity") ApprovalRecordPO approvalRecordPO);

}
