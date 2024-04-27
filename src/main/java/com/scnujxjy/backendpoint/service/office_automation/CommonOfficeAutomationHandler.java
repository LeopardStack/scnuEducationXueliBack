
package com.scnujxjy.backendpoint.service.office_automation;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType.COMMON;
import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationStepStatus.WAITING;


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


    /**
     * 新增一个审批记录
     *
     * @param approvalRecordPO 审批记录
     * @return 新增的审批记录
     */

    @Override
    public Boolean createApprovalRecord(ApprovalRecordPO approvalRecordPO) {
        if (Objects.isNull(approvalRecordPO)
                || Objects.isNull(approvalRecordPO.getApprovalTypeId())) {
            throw new BusinessException("OA记录为空或OA类型id为空");
        }
        // 根据类型id获取步骤
        Long typeId = approvalRecordPO.getApprovalTypeId();
        List<ApprovalStepPO> approvalStepPOS = approvalStepService.selectByTypeId(typeId);
        if (CollUtil.isEmpty(approvalStepPOS)) {
            throw new BusinessException("当前OA类型无步骤");
        }
        // 填充记录表数据
        DateTime date = DateUtil.date();
        ApprovalStepPO approvalStepPO = approvalStepPOS.get(0);
        approvalRecordPO.setInitiatorUsername(StpUtil.getLoginIdAsString())
                .setCreatedAt(date)
                .setCreatedAt(date)
                .setStatus(WAITING.getStatus())
                .setCurrentStepId(approvalStepPO.getId());
        int count = approvalRecordService.create(approvalRecordPO);
        if (count == 0) {
            throw new BusinessException("插入OA记录表失败");
        }
        // 插入步骤记录表
        createApprovalStepRecord(approvalRecordPO.getId(), date, approvalRecordPO.getCurrentStepId());
        return true;
    }
}

