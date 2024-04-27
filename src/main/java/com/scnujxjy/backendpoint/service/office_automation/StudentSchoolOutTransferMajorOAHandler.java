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

                break;
            case THREE_INT:
                // 继续教育学院确认
                break;
            default:
                break;
        }
        return usernameSet;
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
