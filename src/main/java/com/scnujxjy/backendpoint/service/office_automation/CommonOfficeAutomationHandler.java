package com.scnujxjy.backendpoint.service.office_automation;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationStepStatus;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalStepWithRecordInformation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType.STUDENT_TRANSFER_MAJOR;
import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationStepStatus.*;

@Service
public class CommonOfficeAutomationHandler extends OfficeAutomationHandler {


    public CommonOfficeAutomationHandler() {
    }

    /**
     * 根据审批类型id获取对应列表
     *
     * @param typeId 审批类型id
     */
    public CommonOfficeAutomationHandler(Long typeId) {
    }

    public List<ApprovalStepWithRecordInformation> selectApprovalRecordWithStep(Long typeId) {
        if (Objects.isNull(typeId)) {
            throw new BusinessException("审批类型id缺失");
        }
        List<ApprovalStepRecordPO> approvalStepRecordPOS = approvalStepRecordMapper.selectList(Wrappers.<ApprovalStepRecordPO>lambdaQuery()
                .eq(ApprovalStepRecordPO::getApprovalTypeId, typeId));
        if (CollUtil.isEmpty(approvalStepRecordPOS)) {
            return null;
        }
        return approvalStepRecordPOS.stream()
                .map(ele -> approvalInverter.stepWithRecord2Information(ele, approvalStepMapper.selectById(ele.getStepId())))
                .sorted(Comparator.comparing(ApprovalStepWithRecordInformation::getStepOrder))
                .collect(Collectors.toList());
    }

    private Integer updateApprovalRecord(Long recordId, Date updateAt, Long currentStepId, OfficeAutomationStepStatus status) {
        if (Objects.isNull(recordId)) {
            throw new BusinessException("更新记录失败，主键缺失");
        }
        return approvalRecordMapper.update(null, Wrappers.<ApprovalRecordPO>lambdaUpdate()
                .eq(ApprovalRecordPO::getId, recordId)
                .set(Objects.nonNull(updateAt), ApprovalRecordPO::getUpdateAt, updateAt)
                .set(Objects.nonNull(status), ApprovalRecordPO::getStatus, status.getStatus())
                .set(Objects.nonNull(currentStepId), ApprovalRecordPO::getCurrentStepId, currentStepId));
    }

    /**
     * 根据类型查询审批流程
     * <p>根据步骤先后排序</p>
     *
     * @param typeId
     * @return
     */
    public List<ApprovalStepPO> selectApprovalStep(Long typeId) {
        if (Objects.isNull(typeId)) {
            throw new BusinessException("审批类型id缺失");
        }
        List<ApprovalStepPO> approvalRecordPOS = approvalStepMapper.selectList(Wrappers.<ApprovalStepPO>lambdaQuery()
                .eq(ApprovalStepPO::getApprovalTypeId, typeId)
                .orderBy(true, true, ApprovalStepPO::getStepOrder));
        if (CollUtil.isEmpty(approvalRecordPOS)) {
            throw new BusinessException("获取审批步骤失败");
        }
        return approvalRecordPOS;
    }


    /**
     * 新建审批记录
     *
     * @param approvalRecordPO 审批记录
     * @return
     */
    @Override
    @Transactional
    public Boolean createApprovalRecord(ApprovalRecordPO approvalRecordPO) {
        if (Objects.isNull(approvalRecordPO)
                || Objects.isNull(approvalRecordPO.getApprovalTypeId())) {
            throw new BusinessException("OA记录为空或OA类型id为空");
        }
        // 根据类型id获取步骤
        Long typeId = approvalRecordPO.getApprovalTypeId();
        List<ApprovalStepPO> approvalStepPOS = selectApprovalStep(typeId);
        if (CollUtil.isEmpty(approvalStepPOS)) {
            throw new BusinessException("当前OA类型无步骤");
        }
        // 填充记录表数据
        DateTime date = DateUtil.date();
        ApprovalStepPO approvalStepPO = approvalStepPOS.get(0);
//        String userId = "xuelijiaoyuTest1";
        String userId = StpUtil.getLoginIdAsString();
        approvalRecordPO.setInitiatorUserId(userId)
                .setCreatedAt(date)
                .setCreatedAt(date)
                .setStatus(WAITING.getStatus())
                .setCurrentStepId(approvalStepPO.getId());
        int count = approvalRecordMapper.insert(approvalRecordPO);
        if (count == 0) {
            throw new BusinessException("插入OA记录表失败");
        }
        // 插入步骤记录表
        ApprovalStepRecordPO approvalStepRecordPO = ApprovalStepRecordPO.builder()
                .approvalId(approvalRecordPO.getId())
                .approvalTypeId(typeId)
                .stepId(approvalRecordPO.getCurrentStepId())
                .updateAt(date)
                .status(WAITING.getStatus())
                .build();
        count = approvalStepRecordMapper.insert(approvalStepRecordPO);
        if (count == 0) {
            throw new BusinessException("插入当前审批记录失败");
        }
        return true;
    }

