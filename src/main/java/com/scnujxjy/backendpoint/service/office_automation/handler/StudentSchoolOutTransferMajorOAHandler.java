package com.scnujxjy.backendpoint.service.office_automation.handler;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.scnujxjy.backendpoint.constant.enums.PermissionSourceEnum;
import com.scnujxjy.backendpoint.constant.enums.office_automation.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.approval.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.mongoEntity.oa.StudentSchoolInTransferMajorDocument;
import com.scnujxjy.backendpoint.dao.mongoEntity.oa.StudentSchoolOutTransferMajorDocument;
import com.scnujxjy.backendpoint.dao.repository.StudentSchoolOutTransferMajorRepository;
import com.scnujxjy.backendpoint.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.scnujxjy.backendpoint.constant.NumberConstant.*;

@Component
@Transactional
@Slf4j
public class StudentSchoolOutTransferMajorOAHandler extends OfficeAutomationHandler {

    @Resource
    private StudentSchoolOutTransferMajorRepository studentSchoolOutTransferMajorRepository;

    @Override
    public OfficeAutomationHandlerType supportType() {
        return OfficeAutomationHandlerType.STUDENT_SCHOOL_OUT_TRANSFER_MAJOR;
    }

    @Override
    public void afterProcess(ApprovalStepRecordPO approvalStepRecordPO, ApprovalRecordPO approvalRecordPO) {
        log.info("学生校外转专业步骤流转，目前步骤 {} 目前记录 {}", approvalStepRecordPO, approvalRecordPO);
        Long messageId = systemMessageService.saveOrUpdateApprovalMessage(approvalRecordPO, approvalStepRecordPO.getApprovalUsernameSet(), OfficeAutomationHandlerType.STUDENT_SCHOOL_OUT_TRANSFER_MAJOR);
        if (Objects.isNull(messageId)) {
            log.info("发送系统消息失败");
            throw new BusinessException("发送系统消息失败");
        }
    }

    @Override
    public void afterApproval(ApprovalRecordPO approvalRecordPO, ApprovalStepRecordPO approvalStepRecordPO) {
        log.info("校外转专业完成 审批记录 {} 最后一步记录 {}", approvalRecordPO, approvalStepRecordPO);
        // 新增或更新信息
        Long messageId = systemMessageService.saveOrUpdateApprovalMessage(approvalRecordPO, Sets.newHashSet(), OfficeAutomationHandlerType.STUDENT_SCHOOL_OUT_TRANSFER_MAJOR);
        if (Objects.isNull(messageId)) {
            log.error("发送系统消息失败");
            throw new BusinessException("发送系统消息失败");
        }
    }


    @Override
    protected Set<String> buildApprovalUsernameSet(ApprovalStepPO approvalStepPO, String documentId) {
        StudentSchoolOutTransferMajorDocument studentSchoolOutTransferMajorDocument = selectDocument(documentId);
        if (Objects.isNull(studentSchoolOutTransferMajorDocument)) {
            throw new BusinessException("审核表单不存在");
        }
        Set<String> usernameSet = Sets.newHashSet();
        switch (approvalStepPO.getStepOrder()) {
            case ONE_INT:
                // 学生提交表单确认
                usernameSet.add(studentSchoolOutTransferMajorDocument.getStudentUsername());
                break;
            case TWO_INT:
                // 确认表单并打印
                usernameSet.add(studentSchoolOutTransferMajorDocument.getStudentUsername());
                break;
            case THREE_INT:
                // 继续教育学院确认
                usernameSet.addAll(platformUserService.selectUsernameByPermissionResource(Sets.newHashSet(PermissionSourceEnum.APPROVAL_APPROVAL.getPermissionSource())));
                break;
            default:
                break;
        }
        return usernameSet;
    }

    @Override
    protected Set<String> buildWatchUsernameSet(ApprovalRecordPO approvalRecordPO) {
        StudentSchoolOutTransferMajorDocument studentSchoolOutTransferMajorDocument = studentSchoolOutTransferMajorRepository.findById(approvalRecordPO.getDocumentId()).orElseThrow(() -> new BusinessException("无法查询到表单信息"));
        HashSet<String> watchUsernameSet = CollUtil.newHashSet(StpUtil.getLoginIdAsString());
        watchUsernameSet.add(studentSchoolOutTransferMajorDocument.getStudentUsername());
        watchUsernameSet.addAll(platformUserService.selectUsernameByPermissionResource(Sets.newHashSet(PermissionSourceEnum.APPROVAL_WATCH.getPermissionSource(),
                PermissionSourceEnum.APPROVAL_APPROVAL.getPermissionSource())));
        return watchUsernameSet;
    }

    @Override
    public String insertDocument(Map<String, Object> map) {
        if (MapUtil.isEmpty(map)) {
            throw new BusinessException("表单为空");
        }
        StudentSchoolOutTransferMajorDocument studentSchoolOutTransferMajorDocument = JSONObject.parseObject(JSONObject.toJSONString(map), StudentSchoolOutTransferMajorDocument.class);
        studentSchoolOutTransferMajorDocument = studentSchoolOutTransferMajorRepository.insert(studentSchoolOutTransferMajorDocument);
        return studentSchoolOutTransferMajorDocument.getId();
    }

    @Override
    public Integer deleteDocument(String id) {
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 为空");
        }
        studentSchoolOutTransferMajorRepository.deleteById(id);
        return 1;
    }

    @Override
    public StudentSchoolOutTransferMajorDocument selectDocument(String id) {
        if (StrUtil.isBlank(id)) {
            throw new BusinessException("表单 id 为空");
        }
        return studentSchoolOutTransferMajorRepository.findById(id)
                .orElseThrow(() -> new BusinessException("查询表单为空"));
    }

    @Override
    public StudentSchoolOutTransferMajorDocument updateById(Map<String, Object> map, String id) {
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
