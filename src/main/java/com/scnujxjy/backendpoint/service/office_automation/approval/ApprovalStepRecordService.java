package com.scnujxjy.backendpoint.service.office_automation.approval;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationStepStatus;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalStepRecordMapper;
import com.scnujxjy.backendpoint.inverter.office_automation.ApprovalInverter;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalRecordWithStepInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApprovalStepRecordService extends ServiceImpl<ApprovalStepRecordMapper, ApprovalStepRecordPO> implements IService<ApprovalStepRecordPO> {

    @Resource
    private ApprovalInverter approvalInverter;

    @Resource
    private ApprovalStepService approvalStepService;

    /**
     * 根据主键id查询审核步骤记录
     *
     * @param id 主键id
     * @return 审核步骤记录
     */
    public ApprovalStepRecordPO selectById(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }
        return baseMapper.selectById(id);
    }


    /**
     * 根据审核记录id查询步骤记录
     *
     * @param approvalRecordId 审核记录id
     * @return
     */
    public List<ApprovalStepRecordPO> selectByApprovalRecordId(Long approvalRecordId) {
        if (Objects.isNull(approvalRecordId)) {
            return Lists.newArrayList();
        }
        return baseMapper.selectList(Wrappers.<ApprovalStepRecordPO>lambdaQuery()
                .eq(ApprovalStepRecordPO::getApprovalId, approvalRecordId)
                .orderBy(true, true, ApprovalStepRecordPO::getId));
    }

    /**
     * 根据id更新审核步骤记录
     *
     * @param approvalStepRecordPO 审核记录
     * @return 更新条数
     */
    public Integer updateApprovalStepRecordById(ApprovalStepRecordPO approvalStepRecordPO) {
        if (Objects.isNull(approvalStepRecordPO)) {
            return 0;
        }
        if (Objects.isNull(approvalStepRecordPO.getId())) {
            return 0;
        }
        if (Objects.isNull(approvalStepRecordPO.getUpdateAt())) {
            approvalStepRecordPO.setUpdateAt(new Date());
        }
        if (Objects.equals(OfficeAutomationStepStatus.TRANSFER.getStatus(), approvalStepRecordPO.getStatus()) && Objects.isNull(approvalStepRecordPO.getNextStepId())) {
            return 0;
        }
        return baseMapper.updateById(approvalStepRecordPO);
    }


    /**
     * 根据类型id 获取步骤记录及其补充信息
     *
     * @param approvalRecordId 类型 id
     * @return 步骤记录极其补充信息
     */
    public List<ApprovalRecordWithStepInformation> selectApprovalRecordWithApprovalRecordId(Long approvalRecordId) {
        if (Objects.isNull(approvalRecordId)) {
            return Lists.newArrayList();
        }

        List<ApprovalStepRecordPO> approvalStepRecordPOS = baseMapper.selectList(Wrappers.<ApprovalStepRecordPO>lambdaQuery()
                .eq(ApprovalStepRecordPO::getApprovalId, approvalRecordId));
        if (CollUtil.isEmpty(approvalStepRecordPOS)) {
            return Lists.newArrayList();
        }
        return approvalStepRecordPOS.stream()
                .map(ele -> approvalInverter.stepWithRecord2Information(ele, approvalStepService.selectById(ele.getStepId())))
                .sorted(Comparator.comparing(ApprovalRecordWithStepInformation::getStepOrder))
                .collect(Collectors.toList());
    }

    /**
     * 新增审核步骤记录
     *
     * @param approvalStepRecordPO 审核步骤记录
     * @return
     */
    public Integer create(ApprovalStepRecordPO approvalStepRecordPO) {
        if (Objects.isNull(approvalStepRecordPO)) {
            return 0;
        }
        return baseMapper.insert(approvalStepRecordPO);
    }
}
