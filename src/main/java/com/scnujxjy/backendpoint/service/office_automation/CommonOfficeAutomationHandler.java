package com.scnujxjy.backendpoint.service.office_automation;

import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType.COMMON;

@Service
public class CommonOfficeAutomationHandler extends OfficeAutomationHandler {
    /**
     * 新建审批记录
     *
     * @param approvalRecordPO 审批记录
     * @return
     */
    @Override
    @Transactional
    public Boolean createApprovalRecord(ApprovalRecordPO approvalRecordPO) {
        return super.createApprovalRecord(approvalRecordPO);
    }

    /**
     * 审核当前步骤
     *
     * @param approvalStepRecordPO
     * @return
     */
    @Transactional
    @Override
    public Boolean process(ApprovalStepRecordPO approvalStepRecordPO) {
        return super.process(approvalStepRecordPO);
    }

    @Override
    public OfficeAutomationHandlerType supportType() {
        return COMMON;
    }

}
