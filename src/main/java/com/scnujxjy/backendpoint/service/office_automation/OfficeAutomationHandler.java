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
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalStepMapper;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalStepRecordMapper;
import com.scnujxjy.backendpoint.dao.mapper.office_automation.ApprovalTypeMapper;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.inverter.office_automation.ApprovalInverter;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalRecordWithStepInformation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationStepStatus.*;

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
     * 获取支持类型
     *
     * @return 支持类型
     */
    public abstract OfficeAutomationHandlerType supportType();

    /**
     * 审核当前步骤
     *
     * @param approvalStepRecordPO 审核参数
     * @return true-成功
     */
    @Transactional
    public Boolean process(ApprovalStepRecordPO approvalStepRecordPO) {
        // 检查参数
        if (!check(approvalStepRecordPO)) {
            throw new BusinessException("参数非法");
        }
        // 基本参数
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
        switch (Objects.requireNonNull(match(approvalStepRecordPO.getStatus()))) {
            case SUCCESS:
                success(approvalStepRecordPO.getApprovalId(), approvalStepRecordPO.getStepId(), date);
                break;
            case FAILED:
                failed(approvalStepRecordPO.getApprovalId(), date);
                break;
            case TRANSFER:
                transfer(approvalStepRecordPO.getApprovalId(), date, approvalStepRecordPO.getNextStepId());
                break;
            default:
                return false;
        }
        afterProcess(approvalStepRecordPO);
        return false;
    }

    /**
     * 检查参数以及操作合法性
     *
     * @param approvalStepRecordPO 参数
     * @return
     */
    @Transactional
    public Boolean check(ApprovalStepRecordPO approvalStepRecordPO) {
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
        return true;
    }


    /**
     * 步骤成功处理函数
     * <p>会跳过中间已经成功的步骤</p>
     * <p>如果出现错误会直接抛出</p>
     *
     * @param approvalId 事件记录 id
     * @param stepId     步骤 id
     * @param date       更新时间
     * @return true-成功
     */
    @Transactional
    public Boolean success(Long approvalId, Long stepId, Date date) {
        ApprovalStepPO approvalStepPO = approvalStepMapper.selectById(stepId);
        Long typeId = approvalStepPO.getApprovalTypeId();
        // 在已经有的记录中查看是否存在已经成功的，跳过成功的步骤
        List<ApprovalRecordWithStepInformation> approvalRecordWithStepInformation = selectApprovalRecordWithStep(typeId);
        Map<Integer, List<ApprovalRecordWithStepInformation>> stepOrder2InformationMap = approvalRecordWithStepInformation.stream()
                .filter(ele -> approvalId.equals(ele.getApprovalId()))
                .collect(Collectors.groupingBy(ApprovalRecordWithStepInformation::getStepOrder));
        Integer orderId = null;
        for (Integer order : stepOrder2InformationMap.keySet()) {
            // 在当前步骤之前的不用遍历
            if (approvalStepPO.getStepOrder() >= order) {
                continue;
            }
            List<ApprovalRecordWithStepInformation> approvalRecordWithStepInformations = stepOrder2InformationMap.get(order);
            if (CollUtil.isEmpty(approvalRecordWithStepInformations)) {
                continue;
            }
            Set<String> statusSet = approvalRecordWithStepInformations.stream().map(ApprovalRecordWithStepInformation::getStatus).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
            if (!statusSet.contains(SUCCESS.getStatus())) {
                orderId = order;
                break;
            }
        }
        // 如果有的话要找出当前这个 order 对应的步骤，从这里开始申请
        if (Objects.nonNull(orderId)) {
            List<ApprovalRecordWithStepInformation> approvalRecordWithStepInformations = stepOrder2InformationMap.get(orderId);
            Long nextStepId = approvalRecordWithStepInformations.get(0).getStepId();
            createApprovalStepRecord(approvalId, date, nextStepId);
            updateApprovalRecord(approvalId, date, nextStepId, WAITING);
            return true;
        } else {
            // 没有跳过的记录，那就判断是不是最后一个步骤，如果是这说明已经结束了
            List<ApprovalStepPO> approvalStepPOS = selectApprovalStep(typeId);
            if (approvalStepPO.getStepOrder().equals(approvalStepPOS.get(approvalStepPOS.size() - 1).getStepOrder())) {
                // 已经结束
                updateApprovalRecord(approvalId, date, null, SUCCESS);
                return true;
            } else {
                // 未结束：获取下一个步骤
                Long nextStepId = null;
                for (ApprovalStepPO stepPO : approvalStepPOS) {
                    if (stepPO.getStepOrder() > approvalStepPO.getStepOrder()) {
                        nextStepId = stepPO.getId();
                        break;
                    }
                }
                createApprovalStepRecord(approvalId, date, nextStepId);
                updateApprovalRecord(approvalId, date, nextStepId, WAITING);
                return true;
            }
        }
    }

    /**
     * 步骤失败处理函数
     * <p>失败需要重新申请</p>
     * <p>函数错误会抛出</p>
     *
     * @param approvalId 事件记录 id
     * @param date       更新时间
     * @return
     */
    @Transactional
    public Boolean failed(Long approvalId, Date date) {
        Integer updated = updateApprovalRecord(approvalId, date, null, FAILED);
        if (updated == 0) {
            throw new BusinessException("修改OA信息失败");
        }
        return true;
    }

    /**
     * 步骤流转处理函数
     * <p>步骤流转到前面的步骤</p>
     * <p>失败会抛出错误</p>
     *
     * @param approvalId 事件记录 id
     * @param date       更新时间
     * @param nextStepId 下一步的步骤 id
     * @return true-成功
     */
    @Transactional
    public Boolean transfer(Long approvalId, Date date, Long nextStepId) {
        Integer created = createApprovalStepRecord(approvalId, date, nextStepId);
        if (created == 0) {
            throw new BusinessException("新增步骤记录失败");
        }
        Integer updated = updateApprovalRecord(approvalId, date, nextStepId, TRANSFER);
        if (updated == 0) {
            throw new BusinessException("修改OA信息失败");
        }
        return true;
    }

    /**
     * 新增一个审批记录
     *
     * @param approvalRecordPO 审批记录
     * @return 新增的审批记录
     */
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
     * 处理后的过程
     *
     * @param approvalStepRecordPO
     */
    @Transactional
    public abstract void afterProcess(ApprovalStepRecordPO approvalStepRecordPO);

    /**
     * 根据类型id 获取步骤记录及其补充信息
     *
     * @param typeId 类型 id
     * @return 步骤记录极其补充信息
     */
    protected List<ApprovalRecordWithStepInformation> selectApprovalRecordWithStep(Long typeId) {
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
                .sorted(Comparator.comparing(ApprovalRecordWithStepInformation::getStepOrder))
                .collect(Collectors.toList());
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
    protected Integer updateApprovalRecord(Long recordId, Date updateAt, Long currentStepId, OfficeAutomationStepStatus status) {
        if (Objects.isNull(recordId)) {
            throw new BusinessException("更新记录失败，主键缺失");
        }
        int count = approvalRecordMapper.update(null, Wrappers.<ApprovalRecordPO>lambdaUpdate()
                .eq(ApprovalRecordPO::getId, recordId)
                .set(Objects.nonNull(updateAt), ApprovalRecordPO::getUpdateAt, updateAt)
                .set(Objects.nonNull(status), ApprovalRecordPO::getStatus, status.getStatus())
                .set(Objects.nonNull(currentStepId), ApprovalRecordPO::getCurrentStepId, currentStepId));
        if (count == 0) {
            throw new BusinessException("更新记录失败");
        }
        return count;
    }

    /**
     * 根据类型查询审批流程
     * <p>根据步骤先后排序</p>
     *
     * @param typeId 类型 id
     * @return 所属类型的步骤列表
     */
    protected List<ApprovalStepPO> selectApprovalStep(Long typeId) {
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
     * 根据信息插入步骤记录
     *
     * @param approvalId 事件记录 Id
     * @param date       添加时间
     * @param stepId     步骤 id
     */
    protected int createApprovalStepRecord(Long approvalId, Date date, Long stepId) {
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
}