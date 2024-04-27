package com.scnujxjy.backendpoint.service.office_automation;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import com.scnujxjy.backendpoint.constant.enums.OfficeAutomationHandlerType;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalRecordPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepPO;
import com.scnujxjy.backendpoint.dao.entity.office_automation.ApprovalStepRecordPO;
import com.scnujxjy.backendpoint.dao.mongoEntity.StudentSchoolInTransferMajorDocument;
import com.scnujxjy.backendpoint.dao.mongoEntity.StudentSchoolOutTransferMajorDocument;
import com.scnujxjy.backendpoint.dao.repository.StudentSchoolOutTransferMajorRepository;
import com.scnujxjy.backendpoint.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.scnujxjy.backendpoint.constant.NumberConstant.*;
import static com.scnujxjy.backendpoint.constant.enums.OfficeAutomationStepStatus.WAITING;

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
    protected Boolean createApprovalRecord(ApprovalRecordPO approvalRecordPO) {
        return null;
    }

    @Override
    public void afterProcess(ApprovalStepRecordPO approvalStepRecordPO, ApprovalRecordPO approvalRecordPO) {

    }

    @Override
    public void afterApproval(ApprovalRecordPO approvalRecordPO, ApprovalStepRecordPO approvalStepRecordPO) {

    }

    @Override
    protected int createApprovalStepRecord(Long approvalId, Date date, Long stepId) {
        if (Objects.isNull(approvalId)
                || Objects.isNull(date)
                || Objects.isNull(stepId)) {
            throw new BusinessException("新建步骤记录失败，缺失审核记录id、日期、步骤id等信息");
        }
        ApprovalRecordPO approvalRecordPO = approvalRecordMapper.selectById(approvalId);
        if (Objects.isNull(approvalRecordPO)) {
            throw new BusinessException("获取记录失败");
        }
        StudentSchoolOutTransferMajorDocument studentSchoolOutTransferMajorDocument = selectDocument(approvalRecordPO.getDocumentId());
        if (Objects.isNull(studentSchoolOutTransferMajorDocument)) {
            throw new BusinessException("审核表单不存在");
        }
        ApprovalStepPO approvalStepPO = selectApprovalStep(stepId);
        if (Objects.isNull(approvalStepPO)) {
            throw new BusinessException("获取下一个步骤失败，流转失败");
        }
        Set<String> usernameSet = Sets.newHashSet();
        switch (approvalStepPO.getStepOrder()) {
            case ONE_INT:
                // 学生提交表单确认
                usernameSet.add(studentSchoolOutTransferMajorDocument.getStudentUsername());
                break;
            case TWO_INT:
                // 确认表单并打印

                break;
            case THREE_INT:
                // 继续教育学院确认
                break;
            default:
                break;
        }
        ApprovalStepRecordPO approvalStepRecordPO = ApprovalStepRecordPO.builder()
                .approvalId(approvalId)
                .stepId(approvalStepPO.getId())
                .updateAt(date)
                .status(WAITING.getStatus())
                .approvalTypeId(approvalStepPO.getApprovalTypeId())
                .approvalUsernameSet(usernameSet)
                .build();
        int count = approvalStepRecordMapper.insert(approvalStepRecordPO);
        if (count == 0) {
            throw new BusinessException("插入新的步骤记录失败");
        }
        return count;
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
