package com.scnujxjy.backendpoint.service.office_automation;

import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType.COMMON;

@Service
@Slf4j
public class CommonOfficeAutomationHandler extends OfficeAutomationHandler {
    /**
     * 处理后的过程
     *
     * @param approvalStepRecordPO
     */
    @Override
    public void afterProcess(ApprovalStepRecordPO approvalStepRecordPO) {
        log.info("审核后的处理参数: {}", approvalStepRecordPO);
    }

    @Override
    public OfficeAutomationHandlerType supportType() {
        return COMMON;
    }

}
