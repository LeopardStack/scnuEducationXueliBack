package com.scnujxjy.backendpoint.inverter.office_automation;

import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalRecordAllInformation;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalStepRecordWithStepInformation;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalStepWithRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApprovalInverter {
    @Mappings({
            @Mapping(target = "approvalStepWithRecordList", source = "approvalStepWithRecordList")
    })
    ApprovalRecordAllInformation approvalRecordStep2Information(ApprovalRecordPO approvalRecordPO, List<ApprovalStepWithRecord> approvalStepWithRecordList);

    @Mappings({
            @Mapping(target = "stepRecordId", source = "approvalStepRecordPO.id"),
            @Mapping(target = "stepId", source = "approvalStepPO.id"),
            @Mapping(target = "approvalTypeId", source = "approvalStepRecordPO.approvalTypeId"),
            @Mapping(target = "logic", source = "approvalStepRecordPO.logic")
    })
    ApprovalStepRecordWithStepInformation stepWithRecord2Information(ApprovalStepRecordPO approvalStepRecordPO, ApprovalStepPO approvalStepPO);

    @Mappings({})
    ApprovalStepWithRecord step2ApprovalStepWithRecordList(ApprovalStepPO approvalStepPO, List<ApprovalStepRecordPO> approvalStepRecordList);
}
