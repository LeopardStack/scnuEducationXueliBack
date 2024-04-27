package com.scnujxjy.backendpoint.inverter.office_automation;

import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalRecordAllInformation;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalRecordWithStepInformation;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalStepWithRecordList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApprovalInverter {
    @Mappings({
            @Mapping(target = "stepWithRecordLists", source = "stepWithRecordLists")
    })
    ApprovalRecordAllInformation approvalRecordStep2Information(ApprovalRecordPO approvalRecordPO, List<ApprovalStepWithRecordList> stepWithRecordLists);

    @Mappings({
            @Mapping(target = "stepRecordId", source = "approvalStepRecordPO.id"),
            @Mapping(target = "stepId", source = "approvalStepPO.id"),
            @Mapping(target = "approvalTypeId", source = "approvalStepRecordPO.approvalTypeId"),
            @Mapping(target = "logic", source = "approvalStepRecordPO.logic")
    })
    ApprovalRecordWithStepInformation stepWithRecord2Information(ApprovalStepRecordPO approvalStepRecordPO, ApprovalStepPO approvalStepPO);

    @Mappings({})
    ApprovalStepWithRecordList step2ApprovalStepWithRecordList(ApprovalStepPO approvalStepPO, List<ApprovalStepRecordPO> approvalStepRecordList);
}
