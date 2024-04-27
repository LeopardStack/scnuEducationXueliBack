package com.scnujxjy.backendpoint.service.office_automation;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.scnujxjy.backendpoint.constant.SystemConstant;
import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.exception.BusinessException;
import com.scnujxjy.backendpoint.model.vo.office_automation.ApprovalRecordWithStepInformation;
import com.scnujxjy.backendpoint.service.basic.PlatformUserService;
import com.scnujxjy.backendpoint.service.college.CollegeAdminInformationService;
import com.scnujxjy.backendpoint.service.office_automation.approval.ApprovalRecordService;
import com.scnujxjy.backendpoint.service.office_automation.approval.ApprovalStepRecordService;
import com.scnujxjy.backendpoint.service.office_automation.approval.ApprovalStepService;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationStepStatus.*;

/**
 * OA系统的抽象实现类
 */
public abstract class OfficeAutomationHandler {

    @Resource
    protected ApprovalRecordService approvalRecordService;

    @Resource
    protected ApprovalStepRecordService approvalStepRecordService;

    @Resource
    protected ApprovalStepService approvalStepService;


    @Resource
    protected MongoTemplate mongoTemplate;

    @Resource
    protected PlatformUserService platformUserService;

    @Resource
    protected CollegeAdminInformationService collegeAdminInformationService;


    /**
     * 获取支持类型
     *
     * @return 支持类型
     */
    public abstract OfficeAutomationHandlerType supportType();


    /**
     * 新增一个审批记录
     * <p>需要添加可以审核用户的集合</p>
     *
     * @param approvalRecordPO 审批记录
     * @return 新增的审批记录
     * @see CommonOfficeAutomationHandler#createApprovalRecord(ApprovalRecordPO)
     */
    protected abstract Boolean createApprovalRecord(ApprovalRecordPO approvalRecordPO);

    /**
     * 处理后的过程
     * <p>需要重写处理后每个事务需要执行的逻辑都不一样</p>
     *
     * @param approvalStepRecordPO
     * @see CommonOfficeAutomationHandler#afterProcess(ApprovalStepRecordPO, ApprovalRecordPO)
     */
    public abstract void afterProcess(ApprovalStepRecordPO approvalStepRecordPO, ApprovalRecordPO approvalRecordPO);

    /**
     * 处理完所有审核流程后会执行的方法
     * <p>实现时可以根据approvalRecordPO.status来定义不同的行为</p>
     *
     * @param approvalRecordPO     审批完成的审批记录
     * @param approvalStepRecordPO 最后一个步骤记录
     */
    public abstract void afterApproval(ApprovalRecordPO approvalRecordPO, ApprovalStepRecordPO approvalStepRecordPO);

    /**
     * 审核当前步骤
     *
     * @param approvalStepRecordPO 审核参数
     * @return true-成功
     */
    public Boolean process(ApprovalStepRecordPO approvalStepRecordPO) {
        // 检查参数
        if (!check(approvalStepRecordPO)) {
            throw new BusinessException("参数非法");
        }
        // 基本参数
        String username = StpUtil.getLoginIdAsString();
        DateTime date = DateUtil.date();
        // 更新当前步骤记录状态
        int count = approvalStepRecordService.updateApprovalStepRecordById(approvalStepRecordPO);
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
        // 查询更新完的审批记录
        ApprovalRecordPO approvalRecordPO = approvalRecordService.selectById(approvalStepRecordPO.getApprovalId());
        if (Objects.isNull(approvalRecordPO)) {
            throw new BusinessException("获取审核记录失败");
        }
        // 执行当前步骤完成后的函数
        afterProcess(approvalStepRecordPO, approvalRecordPO);
        // 如果当前审批记录为success或者failed状态则说明已经完成，执行完成的步骤
        if (SUCCESS.getStatus().equals(approvalRecordPO.getStatus()) || FAILED.getStatus().equals(approvalRecordPO.getStatus())) {
            afterApproval(approvalRecordPO, approvalStepRecordPO);
        }
        return true;
    }


    /**
     * 根据信息插入步骤记录
     * <p>需要重写拥有审核权限用户的集合的逻辑</p>
     * 请参阅下面的示例方法来实现
     *
     * @param approvalId 事件记录 Id
     * @param date       添加时间
     * @param stepId     步骤 id
     * @see CommonOfficeAutomationHandler#createApprovalStepRecord(Long, Date, Long)
     */
    protected int createApprovalStepRecord(Long approvalId, Date date, Long stepId) {
        if (Objects.isNull(approvalId)
                || Objects.isNull(date)
                || Objects.isNull(stepId)) {
            throw new BusinessException("新建步骤记录失败，缺失审核记录id、日期、步骤id等信息");
        }
        ApprovalRecordPO approvalRecordPO = approvalRecordService.selectById(approvalId);
        if (Objects.isNull(approvalRecordPO)) {
            throw new BusinessException("获取记录失败");
        }

        ApprovalStepPO approvalStepPO = approvalStepService.selectById(stepId);
        if (Objects.isNull(approvalStepPO)) {
            throw new BusinessException("获取下一个步骤失败，流转失败");
        }
        Set<String> usernameSet = buildApprovalUsernameSet(approvalStepPO, approvalRecordPO.getDocumentId());
        ApprovalStepRecordPO approvalStepRecordPO = ApprovalStepRecordPO.builder()
                .approvalId(approvalId)
                .stepId(approvalStepPO.getId())
                .updateAt(date)
                .status(WAITING.getStatus())
                .approvalTypeId(approvalStepPO.getApprovalTypeId())
                .approvalUsernameSet(usernameSet)
                .build();
        int count = approvalStepRecordService.create(approvalStepRecordPO);
        if (count == 0) {
            throw new BusinessException("插入新的步骤记录失败");
        }
        return count;
    }

