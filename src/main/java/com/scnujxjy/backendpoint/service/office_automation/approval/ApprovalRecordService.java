package com.scnujxjy.backendpoint.service.office_automation.approval;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.office_automation.OfficeAutomationStepStatus;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.approval.ApprovalRecordMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class ApprovalRecordService extends ServiceImpl<ApprovalRecordMapper, ApprovalRecordPO> implements IService<ApprovalRecordPO> {

    /**
     * 根据主键id查询审核记录
     *
     * @param id 主键
     * @return 审核记录
     */
    public ApprovalRecordPO selectById(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return baseMapper.selectById(id);
    }

    /**
     * 根据主键id更新审核记录
     *
     * @param approvalRecordPO
     * @return
     */
    public Integer updateApprovalRecordById(ApprovalRecordPO approvalRecordPO) {
        if (Objects.isNull(approvalRecordPO)) {
            return 0;
        }
        if (Objects.isNull(approvalRecordPO.getId())) {
            return 0;
        }
        if (Objects.isNull(approvalRecordPO.getUpdateAt())) {
            approvalRecordPO.setUpdateAt(new Date());
        }
        return baseMapper.updateById(approvalRecordPO);
    }

    /**
     * 更新事件记录
     *
     * @param recordId      事件记录 id
     * @param updateAt      更新时间
     * @param currentStepId 当前步骤
     * @param status        状态
     * @ 更新条数
     */
    public Integer updateApprovalRecordById(Long recordId, Date updateAt, Long currentStepId, OfficeAutomationStepStatus status) {
        if (Objects.isNull(recordId)) {
            return 0;
        }
        int count = updateApprovalRecordById(ApprovalRecordPO.builder()
                .id(recordId)
                .updateAt(updateAt)
                .currentStepId(currentStepId)
                .status(status.getStatus())
                .build());
        return count;
    }

    /**
     * 新增审核记录
     *
     * @param approvalRecordPO 审核记录
     * @return
     */
    public Integer create(ApprovalRecordPO approvalRecordPO) {
        if (Objects.isNull(approvalRecordPO)) {
            return 0;
        }
        return baseMapper.insert(approvalRecordPO);
    }


}
