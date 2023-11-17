package com.scnujxjy.backendpoint.service.office_automation;

import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalStepMapper;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalStepRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalTypeMapper;
import com.scnujxjy.backendpoint.inverter.office_automation.ApprovalInverter;

import javax.annotation.Resource;

/**
 * OA系统的抽象实现类
 */
public abstract class OfficeAutomationHandler {

    @Resource
    protected ApprovalStepRecordMapper approvalStepRecordMapper;

    @Resource
    protected ApprovalStepMapper approvalStepMapper;

    @Resource
    protected ApprovalTypeMapper approvalTypeMapper;

    @Resource
    protected ApprovalRecordMapper approvalRecordMapper;

    @Resource
    protected ApprovalInverter approvalInverter;

    /**
     * 审核当前步骤
     *
     * @param approvalStepRecordPO
     * @return
     */
    public abstract Boolean process(ApprovalStepRecordPO approvalStepRecordPO);


    /**
     * 新增一个审批记录
     *
     * @param approvalRecordPO 审批记录
     * @return 新增的审批记录
     */
    public abstract Boolean createApprovalRecord(ApprovalRecordPO approvalRecordPO);

    /**
     * 获取支持类型
     *
     * @return
     */
    public abstract OfficeAutomationHandlerType supportType();
}