    /**
     * 构建审核步骤记录的审核人群
     *
     * @param approvalStepPO 审核步骤
     * @param documentId     申请表单编号
     * @return username集合
     */
    protected abstract Set<String> buildApprovalUsernameSet(ApprovalStepPO approvalStepPO, String documentId);


    /**
     * 检查参数以及操作合法性
     *
     * @param approvalStepRecordPO 参数
     * @return
     */
    protected Boolean check(ApprovalStepRecordPO approvalStepRecordPO) {
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
        // 检查是否有审核权限
        ApprovalStepRecordPO stepRecordPO = approvalStepRecordService.selectById(approvalStepRecordPO.getId());
        if (Objects.isNull(stepRecordPO)) {
            throw new BusinessException("获取审核步骤记录失败");
        }
        if (!CollUtil.contains(stepRecordPO.getApprovalUsernameSet(), approvalStepRecordPO.getUsername())
                && !SystemConstant.SYSTEM_NAME.equals(approvalStepRecordPO.getUsername())) {
            throw new BusinessException("当前用户无审核权限");
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
    protected Boolean transfer(Long approvalId, Date date, Long nextStepId) {
        Integer created = createApprovalStepRecord(approvalId, date, nextStepId);
        if (created == 0) {
            throw new BusinessException("新增步骤记录失败");
        }
        Integer updated = approvalRecordService.updateApprovalRecordById(approvalId, date, nextStepId, TRANSFER);
        if (updated == 0) {
            throw new BusinessException("修改OA信息失败");
        }
        return true;
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
    protected Boolean failed(Long approvalId, Date date) {
        Integer updated = approvalRecordService.updateApprovalRecordById(approvalId, date, null, FAILED);
        if (updated == 0) {
            throw new BusinessException("修改OA信息失败");
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
    protected Boolean success(Long approvalId, Long stepId, Date date) {
        ApprovalStepPO approvalStepPO = approvalStepService.selectById(stepId);
        if (Objects.isNull(approvalStepPO)) {
            throw new BusinessException("获取审核步骤失败");
        }
        Long typeId = approvalStepPO.getApprovalTypeId();
        // 在已经有的记录中查看是否存在已经成功的，跳过成功的步骤
        List<ApprovalRecordWithStepInformation> approvalRecordWithStepInformation = approvalStepRecordService.selectApprovalRecordWithApprovalRecordId(approvalId);
        if (CollUtil.isEmpty(approvalRecordWithStepInformation)) {
            throw new BusinessException("获取审核步骤记录失败");
        }
        Map<Integer, List<ApprovalRecordWithStepInformation>> stepOrder2InformationMap = approvalRecordWithStepInformation.stream()
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
            Set<String> statusSet = approvalRecordWithStepInformations.stream().map(ApprovalRecordWithStepInformation::getStatus)
                    .filter(StrUtil::isNotBlank).collect(Collectors.toSet());
            if (!statusSet.contains(SUCCESS.getStatus())) {
                orderId = order;
                break;
            }
        }
        // 如果有的话要找出当前这个 order 对应的步骤，从这里开始申请
        Long nextStepId = null;
        if (Objects.nonNull(orderId)) {
            List<ApprovalRecordWithStepInformation> approvalRecordWithStepInformations = stepOrder2InformationMap.get(orderId);
            nextStepId = approvalRecordWithStepInformations.get(0).getStepId();
        } else {
            // 没有跳过的记录，那就判断是不是最后一个步骤，如果是这说明已经结束了
            List<ApprovalStepPO> approvalStepPOS = approvalStepService.selectByTypeId(typeId);
            if (CollUtil.isEmpty(approvalStepPOS)) {
                throw new BusinessException("根据审批类型获取审批步骤失败");
            }
            if (!approvalStepPO.getStepOrder().equals(approvalStepPOS.get(approvalStepPOS.size() - 1).getStepOrder())) {
                // 未结束：获取下一个步骤
                for (ApprovalStepPO stepPO : approvalStepPOS) {
                    if (stepPO.getStepOrder() > approvalStepPO.getStepOrder()) {
                        nextStepId = stepPO.getId();
                        break;
                    }
                }
            }
        }
        // 没有下一步
        if (Objects.isNull(nextStepId)) {
            Integer count = approvalRecordService.updateApprovalRecordById(approvalId, date, null, SUCCESS);
            if (count == 0) {
                throw new BusinessException("更新最后一步审核记录失败");
            }
        } else {
            // 有下一步
            int created = createApprovalStepRecord(approvalId, date, nextStepId);
            if (created == 0) {
                throw new BusinessException("添加审核步骤记录失败");
            }
            Integer count = approvalRecordService.updateApprovalRecordById(approvalId, date, nextStepId, WAITING);
            if (count == 0) {
                throw new BusinessException("更新审核记录失败");
            }
        }
        return true;
    }


    /**
     * 插入表单
     *
     * @param map
     * @return 表单 id
     */
    public abstract String insertDocument(Map<String, Object> map);

    /**
     * 根据 id 删除表单
     *
     * @param id
     * @return
     */
    public abstract Integer deleteDocument(String id);

    /**
     * 根据 id 查询表单
     *
     * @param id
     * @return
     */
    public abstract Object selectDocument(String id);

    /**
     * 根据 id 更新表单信息
     *
     * @param map 表单信息
     * @param id  表单 id
     * @return
     */
    public abstract Object updateById(Map<String, Object> map, String id);
}