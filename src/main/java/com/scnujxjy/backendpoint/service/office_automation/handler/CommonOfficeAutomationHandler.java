
package com.scnujxjy.backendpoint.service.office_automation.handler;

import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalStepRecordPO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType.COMMON;


/**
 * OA 系统通用实现类
 */

@Service
@Slf4j
@Transactional
public class CommonOfficeAutomationHandler extends OfficeAutomationHandler {

    /**
     * 处理每一步审核之后的过程
     *
     * @param approvalStepRecordPO
     */

    @Override
    public void afterProcess(ApprovalStepRecordPO approvalStepRecordPO, ApprovalRecordPO approvalRecordPO) {
        log.info("审核后的处理参数: {}", approvalStepRecordPO);
    }

    /**
     * 处理完所有审核流程后会执行的方法
     * <p>实现时可以根据approvalRecordPO.status来定义不同的行为</p>
     *
     * @param approvalRecordPO     审批完成的审批记录
     * @param approvalStepRecordPO 最后一个步骤记录
     */
    @Override
    public void afterApproval(ApprovalRecordPO approvalRecordPO, ApprovalStepRecordPO approvalStepRecordPO) {
        log.info("审批完成，审批记录：{}，最后一步记录：{}", approvalRecordPO, approvalStepRecordPO);
    }

    @Override
    protected Set<String> buildApprovalUsernameSet(ApprovalStepPO approvalStepPO, String documentId) {
        return Collections.emptySet();
    }

    @Override
    protected Set<String> buildWatchUsernameSet(ApprovalRecordPO approvalRecordPO) {
        return Collections.emptySet();
    }

    /**
     * 插入表单
     *
     * @param map
     * @return
     */

    @Override
    public String insertDocument(Map<String, Object> map) {
        return null;
    }

    /**
     * 根据 id 删除表单
     *
     * @param id
     * @return
     */
    @Override
    public Integer deleteDocument(String id) {
        return null;
    }

    /**
     * 根据 id 查询表单
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> selectDocument(String id) {
        return null;
    }

    /**
     * 根据 id 更新表单信息
     *
     * @param map 表单信息
     * @param id  表单 id
     * @return
     */
    @Override
    public Map<String, Object> updateById(Map<String, Object> map, String id) {
        return null;
    }


    @Override
    public OfficeAutomationHandlerType supportType() {
        return COMMON;
    }
}

