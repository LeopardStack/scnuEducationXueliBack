package com.scnujxjy.backendpoint.service.office_automation;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Sets;
import com.scnujxjy.backendpoint.constant.SystemConstant;
import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.constant.enums.PermissionSourceEnum;
import com.scnujxjy.backendpoint.constant.enums.RoleEnum;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.mongoEntity.StudentSchoolInTransferMajorDocument;
import com.scnujxjy.backendpoint.dao.repository.StudentSchoolInTransferMajorRepository;
import com.scnujxjy.backendpoint.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.scnujxjy.backendpoint.constant.NumberConstant.*;
import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType.STUDENT_SCHOOL_IN_TRANSFER_MAJOR;
import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationStepStatus.SUCCESS;
import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationStepStatus.WAITING;

@Component
@Slf4j
@Transactional
public class StudentSchoolINTransferMajorOAHandler extends OfficeAutomationHandler {

    @Resource
    private StudentSchoolInTransferMajorRepository studentSchoolInTransferMajorRepository;

    /**
     * 获取支持类型
     *
     * @return 支持类型
     */
    @Override
    public OfficeAutomationHandlerType supportType() {
        return STUDENT_SCHOOL_IN_TRANSFER_MAJOR;
    }

    /**
     * 新增一个审批记录
     * <p>需要添加可以审核用户的集合</p>
     *
     * @param approvalRecordPO 审批记录
     * @return 新增的审批记录
     * @see CommonOfficeAutomationHandler#createApprovalRecord(ApprovalRecordPO)
     */
    @Override
    protected Boolean createApprovalRecord(ApprovalRecordPO approvalRecordPO) {
        if (Objects.isNull(approvalRecordPO)
                || Objects.isNull(approvalRecordPO.getApprovalTypeId())
                || Objects.isNull(approvalRecordPO.getDocumentId())) {
            throw new BusinessException("OA记录为空或OA类型id为空");
        }
        // 获取申请表单
        StudentSchoolInTransferMajorDocument studentSchoolInTransferMajorDocument = studentSchoolInTransferMajorRepository.findById(approvalRecordPO.getDocumentId()).orElse(null);
        if (Objects.isNull(studentSchoolInTransferMajorDocument)) {
            throw new BusinessException("学生装专业表单为空");
        }
        // 根据转出学院以及转入学院 id 来获取可见用户名单
        Set<String> watchUserIdSet = CollUtil.newHashSet(StpUtil.getLoginIdAsString());
        CollUtil.addAll(watchUserIdSet, collegeAdminInformationService.adminUsernameByCollegeId(studentSchoolInTransferMajorDocument.getFromCollegeId()));
        CollUtil.addAll(watchUserIdSet, collegeAdminInformationService.adminUsernameByCollegeId(studentSchoolInTransferMajorDocument.getToCollegeId()));
        CollUtil.addAll(watchUserIdSet, platformUserService.selectUsernameByPermissionResource(Sets.newHashSet(PermissionSourceEnum.APPROVAL_WATCH.getPermissionSource(),
                PermissionSourceEnum.APPROVAL_APPROVAL.getPermissionSource())));
        approvalRecordPO.setWatchUsernameSet(watchUserIdSet);
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
                .setUpdateAt(date)
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

    /**
     * 处理后的过程
     * <p>需要重写处理后每个事务需要执行的逻辑都不一样</p>
     *
     * @param approvalStepRecordPO
     * @see CommonOfficeAutomationHandler#afterProcess(ApprovalStepRecordPO, ApprovalRecordPO)
     */
    @Override
    public void afterProcess(ApprovalStepRecordPO approvalStepRecordPO, ApprovalRecordPO approvalRecordPO) {
        log.info("学生转专业步骤流转，目前步骤 {} 目前记录 {}", approvalStepRecordPO, approvalRecordPO);
        // 如果在财务处审批,请判断是否学费相同,学费相同再次流转
        ApprovalStepPO approvalStepPO = approvalStepService.selectById(approvalRecordPO.getCurrentStepId());
        if (Objects.isNull(approvalStepPO)) {
            log.warn("after process 中当前步骤记录查询为空 current step id {}", approvalRecordPO.getCurrentStepId());
            return;
        }
        if (Objects.equals(FOUR_INT, approvalStepPO.getStepOrder())) {
            StudentSchoolInTransferMajorDocument studentSchoolInTransferMajorDocument = selectDocument(approvalRecordPO.getDocumentId());
            if (Objects.equals(studentSchoolInTransferMajorDocument.getOriginalTuitionFee(), studentSchoolInTransferMajorDocument.getCurrentTuitionFee())) {
                // 获取最新一步的审核步骤
                List<ApprovalStepRecordPO> approvalStepRecordPOS = approvalStepRecordService.selectByApprovalRecordId(approvalRecordPO.getId());
                if (CollUtil.isEmpty(approvalStepRecordPOS)) {
                    log.warn("after process获取当前审批所有步骤为空");
                    return;
                }
                ApprovalStepRecordPO currentStepRecordPO = approvalStepRecordPOS.get(approvalStepRecordPOS.size() - 1);
                process(ApprovalStepRecordPO.builder()
                        .id(currentStepRecordPO.getId())
                        .username(SystemConstant.SYSTEM_NAME)
                        .approvalId(currentStepRecordPO.getApprovalId())
                        .comment("原学费标准与现学费标准相同,跳过财务处审核")
                        .status(SUCCESS.getStatus())
                        .stepId(currentStepRecordPO.getStepId())
                        .build());
                // 更新mongoDB
                updateById(JSON.parseObject(JSON.toJSONString(StudentSchoolInTransferMajorDocument.builder()
                        .feeSettlementStatus("原学费标准与现学费标准相同")
                        .build()), new TypeReference<Map<String, Object>>() {
                }), studentSchoolInTransferMajorDocument.getId());
            }
        }
        // TODO 发送通知

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
        log.info("学生转专业审核完成，审批记录：{}，最后一步记录：{}", approvalRecordPO, approvalStepRecordPO);
    }

    @Override
    protected Set<String> buildApprovalUsernameSet(ApprovalStepPO approvalStepPO, String documentId) {
        StudentSchoolInTransferMajorDocument studentSchoolInTransferMajorDocument = studentSchoolInTransferMajorRepository.findById(documentId).orElse(null);
        if (Objects.isNull(studentSchoolInTransferMajorDocument)) {
            throw new BusinessException("审核表单不存在");
        }
        // 根据不同步骤填充不同的审核用户群
        Set<String> usernameSet = CollUtil.newHashSet();
        switch (approvalStepPO.getStepOrder()) {
            case ONE_INT:
                // 提交学生审核
                usernameSet.add(studentSchoolInTransferMajorDocument.getStudentUsername());
                break;
            case TWO_INT:
                // 转出学院审核
                usernameSet.addAll(collegeAdminInformationService.adminUsernameByCollegeId(studentSchoolInTransferMajorDocument.getFromCollegeId()));
                break;
            case THREE_INT:
                // 转入学院审核
                usernameSet.addAll(collegeAdminInformationService.adminUsernameByCollegeId(studentSchoolInTransferMajorDocument.getToCollegeId()));
                break;
            case FOUR_INT:
                // 财务处审核
                usernameSet.addAll(platformUserService.selectUsernameByRoleName(Sets.newHashSet(RoleEnum.CAIWUBU_ADMIN.getRoleName())));
                break;
            case FIVE_INT:
                // 继续学院审核
                usernameSet.addAll(platformUserService.selectUsernameByPermissionResource(Sets.newHashSet(PermissionSourceEnum.APPROVAL_APPROVAL.getPermissionSource())));
                break;
            default:
                log.info("审核完成");
                break;
        }
        log.info("下一步审核用户群 {}", usernameSet);
        return usernameSet;
    }

    /**
     * 插入学生表单
     *
     * @param map 学生表单
     * @return
     */
    @Override
    public String insertDocument(Map<String, Object> map) {
        if (Objects.isNull(map)) {
            throw new BusinessException("专业表单为空");
        }
        StudentSchoolInTransferMajorDocument studentSchoolInTransferMajorDocument = JSONObject.parseObject(JSONObject.toJSONString(map), StudentSchoolInTransferMajorDocument.class);
        if (Objects.isNull(studentSchoolInTransferMajorDocument)) {
            throw new BusinessException("转专业表单为空");
        }
        if (StrUtil.isBlank(studentSchoolInTransferMajorDocument.getStudentUsername())) {
            studentSchoolInTransferMajorDocument.setStudentUsername(StpUtil.getLoginIdAsString());
        }
        if (Objects.isNull(studentSchoolInTransferMajorDocument.getFromCollegeId())) {
            throw new BusinessException("转出学院 id 不能为空");
        }
        if (Objects.isNull(studentSchoolInTransferMajorDocument.getToCollegeId())) {
            throw new BusinessException("转出学院 id 不能为空");
        }

        studentSchoolInTransferMajorDocument = studentSchoolInTransferMajorRepository.insert(studentSchoolInTransferMajorDocument);
        return studentSchoolInTransferMajorDocument.getId();
    }

    /**
     * 根据 id 删除表单
     *
     * @param id
     * @return
     */
    @Override
    public Integer deleteDocument(String id) {
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 为空");
        }
        studentSchoolInTransferMajorRepository.deleteById(id);
        return 1;
    }

    /**
     * 根据 id 查询表单
     *
     * @param id
     * @return
     */
    @Override
    public StudentSchoolInTransferMajorDocument selectDocument(String id) {
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 为空");
        }
        return studentSchoolInTransferMajorRepository.findById(id).orElseThrow(() -> new BusinessException("查询表单为空"));
    }


    /**
     * 根据 id 更新表单信息
     *
     * @param map 表单信息
     * @param id  表单 id
     * @return
     */
    @Override
    public StudentSchoolInTransferMajorDocument updateById(Map<String, Object> map, String id) {
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 为空");
        }
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update();
        map.forEach((key, value) -> {
            if (!"id".equals(key)) {
                update.set(key, value);
            }
        });
        mongoTemplate.updateMulti(query, update, StudentSchoolInTransferMajorDocument.class);
        return selectDocument(id);
    }
}