    /**
     * 审核当前步骤
     *
     * @param approvalStepRecordPO
     * @return
     */
    @Transactional
    public Boolean process(ApprovalStepRecordPO approvalStepRecordPO) {
        if (Objects.isNull(approvalStepRecordPO)
                || StrUtil.isBlank(approvalStepRecordPO.getComment())
                || StrUtil.isBlank(approvalStepRecordPO.getStatus())
                || Objects.isNull(approvalStepRecordPO.getStepId())
                || Objects.isNull(approvalStepRecordPO.getId())) {
            throw new BusinessException("当前步骤参数缺失，请检查审批意见、审批状态、审核步骤等信息");
        }
        // 针对不同状态设置下一步记录
        if (StrUtil.equals(TRANSFER.getStatus(), approvalStepRecordPO.getStatus())
                && Objects.isNull(approvalStepRecordPO.getNextStepId())) {
            throw new BusinessException("流转状态下必须给出下一个步骤");
        }
        // 基本参数
        ApprovalStepRecordPO stepRecordPO = approvalStepRecordMapper.selectById(approvalStepRecordPO.getId());
        ApprovalStepPO approvalStepPO = approvalStepMapper.selectById(stepRecordPO.getStepId());
        Long typeId = stepRecordPO.getApprovalTypeId();
        String userId = StpUtil.getLoginIdAsString();
//        String userId = "xuelijiaoyuTest1";
        DateTime date = DateUtil.date();
        // 更新当前步骤记录状态
        LambdaUpdateWrapper<ApprovalStepRecordPO> wrapper = Wrappers.<ApprovalStepRecordPO>lambdaUpdate()
                .eq(ApprovalStepRecordPO::getId, approvalStepRecordPO.getId())
                .set(ApprovalStepRecordPO::getUserId, userId)
                .set(ApprovalStepRecordPO::getUpdateAt, date)
                .set(ApprovalStepRecordPO::getComment, approvalStepRecordPO.getComment())
                .set(ApprovalStepRecordPO::getStatus, approvalStepRecordPO.getStatus())
                .set(StrUtil.equals(TRANSFER.getStatus(), approvalStepRecordPO.getStatus()), ApprovalStepRecordPO::getNextStepId, approvalStepRecordPO.getNextStepId());
        int count = approvalStepRecordMapper.update(null, wrapper);
        if (count == 0) {
            throw new BusinessException("更新当前步骤失败");
        }
        // 审批成功：跳过中间已经成功的步骤，寻找下一个未开始或者失败的步骤
        if (approvalStepRecordPO.getStatus().equals(SUCCESS.getStatus())) {
            // 在已经有的记录中查看是否存在已经成功的，跳过成功的步骤
            List<ApprovalStepWithRecordInformation> approvalStepWithRecordInformation = selectApprovalRecordWithStep(typeId);
            Map<Integer, List<ApprovalStepWithRecordInformation>> stepOrder2InformationMap = approvalStepWithRecordInformation.stream()
                    .collect(Collectors.groupingBy(ApprovalStepWithRecordInformation::getStepOrder));
            Integer orderId = null;
            for (Integer order : stepOrder2InformationMap.keySet()) {
                List<ApprovalStepWithRecordInformation> approvalStepWithRecordInformations = stepOrder2InformationMap.get(order);
                if (CollUtil.isEmpty(approvalStepWithRecordInformations)) {
                    continue;
                }
                Set<String> statusSet = approvalStepWithRecordInformations.stream().map(ApprovalStepWithRecordInformation::getStatus).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
                if (statusSet.contains(SUCCESS.getStatus())) {
                    continue;
                }
                orderId = order;
                break;
            }
            // 如果有的话要找出当前这个 order 对应的步骤，从这里开始申请
            if (Objects.nonNull(orderId)) {
                List<ApprovalStepWithRecordInformation> approvalStepWithRecordInformations = stepOrder2InformationMap.get(orderId);
                Long stepId = approvalStepWithRecordInformations.get(0).getStepId();
                int created = createApprovalStepRecord(stepRecordPO.getApprovalId(), date, stepId);
                if (created == 0) {
                    throw new BusinessException("新增步骤记录失败");
                }
                count = updateApprovalRecord(stepRecordPO.getApprovalId(), date, stepId, WAITING);
                if (count == 0) {
                    throw new BusinessException("更新事件记录失败");
                }
                return true;
            } else {
                // 没有跳过的记录，那就判断是不是最后一个步骤，如果是这说明已经结束了
                List<ApprovalStepPO> approvalStepPOS = selectApprovalStep(typeId);
                if (approvalStepPO.getStepOrder().equals(approvalStepPOS.get(approvalStepPOS.size() - 1).getStepOrder())) {
                    // 已经结束
                    Integer updated = updateApprovalRecord(stepRecordPO.getApprovalId(), date, null, SUCCESS);
                    if (updated == 0) {
                        throw new BusinessException("更新审核状态失败");
                    }
                    count = updateApprovalRecord(stepRecordPO.getApprovalId(), date, null, SUCCESS);
                    if (count == 0) {
                        throw new BusinessException("更新事件记录失败");
                    }
                    return true;
                } else {
                    // 未结束：获取下一个步骤
                    Long stepId = null;
                    for (ApprovalStepPO stepPO : approvalStepPOS) {
                        if (stepPO.getStepOrder() > approvalStepPO.getStepOrder()) {
                            stepId = stepPO.getId();
                            break;
                        }
                    }
                    int created = createApprovalStepRecord(stepRecordPO.getApprovalId(), date, stepId);
                    if (created == 0) {
                        throw new BusinessException("新增步骤记录失败");
                    }
                    count = updateApprovalRecord(stepRecordPO.getApprovalId(), date, stepId, WAITING);
                    if (count == 0) {
                        throw new BusinessException("更新事件记录失败");
                    }
                    return true;
                }
            }
        }
        // 审核失败
        else if (approvalStepRecordPO.getStatus().equals(FAILED.getStatus())) {
            // 更新当前审批记录状态
            Integer updated = updateApprovalRecord(stepRecordPO.getApprovalId(), date, null, FAILED);
            if (updated == 0) {
                throw new BusinessException("更新审核状态失败");
            }
            return true;
        }
        // 审核流转
        else if (approvalStepRecordPO.getStatus().equals(TRANSFER.getStatus())) {
            // 插入新的流程步骤
            count = createApprovalStepRecord(approvalStepRecordPO.getApprovalId(), date, approvalStepRecordPO.getNextStepId());
            return count != 0;
        }
        return false;
    }

    /**
     * 根据信息插入步骤记录
     *
     * @param approvalId
     * @param date
     * @param stepId
     */
    private int createApprovalStepRecord(Long approvalId, Date date, Long stepId) {
        if (Objects.isNull(approvalId)
                || Objects.isNull(date)
                || Objects.isNull(stepId)) {
            throw new BusinessException("新建步骤记录失败，缺失审核记录id、日期、步骤id等信息");
        }
        // 获取下一个流程步骤
        ApprovalStepPO approvalStepPO = approvalStepMapper.selectById(stepId);
        if (Objects.isNull(approvalStepPO)) {
            throw new BusinessException("获取下一个步骤失败，流转失败");
        }
        // 填充步骤记录
        ApprovalStepRecordPO recordPO = ApprovalStepRecordPO.builder()
                .approvalId(approvalId)
                .stepId(approvalStepPO.getId())
                .updateAt(date)
                .status(WAITING.getStatus())
                .approvalTypeId(approvalStepPO.getApprovalTypeId())
                .build();
        int count = approvalStepRecordMapper.insert(recordPO);
        if (count == 0) {
            throw new BusinessException("插入新的步骤记录失败");
        }
        return count;
    }

    @Override
    public OfficeAutomationHandlerType supportType() {
        return STUDENT_TRANSFER_MAJOR;
    }

}
